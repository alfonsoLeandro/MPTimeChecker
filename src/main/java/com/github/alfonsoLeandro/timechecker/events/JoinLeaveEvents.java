package com.github.alfonsoLeandro.timechecker.events;

import com.github.alfonsoLeandro.timechecker.TimeChecker;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class JoinLeaveEvents implements Listener {

    final private TimeChecker plugin;

    public JoinLeaveEvents(TimeChecker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        FileConfiguration players = plugin.getPlayersYaml().getAccess();

        players.set("players."+event.getPlayer().getName(), System.currentTimeMillis());
        plugin.getPlayersYaml().save(true);
        if(event.getPlayer().isOp() && !plugin.getVersion().equals(plugin.getLatestVersion())){
            String exclamation = "&e&l(&4&l!&e&l)";
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', exclamation+"&c There is a new version available. &e(&7"+ plugin.getLatestVersion() +"&e)"));
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', exclamation+"&c Download it here:&f http://bit.ly/TimeCheckerUpdate"));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        FileConfiguration players = plugin.getPlayersYaml().getAccess();

        players.set("players."+event.getPlayer().getName(), null);
        plugin.getPlayersYaml().save(plugin.isEnabled());
    }
}
