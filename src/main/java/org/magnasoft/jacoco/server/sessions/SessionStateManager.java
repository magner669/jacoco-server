package org.magnasoft.jacoco.server.sessions;

import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.runtime.RemoteControlReader;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Handles obtaining the execution data and session information from the JaCoCo agent.
 */
@Service
public class SessionStateManager implements Consumer<RemoteControlReader> {
    private final SessionRepository sessionRepository;

    SessionStateManager(final SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void accept(final RemoteControlReader remoteControlReader) {
        // first the session info is received , then the execution data.
        // if they are not received in this order, exceptions would be thrown by RemoteControlReader.
        remoteControlReader.setSessionInfoVisitor(sessionInfo -> {
            final var session = sessionRepository.getOrCreate(sessionInfo);
            final IExecutionDataVisitor executionDataVisitor  = session::accept;
            remoteControlReader.setExecutionDataVisitor(executionDataVisitor);
        });
    }
}
