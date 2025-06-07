package org.magnasoft.jacoco.server.agentserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.currentThread;

/**
 * Accepts TCP connections from JaCoCo agents.
 * <p>
 * On construction, it starts a virtual thread that listens for incoming connections.
 * <p>
 * Each connection made would be delegated to the {@link AgentWorkerLifecycleManager} for processing.
 */
@Component
class AgentServer implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentServer.class);
    private final ServerSocket serverSocket;
    private final AgentWorkerLifecycleManager agentWorkerLifecycleManager;
    private final Thread thread;

    AgentServer(
            final int port,
            final AgentWorkerLifecycleManager agentWorkerLifecycleManager) throws IOException {
        serverSocket = new ServerSocket(port);
        this.agentWorkerLifecycleManager = agentWorkerLifecycleManager;
        thread = Thread.ofVirtual().start(this::acceptAgentConnections);
        LOGGER.info("Listening for connections on {}", this.serverSocket);
    }

    @Autowired
    AgentServer(
            final AgentWorkerLifecycleManager agentWorkerLifecycleManager) throws IOException {
        this(6300, agentWorkerLifecycleManager);
    }

    @Override
    public void close() throws InterruptedException {
        LOGGER.debug("Closing {}", serverSocket);
        thread.interrupt();
        thread.join();
    }

    int getPort() {
        return serverSocket.getLocalPort();
    }

    private void acceptAgentConnections() {
        while (true) {
            final Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                agentWorkerLifecycleManager.accept(clientSocket);
            } catch (final RuntimeException | IOException e) {
                if (currentThread().isInterrupted()) {
                    LOGGER.debug("Interrupted");
                    break;
                } else {
                    LOGGER.error("Exception while accepting socket", e);
                }
            }

        }
    }

}
