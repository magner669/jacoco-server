package org.magnasoft.jacoco.server.agentserver;

import java.net.Socket;
import java.net.SocketAddress;

/**
 * Information about a JaCoCo agent connected to the server, to be used for logging and debugging.
 */
class AgentInfo {
    private final SocketAddress socketAddress;

    private AgentInfo(final SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    static AgentInfo of (final Socket socket) {
        final var socketAddress = socket.getRemoteSocketAddress();
        return new AgentInfo(socketAddress);
    }

    public String toString() {
        return socketAddress.toString();
    }
}
