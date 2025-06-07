package org.magnasoft.jacoco.server.sessions;

import java.util.function.Consumer;
import org.jacoco.core.data.*;

/**
 * A session that holds execution data collected by multiple JaCoCo agents having the same session
 * ID. Multiple agents can report execution data concurrently, and this class is thread-safe.
 */
class Session implements Consumer<ExecutionData> {
  private final ExecutionDataStore executionDataStore = new ExecutionDataStore();

  /**
   * @see SessionInfo#getId()
   */
  private final String id;

  private Session(final String id) {
    this.id = id;
  }

  static Session of(final SessionInfo sessionInfo) {
    final var id = sessionInfo.getId();
    return new Session(id);
  }

  @Override
  public synchronized void accept(final ExecutionData executionData) {
    // internally merges the execution data.
    executionDataStore.put(executionData);
  }

  void accept(final ISessionInfoVisitor iSessionInfoVisitor) {
    final var sessionInfo = new SessionInfo(id, 0, 0);
    iSessionInfoVisitor.visitSessionInfo(sessionInfo);
  }

  synchronized void accept(final IExecutionDataVisitor iExecutionDataVisitor) {
    executionDataStore.accept(iExecutionDataVisitor);
  }
}
