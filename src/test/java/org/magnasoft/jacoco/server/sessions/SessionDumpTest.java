package org.magnasoft.jacoco.server.sessions;

import static org.mockito.Mockito.verify;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionDumpTest {
  @Mock private ExecutionDataWriter executionDataWriter;
  @Mock private Session session;

  private SessionDump sessionDump;

  @BeforeEach
  void setUp() {
    sessionDump = new SessionDump(executionDataWriter, session);
  }

  @Test
  void run() {
    sessionDump.run();
    verify(session).accept((ISessionInfoVisitor) executionDataWriter);
    verify(session).accept((IExecutionDataVisitor) executionDataWriter);
  }
}
