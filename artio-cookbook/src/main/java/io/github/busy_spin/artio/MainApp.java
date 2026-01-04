package io.github.busy_spin.artio;

import io.github.busy_spin.artio.media_driver.MediaDriverLauncher;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.ShutdownSignalBarrier;

@Slf4j
public class MainApp {

    public static void main(String[] args) {
        try (ShutdownSignalBarrier barrier = new ShutdownSignalBarrier()) {
            String appId = System.getenv("APP_ID");
            AppLauncher launcher = null;
            if (AppLauncherUtils.MEDIA_DRIVER_APP_ID.equals(appId)) {
                launcher = new MediaDriverLauncher();
                launcher.launch();
            }

            if (launcher != null) {
                barrier.await();
                try {
                    launcher.close();
                } catch (Exception e) {
                    log.error("Error closing launcher", e);
                }
            } else {
                log.error("AppLauncher not found");
            }
        } finally {
            log.info("MainApp shutdown signal");
        }
    }

}