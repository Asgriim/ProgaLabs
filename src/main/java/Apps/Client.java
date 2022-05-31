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
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        ByteBuffer bufferToServer = ByteBuffer.allocate(100000);
        String input = "";
        ByteBuffer fromServer = ByteBuffer.allocate(1000000);
        boolean auth = false;
        ServerResponse response = null;
        while (!auth) {
            writer.write("type 1 to sign in or 0 to register:\n");
            writer.flush();
            input = reader.readLine();
            if (input.equals("1") || input.equals("0")){
                writer.write("enter login: ");
                writer.flush();
                request.setLogIn(reader.readLine());
                if (input.equals("1")){
                    writer.write("enter password: ");
                    writer.flush();
                    request.setPassword(getHash(reader.readLine()));
                    request.setCommand("sign");
                }
                else {
                    while (true) {
                        writer.write("enter password: ");
                        writer.flush();
                        input = reader.readLine();
                        writer.write("repeat password: ");
                        writer.flush();
                        if (input.equals(reader.readLine())) {
                            break;
                        }
                        writer.write("passwords are not the same, try one more time\n");
                        writer.flush();
                    }
                    request.setCommand("register");
                    request.setPassword(getHash(input));
                }
            }
            else continue;
            bufferToServer.put(serializator.serialize(request));
            bufferToServer.flip();
            WaitResponse waitResponse = new WaitResponse();
            tryCount++;


            writer.write("connecting to server ...\n");
            writer.flush();
            channel.send(bufferToServer, server);
            bufferToServer.flip();
            while (!waitResponse.waitReceive(channel, fromServer, 5)) {
                writer.write("connecting to server ...\n");
                writer.flush();
                channel.send(bufferToServer, server);
                bufferToServer.flip();
                tryCount++;
                if (tryCount > 2) {
                    writer.write("server doesn't response\ntry later\n");
                    writer.flush();
                    System.exit(0);
                }
            }

            try {
                response = (ServerResponse) serializator.deSerialization(fromServer);
                fromServer.clear();
            } catch (ClassNotFoundException e) {
                writer.write("data from server was corrupted\ntry later");
                writer.flush();
                System.exit(1);
            }
            if (response.getOwnerId() != 0) auth = true;
            else {
                writer.write(response.getResponse()+"\n");
                writer.flush();
            }
        }
        writer.write(response.getResponse() + "\n");
        writer.flush();
        writer.write("app is ready\n");
        writer.flush();
        FileManager fileManager = new FileManager(null);
        ClientCommandManager commandManager = new ClientCommandManager(fileManager,writer,new CityReader(reader,new OutputStreamWriter(System.out)),
                channel,server);
        commandManager.setOwnerId(response.getOwnerId());
        commandManager.setCommandClassMap(response.getCommandMap());
        commandManager.setValidator(new ClientValidator(response.getCommandMap(),fileManager));
        while (true){
            writer.write("enter command: ");
            writer.flush();
            commandManager.handle(reader.readLine());
        }
    }

    private String getHash(String s){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");
            byte[] bytes = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInteger = new BigInteger(1,bytes);
            String hash = bigInteger.toString(16);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
