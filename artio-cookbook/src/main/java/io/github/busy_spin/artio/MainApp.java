package io.github.busy_spin.artio;

import io.github.busy_spin.artio.fix_engine.FixEngineLauncher;
import io.github.busy_spin.artio.fix_initiator.FixInitiatorLauncher;
import io.github.busy_spin.artio.media_driver.MediaDriverLauncher;
import io.github.busy_spin.artio.utils.EnvToProps;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.ShutdownSignalBarrier;

import static io.github.busy_spin.artio.AppLauncherUtils.FIX_ENGINE_APP_ID;
import static io.github.busy_spin.artio.AppLauncherUtils.FIX_INITIATOR_APP_ID;
import static io.github.busy_spin.artio.AppLauncherUtils.MEDIA_DRIVER_APP_ID;

@Slf4j
public class MainApp {

    public static void main(String[] args) {
        try (ShutdownSignalBarrier barrier = new ShutdownSignalBarrier()) {
            EnvToProps.initialize();
            String appId = System.getenv("APP_ID");
            AppLauncher launcher = switch (appId) {
                case MEDIA_DRIVER_APP_ID -> new MediaDriverLauncher();
                case FIX_ENGINE_APP_ID -> new FixEngineLauncher();
                case FIX_INITIATOR_APP_ID ->  new FixInitiatorLauncher();
                default -> null;
            };

            if (launcher != null) {
                launcher.launch();
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