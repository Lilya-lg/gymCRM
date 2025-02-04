package uz.gym.crm.metrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TimerMetricService {

    private final Timer requestTimer;

    public TimerMetricService(MeterRegistry meterRegistry) {
        this.requestTimer = meterRegistry.timer("custom_request_timer", "endpoint", "sample");
    }

    public void trackRequestTime() {
        long startTime = System.nanoTime();

        try {
            // Simulate work
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            requestTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
    }
}
