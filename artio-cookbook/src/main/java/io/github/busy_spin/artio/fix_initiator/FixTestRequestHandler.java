package io.github.busy_spin.artio.fix_initiator;

import io.aeron.logbuffer.ControlledFragmentHandler;
import lombok.extern.slf4j.Slf4j;
import org.HdrHistogram.Histogram;
import org.agrona.DirectBuffer;
import uk.co.real_logic.artio.decoder.HeartbeatDecoder;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConnectHandler;
import uk.co.real_logic.artio.library.OnMessageInfo;
import uk.co.real_logic.artio.library.SessionHandler;
import uk.co.real_logic.artio.messages.DisconnectReason;
import uk.co.real_logic.artio.session.CompositeKey;
import uk.co.real_logic.artio.session.Session;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

@Slf4j
public class FixTestRequestHandler implements SessionHandler, LibraryConnectHandler {

    private final MutableAsciiBuffer asciiBuffer = new MutableAsciiBuffer();
    private final HeartbeatDecoder heartbeatDecoder = new HeartbeatDecoder();

    private final Histogram histogram = new Histogram(3);

    @Override
    public void onConnect(FixLibrary fixLibrary) {
        log.info("Connected to fix engine");
    }

    @Override
    public void onDisconnect(FixLibrary fixLibrary) {
        log.info("Disconnected from fix engine");
    }

    @Override
    public ControlledFragmentHandler.Action onMessage(
            DirectBuffer buffer,
            int offset,
            int length,
            int libraryId,
            Session session,
            int sequenceIndex,
            long messageType,
            long timestampInNs,
            long position,
            OnMessageInfo messageInfo) {
        asciiBuffer.wrap(buffer, offset, length);
        if (messageType == HeartbeatDecoder.MESSAGE_TYPE) {
            heartbeatDecoder.decode(asciiBuffer, 0, length);
            if (heartbeatDecoder.hasTestReqID()) {
                String testReqId = heartbeatDecoder.testReqIDAsString();
                long value = Long.parseLong(testReqId);
                long delay = System.currentTimeMillis() - value;
                if (delay < 0) {
                    histogram.recordValue(delay);
                } else {
                    histogram.recordValue(0);
                }
            }
        }

        return ControlledFragmentHandler.Action.CONTINUE;
    }

    public void tryConnect() {

    }

    @Override
    public void onTimeout(int libraryId, Session session) {
        log.info("Session timed out");
    }

    @Override
    public void onSlowStatus(int libraryId, Session session, boolean hasBecomeSlow) {
        if (hasBecomeSlow) {
            log.info("Become slow session");
        } else {
            log.info("Session is no longer slow");
        }

    }

    @Override
    public ControlledFragmentHandler.Action onDisconnect(int libraryId, Session session, DisconnectReason disconnectReason) {
        return ControlledFragmentHandler.Action.CONTINUE;
    }

    @Override
    public void onSessionStart(Session session) {
        CompositeKey key = session.compositeKey();
        log.info("Session started {} {} -> {}", session.beginString(), key.localCompId(), key.remoteCompId());
    }
}
