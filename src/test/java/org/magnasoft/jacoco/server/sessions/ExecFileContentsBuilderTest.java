package org.magnasoft.jacoco.server.sessions;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.magnasoft.jacoco.server.sessions.EmptyExecFile.EMPTY_EXEC_FILE;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExecFileContentsBuilderTest {

  @Mock private Session session;

  @Test
  void buildEmptySession() throws IOException {
    final var actual = new ExecFileContentsBuilder(session).build();
    assertArrayEquals(
        EMPTY_EXEC_FILE, actual, "Expected empty byte array for uninitialized session");
  }
}
