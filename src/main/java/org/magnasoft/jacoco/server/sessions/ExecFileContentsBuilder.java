package org.magnasoft.jacoco.server.sessions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.jacoco.core.data.ExecutionDataWriter;

public class ExecFileContentsBuilder {

  @Nonnull private final Session session;

  public ExecFileContentsBuilder(final @Nonnull Session session) {
    this.session = session;
  }

  public byte[] build() throws IOException {
    try (final var outputStream = new ByteArrayOutputStream()) {
      final var executionDataWriter = new ExecutionDataWriter(outputStream);
      new SessionDump(executionDataWriter, session).run();
      return outputStream.toByteArray();
    }
  }
}
