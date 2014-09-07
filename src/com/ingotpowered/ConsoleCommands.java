package com.ingotpowered;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleCommands {

    public void startHandling() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                dispatchConsoleCommand(br.readLine());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            IngotServer.server.stop();
        }
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
        System.out.println("Unknown command '" + commandName + "'.");
    }
}
