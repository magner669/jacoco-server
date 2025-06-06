package org.magnasoft.jacoco.server.agentserver;

import org.jacoco.core.runtime.RemoteControlReader;
import org.magnasoft.jacoco.server.sessions.SessionStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Manages the lifecycle of a JaCoCo agent TCP connections.
 * <p>
 * The {@link #accept(Socket)} method is called when new connection is made.
 * <p>
 * It delegates to {@link SessionStateManager} to deal with the JaCoCo execution data and session information.
 * It will build a {@link AgentWorker} to handle the connection and run it in a virtual thread.
 */
@Service
class AgentWorkerLifecycleManager implements Consumer<Socket>,AutoCloseable{
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentWorkerLifecycleManager.class);
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final SessionStateManager sessionStateManager;

    AgentWorkerLifecycleManager(final SessionStateManager sessionStateManager) {
        this.sessionStateManager = sessionStateManager;
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }

    @Override
    public void accept(final Socket clientSocket) {
        try {
            final var inputStream = clientSocket.getInputStream();
            final var remoteControlReader = new RemoteControlReader(inputStream);
            sessionStateManager.accept(remoteControlReader);
            final var agentInfo = clientSocket.getRemoteSocketAddress();
            final var worker = new AgentWorker(remoteControlReader , agentInfo);
            final var executionCompleteFuture = runAsync(worker, executorService);
            logException(executionCompleteFuture,clientSocket);
            closeSocket(executionCompleteFuture, clientSocket);
        } catch (final IOException| RuntimeException e) {
            LOGGER.warn("Exception setting up agent connection" , e);
        }
    }

    void logException(final CompletionStage<Void> future,final Socket clientSocket) {
        future.exceptionally(throwable -> {
            LOGGER.warn("Exception: {}", clientSocket, throwable);
            return null;
        });
    }

    void closeSocket(final CompletionStage<Void> future, final Socket clientSocket) {
        future.whenComplete((ignored, ignored2) -> {
            try {
                LOGGER.trace("Closing: {}", clientSocket);
                clientSocket.close();
            } catch (final IOException e) {
                LOGGER.warn("Exception while closing: {}", clientSocket, e);
            }
        });

    }
}
