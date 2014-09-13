package com.ingotpowered;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleCommands {

    public boolean stopped = false;

    public void startHandling() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                if (stopped) {
                    break;
                }
                dispatchConsoleCommand(br.readLine());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            IngotServer.server.stop();
        }
        System.out.println("Console command handler has been shut down!");
    }

    public void dispatchConsoleCommand(String command) {
        if (command == null) {
            return;
        }
        String[] params = command.split(" ");
        if (params.length == 0) {
            return;
        }
        String[] args = new String[params.length - 1];
        System.arraycopy(params, 1, args, 0, args.length);
        dispatchConsoleCommand(params[0], args);
    }

    private void dispatchConsoleCommand(String commandName, String[] args) {
        if (commandName.equalsIgnoreCase("stop")) {
            IngotServer.server.stop();
        } else if (!IngotServer.server.commandRegistry.runServerCommand(IngotServer.server, commandName, args)) {
            System.out.println("Unknown command '" + commandName + "'.");
        }
    }
}
