package org.magnasoft.jacoco.server.agentserver;

import static java.lang.Thread.currentThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Accepts TCP connections from JaCoCo agents.
 *
 * <p>On construction, it starts a virtual thread that listens for incoming connections.
 *
 * <p>Each connection made would be delegated to the {@link AgentWorkerLifecycleManager} for
 * processing.
 */
@Component
class AgentServer implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(AgentServer.class);
  private static final int DEFAULT_PORT = 6300;
  private final ServerSocket serverSocket;
  private final AgentWorkerLifecycleManager agentWorkerLifecycleManager;
  private final Thread thread;

  AgentServer(final int port, final AgentWorkerLifecycleManager agentWorkerLifecycleManager)
      throws IOException {
    this.agentWorkerLifecycleManager = agentWorkerLifecycleManager;
    // start listening for agent connections on the specified port
    serverSocket = new ServerSocket(port);
    // start a virtual thread to accept connections
    thread = Thread.ofVirtual().start(this::acceptAgentConnections);
    LOGGER.info("Listening for connections on {}", this.serverSocket);
  }

  @Autowired
  AgentServer(final AgentWorkerLifecycleManager agentWorkerLifecycleManager) throws IOException {
    this(DEFAULT_PORT, agentWorkerLifecycleManager);
  }

  @Override
  public void close() throws InterruptedException {
    LOGGER.debug("Closing {}", serverSocket);
    // for a virtual thread, interrupting also closes the socket
    thread.interrupt();
    thread.join();
  }

  /**
   * @return the actual bound port. This is useful for unit tests when port is passed as 0 to the
   *     constructor.
   */
  int getActualPort() {
    return serverSocket.getLocalPort();
  }

  private void acceptAgentConnections() {
    while (true) {
      final Socket agentSocket;
      try {
        agentSocket = serverSocket.accept();
        // delegate the client socket to the lifecycle manager
        agentWorkerLifecycleManager.accept(agentSocket);
      } catch (final RuntimeException | IOException e) {
        if (currentThread().isInterrupted()) {
          LOGGER.debug("Interrupted. Socket closed: {}", serverSocket.isClosed());
          break;
        } else {
          LOGGER.warn("Exception while accepting socket", e);
        }
      }
    }
  }
}
