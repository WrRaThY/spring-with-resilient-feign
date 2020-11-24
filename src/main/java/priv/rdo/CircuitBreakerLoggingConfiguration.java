package priv.rdo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.event.RetryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerLoggingConfiguration {
    private static final Logger circuitBreakerState = LoggerFactory.getLogger("circuitBreakerState");
    private static final Logger circuitBreakerRetry = LoggerFactory.getLogger("circuitBreakerRetry");

    private static final String EVENT_TYPE_LABEL = "circuitBreakerEventType";
    private static final String NAME_LABEL = "circuitBreakerName";

    @Bean
    public RegistryEventConsumer<CircuitBreaker> customCircuitBreakerRegistryEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                CircuitBreaker.EventPublisher eventPublisher = entryAddedEvent.getAddedEntry().getEventPublisher();

                eventPublisher.onEvent(event -> {
                    if (event.getEventType() == CircuitBreakerEvent.Type.SUCCESS && !circuitBreakerState.isDebugEnabled()) {
                        return;
                    }

                    // that little trick adds it to MDC, which can then be used in a log statement
                    MDC.put(EVENT_TYPE_LABEL, event.getEventType().name());
                    MDC.put(NAME_LABEL, event.getCircuitBreakerName());

                    if (event.getEventType() == CircuitBreakerEvent.Type.SUCCESS) {
                        circuitBreakerState.debug(event.toString());
                    } else {
                        circuitBreakerState.error(event.toString());
                    }

                    MDC.remove(EVENT_TYPE_LABEL);
                    MDC.remove(NAME_LABEL);
                });
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
            }
        };
    }

    @Bean
    public RegistryEventConsumer<Retry> customRetryRegistryEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
                Retry.EventPublisher eventPublisher = entryAddedEvent.getAddedEntry().getEventPublisher();

                eventPublisher.onEvent(event -> {
                    if (event.getEventType() == RetryEvent.Type.SUCCESS && !circuitBreakerRetry.isDebugEnabled()) {
                        return;
                    }

                    MDC.put(EVENT_TYPE_LABEL, event.getEventType().name());
                    MDC.put(NAME_LABEL, event.getName());

                    if (event.getEventType() == RetryEvent.Type.SUCCESS) {
                        circuitBreakerRetry.debug(event.toString());
                    } else {
                        circuitBreakerRetry.error(event.toString(), event.getLastThrowable());
                    }

                    MDC.remove(EVENT_TYPE_LABEL);
                    MDC.remove(NAME_LABEL);
                });
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {
            }
        };
    }
}
