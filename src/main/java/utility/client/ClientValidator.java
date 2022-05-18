package utility.client;

import data.Climate;
import data.StandardOfLiving;
import exсeptions.FileIssueException;
import exсeptions.NoValidArgumentException;
import utility.FileManager;

import java.util.Locale;
import java.util.Map;
//мда х2
public class ClientValidator {
    private Map<String,String> commandMap;
    private FileManager fileManager;
    public ClientValidator(Map<String, String> commandMap,FileManager fileManager) {
        this.commandMap = commandMap;
        this.fileManager = fileManager;
    }
    public boolean validate(String commandName,String[] arg) throws NoValidArgumentException {
        String s;
        if (!commandMap.containsKey(commandName)) throw new NoValidArgumentException("no such command\n type help to see command list");
        String commandClass = commandMap.get(commandName);
        switch (commandClass){
            case "Show":
                if(arg[0].length() != 0){
                    if (arg[0].toLowerCase(Locale.ROOT).equals("my")) return true;
                    throw new NoValidArgumentException("illegal arg");
                }
                return true;
            case "Add":
            case "AddIfMax":
            case "Clear":
            case "Exit":
            case "Info":
            case "PrintUniqueSOL":
            case "RemoveFirst":
            case "RemoveHead":
                if (arg[0].length() != 0) throw new NoValidArgumentException("illegal arg");
                return true;
            case "CountBySOL":
                if (arg[0].length() == 0) throw new NoValidArgumentException("illegal arg"+
                        "\ncommand receive standard of living const as argument: [ULTRA_HIGH, HIGH, MEDIUM, ULTRA_LOW, NIGHTMARE]");
                s = arg[0].toUpperCase();
                try {
                    StandardOfLiving.valueOf(s);
                } catch (IllegalArgumentException e) {
                    throw new NoValidArgumentException("no such standard of living\n" +
                            "command receive standard of living const as argument: [ULTRA_HIGH, HIGH, MEDIUM, ULTRA_LOW, NIGHTMARE]");
                }
                return true;
            case "ExecuteScript":
                if (arg[0].length() == 0) throw new NoValidArgumentException("file not specified");
                try {
                    fileManager.checkFile(arg[0],false,false);
                } catch (FileIssueException e) {
                    throw new NoValidArgumentException(e.getMessage());
                }
                return true;
            case "Help":
                if(arg[0].length() != 0){
                    if (commandMap.get(arg[0]) == null){
                        throw new NoValidArgumentException("illegal arg");
                    }
                }
                return true;
            case "RemoveAnyByClimate":
                if (arg[0].length() == 0) throw new NoValidArgumentException("illegal arg"+
                        "command receive climate const as argument: [RAIN_FOREST, OCEANIC, MEDITERRANIAN, SUBARCTIC]");
                s = arg[0].toUpperCase();
                try {
                    Climate.valueOf(s);
                } catch (IllegalArgumentException e) {
                    throw new NoValidArgumentException("no such climate\n" +
                            "command receive climate const as argument: [RAIN_FOREST, OCEANIC, MEDITERRANIAN, SUBARCTIC]");
                }
                return true;
            case "RemoveById":
            case "Update":
                if (arg[0].length() == 0) throw new NoValidArgumentException("id not recognized");
                try {
                    Integer.valueOf(arg[0]);
                } catch (NumberFormatException e) {
                    throw new NoValidArgumentException("arg must be integer");
                }
                return true;
        }
    return true;
    }
}
