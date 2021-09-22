package com.github.alfonsoleandro.timechecker.events;

import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.timechecker.TimeChecker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class JoinLeaveEvents implements Listener {

    private final TimeChecker plugin;
    private final MessageSender<TimeChecker.Message> messageSender;

    public JoinLeaveEvents(TimeChecker plugin) {
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        FileConfiguration players = plugin.getPlayersYaml().getAccess();
        Player player = event.getPlayer();

        players.set("players."+player.getName(), System.currentTimeMillis());
        plugin.getPlayersYaml().save(true);

        if(player.isOp() && !plugin.getVersion().equals(plugin.getLatestVersion())){
            String exclamation = "&e&l(&4&l!&e&l)";
            this.messageSender.send(player, exclamation+"&c There is a new version available. &e(&7"+ plugin.getLatestVersion() +"&e)");
            this.messageSender.send(player, exclamation+"&c Download it here:&f http://bit.ly/TimeCheckerUpdate");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        FileConfiguration players = plugin.getPlayersYaml().getAccess();

        players.set("players."+event.getPlayer().getName(), null);
        plugin.getPlayersYaml().save(plugin.isEnabled());
    }
}
