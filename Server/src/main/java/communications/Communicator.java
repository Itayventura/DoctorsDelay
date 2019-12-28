package communications;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * knows how to read and write a proto like message into a stream.
 */
public class Communicator {
    private DataOutputStream out;
    private DataInputStream in;

    public Communicator(DataInputStream in, DataOutputStream out) throws IOException {
        this.out = out;
        this.in = in;
    }

    public void closeStreams() throws IOException{
        out.close();
        in.close();
    }

    protected <T extends MessageLite> T readMessage(Parser<T> parser) throws IOException {
        int c2sSize = in.readInt();
        byte[] buffer = new byte[c2sSize];
        in.readFully(buffer);
        return parser.parseFrom(buffer);
    }

    protected void sendMessage(MessageLite message) throws IOException {
        out.writeInt(message.getSerializedSize());
        message.writeTo(out);
        out.flush();
    }
}
