import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;


import static org.junit.Assert.*;

public class CommunicatorTest {
    private Communicator communicator;
    private Communication.C2S client2server;
    private Communication.S2C server2client;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        client2server = Communication.C2S.newBuilder()
                .setReport(Communication.C2S.Report.newBuilder()
                    .setDoctorsName("Doctor")
                    .setCurrentDelayMinutes(20)
                ).setRequest(Communication.C2S.Request.newBuilder()
                    .setType(Communication.C2S.Request.Type.NOW)
                    .setDoctorsName("Doctor")
                    .setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                ).setFinish(false)
                .build();
        server2client = Communication.S2C.newBuilder()
                .setResponse(Communication.S2C.Response.newBuilder()
                    .setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL)
                    .setExpectedDelay(Communication.S2C.Response.ExpectedDelay.newBuilder().setTime(10))
                ).setFinish(false)
                .build();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(b);
        dataOutputStream.writeInt(client2server.getSerializedSize());
        client2server.writeTo(dataOutputStream);
        communicator = new Communicator(new DataInputStream(new ByteArrayInputStream(b.toByteArray()))
                , new DataOutputStream(byteArrayOutputStream));
    }

    @After
    public void tearDown() throws Exception {
        communicator.closeStreams();
    }

    @Test
    public void testReadMessage() throws Exception {
        Assert.assertEquals(client2server, communicator.readMessage(Communication.C2S.parser()));
    }

    @Test
    public void testSendMessage() throws Exception {
        communicator.sendMessage(server2client);
        byte[] sentBytes = byteArrayOutputStream.toByteArray();
        byte[] sentMessage = Arrays.copyOfRange(sentBytes, Integer.BYTES, sentBytes.length);
        Assert.assertEquals(server2client, Communication.S2C.parseFrom(sentMessage));
    }
}