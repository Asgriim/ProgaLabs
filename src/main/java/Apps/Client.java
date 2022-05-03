package Apps;
import API.ServerRequest;
import API.ServerResponse;
import readers.CityReader;
import utility.FileManager;
import utility.SerializationHelper;
import utility.client.ClientCommandManager;
import utility.client.ClientValidator;
import utility.client.WaitResponse;
import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Client {

    public void run(String serverName,int port) throws IOException, JAXBException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int tryCount = 0;
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        SocketAddress server = new InetSocketAddress(InetAddress.getByName(serverName),port);
        ServerRequest request = new ServerRequest();
        request.setArgument(new String[0]);
        request.setCommand("api command");
        SerializationHelper serializator = new SerializationHelper();
        ByteBuffer bufferToServer = ByteBuffer.allocate(2048);
        bufferToServer.put(serializator.serialize(request));
        bufferToServer.flip();
        WaitResponse waitResponse = new WaitResponse();
        tryCount++;
        ByteBuffer fromServer = ByteBuffer.allocate(2048);
        writer.write("connecting to server ...\n");
        writer.flush();
        channel.send(bufferToServer,server);
        bufferToServer.flip();
        while (!waitResponse.waitReceive(channel,fromServer,5)){
            writer.write("connecting to server ...\n");
            writer.flush();
            channel.send(bufferToServer,server);
            bufferToServer.flip();
            tryCount++;
            if(tryCount > 2){
                writer.write("server doesn't response\ntry later\n");
                writer.flush();
                System.exit(0);
            }
        }

        ServerResponse response = null;
        try {
            response = (ServerResponse) serializator.deSerialization(fromServer);
        } catch (ClassNotFoundException e) {
            writer.write("data from server was corrupted\ntry later");
            writer.flush();
            System.exit(1);
        }
        writer.write("app is ready\n");
        writer.flush();
        FileManager fileManager = new FileManager(null);
        ClientCommandManager commandManager = new ClientCommandManager(fileManager,writer,new CityReader(reader,new OutputStreamWriter(System.out)),
                channel,server);
        commandManager.setCommandClassMap(response.getCommandMap());
        commandManager.setValidator(new ClientValidator(response.getCommandMap(),fileManager));
        while (true){
            writer.write("enter command: ");
            writer.flush();
            commandManager.handle(reader.readLine());
        }
    }
}
