package uz.gym.crm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class MetricService {
    private final Counter myCounter;

    public MetricService(MeterRegistry meterRegistry) {
        this.myCounter = meterRegistry.counter("my_metric_counter", "type", "my");
    }

    public void incrementCounter() {
        myCounter.increment();
    }
}
