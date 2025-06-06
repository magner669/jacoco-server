package org.magnasoft.jacoco.server.agentserver;

import org.jacoco.core.runtime.RemoteControlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

import static java.lang.Thread.currentThread;

/**
 * Handles a TCP connection from a JaCoCo agent.
 * @see <A HREF="https://www.jacoco.org/jacoco/trunk/doc/agent.html">Java Agent</A>
 */
class AgentWorker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentWorker.class);
    private final RemoteControlReader remoteControlReader;
    private final Object info;

    AgentWorker(final RemoteControlReader remoteControlReader, final Object agentInfo) {
        this.remoteControlReader = remoteControlReader;
        this.info = agentInfo;
    }

    @Override
    public void run() {
        LOGGER.debug("Connected {}", info);
        while (true) {
            try {
                if (!remoteControlReader.read()) {
                    LOGGER.debug("Closed: {}", info);
                    break;
                }
            } catch (final IOException e) {
                if (currentThread().isInterrupted()) {
                    LOGGER.info("Interrupted: {}", info);
                    break;
                }
                throw new UncheckedIOException(e);
            }
        }
    }
}
