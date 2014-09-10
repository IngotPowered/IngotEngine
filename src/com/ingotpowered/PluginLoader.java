package com.ingotpowered;

import com.ingotpowered.api.plugins.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PluginLoader {

    public ArrayList<Plugin> plugins = new ArrayList<Plugin>();

    public void load() {
        File pluginFolder = new File("plugins");
        if (!pluginFolder.exists() || !pluginFolder.isDirectory()) {
            if (!pluginFolder.mkdir()) {
                System.out.println("[ SEVERE ] Could not create plugins folder!");
                System.out.println("[ SEVERE ] Aborting plugin loading");
                return;
            }
        }
        File[] jarFiles = pluginFolder.listFiles();
        for (int i = 0; i < jarFiles.length; i++) {
            if (!jarFiles[i].getName().endsWith(".jar")) {
                continue;
            }
            try {
                ClassLoader loader = URLClassLoader.newInstance(new URL[] { jarFiles[i].toURI().toURL() });
                /*System.out.println("Trying to load " + "jar:file:" + jarFiles[i].getAbsoluteFile() + File.separator + "plugin.yml");
                BufferedReader br = new BufferedReader(new InputStreamReader(new URL("jar:file:/" + jarFiles[i].getAbsoluteFile() + "/plugin.yml").openConnection().getInputStream()));
                */
                BufferedReader br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("plugin.yml")));
                String in;
                boolean hasStart = false;
                String mainClass = null;
                while ((in = br.readLine()) != null) {
                    if (!hasStart) {
                        if (in.equals("ingot-config:")) {
                            hasStart = true;
                        }
                        continue;
                    }
                    String[] params = in.trim().split(Pattern.quote(":"));
                    if (params[0].equals("main")) {
                        mainClass = params[1].trim();
                    }
                }
                br.close();
                if (null == mainClass) {
                    System.out.println("Main class cannot be null!");
                    continue;
                }

                Plugin plugin = (Plugin) loader.loadClass(mainClass).newInstance();
                plugins.add(plugin);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (Plugin p : plugins) {
            p.onEnable();
        }
    }
}
