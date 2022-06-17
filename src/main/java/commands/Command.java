package commands;

import exсeptions.NoValidArgumentException;

public interface Command<T> {
    T execute(String[] argument);
    String getName();
    String getDescription();
    String getUsage();
    void setOwnerId(Integer id);
    @Deprecated
    boolean validateArgument(String[] arg) throws NoValidArgumentException;

}
