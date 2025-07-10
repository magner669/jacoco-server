package org.magnasoft.jacoco.server.api;

import javax.annotation.Nonnull;

class ContentDispositionHeaderBuilder {
  private final @Nonnull String sessionId;

  ContentDispositionHeaderBuilder(final @Nonnull String sessionId) {
    this.sessionId = sessionId;
  }

  String build() {
    return "attachment; filename=\"%s.exec\"".formatted(sessionId);
  }
}
