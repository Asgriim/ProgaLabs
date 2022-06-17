package API;
import data.City;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 12;
    private String response;
    private Map<String, String> commandMap;
    private Integer ownerId;
    private PriorityBlockingQueue<City> cityCollection;

    public PriorityBlockingQueue<City> getCityCollection() {
        return cityCollection;
    }

    public void setCityCollection(PriorityBlockingQueue<City> cityCollection) {
        this.cityCollection = cityCollection;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

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
                ", ownerId=" + ownerId +
                '}';
    }
}
