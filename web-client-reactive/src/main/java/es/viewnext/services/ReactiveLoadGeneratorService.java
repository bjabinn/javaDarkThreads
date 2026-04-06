package es.viewnext.services;

import es.viewnext.dto.LoadRequest;
import es.viewnext.dto.LoadResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ReactiveLoadGeneratorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReactiveLoadGeneratorService.class);
    
    private final Counter requestCounter;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer requestTimer;
    private final MeterRegistry meterRegistry;
    private final WebClient webClient;
    
    public ReactiveLoadGeneratorService(MeterRegistry meterRegistry, WebClient webClient) {
        this.meterRegistry = meterRegistry;
        this.webClient = webClient;
        this.requestCounter = Counter.builder("load.requests.total")
                .description("Total number of load test requests")
                .tag("thread_type", "reactive")
                .register(meterRegistry);
        
        this.successCounter = Counter.builder("load.requests.success")
                .description("Number of successful requests")
                .tag("thread_type", "reactive")
                .register(meterRegistry);
        
        this.failureCounter = Counter.builder("load.requests.failure")
                .description("Number of failed requests")
                .tag("thread_type", "reactive")
                .register(meterRegistry);
        
        this.requestTimer = Timer.builder("load.request.duration")
                .description("Duration of individual requests")
                .tag("thread_type", "reactive")
                .register(meterRegistry);
    }
    
    public Mono<LoadResponse> executeLoad(LoadRequest request) {
        logger.info("Starting reactive load test: {}", request);
        
        LoadResponse response = new LoadResponse();
        response.setTotalRequests(request.getRequests());
        response.setThreadsUsed(request.getThreads());
        response.setStartTime(LocalDateTime.now());
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // Execute requests reactively using Flux
        return Flux.range(0, request.getRequests())
                .flatMap(i -> executeReactiveRequest(i, request.getDelayMs(), 
                        request.getTargetUrl(), successCount, failureCount),
                        request.getThreads()) // Concurrency level
                .collectList()
                .map(responseTimes -> {
                    long endTime = System.currentTimeMillis();
                    long totalDuration = endTime - startTime;
                    
                    // Calculate statistics
                    LongSummaryStatistics stats = responseTimes.stream()
                            .mapToLong(Long::longValue)
                            .summaryStatistics();
                    
                    response.setEndTime(LocalDateTime.now());
                    response.setTotalDurationMs(totalDuration);
                    response.setSuccessCount(successCount.get());
                    response.setFailureCount(failureCount.get());
                    response.setAvgResponseTimeMs(stats.getAverage());
                    response.setMinResponseTimeMs(stats.getMin());
                    response.setMaxResponseTimeMs(stats.getMax());
                    
                    // Calculate throughput (requests per second)
                    double throughput = (double) request.getRequests() / (totalDuration / 1000.0);
                    response.setThroughput(throughput);
                    
                    // Additional metrics
                    Map<String, Object> additionalMetrics = new HashMap<>();
                    additionalMetrics.put("activeThreads", Thread.activeCount());
                    additionalMetrics.put("totalThreads", Thread.getAllStackTraces().size());
                    additionalMetrics.put("availableProcessors", Runtime.getRuntime().availableProcessors());
                    additionalMetrics.put("freeMemoryMB", Runtime.getRuntime().freeMemory() / 1024 / 1024);
                    additionalMetrics.put("totalMemoryMB", Runtime.getRuntime().totalMemory() / 1024 / 1024);
                    additionalMetrics.put("maxMemoryMB", Runtime.getRuntime().maxMemory() / 1024 / 1024);
                    response.setAdditionalMetrics(additionalMetrics);
                    
                    logger.info("Reactive load test completed: {} requests in {} ms, throughput: {} req/s", 
                               request.getRequests(), totalDuration, String.format("%.2f", throughput));
                    
                    return response;
                })
                .doOnError(error -> logger.error("Error executing reactive load test", error));
    }
    
    private Mono<Long> executeReactiveRequest(int requestNum, long delayMs, String targetUrl,
                                              AtomicInteger successCount, AtomicInteger failureCount) {
        long start = System.nanoTime();
        
        // Increment request counter
        requestCounter.increment();
        
        Mono<Long> requestMono;
        
        // If targetUrl is provided, make real HTTP call
        if (targetUrl != null && !targetUrl.isEmpty()) {
            requestMono = webClient.get()
                    .uri(targetUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(body -> {
                        successCount.incrementAndGet();
                        successCounter.increment();
                        logger.debug("Request #{} received HTTP 200 from {}", requestNum, targetUrl);
                    })
                    .onErrorResume(WebClientResponseException.class, e -> {
                        failureCount.incrementAndGet();
                        failureCounter.increment();
                        logger.warn("Request #{} received HTTP {} from {}", 
                                  requestNum, e.getStatusCode(), targetUrl);
                        return Mono.empty();
                    })
                    .onErrorResume(error -> {
                        failureCount.incrementAndGet();
                        failureCounter.increment();
                        logger.error("Request #{} failed to call: {}", requestNum, targetUrl, error);
                        return Mono.empty();
                    })
                    .then(Mono.fromCallable(() -> {
                        long duration = (System.nanoTime() - start) / 1_000_000;
                        logger.debug("Request #{} completed in {} ms", requestNum, duration);
                        requestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                        return duration;
                    }));
        } else {
            // No URL provided, just simulate delay reactively
            Mono<Void> delayMono = delayMs > 0 
                    ? Mono.delay(Duration.ofMillis(delayMs)).then()
                    : Mono.empty();
            
            requestMono = delayMono
                    .then(Mono.fromCallable(() -> {
                        successCount.incrementAndGet();
                        successCounter.increment();
                        long duration = (System.nanoTime() - start) / 1_000_000;
                        logger.debug("Request #{} completed in {} ms", requestNum, duration);
                        requestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
                        return duration;
                    }));
        }
        
        return requestMono.onErrorResume(error -> {
            failureCount.incrementAndGet();
            failureCounter.increment();
            long duration = (System.nanoTime() - start) / 1_000_000;
            logger.error("Request #{} failed", requestNum, error);
            requestTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            return Mono.just(duration);
        });
    }
}