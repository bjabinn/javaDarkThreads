package es.viewnext.services;

import es.viewnext.dto.LoadRequest;
import es.viewnext.dto.LoadResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PlatformThreadLoadGeneratorService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlatformThreadLoadGeneratorService.class);
    
    private final Counter requestCounter;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer requestTimer;
    private final MeterRegistry meterRegistry;
    private final RestTemplate restTemplate;
    
    public PlatformThreadLoadGeneratorService(MeterRegistry meterRegistry, RestTemplate restTemplate) {
        this.meterRegistry = meterRegistry;
        this.restTemplate = restTemplate;
        this.requestCounter = Counter.builder("load.requests.total")
                .description("Total number of load test requests")
                .tag("thread_type", "platform")
                .register(meterRegistry);
        
        this.successCounter = Counter.builder("load.requests.success")
                .description("Number of successful requests")
                .tag("thread_type", "platform")
                .register(meterRegistry);
        
        this.failureCounter = Counter.builder("load.requests.failure")
                .description("Number of failed requests")
                .tag("thread_type", "platform")
                .register(meterRegistry);
        
        this.requestTimer = Timer.builder("load.request.duration")
                .description("Duration of individual requests")
                .tag("thread_type", "platform")
                .register(meterRegistry);
    }
    
    public LoadResponse executeLoad(LoadRequest request) {
        logger.info("Starting load test: {}", request);
        
        LoadResponse response = new LoadResponse();
        response.setTotalRequests(request.getRequests());
        response.setThreadsUsed(request.getThreads());
        response.setStartTime(LocalDateTime.now());
        
        // Create thread pool with platform threads
        ExecutorService executorService = Executors.newFixedThreadPool(request.getThreads(), r -> {
            Thread thread = new Thread(r);
            thread.setName("platform-thread-" + System.currentTimeMillis());
            return thread;
        });
        
        List<Future<Long>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Submit all tasks to the executor
            for (int i = 0; i < request.getRequests(); i++) {
                final int requestNum = i;
                Future<Long> future = executorService.submit(() -> {
                    return executeRequest(requestNum, request.getTargetUrl(), successCount, failureCount);
                });
                futures.add(future);
            }
            
            // Wait for all tasks to complete
            List<Long> responseTimes = new ArrayList<>();
            for (Future<Long> future : futures) {
                try {
                    Long responseTime = future.get();
                    responseTimes.add(responseTime);
                } catch (Exception e) {
                    logger.error("Error getting future result", e);
                    failureCount.incrementAndGet();
                }
            }
            
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
            
            logger.info("Load test completed: {} requests in {} ms, throughput: {} req/s", 
                       request.getRequests(), totalDuration, String.format("%.2f", throughput));
            
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        return response;
    }
    
    private Long executeRequest(int requestNum, String targetUrl,
                                AtomicInteger successCount, AtomicInteger failureCount) {
        return requestTimer.record(() -> {
            long start = System.nanoTime();
            try {
                // Increment request counter
                requestCounter.increment();
                
                // If targetUrl is provided, make real HTTP call
                ResponseEntity<String> response = restTemplate.getForEntity(targetUrl, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    successCount.incrementAndGet();
                    successCounter.increment();
                    logger.debug("Request #{} received HTTP {} from {}", requestNum, response.getStatusCode(), targetUrl);
                } else {
                    failureCount.incrementAndGet();
                    failureCounter.increment();
                    logger.warn("Request #{} received non-2xx HTTP {} from {}", requestNum, response.getStatusCode(), targetUrl);
                }
                
                long duration = (System.nanoTime() - start) / 1_000_000; // convert to ms
                logger.debug("Request #{} completed in {} ms", requestNum, duration);
                return duration;
                
            } catch (Exception e) {
                failureCount.incrementAndGet();
                failureCounter.increment();
                logger.error("Request #{} failed", requestNum, e);
                return (System.nanoTime() - start) / 1_000_000;
            }
        });
    }
}