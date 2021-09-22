package com.github.alfonsoleandro.timechecker.managers;

import com.github.alfonsoleandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;

//todo
public class TopPlayersManager extends Reloadable {

    private final TimeChecker plugin;
    private LinkedHashMap<OfflinePlayer, Integer> topPlayers;
    private LinkedHashMap<OfflinePlayer, Integer> worstPlayers;

    public TopPlayersManager(TimeChecker plugin){
        super(plugin);
        this.plugin = plugin;
    }

    private void generateTops(){
        this.topPlayers = new LinkedHashMap<>();
    }


    @Override
    public void reload(boolean deep) {
        generateTops();
    }
}
