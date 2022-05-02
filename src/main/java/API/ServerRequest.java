package API;

import commands.Command;

import java.io.Serializable;
import java.util.Arrays;

public class ServerRequest implements Serializable {
    private static final long serialVersionUID = 3;
    private String[] argument;
    private String command;
    private String[] id;

    public String[] getId() {
        return id;
    }

    public void setId(String[] id) {
        this.id = id;
    }

    public void setArgument(String[] argument) {
        this.argument = argument;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getArgument() {
        return argument;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "ServerRequest{" +
                "argument=" + Arrays.toString(argument) +
                ", command='" + command + '\'' +
                ", id=" + Arrays.toString(id) +
                '}';
    }
}
