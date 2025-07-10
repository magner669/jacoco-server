package org.magnasoft.jacoco.server.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.magnasoft.jacoco.server.sessions.Session;
import org.magnasoft.jacoco.server.sessions.SessionRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionDumpControllerTest {

  private static final String TEST_SESSION_ID = "test-session-id";

  @Mock private SessionRepository sessionRepository;
  @Mock private Session session;

  private SessionDumpController controller;

  @BeforeEach
  void setUp() {
    controller = new SessionDumpController(sessionRepository);
  }

  @Test
  void getBinaryExisting() {
    when(sessionRepository.get(TEST_SESSION_ID)).thenReturn(Optional.of(session));
    final var response = controller.getBinary(TEST_SESSION_ID);
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    assertEquals(APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
    assertThat(response.getHeaders(), Matchers.hasKey(CONTENT_DISPOSITION));
    assertEquals(
        "attachment; filename=\"test-session-id.exec\"",
        response.getHeaders().getFirst(CONTENT_DISPOSITION));
  }

  @Test
  void getBinaryEmpty() {
    final var response = controller.getBinary(TEST_SESSION_ID);
    assertNotNull(response);
    assertEquals(404, response.getStatusCode().value());
  }
}
