package utility.server;

import API.ServerRequest;
import API.ServerResponse;
import exсeptions.NoValidArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.CommandManager;
import utility.FileManager;
import utility.SerializationHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerCommandManager extends CommandManager {
    private ServerResponse response;
    private DatagramChannel channel;
    private SocketAddress clientAddress;
    private Map<String,String> commandClassMap;
    private SerializationHelper serializer;
    private ByteBuffer buffer;
    private ServerRequest request;
    private Logger logger;
    public ServerCommandManager(FileManager fileManager, BufferedWriter writer,DatagramChannel channel) {
        super(fileManager, writer, null);
        this.channel = channel;
        this.serializer = new SerializationHelper();
        this.buffer = ByteBuffer.allocate(52428800);
        this.response = new ServerResponse();
        this.logger = LogManager.getLogger();
    }

    @Override
    public void handle(String command) throws IOException {
        setCommandName(command);
        if (getCommandName().equals("api command")){
            response.setCommandMap(commandClassMap);
            sendResponse();
            return;
        }
        setCurrentCommand(getCommandMap().get(getCommandName()));
        try {
            if(getCurrentCommand().getClass().getDeclaredMethod("execute", String[].class).getReturnType().getSimpleName().equals("Boolean")){
                if (getCurrentCommand().getClass().getSimpleName().equals("Update")) {
                    try {
                        getCurrentCommand().validateArgument(request.getId());
                    } catch (NoValidArgumentException e) {
                        response.setResponse(e.getMessage());
                        sendResponse();
                        return;
                    }
                }

                if ((boolean) getCurrentCommand().execute(getArgument()))
                    response.setResponse(getCurrentCommand().getName() + " executed successfully\n");
                else response.setResponse(getCurrentCommand().getName() + " not executed \n");
                sendResponse();
                return;
            }
            response.setResponse((String) getCurrentCommand().execute(getArgument()));
            sendResponse();
            return;
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
            response.setResponse("unexpected error on server");
            sendResponse();
            logger.error("unexpected error");
            return;
        }


    }
    private void sendResponse() throws IOException {
        buffer.put(serializer.serialize(response));
        logger.info("created response: " + response);
        buffer.flip();
        channel.send(buffer,clientAddress);
        logger.info("response sent to " + clientAddress);
        buffer.clear();
        resetResponse();
    }

    private void resetResponse(){
        response.setResponse("");
        response.setCommandMap(null);
    }

    public void setRequest(ServerRequest request) {
        this.request = request;
    }

    public void setClientAddress(SocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void createClassMap(){
        commandClassMap = getCommandMap().values().stream().collect(Collectors.toMap(x -> x.getName(),x -> x.getClass().getSimpleName()));
    }
}
