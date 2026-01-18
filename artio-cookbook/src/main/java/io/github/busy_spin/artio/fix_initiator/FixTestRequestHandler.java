package io.github.busy_spin.artio.fix_initiator;

import io.aeron.logbuffer.ControlledFragmentHandler;
import lombok.extern.slf4j.Slf4j;
import org.HdrHistogram.Histogram;
import org.agrona.DirectBuffer;
import uk.co.real_logic.artio.Reply;
import uk.co.real_logic.artio.builder.TestRequestEncoder;
import uk.co.real_logic.artio.decoder.HeartbeatDecoder;
import uk.co.real_logic.artio.library.FixLibrary;
import uk.co.real_logic.artio.library.LibraryConnectHandler;
import uk.co.real_logic.artio.library.OnMessageInfo;
import uk.co.real_logic.artio.library.SessionAcquireHandler;
import uk.co.real_logic.artio.library.SessionAcquiredInfo;
import uk.co.real_logic.artio.library.SessionConfiguration;
import uk.co.real_logic.artio.library.SessionHandler;
import uk.co.real_logic.artio.messages.DisconnectReason;
import uk.co.real_logic.artio.session.CompositeKey;
import uk.co.real_logic.artio.session.Session;
import uk.co.real_logic.artio.util.MutableAsciiBuffer;

import static uk.co.real_logic.artio.Reply.State.ERRORED;

@Slf4j
public class FixTestRequestHandler implements SessionHandler, LibraryConnectHandler, SessionAcquireHandler {

    private final MutableAsciiBuffer asciiBuffer = new MutableAsciiBuffer();
    private final HeartbeatDecoder heartbeatDecoder = new HeartbeatDecoder();

    private final TestRequestEncoder testRequestEncoder = new TestRequestEncoder();

    private final Histogram histogram = new Histogram(3);

    private FixLibrary fixLibrary;

    private Reply<Session> reply = errorReply();

    private Session session = null;

    @Override
    public void onConnect(FixLibrary fixLibrary) {
        this.fixLibrary = fixLibrary;
        log.info("Connected to fix engine");
    }

    @Override
    public void onDisconnect(FixLibrary fixLibrary) {
        this.session = null;
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
                if (delay > 0) {
                    histogram.recordValue(delay);
                } else {
                    histogram.recordValue(0);
                }
            }
        }

        return ControlledFragmentHandler.Action.CONTINUE;
    }

    public void tryConnect() {
        if (session != null) {
            return;
        }
        if (fixLibrary != null && fixLibrary.isConnected()) {
            if (reply != null && reply.hasErrored()) {
                SessionConfiguration sessionConfig = SessionConfiguration.builder()
                        .senderCompId(System.getProperty("artio.sender.comp.id"))
                        .targetCompId(System.getProperty("artio.target.comp.id"))
                        .address(System.getProperty("artio.host"), Integer.getInteger("artio.port"))
                        .build();
                reply = fixLibrary.initiate(sessionConfig);
            } else if (reply != null &&  reply.hasCompleted()) {
                reply = null;
            }
        }
    }

    public void printAndResetCounters() {
        log.info("p99.9 = {} | p99.99 = {} | p100 = {} | counter {}",
                histogram.getValueAtPercentile(99.9),
                histogram.getValueAtPercentile(99.99),
                histogram.getValueAtPercentile(100),
                histogram.getTotalCount());
        histogram.reset();
    }

    public void sendTestReq() {
        if (session != null && session.isConnected()) {
            testRequestEncoder.testReqID(String.valueOf(System.currentTimeMillis()).toCharArray());
            session.trySend(testRequestEncoder);
        }
    }

    @Override
    public void onTimeout(int libraryId, Session session) {
        log.info("Session timed out");
        reply = errorReply();
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
    public ControlledFragmentHandler.Action onDisconnect(int libraryId,
                                                         Session session,
                                                         DisconnectReason disconnectReason) {
        log.info("Session disconnected");
        reply = errorReply();
        this.session = null;
        return ControlledFragmentHandler.Action.CONTINUE;
    }

    @Override
    public void onSessionStart(Session session) {
        this.session = session;
        CompositeKey key = session.compositeKey();
        log.info("Session started {} {} -> {}", session.beginString(), key.localCompId(), key.remoteCompId());
    }

    @Override
    public SessionHandler onSessionAcquired(Session session, SessionAcquiredInfo sessionAcquiredInfo) {
        this.session = session;
        log.info("Session acquired {}", sessionAcquiredInfo.metaDataStatus());
        return this;
    }

    private static Reply<Session> errorReply() {
        return new Reply<>() {
            @Override
            public Throwable error() {
                return null;
            }

            @Override
            public Session resultIfPresent() {
                return null;
            }

            @Override
            public State state() {
                return ERRORED;
            }
        };
    }
}
