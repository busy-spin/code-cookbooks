package io.github.busy_spin.artio.fix_initiator;

import io.aeron.Aeron;
import io.github.busy_spin.artio.utils.ThreadFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.Agent;
import uk.co.real_logic.artio.library.AcquiringSessionExistsHandler;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConfiguration;

import java.util.Collections;

@Slf4j
public class InitiatorAgent implements Agent {

    private long lastReqSendTime = System.currentTimeMillis();
    private long lastConnectAttemptTime = System.currentTimeMillis();

    private final int hosueKeepIntervalMs = 5_000;
    private final int reqPerMs = Integer.getInteger("artio.request.per.ms");
    private long reqCounter = 0;

    private final FixTestRequestHandler handler = new FixTestRequestHandler();
    private FixLibrary fixLibrary;

    public InitiatorAgent() {
    }

    @Override
    public void onStart() {
        log.info("Agent {} has started", roleName());

        LibraryConfiguration configuration = new LibraryConfiguration();
        configuration.threadFactory(ThreadFactoryUtils.newDeamonThreadFactory());
        configuration.sessionExistsHandler(new AcquiringSessionExistsHandler())
                .libraryAeronChannels(Collections.singletonList(Aeron.Context.IPC_CHANNEL))
                .sessionAcquireHandler(handler)
                .libraryConnectHandler(handler)
                .inboundLibraryStream(Integer.getInteger("artio.inbound.library.stream"))
                .outboundLibraryStream(Integer.getInteger("artio.outbound.library.stream"));
        fixLibrary = FixLibrary.connect(configuration);
    }

    @Override
    public int doWork() throws Exception {
        fixLibrary.poll(10);
        long currentTimeMs = System.currentTimeMillis();
        if (lastConnectAttemptTime + hosueKeepIntervalMs < currentTimeMs) {
            handler.tryConnect();
            handler.printAndResetCounters();
            lastConnectAttemptTime = currentTimeMs;
        }
        if (lastReqSendTime == currentTimeMs) {
            handler.sendTestReq();
            if (reqCounter < reqPerMs) {
                handler.sendTestReq();
                reqCounter++;
            }
        } else {
            lastReqSendTime = currentTimeMs;
            reqCounter = 0;
        }


        return 1;
    }

    @Override
    public void onClose() {
        Agent.super.onClose();
    }

    @Override
    public String roleName() {
        return "test-req-firing";
    }
}
