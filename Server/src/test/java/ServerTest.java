import com.mysql.fabric.xmlrpc.Client;
import communications.Communication;
import mockers.AlgorithmsMocker;
import mockers.ClientMocker;
import mockers.DataBaseMocker;
import mockers.TestBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ServerTest {
    private Thread serverThread;
    private Server server;

    @Before
    public void setUp() throws Exception {
        serverThread = new Thread(() -> {
            try {
                server = new Server(Main.listeningPort, Main.numThreads,
                        AlgorithmsMocker.class.getConstructor(), DataBaseMocker.class.getConstructor());
                server.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        serverThread.join();
    }

    @Test
    public void testLoginFalied() throws Exception {
        ClientMocker clientMocker = new ClientMocker(Main.listeningPort, null);
        Assert.assertFalse(clientMocker.login(5, "5"));
    }

    @Test
    public void testRequest() throws Exception{
        ClientMocker clientMocker = new ClientMocker(Main.listeningPort,
                Communication.C2S.newBuilder().setRequest(
                        TestBase.getValidRequest(TestBase.doctors.get(0),
                                Communication.C2S.Request.Type.NOW)).build());
        if (!clientMocker.login(2, "2"))
            Assert.fail();
        runClient(clientMocker);
        TestBase.assertSuccessfulResponse(clientMocker.getResponse());
    }

    @Test
    public void testMultipleClients() throws Exception {
        List<ClientMocker> clients = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i=0; i<3; i++) {
            ClientMocker clientMocker = new ClientMocker(Main.listeningPort,
                    Communication.C2S.newBuilder().setRequest(
                            TestBase.getValidRequest(TestBase.doctors.get(0),
                                    Communication.C2S.Request.Type.NOW)).build());
            if (!clientMocker.login(i + 2, Integer.toString(i + 2)))
                Assert.fail();
            Thread t = new Thread(clientMocker);
            clients.add(clientMocker);
            threads.add(t);
            t.start();
        }
        threads.forEach(t -> {
            try {
                t.join();
            } catch (Exception e) {
                throw new RuntimeException("join failed");
            }
        });
        clients.forEach(client -> TestBase.assertSuccessfulResponse(client.getResponse()));
    }

    private void runClient(ClientMocker clientMocker) throws InterruptedException{
        Thread t = new Thread(clientMocker);
        t.start();
        t.join();
    }
}