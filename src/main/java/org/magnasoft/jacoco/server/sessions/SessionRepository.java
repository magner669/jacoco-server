package org.magnasoft.jacoco.server.sessions;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jacoco.core.data.SessionInfo;
import org.springframework.stereotype.Repository;

/** Builds and holds {@link Session} instances. */
@Repository
public class SessionRepository {
  private final ConcurrentMap<String, Session> sessionDataStore = new ConcurrentHashMap<>();

  Session getOrCreate(final SessionInfo sessionInfo) {
    final var sessionId = sessionInfo.getId();
    return sessionDataStore.computeIfAbsent(sessionId, ignored -> Session.of(sessionInfo));
  }

  public Optional<Session> get(final String sessionId) {
    final var session = sessionDataStore.get(sessionId);
    return Optional.ofNullable(session);
  }
}
