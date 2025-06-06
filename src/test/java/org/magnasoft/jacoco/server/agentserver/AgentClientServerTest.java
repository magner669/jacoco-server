package org.magnasoft.jacoco.server.agentserver;

import org.jacoco.agent.rt.internal.output.TcpClientOutput;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.runtime.AgentOptions;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RuntimeData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.magnasoft.jacoco.server.sessions.SessionStateManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.synchronizedList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentClientServerTest {

    private static final String TEST_SESSION_ID = "test-session-id";
    private static final String TEST_CLASS_NAME = "test.Class";
    private static final long TEST_CLASS_ID = 1234;
    private final List<Exception> exceptions = synchronizedList(new ArrayList<>());
    private AgentServer agentServer;
    @Mock
    private AgentOptions agentOptions;
    private final RuntimeData runtimeData = new RuntimeData();
    private final TcpClientOutput tcpClientOutput = new TcpClientOutput(exceptions::add);
    @Mock
    private SessionStateManager sessionStateManager;
    private AgentWorkerLifecycleManager agentWorkerLifecycleManager;

    @BeforeEach
    void setUp() throws IOException {
        agentWorkerLifecycleManager = new AgentWorkerLifecycleManager(sessionStateManager);
        agentServer = new AgentServer(0 , agentWorkerLifecycleManager);
        final int port = agentServer.getPort();
        when(agentOptions.getPort()).thenReturn(port);
        when(agentOptions.getAddress()).thenReturn("localhost");
    }

    @AfterEach
    void tearDown() throws Exception {
        tcpClientOutput.shutdown();
        agentWorkerLifecycleManager.close();
        agentServer.close();
        assertTrue( exceptions.isEmpty(), "Exceptions occurred during test: " + exceptions);
    }

    @Test
    void connectDisconnect() throws Exception {
        tcpClientOutput.startup(agentOptions,runtimeData);
    }

    @Nested
    class SessionTest {
        private final List<SessionInfo> actualSessionInfos = Collections.synchronizedList(new ArrayList<>());
        private final List<ExecutionData> actualExecutionDatas = Collections.synchronizedList(new ArrayList<>());

        @BeforeEach
        void setUp() throws IOException {
            runtimeData.setSessionId(TEST_SESSION_ID);
            doAnswer(invocationOnMock -> {
                final RemoteControlReader remoteControlReader = invocationOnMock.getArgument(0);
                remoteControlReader.setExecutionDataVisitor(actualExecutionDatas::add);
                remoteControlReader.setSessionInfoVisitor(actualSessionInfos::add);
                return null;
            }).when(sessionStateManager).accept(any());
            tcpClientOutput.startup(agentOptions,runtimeData);
        }

        @Test
        void sendOneExecutionData() throws Exception {
            final var executionData = runtimeData.getExecutionData(TEST_CLASS_ID , TEST_CLASS_NAME, 1);
            executionData.getProbes()[0]=true;
            tcpClientOutput.writeExecutionData(false);
            verify(sessionStateManager).accept(any());
            assertEquals( 1 , actualSessionInfos.size());
            final var actualSession = actualSessionInfos.getFirst();
            assertEquals( TEST_SESSION_ID , actualSession.getId() );
            assertEquals( 1 , actualExecutionDatas.size());
            final var actualExecutionData = actualExecutionDatas.getFirst();
            assertEquals( TEST_CLASS_NAME , actualExecutionData.getName() );
            assertEquals( TEST_CLASS_ID , actualExecutionData.getId() );
            assertArrayEquals( new boolean[]{true} , actualExecutionData.getProbes() );
        }

    }


}
