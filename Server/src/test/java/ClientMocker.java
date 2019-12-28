import communications.Communication;
import communications.Communicator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientMocker implements Runnable{
    private Communicator communicator;
    private Socket socket;
    private Communication.C2S message;
    private Communication.S2C response;

    public ClientMocker(String hostName, int serverPort, Communication.C2S message) throws Exception{
        this.socket = new Socket(hostName, serverPort);
        this.communicator = new Communicator(new DataInputStream(socket.getInputStream()),
                new DataOutputStream(socket.getOutputStream()));
        this.message = message;
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
