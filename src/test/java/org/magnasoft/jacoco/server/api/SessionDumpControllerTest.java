package org.magnasoft.jacoco.server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.magnasoft.jacoco.server.sessions.EmptyExecFile.EMPTY_EXEC_FILE;
import static org.magnasoft.jacoco.server.sessions.SessionTestData.TEST_SESSION_ID;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.magnasoft.jacoco.server.sessions.Session;
import org.magnasoft.jacoco.server.sessions.SessionRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.CacheControl;

@ExtendWith(MockitoExtension.class)
class SessionDumpControllerTest {

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
    assertSame(OK, response.getStatusCode());
    assertEquals(APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
    assertEquals(CacheControl.noCache().getHeaderValue(), response.getHeaders().getCacheControl());
    assertEquals(5, response.getHeaders().getContentLength());
    assertEquals(
        "attachment; filename=\"%s.exec\"".formatted(TEST_SESSION_ID),
        response.getHeaders().getFirst(CONTENT_DISPOSITION));
    assertArrayEquals(EMPTY_EXEC_FILE, response.getBody());
  }

  @Test
  void getBinaryNonExisting() {
    final var response = controller.getBinary(TEST_SESSION_ID);
    assertSame(NOT_FOUND, response.getStatusCode());
    assertEquals(CacheControl.noCache().getHeaderValue(), response.getHeaders().getCacheControl());
  }
}
