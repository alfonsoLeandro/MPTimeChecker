package com.github.alfonsoLeandro.timechecker.events;

import com.github.alfonsoLeandro.timechecker.TimeChecker;
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
        plugin.getPlayersYaml().save();
        if(event.getPlayer().isOp() && !plugin.getVersion().equals(plugin.getLatestVersion())){
            //todo
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        FileConfiguration players = plugin.getPlayersYaml().getAccess();

        players.set("players."+event.getPlayer().getName(), null);
        plugin.getPlayersYaml().save();
    }
}
