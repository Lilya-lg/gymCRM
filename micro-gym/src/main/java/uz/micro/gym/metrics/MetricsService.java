package uz.micro.gym.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Counter createCounter(String name, String... tags) {
        return meterRegistry.counter(name, tags);
    }

    public Timer createTimer(String name, String... tags) {
        return meterRegistry.timer(name, tags);
    }

    public Gauge createGauge(String name, AtomicInteger value, String description) {
        return Gauge.builder(name, value, AtomicInteger::get)
                .description(description)
                .register(meterRegistry);
    }

    public void recordTimer(Timer timer, long startTime) {
        timer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    }
}