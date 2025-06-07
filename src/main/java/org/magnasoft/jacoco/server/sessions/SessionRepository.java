package org.magnasoft.jacoco.server.sessions;

import org.jacoco.core.data.SessionInfo;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Builds and holds {@link Session} instances.
 */
@Repository
class SessionRepository {
    private final ConcurrentMap<String, Session> sessionDataStore = new ConcurrentHashMap<>();

    Session getOrCreate(final SessionInfo sessionInfo) {
        final var sessionId = sessionInfo.getId();
        return sessionDataStore.computeIfAbsent(sessionId,ignored -> Session.of(sessionInfo));
    }
}
