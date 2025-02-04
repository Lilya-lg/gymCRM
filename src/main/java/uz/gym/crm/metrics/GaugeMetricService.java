package uz.gym.crm.metrics;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GaugeMetricService {

    private final AtomicInteger activeUsers = new AtomicInteger(0);

    public GaugeMetricService(MeterRegistry meterRegistry) {
        Gauge.builder("custom_active_users_gauge", activeUsers, AtomicInteger::get)
                .description("Tracks active users")
                .register(meterRegistry);
    }

    public void userLoggedIn() {
        activeUsers.incrementAndGet();
    }

    public void userLoggedOut() {
        activeUsers.decrementAndGet();
    }
}

