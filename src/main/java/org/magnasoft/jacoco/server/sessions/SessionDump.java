package org.magnasoft.jacoco.server.sessions;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;

class SessionDump implements Runnable {
  private final ExecutionDataWriter executionDataWriter;
  private final Session session;

  SessionDump(final ExecutionDataWriter executionDataWriter, final Session session) {
    this.executionDataWriter = executionDataWriter;
    this.session = session;
  }

  @Override
  public void run() {
    session.accept((ISessionInfoVisitor) executionDataWriter);
    session.accept((IExecutionDataVisitor) executionDataWriter);
  }
}
