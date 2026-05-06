package com.reactive.apps.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class ProductMetricsCollector {

    private final AtomicLong totalCreated = new AtomicLong(0);
    private final AtomicLong totalFetched = new AtomicLong(0);
    private final AtomicLong totalUpdated = new AtomicLong(0);
    private final AtomicLong totalDeleted = new AtomicLong(0);

    private ProductMetricsCollector() {}

    // Lazy Initialization
    private static class Holder {
        private static final ProductMetricsCollector INSTANCE = new ProductMetricsCollector();
    }

    // Lazy Initialization
    public static ProductMetricsCollector getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * AtomicLong.incrementAndGet() Lee, suma y escribe
     * Hace los tres pasos como una sola operación indivisible
     * ningún hilo puede interrumpirla en la mitad.
     */
    public void recordCreate() { totalCreated.incrementAndGet(); }
    public void recordFetch()  { totalFetched.incrementAndGet(); }
    public void recordUpdate() { totalUpdated.incrementAndGet(); }
    public void recordDelete() { totalDeleted.incrementAndGet(); }

    public long getCreated() { return totalCreated.get(); }
    public long getFetched() { return totalFetched.get(); }
    public long getUpdated() { return totalUpdated.get(); }
    public long getDeleted() { return totalDeleted.get(); }

    public void reset() {
        totalCreated.set(0);
        totalFetched.set(0);
        totalUpdated.set(0);
        totalDeleted.set(0);
    }

}
