package utility.client;

import API.ServerRequest;
import API.ServerResponse;
import exсeptions.NoValidArgumentException;
import exсeptions.ReaderInterruptionException;
import exсeptions.ScriptInputIssueException;
import readers.CityReader;
import readers.ScriptCityReader;
import utility.CommandManager;
import utility.FileManager;
import utility.SerializationHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;

public class ClientCommandManager extends CommandManager {
    private int tryCount = 0;
    private DatagramChannel channel;
    private SocketAddress server;
    private ServerResponse response;
    private ServerRequest request;
    private SerializationHelper serializer;
    private WaitResponse waitResponce;
    private ByteBuffer buffer;
    private ClientValidator validator;
    private Map<String,String> commandClassMap;
    private Integer ownerId;
    public ClientCommandManager(FileManager fileManager, BufferedWriter writer, CityReader cityReader,DatagramChannel channel, SocketAddress server) {
        super(fileManager, writer, cityReader);
        this.server = server;
        this.request = new ServerRequest();
        this.response = new ServerResponse();
        this.serializer = new SerializationHelper();
        this.waitResponce = new WaitResponse();
        this.buffer = ByteBuffer.allocate(52428800);
        this.channel = channel;

    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    //мда
    @Override
    public void handle(String command) throws IOException {
        super.parseInput(command);
        try {
            validator.validate(getCommandName(),getArgument());
        } catch (NoValidArgumentException e) {
            getWriter().write(e.getMessage() + "\n");
            getWriter().flush();
            return;
        }
        String currClass = commandClassMap.get(getCommandName());
        if (currClass.equals("Exit")) System.exit(0);
        if (currClass.equals("Add") || currClass.equals("Update")
                || currClass.equals("AddIfMax")){
            try {
                if (currClass.equals("Update")) request.setId(getArgument());
                request.setArgument(getCityReader().read());
                request.setCommand(getCommandName());
                request.setOwnerId(ownerId);
                buffer.put(serializer.serialize(request));
                buffer.flip();
                channel.send(buffer,server);
                buffer.clear();
                waitServerResponse();
                return;
            } catch (ReaderInterruptionException e) {
                getWriter().write(e.getMessage());
                getWriter().flush();
                return;
            }
        }

        if (currClass.equals("ExecuteScript")){
            execScript(getArgument()[0]);
            return;
        }
        request.setArgument(getArgument());
        request.setCommand(getCommandName());
        request.setOwnerId(ownerId);
        buffer.put(serializer.serialize(request));
        buffer.flip();
        channel.send(buffer,server);
        buffer.clear();
        waitServerResponse();
        return;
    }


    @Override
    protected void scriptHandle(BufferedReader scriptReader) throws ScriptInputIssueException, IOException {
        parseInput(scriptReader.readLine());
        try {
            validator.validate(getCommandName(),getArgument());
        } catch (NoValidArgumentException e) {
           throw new ScriptInputIssueException("script error\n"
                     + getCommandName() + ": " + e.getMessage() + "\n");
        }
        String currClass = commandClassMap.get(getCommandName());
        if (currClass.equals("Exit")) System.exit(0);
        if (currClass.equals("Add") || currClass.equals("Update")
                || currClass.equals("AddIfMax")){
            if (currClass.equals("Update")) request.setId(getArgument());
            request.setArgument(new ScriptCityReader(scriptReader).read());
            request.setCommand(getCommandName());
            request.setOwnerId(ownerId);
            buffer.put(serializer.serialize(request));
            buffer.flip();
            channel.send(buffer,server);
            buffer.clear();
            waitServerResponse();
            return;
        }
        if (currClass.equals("ExecuteScript")){
            if(getScriptStack().contains(getArgument()[0])){
                getScriptStack().pop();
                throw new ScriptInputIssueException("script error\n script looping\n");
            }
            execScript(getArgument()[0]);
            return;
        }
        request.setArgument(getArgument());
        request.setCommand(getCommandName());
        request.setOwnerId(ownerId);
        buffer.put(serializer.serialize(request));
        buffer.flip();
        channel.send(buffer,server);
        buffer.clear();
        waitServerResponse();
        return;
    }

    private void waitServerResponse() throws IOException {
        if (!waitResponce.waitReceive(channel,buffer,5)){
            tryCount++;
            if (tryCount > 1) {
                getWriter().write("server doesn't responce for a long time\ntry later");
                getWriter().flush();
                System.exit(0);
            }
            getWriter().write("server doesn't response\ntry one more time or later\n");
            getWriter().flush();
            return;
        }
        tryCount = 0;

        buffer.flip();
        try {
            response = (ServerResponse) serializer.deSerialization(buffer);
        } catch (ClassNotFoundException e) {
            getWriter().write("data from server was corrupted");
            getWriter().flush();
            return;
        }
        getWriter().write(response.getResponse() + "\n");
        getWriter().flush();
        buffer.clear();
        return;
    }



    public void setCommandClassMap(Map<String, String> commandClassMap) {
        this.commandClassMap = commandClassMap;
    }

    public void setValidator(ClientValidator validator) {
        this.validator = validator;
    }
}
