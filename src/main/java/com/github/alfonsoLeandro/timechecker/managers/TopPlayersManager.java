package com.github.alfonsoLeandro.timechecker.managers;

import com.github.alfonsoLeandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
//todo
public class TopPlayersManager extends Reloadable {

    private final TimeChecker plugin;

    public TopPlayersManager(TimeChecker plugin){
        super(plugin);
        this.plugin = plugin;
    }


    @Override
    public void reload() {

    }
}
