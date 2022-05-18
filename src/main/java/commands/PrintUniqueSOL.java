package commands;

import exсeptions.NoValidArgumentException;
import utility.CollectionManager;

import java.io.Serializable;

public class PrintUniqueSOL implements Command<String>, Serializable {
    private static final long serialVersionUID = 1;
    private Integer ownerId;
    private String name;
    private String description;
    private String usage;
    private CollectionManager collectionManager;

    public PrintUniqueSOL(String name, String description, String usage, CollectionManager collectionManager) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] argument) {
        if(collectionManager.getUniqueStandards().isEmpty()){
            if(collectionManager.getCollection().isEmpty()) return "collection is empty\n";
            return "no cities with standard of living\n";
        }
        return collectionManager.getUniqueStandards().toString() + "\n";
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
