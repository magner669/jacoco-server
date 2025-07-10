package org.magnasoft.jacoco.server.sessions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.annotation.Nonnull;
import org.jacoco.core.data.ExecutionDataWriter;

public class ExecFileContentsBuilder {

  @Nonnull private final Session session;

  public ExecFileContentsBuilder(final @Nonnull Session session) {
    this.session = session;
  }

  public byte[] build() {
    try (final var outputStream = new ByteArrayOutputStream()) {
      final var executionDataWriter = new ExecutionDataWriter(outputStream);
      new SessionDump(executionDataWriter, session).run();
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new UncheckedIOException("Error writing session data", e);
    }
  }
}
