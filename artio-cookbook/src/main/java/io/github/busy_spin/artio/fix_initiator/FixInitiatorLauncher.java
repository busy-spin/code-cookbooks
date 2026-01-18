package io.github.busy_spin.artio.fix_initiator;

import io.github.busy_spin.artio.AppLauncher;
import io.github.busy_spin.artio.utils.ThreadFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.SleepingIdleStrategy;

@Slf4j
public class FixInitiatorLauncher implements AppLauncher {

    private AgentRunner agentRunner;

    @Override
    public void launch() {
        agentRunner = new AgentRunner(new SleepingIdleStrategy(), throwable -> {
            log.error("Error launching fix initiator", throwable);
        }, null, new InitiatorAgent());

        AgentRunner.startOnThread(agentRunner, ThreadFactoryUtils.newDeamonThreadFactory());
    }

    @Override
    public void close() throws Exception {
        if (agentRunner != null && !agentRunner.isClosed()) {
            agentRunner.close();
        }
    }
}
