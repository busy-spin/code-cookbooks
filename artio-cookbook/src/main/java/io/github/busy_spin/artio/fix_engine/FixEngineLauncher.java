package io.github.busy_spin.artio.fix_engine;

import io.aeron.Aeron;
import io.github.busy_spin.artio.AppLauncher;
import io.github.busy_spin.artio.utils.ThreadFactoryUtils;
import org.agrona.concurrent.NoOpIdleStrategy;
import uk.co.real_logic.artio.engine.EngineConfiguration;
import uk.co.real_logic.artio.engine.FixEngine;

public class FixEngineLauncher implements AppLauncher {

    private FixEngine fixEngine;

    @Override
    public void launch() {
        EngineConfiguration configuration = new EngineConfiguration();
        configuration
                .bindTo(System.getProperty("artio.host"), Integer.getInteger("artio.port"))
                .libraryAeronChannel(Aeron.Context.IPC_CHANNEL)
                .deleteLogFileDirOnStart(true)
                .logFileDir(System.getProperty("artio.logs.dir"))
                .logInboundMessages(false)
                .logOutboundMessages(false)
                .threadFactory(ThreadFactoryUtils.newDeamonThreadFactory())
                .framerIdleStrategy(new NoOpIdleStrategy())
                .monitoringThreadIdleStrategy(new NoOpIdleStrategy())
                .archiverIdleStrategy(new NoOpIdleStrategy())
                .printAeronStreamIdentifiers(true);

        fixEngine = FixEngine.launch(configuration);
    }

    @Override
    public void close() throws Exception {
        if (fixEngine != null) {
            fixEngine.close();
        }
    }
}
