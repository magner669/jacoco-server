package org.magnasoft.jacoco.server.sessions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jacoco.core.data.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionTest {
  public static final String TEST_SESSION_ID = "test-session-id";
  public static final String TEST_CLASS = "test-class";
  public static final long TEST_CLASS_ID = 1L;
  @Mock private SessionInfo sessionInfo;
  @Mock private ISessionInfoVisitor iSessionInfoVisitor;
  @Captor private ArgumentCaptor<SessionInfo> sessionInfoCaptor;
  private Session session;

  @BeforeEach
  void setUp() {
    when(sessionInfo.getId()).thenReturn(TEST_SESSION_ID);
    session = Session.of(sessionInfo);
  }

  @AfterEach
  void tearDown() {}

  @Test
  void acceptSessionInfoVisitor() {
    session.accept(iSessionInfoVisitor);
    verify(iSessionInfoVisitor).visitSessionInfo(sessionInfoCaptor.capture());
    assertEquals(TEST_SESSION_ID, sessionInfoCaptor.getValue().getId());
  }

  @Nested
  class ExecutionDataTests {
    @Mock private IExecutionDataVisitor iExecutionDataVisitor;
    @Mock private ExecutionData executionData;
    @Mock private ExecutionData otherExecutionData;

    @BeforeEach
    void setUp() {
      when(executionData.getName()).thenReturn(TEST_CLASS);
      when(executionData.getId()).thenReturn(TEST_CLASS_ID);
    }

    @Test
    void acceptExecutionDataVisitor() {
      session.accept(executionData);
      session.accept(iExecutionDataVisitor);
      verify(iExecutionDataVisitor).visitClassExecution(executionData);
    }

    @Test
    void merge() {
      when(otherExecutionData.getId()).thenReturn(TEST_CLASS_ID);
      session.accept(executionData);
      session.accept(otherExecutionData);
      verify(executionData).merge(otherExecutionData);
      session.accept(iExecutionDataVisitor);
      verify(iExecutionDataVisitor).visitClassExecution(executionData);
    }
  }
}
