package utility.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class WaitResponse {
    public boolean waitReceive(DatagramChannel channel,ByteBuffer buffer, long time) throws IOException {
        SocketAddress address = null;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < time * 1000){
            address = channel.receive(buffer);
            if (address != null) return true;
        }
        return false;
    }

}
