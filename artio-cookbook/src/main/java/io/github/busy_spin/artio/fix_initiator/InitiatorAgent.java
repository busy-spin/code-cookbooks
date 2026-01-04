package io.github.busy_spin.artio.fix_initiator;

import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.Agent;

@Slf4j
public class InitiatorAgent implements Agent {

    private long lastReqSendTime = System.currentTimeMillis();
    private long lastConnectAttemptTime = System.currentTimeMillis();

    private final int connectInternalMs = 5_000;
    private final int reqPerMs = Integer.getInteger("artio.request.per.ms");
    private final FixTestRequestHandler fixTestRequestHandler;

    public InitiatorAgent(FixTestRequestHandler fixTestRequestHandler) {
        this.fixTestRequestHandler = fixTestRequestHandler;
    }

    @Override
    public void onStart() {
        log.info("Agent {} has started", roleName());
    }

    @Override
    public int doWork() throws Exception {

        return 0;
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
