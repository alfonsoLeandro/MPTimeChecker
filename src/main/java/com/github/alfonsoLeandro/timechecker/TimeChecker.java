package com.github.alfonsoLeandro.timechecker;

import com.github.alfonsoLeandro.timechecker.commands.MainCommand;
import com.github.alfonsoLeandro.timechecker.commands.MainCommandTabCompleter;
import com.github.alfonsoLeandro.timechecker.events.JoinLeaveEvents;
import com.github.alfonsoLeandro.timechecker.utils.PAPIPlaceholder;
import com.github.alfonsoleandro.mputils.files.YamlFile;
import com.github.alfonsoleandro.mputils.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class TimeChecker extends JavaPlugin {

    private final PluginDescriptionFile pdfFile = getDescription();
    private final String version = pdfFile.getVersion();
    private String latestVersion;
    private final char color = 'e';
    private final String name = "&f[&" + color + pdfFile.getName() + "&f]";
    private YamlFile configYaml;
    private YamlFile playersYaml;
    private PAPIPlaceholder papiExpansion;

    /**
     * Sends a message to the console, with colors and prefix added.
     * @param msg The message to be sent.
     */
    private void send(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', name + " " + msg));
    }


    /**
     * Plugin enable logic.
     */
    @Override
    public void onEnable() {
        send("&aEnabled&f. Version: &e" + version);
        send("&fThank you for using my plugin! &" + color + pdfFile.getName() + "&f By " + pdfFile.getAuthors().get(0));
        send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        send("Please consider subscribing to my yt channel: &c" + pdfFile.getWebsite());
        updateChecker();
        reloadFiles();
        registerCommands();
        registerEvents();
        startMetrics();
        registerPAPIPlaceholder();
    }

    /**
     * Plugin disable logic.
     */
    @Override
    public void onDisable() {
        playersYaml.getAccess().set("players", null);
        playersYaml.save();
        send("&cDisabled&f. Version: &e" + version);
        send("&fThank you for using my plugin! &" + color + pdfFile.getName() + "&f By " + pdfFile.getAuthors().get(0));
        send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        send("Please consider subscribing to my yt channel: &c" + pdfFile.getWebsite());
        unRegisterPAPIPlaceholder();
    }

    //
    //updates
    //
    public void updateChecker(){
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=89890").openConnection();
            final int timed_out = 1250;
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);
            latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (latestVersion.length() <= 7) {
                if(!version.equals(latestVersion)){
                    String exclamation = "&e&l(&4&l!&e&l)";
                    send(exclamation+"&c There is a new version available. &e(&7"+ latestVersion +"&e)");
                    send(exclamation+"&c Download it here:&f http://bit.ly/TimeCheckerUpdate");
                }
            }
        } catch (Exception ex) {
            send("Error while checking updates.");
        }
    }



    /**
     * Gets the plugins current version.
     * @return The version string.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Gets the plugins latest version available on spigot.
     * @return The latest version string.
     */
    public String getLatestVersion() {
        return this.latestVersion;
    }


    /**
     * Starts bStats metrics collection
     */
    private void startMetrics(){
        new Metrics(this, 9345);
    }

    public void registerPAPIPlaceholder(){
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if(papi != null && papi.isEnabled()){
            send("&aPlaceholderAPI found, the placeholder has been registered successfully");
            papiExpansion = new PAPIPlaceholder(this);
            papiExpansion.register();
        }else{
            send("&cPlaceholderAPI not found, the placeholder was not registered");
        }
    }

    private void unRegisterPAPIPlaceholder(){
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if(papi != null && papi.isEnabled() && papiExpansion != null){
            papiExpansion.unregister();
        }
    }


    /**
     * Registers and reloads plugin files.
     */
    public void reloadFiles() {
        configYaml = new YamlFile(this, "config.yml");
        playersYaml = new YamlFile(this, "players.yml");
    }



    /**
     * Registers commands and command classes.
     */
    private void registerCommands() {
        PluginCommand mainCommand = getCommand("timeChecker");
        assert mainCommand != null;
        mainCommand.setExecutor(new MainCommand(this));
        mainCommand.setTabCompleter(new MainCommandTabCompleter());
    }

    /**
     * Registers the event listeners.
     */
    private void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinLeaveEvents(this), this);
    }

    /**
     * Get the config YamlFile.
     * @return The YamlFile containing the config.
     */
    public YamlFile getConfigYaml(){
        return this.configYaml;
    }

    /**
     * Get the players YamlFile.
     * @return The YamlFile containing the players file.
     */
    public YamlFile getPlayersYaml(){
        return this.playersYaml;
    }



}
