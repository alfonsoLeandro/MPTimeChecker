package com.github.alfonsoLeandro.timechecker.managers;

import com.github.alfonsoLeandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
//todo
public class MessageSender extends Reloadable {

    private final TimeChecker plugin;

    public MessageSender(TimeChecker plugin){
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void reload() {

    }
}
