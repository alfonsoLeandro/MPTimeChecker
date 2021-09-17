package com.github.alfonsoLeandro.timechecker.commands;

import com.github.alfonsoLeandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import com.github.alfonsoleandro.mputils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public final class MainCommand implements CommandExecutor {

    final private TimeChecker plugin;
    //Messages
    private String noPerm;
    private String unknown;
    private String reloaded;
    private String cannotCheckConsole;
    private String notExist;
    private String selfCheck;
    private String otherCheck;
    private String errorCheckingSession;
    private String selfSessionCheck;
    private String otherSessionCheck;
    private String calculating;
    private String topList;
    private String topPlayer;
    private int amountTop;

    /**
     * MainCommand class constructor.
     * @param plugin The main class instance.
     */
    public MainCommand(TimeChecker plugin){
        this.plugin = plugin;
        loadMessages();
    }

    /**
     * Loads every message used in commands.
     */
    private void loadMessages(){
        FileConfiguration config = plugin.getConfigYaml().getAccess();

        noPerm = config.getString("config.messages.no permission");
        unknown = config.getString("config.messages.unknown command");
        reloaded = config.getString("config.messages.reloaded");
        cannotCheckConsole = config.getString("config.messages.cannot check console");
        notExist = config.getString("config.messages.not exist");
        selfCheck = config.getString("config.messages.self check");
        otherCheck = config.getString("config.messages.other check");
        errorCheckingSession = config.getString("config.messages.error checking session");
        selfSessionCheck = config.getString("config.messages.self session check");
        otherSessionCheck = config.getString("config.messages.other session check");
        calculating = config.getString("config.messages.calculating");
        amountTop = config.getInt("config.messages.amount top");
        topList = config.getString("config.messages.top list").replaceAll("%amounttop%", String.valueOf(amountTop));
        topPlayer = config.getString("config.messages.top player");
    }

    /**
     * Sends a message to the CommandSender.
     * @param msg The message to be sent.
     */
    private void send(CommandSender sender, String msg){
        sender.sendMessage(StringUtils.colorizeString('&', plugin.getConfigYaml().getAccess().getString("config.prefix")+" "+msg));
    }

    /**
     * Translates and amount of ticks into days, hours and minutes.
     * @param ticks The amount of ticks to translate
     * @return A string with an h,m and s format.
     */
    private String getTime(long ticks){
        FileConfiguration config = plugin.getConfigYaml().getAccess();
        return TimeUtils.getTimeString(ticks)
                .replace("%weeks%", Objects.requireNonNull(config.getString("config.messages.weeks", "weeks")))
                .replace("%week%", Objects.requireNonNull(config.getString("config.messages.week", "week")))
                .replace("%days%", Objects.requireNonNull(config.getString("config.messages.days", "days")))
                .replace("%day%", Objects.requireNonNull(config.getString("config.messages.day", "day")))
                .replace("%hours%", Objects.requireNonNull(config.getString("config.messages.hours", "hours")))
                .replace("%hour%", Objects.requireNonNull(config.getString("config.messages.hour", "hour")))
                .replace("%minutes%", Objects.requireNonNull(config.getString("config.messages.minutes", "minutes")))
                .replace("%minute%", Objects.requireNonNull(config.getString("config.messages.minute", "minute")))
                .replace("%seconds%", Objects.requireNonNull(config.getString("config.messages.seconds", "seconds")))
                .replace("%second%", Objects.requireNonNull(config.getString("config.messages.second", "second")))
                .replace("%and%", Objects.requireNonNull(config.getString("config.messages.and", "and")));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            send(sender, "&6List of commands");
            send(sender, "&f/"+label+" help");
            send(sender, "&f/"+label+" version");
            send(sender, "&f/"+label+" reload");
            send(sender, "&f/"+label+" check <player>");
            send(sender, "&f/"+label+" session <player>");
            send(sender, "&f/"+label+" top");



        }else if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("timeChecker.reload")) {
                send(sender, noPerm);
                return true;
            }
            plugin.reloadFiles();
            loadMessages();
            send(sender, reloaded);


        }else if(args[0].equalsIgnoreCase("version")) {
            if(!sender.hasPermission("timeChecker.version")) {
                send(sender, noPerm);
                return true;
            }
            if(plugin.getVersion().equals(plugin.getLatestVersion())) {
                send(sender, "&fVersion: &e" + plugin.getVersion()+" &aUp to date!");
            }else{
                send(sender, "&fVersion: &e" + plugin.getVersion()+" &cUpdate available!");
            }



        }else if(args[0].equalsIgnoreCase("check")) {
            if(args.length < 2) {
                if(!sender.hasPermission("timeChecker.check")) {
                    send(sender, noPerm);
                    return true;
                }
                if(sender instanceof ConsoleCommandSender) {
                    send(sender, cannotCheckConsole);
                    return true;
                }

                //Check self time
                int ticks = ((Player) sender).getStatistic(Statistic.PLAY_ONE_MINUTE);

                send(sender, selfCheck.replace("%time%", getTime(ticks)));

            } else {
                if(!sender.hasPermission("timeChecker.check.others")) {
                    send(sender, noPerm);
                    return true;
                }

                OfflinePlayer toCheck = Bukkit.getOfflinePlayer(args[1]);

                if(toCheck.hasPlayedBefore() || toCheck.isOnline()) {
                    int ticks = toCheck.getStatistic(Statistic.PLAY_ONE_MINUTE);

                    send(sender, otherCheck.replace("%player%", args[1]).replace("%time%", getTime(ticks)));

                } else {
                    send(sender, notExist);
                }
            }



        }else if(args[0].equalsIgnoreCase("session")) {
            FileConfiguration players = plugin.getPlayersYaml().getAccess();

            if(args.length < 2) {
                if(!sender.hasPermission("timeChecker.session")) {
                    send(sender, noPerm);
                    return true;
                }
                if(sender instanceof ConsoleCommandSender) {
                    send(sender, cannotCheckConsole);
                    return true;
                }
                if(!players.contains("players." + sender.getName())) {
                    send(sender, errorCheckingSession);
                    return true;
                }

                //Check self session time
                long ticks = (System.currentTimeMillis() - players.getLong("players." + sender.getName())) / 50;

                send(sender, selfSessionCheck.replace("%time%", getTime(ticks)));

            } else {
                if(!sender.hasPermission("timeChecker.session.others")) {
                    send(sender, noPerm);
                    return true;
                }

                Player toCheck = Bukkit.getPlayer(args[1]);

                if(toCheck != null) {

                    if(!players.contains("players." + args[1])) {
                        send(sender, errorCheckingSession);
                        return true;
                    }
                    long ticks = (System.currentTimeMillis() - players.getLong("players." + toCheck.getName())) / 50;

                    send(sender, otherSessionCheck.replace("%player%", args[1]).replace("%time%", getTime(ticks)));

                } else {
                    send(sender, notExist);
                }
            }

        }else if(args[0].equalsIgnoreCase("top")){
            if(!sender.hasPermission("timeChecker.top")){
                send(sender, noPerm);
                return true;
            }
            send(sender, calculating);

            CompletableFuture.supplyAsync(this::getTop)
                    .thenAcceptAsync(k -> sendTop(sender, k));


            //unknown command
        }else {
            send(sender, unknown.replace("%command%", label));
        }



        return true;
    }

    /**
     * Gets an descending ordered map containing the top 10 players with the most playtime.
     * @return The ordered map to be displayed elsewhere.
     */
    private LinkedHashMap<OfflinePlayer, String> getTop(){
        Map<OfflinePlayer, Integer> allPlayers = new HashMap<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            allPlayers.put(player, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
        }

        LinkedHashMap<OfflinePlayer, Integer> sortedMap = allPlayers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        LinkedHashMap<OfflinePlayer, String> top = new LinkedHashMap<>();
        List<OfflinePlayer> players = new ArrayList<>(sortedMap.keySet());

        for(int i = sortedMap.size() - 1 ; i >= Math.max(0, sortedMap.size()-amountTop); i--) {
            OfflinePlayer player = players.get(i);
            top.put(player, getTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)));
        }

        return top;
    }

    /**
     * Sends a given map of players and strings to the commandSender.
     * @param topMap Said map.
     */
    private void sendTop(CommandSender sender, Map<OfflinePlayer, String> topMap){
        send(sender, topList);
        int j = 1;

        for(OfflinePlayer player : topMap.keySet()){
            send(sender, topPlayer.replace("%player%", player.getName()+"").replace("%time%", topMap.get(player)).replace("%pos%", String.valueOf(j)));
            j++;
        }
    }
}
