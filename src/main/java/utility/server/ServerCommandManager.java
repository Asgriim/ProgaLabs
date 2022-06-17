package utility.server;

import API.ServerRequest;
import API.ServerResponse;
import database.DatabaseManager;
import exсeptions.NoValidArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.CommandManager;
import utility.FileManager;
import utility.SerializationHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private DatabaseManager databaseManager;
    public ServerCommandManager(FileManager fileManager, BufferedWriter writer,DatagramChannel channel,DatabaseManager databaseManager) {
        super(fileManager, writer, null);
        this.channel = channel;
        this.serializer = new SerializationHelper();
        this.buffer = ByteBuffer.allocate(0);
        this.response = new ServerResponse();
        this.logger = LogManager.getLogger();
        this.databaseManager = databaseManager;
    }

    private void apiCommand() throws IOException {
        response.setCommandMap(commandClassMap);
        sendResponse();
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

    private boolean registerCommand() throws IOException {
        Integer id = databaseManager.getNextOwnerId();
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement("insert into users (id, login, password) values (?,?,?)");
            ResultSet set = databaseManager.getConnection().createStatement().executeQuery("select login  from users where login ='" + request.getLogIn()+"'");
            if (set.next()){
                response.setOwnerId(0);
                response.setResponse("this login is already exist\n enter different one");
                sendResponse();
                return false;
            }
            statement.setInt(1,id);
            statement.setString(2,request.getLogIn());
            statement.setString(3,getHash(request.getPassword() + id));
            statement.execute();
            response.setOwnerId(id);
            response.setResponse("register successfully");
            apiCommand();
            return true;
        } catch (SQLException e) {
            response.setResponse("error 228");
            response.setOwnerId(0);
            sendResponse();
            e.printStackTrace();
            return false;
        }
    }

    private boolean signInCommand() throws IOException {
        try {
            PreparedStatement statement = databaseManager.getConnection().prepareStatement("select * from users where login = ?");
            statement.setString(1,request.getLogIn());
            ResultSet set = statement.executeQuery();
            boolean flag = false;
            while (set.next()){
                if (set.getString("password").equals(getHash(request.getPassword() + set.getInt("id")))) {
                    flag = true;
                    break;
                }
            }
            if (flag == false){
                response.setResponse("no such login or wrong password");
                response.setOwnerId(0);
                sendResponse();
                return false;
            }
            response.setOwnerId(set.getInt("id"));
            response.setResponse("sign in successfully");
            apiCommand();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            response.setOwnerId(0);
            response.setResponse("error 228");
            sendResponse();
            return false;
        }
    }

    @Override
    public void handle(String command) throws IOException {
        setCommandName(command);
        if (getCommandName().equals("register")){
            registerCommand();
            return;
        }
        if(getCommandName().equals("sign")){
            signInCommand();
            return;
        }
        setCurrentCommand(getCommandMap().get(getCommandName()));
        getCurrentCommand().setOwnerId(request.getOwnerId());
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
    private  void sendResponse() throws IOException {
        ByteBuffer toSend = ByteBuffer.wrap(serializer.serialize(response));
//        ByteBuffer toSend = buffer.put(serializer.serialize(response));
        logger.info("created response: " + response);
        buffer.flip();
        channel.send(toSend,clientAddress);
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
