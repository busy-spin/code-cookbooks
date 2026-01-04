package io.github.busy_spin.artio.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ShutdownUtils {

    private ShutdownUtils() {
    }

    public static void registerHook(Runnable hook) {
        Runtime.getRuntime().addShutdownHook(ThreadFactoryUtils.getDeamonThread(() -> {
            log.info("🛑 Stopping trading bot ...");
            hook.run();
        }, "shutdown-hook"));
    }

}
