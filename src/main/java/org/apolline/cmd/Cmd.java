package org.apolline.cmd;

/**
 * This interface represent a command send to a sensor
 */
public interface Cmd {

    /**
     * Execute the command
     */
    void execute();

    /**
     * Read the response of the command
     */
    void read();

    /**
     * Find the response of the command
     * @return The response of the command
     */
    String getResponse();

    /**
     * Indicate if the command is finish
     * @return True if the command is finish, else false
     */
    boolean isFinish();
}
