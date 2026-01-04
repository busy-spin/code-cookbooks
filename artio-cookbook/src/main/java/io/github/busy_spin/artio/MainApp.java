package io.github.busy_spin.artio;

import io.github.busy_spin.artio.media_driver.MediaDriverLauncher;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.ShutdownSignalBarrier;

@Slf4j
public class MainApp {

    public static void main(String[] args) {
        try (ShutdownSignalBarrier barrier = new ShutdownSignalBarrier()) {
            if (args.length != 1) {
                log.error("Invalid number of parameters");
            } else {
                AppLauncher launcher = null;
                if (args[0].equals(AppLauncherUtils.MEDIA_DRIVER_APP_ID)) {
                    launcher = new MediaDriverLauncher();
                    launcher.launch();
                }

                barrier.await();

                if (launcher != null) {
                    try {
                        launcher.close();
                    } catch (Exception e) {
                        log.error("Error closing launcher", e);
                    }
                }
            }
        } finally {
            log.info("MainApp shutdown signal");
        }
    }

}