package mockers;

import communications.Communicator;
import generated.Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientMocker implements Runnable{
    private Communicator communicator;
    private Socket socket;
    private Communication.C2S message;
    private Communication.S2C response;

    public ClientMocker(int serverPort, Communication.C2S message) throws Exception{
        this.socket = new Socket("localhost", serverPort);
        this.communicator = new Communicator(new DataInputStream(socket.getInputStream()),
                new DataOutputStream(socket.getOutputStream()));
        this.message = message;
    }

    public boolean login(int clientId, String password) throws IOException {
        communicator.sendMessage(Communication.C2S.newBuilder().setLogin(
                Communication.C2S.Login.newBuilder().setClientId(clientId).setPassword(password)).build());
        boolean connected = communicator.readMessage(Communication.S2C.parser()).getResponse().getStatusCode() == Communication.S2C.Response.Status.SUCCESSFUL;
        if (!connected) {
            communicator.sendMessage(Communication.C2S.newBuilder().setFinish(true).build());
        }
        return connected;
    }

    @Override
    public void run() {
        try {
            communicator.sendMessage(message);
            response = communicator.readMessage(Communication.S2C.parser());
            communicator.sendMessage(Communication.C2S.newBuilder().setFinish(true).build());
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Communication.S2C getResponse() { return response; }
}
