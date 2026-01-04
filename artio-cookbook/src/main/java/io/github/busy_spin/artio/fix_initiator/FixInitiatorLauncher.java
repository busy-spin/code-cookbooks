package io.github.busy_spin.artio.fix_initiator;

import io.aeron.Aeron;
import io.github.busy_spin.artio.AppLauncher;
import io.github.busy_spin.artio.utils.ThreadFactoryUtils;
import uk.co.real_logic.artio.library.AcquiringSessionExistsHandler;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConfiguration;

import java.util.Collections;

public class FixInitiatorLauncher implements AppLauncher {

    private FixLibrary fixLibrary;

    @Override
    public void launch() {
        LibraryConfiguration configuration = new LibraryConfiguration();
        configuration.threadFactory(ThreadFactoryUtils.newDeamonThreadFactory());
        FixTestRequestHandler handler = new FixTestRequestHandler();
        configuration.sessionExistsHandler(new AcquiringSessionExistsHandler())
                .libraryAeronChannels(Collections.singletonList(Aeron.Context.IPC_CHANNEL))
                .sessionAcquireHandler(handler)
                .libraryConnectHandler(handler)
                .inboundLibraryStream(Integer.getInteger("artio.inbound.library.stream"))
                .outboundLibraryStream(Integer.getInteger("artio.outbound.library.stream"));
        fixLibrary = FixLibrary.connect(configuration);
    }

    @Override
    public void close() throws Exception {
        if (fixLibrary != null) {
            fixLibrary.close();
        }
    }
}
