package com.ingotpowered;

import com.ingotpowered.api.plugins.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
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
                BufferedReader br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("plugin.yml")));
                String in;
                boolean hasStart = false;
                String mainClass = null;
                String pluginName = null;
                String pluginAuthor = null;
                String pluginDesc = null;
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
                    } else if (params[0].equals("name")) {
                        pluginName = params[1].trim();
                    } else if (params[0].equals("author")) {
                        pluginAuthor = params[1].trim();
                    } else if (params[0].equals("description")) {
                        pluginDesc = params[1].trim();
                    }
                }
                br.close();
                if (mainClass == null) {
                    System.out.println("Main class cannot be null!");
                    continue;
                }
                if (pluginName == null) {
                    System.out.println("Plugin name cannot be null!");
                    continue;
                }
                if (pluginAuthor == null) {
                    System.out.println("Plugin author cannot be null!");
                    continue;
                }
                if (pluginDesc == null) {
                    System.out.println("Plugin description cannot be null!");
                    continue;
                }
                Plugin plugin = (Plugin) loader.loadClass(mainClass).newInstance();
                setPluginField(plugin, "name", pluginName);
                setPluginField(plugin, "author", pluginAuthor);
                setPluginField(plugin, "description", pluginDesc);
                setPluginField(plugin, "jarFilePath", jarFiles[i].getAbsolutePath());
                setPluginField(plugin, "pluginDirectory", new File(pluginFolder, pluginName));
                plugins.add(plugin);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        for (Plugin p : plugins) {
            long start = System.currentTimeMillis();
            try {
                p.onEnable();
            }
            catch(Exception ex){
                ex.printStackTrace();
                try {
                    p.handleException(ex);
                }
                catch(Exception ex2){
                    ex2.printStackTrace();
                }
            }
            System.out.println("Enabled " + p.getName() + ", took " + (System.currentTimeMillis() - start) + " milliseconds.");
        }
    }

    public void unload() {
        System.out.println("========= Disabling all plugins =========");
        for (Plugin p : plugins) {
            try {
                p.onDisable();
            }
            catch(Exception ex){
                ex.printStackTrace();
                try{
                    p.handleException(ex);
                }
                catch(Exception ex2){
                    ex2.printStackTrace();
                }
            }
        }
        System.out.println("==========================================");
    }

    private void setPluginField(Plugin plugin, String variable, Object value) throws Exception {
        Field field = Plugin.class.getDeclaredField(variable);
        field.setAccessible(true);
        field.set(plugin, value);
        field.setAccessible(false);
    }
}
