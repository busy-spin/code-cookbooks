package io.github.busy_spin.artio.media_driver;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import io.github.busy_spin.artio.AppLauncher;
import io.github.busy_spin.artio.utils.ThreadFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.NoOpIdleStrategy;

import java.util.concurrent.ThreadFactory;

@Slf4j
public class MediaDriverLauncher implements AppLauncher {

    private MediaDriver mediaDriver;

    @Override
    public void launch() {
        ThreadFactory threadFactory = ThreadFactoryUtils.newDeamonThreadFactory();
        final MediaDriver.Context ctx = new MediaDriver.Context()
                .termBufferSparseFile(false)
                .useWindowsHighResTimer(true)
                .threadingMode(ThreadingMode.DEDICATED)
                .conductorThreadFactory(threadFactory)
                .receiverThreadFactory(threadFactory)
                .senderThreadFactory(threadFactory)
                .conductorIdleStrategy(BusySpinIdleStrategy.INSTANCE)
                .receiverIdleStrategy(NoOpIdleStrategy.INSTANCE)
                .senderIdleStrategy(NoOpIdleStrategy.INSTANCE)
                .printConfigurationOnStart(true);

        mediaDriver = MediaDriver.launch(ctx);
    }

    @Override
    public void close() throws Exception {
        if (mediaDriver != null) {
            mediaDriver.close();
            log.info("MediaDriver closed successfully");
        }
    }
}
