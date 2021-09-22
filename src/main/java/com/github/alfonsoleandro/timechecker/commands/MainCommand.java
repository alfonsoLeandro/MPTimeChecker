package com.github.alfonsoleandro.timechecker.commands;

import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.timechecker.managers.TopPlayersManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MainCommand implements CommandExecutor {

    private final TimeChecker plugin;
    private final MessageSender<TimeChecker.Message> messageSender;
    private final TopPlayersManager topPlayersManager;

    /**
     * MainCommand class constructor.
     * @param plugin The main class instance.
     */
    public MainCommand(TimeChecker plugin){
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
        this.topPlayersManager = plugin.getTopPlayersManager();
    }




    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            this.messageSender.send(sender, "&6List of commands");
            this.messageSender.send(sender, "&f/"+label+" help");
            this.messageSender.send(sender, "&f/"+label+" version");
            this.messageSender.send(sender, "&f/"+label+" reload");
            this.messageSender.send(sender, "&f/"+label+" check <player>");
            this.messageSender.send(sender, "&f/"+label+" session <player>");
            this.messageSender.send(sender, "&f/"+label+" top");
            this.messageSender.send(sender, "&f/"+label+" worst");



        }else if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("timeChecker.reload")) {
                this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                return true;
            }
            plugin.reload(false);
            this.messageSender.send(sender, TimeChecker.Message.RELOADED);


        }else if(args[0].equalsIgnoreCase("version")) {
            if(!sender.hasPermission("timeChecker.version")) {
                this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                return true;
            }
            if(plugin.getVersion().equals(plugin.getLatestVersion())) {
                this.messageSender.send(sender, "&fVersion: &e" + plugin.getVersion()+" &aUp to date!");
            }else{
                this.messageSender.send(sender, "&fVersion: &e" + plugin.getVersion()+" &cUpdate available!");
            }



        }else if(args[0].equalsIgnoreCase("check")) {
            if(args.length < 2) {
                if(!sender.hasPermission("timeChecker.check")) {
                    this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                    return true;
                }
                if(sender instanceof ConsoleCommandSender) {
                    this.messageSender.send(sender, TimeChecker.Message.CANNOT_CHECK_CONSOLE);
                    return true;
                }

                //Check self time
                int ticks = ((Player) sender).getStatistic(Statistic.PLAY_ONE_MINUTE);

                this.messageSender.send(sender, TimeChecker.Message.SELF_CHECK,
                        "%time%", topPlayersManager.getTime(ticks));

            } else {
                if(!sender.hasPermission("timeChecker.check.others")) {
                    this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                    return true;
                }

                OfflinePlayer toCheck = Bukkit.getOfflinePlayer(args[1]);

                if(toCheck.hasPlayedBefore() || toCheck.isOnline()) {
                    int ticks = toCheck.getStatistic(Statistic.PLAY_ONE_MINUTE);

                    this.messageSender.send(sender, TimeChecker.Message.OTHER_CHECK,
                            "%player%", args[1],
                            "%time%", topPlayersManager.getTime(ticks));

                } else {
                    this.messageSender.send(sender, TimeChecker.Message.NOT_EXIST);
                }
            }



        }else if(args[0].equalsIgnoreCase("session")) {
            FileConfiguration players = plugin.getPlayersYaml().getAccess();

            if(args.length < 2) {
                if(!sender.hasPermission("timeChecker.session")) {
                    this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                    return true;
                }
                if(sender instanceof ConsoleCommandSender) {
                    this.messageSender.send(sender, TimeChecker.Message.CANNOT_CHECK_CONSOLE);
                    return true;
                }
                if(!players.contains("players." + sender.getName())) {
                    this.messageSender.send(sender, TimeChecker.Message.ERROR_CHECKING_SESSION);
                    return true;
                }

                //Check self session time
                long ticks = (System.currentTimeMillis() - players.getLong("players." + sender.getName())) / 50;

                this.messageSender.send(sender, TimeChecker.Message.SELF_SESSION_CHECK,
                        "%time%", topPlayersManager.getTime(ticks));

            } else {
                if(!sender.hasPermission("timeChecker.session.others")) {
                    this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                    return true;
                }

                Player toCheck = Bukkit.getPlayer(args[1]);

                if(toCheck != null) {

                    if(!players.contains("players." + args[1])) {
                        this.messageSender.send(sender, TimeChecker.Message.ERROR_CHECKING_SESSION);
                        return true;
                    }
                    long ticks = (System.currentTimeMillis() - players.getLong("players." + toCheck.getName())) / 50;

                    this.messageSender.send(sender, TimeChecker.Message.OTHER_SESSION_CHECK,
                            "%player%", args[1],
                            "%time%", topPlayersManager.getTime(ticks));

                } else {
                    this.messageSender.send(sender, TimeChecker.Message.NOT_EXIST);
                }
            }

        }else if(args[0].equalsIgnoreCase("top")){
            if(!sender.hasPermission("timeChecker.top")){
                this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                return true;
            }

            this.topPlayersManager.sendTop(sender);

        }else if(args[0].equalsIgnoreCase("worst")){
            if(!sender.hasPermission("timeChecker.top")){
                this.messageSender.send(sender, TimeChecker.Message.NO_PERMISSION);
                return true;
            }

            this.topPlayersManager.sendWorst(sender);


            //unknown command
        }else {
            this.messageSender.send(sender, TimeChecker.Message.UNKNOWN_COMMAND,
                    "%command%", label);
        }



        return true;
    }


}
