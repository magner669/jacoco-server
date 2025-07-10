package org.magnasoft.jacoco.server.agentserver;

import static java.util.Collections.synchronizedList;
import static org.junit.jupiter.api.Assertions.*;
import static org.magnasoft.jacoco.server.sessions.SessionTestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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

@ExtendWith(MockitoExtension.class)
class AgentClientServerTest {

  @Mock private Consumer<Exception> exceptionConsumer;
  @Mock private SessionStateManager sessionStateManager;
  @Mock private AgentOptions agentOptions;
  private AgentServer agentServer;
  private final RuntimeData runtimeData = new RuntimeData();
  private TcpClientOutput tcpClientOutput;
  private AgentWorkerLifecycleManager agentWorkerLifecycleManager;

  @BeforeEach
  void setUp() throws IOException {
    tcpClientOutput = new TcpClientOutput(exceptionConsumer::accept);
    agentWorkerLifecycleManager = new AgentWorkerLifecycleManager(sessionStateManager);
    agentServer = new AgentServer(0, agentWorkerLifecycleManager);
    final int port = agentServer.getActualPort();
    when(agentOptions.getPort()).thenReturn(port);
    when(agentOptions.getAddress()).thenReturn("localhost");
  }

  @AfterEach
  void tearDown() throws IOException, InterruptedException {
    tcpClientOutput.shutdown();
    agentServer.close();
    agentWorkerLifecycleManager.close();
    verifyNoInteractions(exceptionConsumer);
  }

  @Test
  void connectDisconnect() throws IOException {
    tcpClientOutput.startup(agentOptions, runtimeData);
  }

  @Nested
  class SessionTest {
    private final List<SessionInfo> actualSessionInfos = synchronizedList(new ArrayList<>());
    private final List<ExecutionData> actualExecutionDatas = synchronizedList(new ArrayList<>());

    @BeforeEach
    void setUp() throws IOException {
      runtimeData.setSessionId(TEST_SESSION_ID);
      doAnswer(
              invocationOnMock -> {
                final RemoteControlReader remoteControlReader = invocationOnMock.getArgument(0);
                remoteControlReader.setExecutionDataVisitor(actualExecutionDatas::add);
                remoteControlReader.setSessionInfoVisitor(actualSessionInfos::add);
                return null;
              })
          .when(sessionStateManager)
          .accept(any());
      tcpClientOutput.startup(agentOptions, runtimeData);
      // create some execution data, and flip one of the probes to true
      runtimeData.getExecutionData(TEST_CLASS_ID, TEST_CLASS, 1).getProbes()[0] = true;
    }

    @Test
    void sendOneExecutionData() throws IOException {
      tcpClientOutput.writeExecutionData(false);
      verify(sessionStateManager).accept(any());
      final var actualSession = actualSessionInfos.getFirst();
      assertEquals(1, actualSessionInfos.size());
      assertEquals(TEST_SESSION_ID, actualSession.getId());
      assertEquals(1, actualExecutionDatas.size());
      final var actualExecutionData = actualExecutionDatas.getFirst();
      assertEquals(TEST_CLASS, actualExecutionData.getName());
      assertEquals(TEST_CLASS_ID, actualExecutionData.getId());
      assertArrayEquals(new boolean[] {true}, actualExecutionData.getProbes());
    }

    @Test
    void twoClients() throws Exception {
      final var otherTcpClientOutput = new TcpClientOutput(exceptionConsumer::accept);
      otherTcpClientOutput.startup(agentOptions, runtimeData);
      try {
        tcpClientOutput.writeExecutionData(false);
        otherTcpClientOutput.writeExecutionData(false);
        // for some reason this test is flaky,so sleep a little
        Thread.sleep(500);
        verify(sessionStateManager, times(2)).accept(any());
        assertEquals(2, actualSessionInfos.size());
        assertEquals(2, actualExecutionDatas.size());
      } finally {
        otherTcpClientOutput.shutdown();
      }
    }
  }
}
