package io.github.busy_spin.artio.fix_engine;

import io.aeron.Aeron;
import io.github.busy_spin.artio.AppLauncher;
import io.github.busy_spin.artio.utils.ThreadFactoryUtils;
import org.agrona.concurrent.NoOpIdleStrategy;
import uk.co.real_logic.artio.engine.EngineConfiguration;
import uk.co.real_logic.artio.engine.FixEngine;

public class FixEngineLauncher implements AppLauncher {

    private FixEngine fixEngine;

    /*
    this.outboundReplayStream = 3;
    this.archiveReplayStream = 4;
    this.reproductionLogStream = 6;
    this.reproductionReplayStream = 7;
    this.inboundAdminStream = 21;
    this.outboundAdminStream = 22;
    *
    * */
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
                .outboundReplayStream(Integer.getInteger("artio.outbound.replay.stream"))
                .archiveReplayStream(Integer.getInteger("artio.archive.replay.stream"))
                .reproductionLogStream(Integer.getInteger("artio.reproduction.log.stream"))
                .reproductionReplayStream(Integer.getInteger("artio.reproduction.replay.stream"))
                .inboundAdminStream(Integer.getInteger("artio.inbound.admin.stream"))
                .outboundAdminStream(Integer.getInteger("artio.outbound.admin.stream"))
                .inboundLibraryStream(Integer.getInteger("artio.inbound.library.stream"))
                .outboundLibraryStream(Integer.getInteger("artio.outbound.library.stream"))
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
