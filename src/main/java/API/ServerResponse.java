package API;

import commands.Command;

import java.io.Serializable;
import java.util.Map;

public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 12;
    private String response;
    private Map<String, String> commandMap;
    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String, String> getCommandMap() {
        return commandMap;
    }

    public void setCommandMap(Map<String, String> commandMap) {
        this.commandMap = commandMap;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "response='" + response + '\'' +
                ", commandMap=" + commandMap +
                '}';
    }
}
