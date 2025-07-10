package org.magnasoft.jacoco.server.sessions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.magnasoft.jacoco.server.sessions.SessionTestData.TEST_SESSION_ID;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.jacoco.core.data.SessionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionRepositoryTest {

  @Mock private SessionInfo sessionInfo;
  private final SessionRepository sessionRepository = new SessionRepository();

  @Test
  void getExisting() {
    when(sessionInfo.getId()).thenReturn(TEST_SESSION_ID);
    final var expected = sessionRepository.getOrCreate(sessionInfo);
    assertEquals(Optional.of(expected), sessionRepository.get(TEST_SESSION_ID));
  }

  @Test
  void getNonExisting() {
    assertEquals(Optional.empty(), sessionRepository.get(TEST_SESSION_ID));
  }
}
