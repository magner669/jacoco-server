package org.magnasoft.jacoco.server.sessions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jacoco.core.data.ExecutionDataWriter;

class EmptyExecFile {

  static final byte[] EMPTY_EXEC_FILE;

  static {
    try (final var outputStream = new ByteArrayOutputStream()) {
      new ExecutionDataWriter(outputStream);
      EMPTY_EXEC_FILE = outputStream.toByteArray();
    } catch (final IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
