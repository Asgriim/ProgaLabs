package commands;

import exсeptions.NoValidArgumentException;
import utility.CollectionManager;

import java.io.Serializable;
import java.util.Locale;
import java.util.stream.Collectors;

public class Show implements Command<String>, Serializable {
    private static final long serialVersionUID = 1;
    private Integer ownerId;
    private String name;
    private String description;
    private String usage;
    private CollectionManager collectionManager;

    public Show(String name, String description, String usage, CollectionManager collectionManager) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] argument) {
        if (argument[0].toLowerCase(Locale.ROOT).equals("my")){
            String s = collectionManager.getCollection().stream().filter(x -> x.getOwnerId().equals(ownerId)).map(x -> x.toString()).collect(Collectors.joining("\n"));
            if (s.length() == 0) return "collection is empty";
            return s;
        }
        return collectionManager.getStringCollection() + "\n";
    }

    @Override
    public void setOwnerId(Integer id) {
        this.ownerId = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getUsage() {
        return this.usage;
    }

    @Override
    public boolean validateArgument(String[] arg) throws NoValidArgumentException {
        if (arg[0].length() != 0) throw new NoValidArgumentException("illegal arg");
        return true;
    }
}
