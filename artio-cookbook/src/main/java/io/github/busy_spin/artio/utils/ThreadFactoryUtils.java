package io.github.busy_spin.artio.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadFactoryUtils {

    private ThreadFactoryUtils() {
        throw new IllegalStateException("Utility class");
    }


    public static ThreadFactory newThreadFactory(String prefix, boolean daemon) {
        return new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                String threadNameFormat = "%s-%d";
                thread.setName(String.format(threadNameFormat, prefix, counter.incrementAndGet()));
                thread.setDaemon(daemon);
                return thread;
            }
        };
    }

    public static ThreadFactory newDeamonThreadFactory() {
        return newThreadFactory("", true);
    }

    public static ThreadFactory newDeamonThreadFactory(String prefix) {
        return newThreadFactory(prefix, true);
    }

    public static Thread getDeamonThread(Runnable r, String threadName) {
        Thread thread = new Thread(r);
        thread.setName(threadName);
        thread.setDaemon(true);
        return thread;
    }
}
