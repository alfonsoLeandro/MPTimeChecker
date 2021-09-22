package com.github.alfonsoleandro.timechecker;

import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.timechecker.commands.MainCommand;
import com.github.alfonsoleandro.timechecker.commands.MainCommandTabCompleter;
import com.github.alfonsoleandro.timechecker.events.JoinLeaveEvents;
import com.github.alfonsoleandro.timechecker.managers.TopPlayersManager;
import com.github.alfonsoleandro.timechecker.utils.PAPIPlaceholder;
import com.github.alfonsoleandro.mputils.files.YamlFile;
import com.github.alfonsoleandro.mputils.metrics.Metrics;
import com.github.alfonsoleandro.mputils.reloadable.ReloaderPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class TimeChecker extends ReloaderPlugin {

    private final char color = 'e';
    private final PluginDescriptionFile pdfFile = getDescription();
    private final String version = pdfFile.getVersion();
    private String latestVersion;
    private YamlFile configYaml;
    private YamlFile playersYaml;
    private PAPIPlaceholder papiExpansion;
    private MessageSender<Message> messageSender;
    private TopPlayersManager topPlayersManager;


    /**
     * Plugin enable logic.
     */
    @Override
    public void onEnable() {
        registerFiles();
        this.messageSender = new MessageSender<>(this, Message.values(), this.configYaml,
                "config.messages", "config.prefix");
        this.messageSender.send("&aEnabled&f. Version: &e" + version);
        this.messageSender.send("&fThank you for using my plugin! &" + color + pdfFile.getName() + "&f By " + pdfFile.getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + pdfFile.getWebsite());
        this.topPlayersManager = new TopPlayersManager(this);
        updateChecker();
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
        playersYaml.save(false);
        this.messageSender.send("&cDisabled&f. Version: &e" + version);
        this.messageSender.send("&fThank you for using my plugin! &" + color + pdfFile.getName() + "&f By " + pdfFile.getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + pdfFile.getWebsite());
        unRegisterPAPIPlaceholder();
    }

    /**
     * Checks for a new update in spigot.
     */
    public void updateChecker() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=89890").openConnection();
            final int timed_out = 1250;
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);
            latestVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if(latestVersion.length() <= 7) {
                if(!version.equals(latestVersion)) {
                    String exclamation = "&e&l(&4&l!&e&l)";
                    this.messageSender.send(exclamation + "&c There is a new version available. &e(&7" + latestVersion + "&e)");
                    this.messageSender.send(exclamation + "&c Download it here:&f http://bit.ly/TimeCheckerUpdate");
                }
            }
        } catch (Exception ex) {
            this.messageSender.send("Error while checking updates.");
        }
    }


    /**
     * Gets the plugins current version.
     *
     * @return The version string.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Gets the plugins latest version available on spigot.
     *
     * @return The latest version string.
     */
    public String getLatestVersion() {
        return this.latestVersion;
    }


    /**
     * Starts bStats metrics collection
     */
    private void startMetrics() {
        new Metrics(this, 9345);
    }

    public void registerPAPIPlaceholder() {
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if(papi != null && papi.isEnabled()) {
            this.messageSender.send("&aPlaceholderAPI found, the placeholder has been registered successfully");
            papiExpansion = new PAPIPlaceholder(this);
            papiExpansion.register();
        } else {
            this.messageSender.send("&cPlaceholderAPI not found, the placeholder was not registered");
        }
    }

    private void unRegisterPAPIPlaceholder() {
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if(papi != null && papi.isEnabled() && papiExpansion != null) {
            papiExpansion.unregister();
        }
    }


    /**
     * Registers plugin files.
     */
    public void registerFiles() {
        configYaml = new YamlFile(this, "config.yml");
        playersYaml = new YamlFile(this, "players.yml");
    }

    /**
     * Reloads plugin files.
     */
    public void reloadFiles() {
        configYaml.saveDefault();
        configYaml.loadFileConfiguration();

        playersYaml.saveDefault();
        playersYaml.loadFileConfiguration();
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
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinLeaveEvents(this), this);
    }

    /**
     * Get the config YamlFile.
     *
     * @return The YamlFile containing the config.
     */
    public YamlFile getConfigYaml() {
        return this.configYaml;
    }

    /**
     * Get the players YamlFile.
     *
     * @return The YamlFile containing the players file.
     */
    public YamlFile getPlayersYaml() {
        return this.playersYaml;
    }

    /**
     * Gets the message sending manager for this plugin.
     * @return Gets the unique instance of the MessageSender object for this plugin.
     */
    public MessageSender<Message> getMessageSender(){
        return this.messageSender;
    }



    public enum Message{
        NO_PERMISSION,
        UNKNOWN_COMMAND,
        RELOADED,
        CANNOT_CHECK_CONSOLE,
        NOT_EXIST,
        SELF_CHECK,
        OTHER_CHECK,
        ERROR_CHECKING_SESSION,
        SELF_SESSION_CHECK,
        OTHER_SESSION_CHECK,
        CALCULATING,
        AMOUNT_TOP,
        TOP_LIST,
        TOP_PLAYER
    }
}
