package org.magnasoft.jacoco.server.sessions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jacoco.core.data.ExecutionDataWriter;

class EmptyExecFile {

  static final byte[] INSTANCE;

  static {
    try (final var outputStream = new ByteArrayOutputStream()) {
      new ExecutionDataWriter(outputStream);
      INSTANCE = outputStream.toByteArray();
    } catch (final IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
