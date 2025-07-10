package org.magnasoft.jacoco.server.sessions;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExecFileContentsBuilderTest {

  @Mock private Session session;

  @Test
  void buildEmptySession() {
    final var actual = new ExecFileContentsBuilder(session).build();
    assertArrayEquals(
        EmptyExecFile.INSTANCE, actual, "Expected empty byte array for uninitialized session");
  }
}
