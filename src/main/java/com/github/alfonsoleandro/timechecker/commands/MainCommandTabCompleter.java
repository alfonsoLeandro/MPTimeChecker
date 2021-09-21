package com.github.alfonsoleandro.timechecker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainCommandTabCompleter implements TabCompleter {

    public boolean equalsToStringUnCompleted(String input, String string){
        for(int i = 0; i < string.length(); i++){
            if(input.equalsIgnoreCase(string.substring(0,i))){
                return true;
            }
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final List<String> lista = new ArrayList<>();

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("")) {
                lista.add("help");
                lista.add("version");
                lista.add("reload");
                lista.add("check");
                lista.add("session");
                lista.add("top");

            } else if(equalsToStringUnCompleted(args[0], "help")) {
                lista.add("help");

            } else if(equalsToStringUnCompleted(args[0], "version")) {
                lista.add("version");

            } else if(equalsToStringUnCompleted(args[0], "reload")) {
                lista.add("reload");

            } else if(equalsToStringUnCompleted(args[0], "check")) {
                lista.add("check");

            } else if(equalsToStringUnCompleted(args[0], "session")) {
                lista.add("session");

            } else if(equalsToStringUnCompleted(args[0], "top")) {
                lista.add("top");
            }

        }else if(args.length > 1){
            if(args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("session")){
                return null;
            }
        }

        return lista;
    }
}
