import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ServerTest {
    Thread serverThread;

    @Before
    public void setUp() throws Exception {
        serverThread = new Thread(() -> {
            Server server = new Server(Main.listeningPort, Main.numThreads, new AlgorithmsMocker());
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
    }

    @After
    public void tearDown() throws Exception {
        serverThread.interrupt();
    }

    @Test
    public void testRequest() throws Exception{
        ClientMocker clientMocker = new ClientMocker("localhost", Main.listeningPort,
                Communication.C2S.newBuilder().setRequest(
                        TestBase.getValidRequest(TestBase.doctors.get(0),
                                Communication.C2S.Request.Type.NOW)).build());
        runClient(clientMocker);
        TestBase.assertSuccessfulResponse(clientMocker.getResponse());
    }

    private void runClient(ClientMocker clientMocker) throws InterruptedException{
        Thread t = new Thread(clientMocker);
        t.start();
        t.join();
    }
}