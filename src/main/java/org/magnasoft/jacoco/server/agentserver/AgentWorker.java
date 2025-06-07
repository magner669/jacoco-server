package org.magnasoft.jacoco.server.agentserver;

import static java.lang.Thread.currentThread;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.jacoco.core.runtime.RemoteControlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a TCP connection from a JaCoCo agent.
 *
 * @see <A HREF="https://www.jacoco.org/jacoco/trunk/doc/agent.html">Java Agent</A>
 */
class AgentWorker implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(AgentWorker.class);
  private final RemoteControlReader remoteControlReader;
  private final AgentInfo agentInfo;

  AgentWorker(final RemoteControlReader remoteControlReader, final AgentInfo agentInfo) {
    this.remoteControlReader = remoteControlReader;
    this.agentInfo = agentInfo;
  }

  @Override
  public void run() {
    LOGGER.debug("Connected {}", agentInfo);
    while (true) {
      try {
        if (!remoteControlReader.read()) {
          LOGGER.debug("Agent closed connection: {}", agentInfo);
          break;
        }
      } catch (final IOException e) {
        if (currentThread().isInterrupted()) {
          // If the thread is interrupted,the exception is resulting from the interruption
          // and need not be handled any further.
          LOGGER.debug("Interrupted agent is still connected: {}", agentInfo, e);
          break;
        } else {
          throw new UncheckedIOException(e);
        }
      }
    }
  }
}
