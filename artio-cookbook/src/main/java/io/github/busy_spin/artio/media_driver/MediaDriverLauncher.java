package io.github.busy_spin.artio.media_driver;

import io.aeron.driver.MediaDriver;
import io.github.busy_spin.artio.AppLauncher;
import io.github.busy_spin.artio.AppLauncherUtils;

public class MediaDriverLauncher implements AppLauncher {

    MediaDriver mediaDriver;

    @Override
    public String id() {
        return AppLauncherUtils.MEDIA_DRIVER_APP_ID;
    }

    @Override
    public void launch() {
        mediaDriver = MediaDriver.launch(new MediaDriver.Context());
    }

    @Override
    public void close() throws Exception {
        if (mediaDriver != null) {
            mediaDriver.close();
        }
    }
}
