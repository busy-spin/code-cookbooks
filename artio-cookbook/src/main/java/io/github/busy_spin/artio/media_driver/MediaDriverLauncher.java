package io.github.busy_spin.artio.media_driver;

import io.aeron.driver.MediaDriver;
import io.github.busy_spin.artio.AppLauncher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MediaDriverLauncher implements AppLauncher {

    private MediaDriver mediaDriver;

    @Override
    public void launch() {
        mediaDriver = MediaDriver.launch(new MediaDriver.Context()
                .printConfigurationOnStart(true));
    }

    @Override
    public void close() throws Exception {
        if (mediaDriver != null) {
            mediaDriver.close();
            log.info("MediaDriver closed successfully");
        }
    }
}
