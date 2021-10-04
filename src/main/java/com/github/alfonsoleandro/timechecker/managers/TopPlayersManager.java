package com.github.alfonsoleandro.timechecker.managers;

import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.time.TimeUtils;
import com.github.alfonsoleandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.github.alfonsoleandro.timechecker.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class TopPlayersManager extends Reloadable {

    private final TimeChecker plugin;
    private final MessageSender<Message> messageSender;
    private final LinkedHashMap<OfflinePlayer, String> topPlayers = new LinkedHashMap<>();
    private final LinkedHashMap<OfflinePlayer, String> worstPlayers = new LinkedHashMap<>();
    private BukkitTask topsTask;
    private int amountTop;
    private int amountWorst;
    private int ticks;

    public TopPlayersManager(TimeChecker plugin){
        super(plugin);
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
        FileConfiguration config = plugin.getConfigYaml().getAccess();
        this.amountTop = config.getInt("config.amount top");
        this.amountWorst = config.getInt("config.amount worst");
        this.ticks = TimeUtils.getTicks(config.getString("config.generate tops time"));
        if(this.ticks < 100){
            this.ticks = 100;
            messageSender.send("&cYou are generating the tops after too little time!");
            messageSender.send("&cThis will cost your server performance.");
            messageSender.send("&cPlease set generate tops time to a value larger than 5s in config. Value set to 5s");
        }
        automaticallyGenerateTops();
        generateTops();
    }

    private void automaticallyGenerateTops(){
        this.topsTask = new BukkitRunnable(){

            @Override
            public void run(){
                generateTops();
            }

        }.runTaskTimerAsynchronously(plugin, 0, this.ticks);
    }

    /**
     * Generates the top best and worst players.
     */
    private void generateTops(){
        //Grab all players and put them in a hashmap.
        Map<OfflinePlayer, Integer> allPlayers = new HashMap<>();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            allPlayers.put(player, player.getStatistic(Statistic.PLAY_ONE_MINUTE));
        }


        LinkedHashMap<OfflinePlayer, Integer> sortedMap = allPlayers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        topPlayers.clear();
        List<OfflinePlayer> players = new ArrayList<>(sortedMap.keySet());

        for (int i = sortedMap.size() - 1; i >= Math.max(0, sortedMap.size() - amountTop); i--) {
            OfflinePlayer player = players.get(i);
            topPlayers.put(player, getTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)));
        }

        worstPlayers.clear();

        for (int i = 0; i < Math.min(sortedMap.size(), amountWorst); i++) {
            OfflinePlayer player = players.get(i);
            worstPlayers.put(player, getTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)));
        }

    }



    /**
     * Sends a top of the best players by playtime.
     */
    public void sendTop(CommandSender sender) {
        this.messageSender.send(sender, Message.TOP_LIST,
                "%amounttop%", String.valueOf(amountTop));
        int j = 1;

        for (OfflinePlayer player : topPlayers.keySet()) {
            this.messageSender.send(sender, Message.TOP_PLAYER,
                    "%player%", player.getName() + "",
                    "%time%", topPlayers.get(player),
                    "%pos%", String.valueOf(j));
            j++;
        }
    }

    /**
     * Sends a top of the worst players by playtime to the given command sender.
     */
    public void sendWorst(CommandSender sender) {
        this.messageSender.send(sender, Message.WORST_LIST,
                "%amountworst%", String.valueOf(amountWorst));
        int j = 1;

        for (OfflinePlayer player : worstPlayers.keySet()) {
            this.messageSender.send(sender, Message.TOP_PLAYER,
                    "%player%", player.getName() + "",
                    "%time%", worstPlayers.get(player),
                    "%pos%", String.valueOf(j));
            j++;
        }
    }


    /**
     * Gets the top position for the given place in the best players top (by playtime).
     * @param place The place requested.
     * @return The player that is in the given place of the top, or null.
     */
    public OfflinePlayer getTopPlayer(int place){
        place = Math.min(Math.max(place, 1), amountTop-1);


        int i = 1;
        for(OfflinePlayer player : topPlayers.keySet()){
            if(i == place) return player;
            i++;
        }

        return null;
    }

    /**
     * Gets the time played for a given player in the best players top.
     * @param player The player to get the time for.
     * @return The time for the given player.
     */
    public String getTopTime(OfflinePlayer player){
        if(player == null || !this.topPlayers.containsKey(player)) {
            return messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
        }
        return this.topPlayers.get(player);
    }

    /**
     * Gets the worst position for the given place in the worst players top (by playtime).
     * @param place The place requested.
     * @return The player that is in the given place of the top, or null.
     */
    public OfflinePlayer getWorstPlayer(int place){
        place = Math.min(Math.max(place, 1), amountWorst-1);

        int i = 1;
        for(OfflinePlayer player : worstPlayers.keySet()){
            if(i == place) return player;
            i++;
        }

        return null;
    }

    /**
     * Gets the time played for a given player in the worst players top.
     * @param player The player to get the time for.
     * @return The time for the given player.
     */
    public String getWorstTime(OfflinePlayer player){
        if(player == null || !this.worstPlayers.containsKey(player)) {
            return messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
        }
        return this.worstPlayers.get(player);
    }


    /**
     * Translates and amount of ticks into days, hours and minutes.
     * @param ticks The amount of ticks to translate
     * @return A string with an h,m and s format.
     */
    public String getTime(long ticks){
        return TimeUtils.getTimeString(ticks)
                .replace("%weeks%", " "+messageSender.getString(Message.WEEKS))
                .replace("%week%", " "+messageSender.getString(Message.WEEK))
                .replace("%days%", " "+messageSender.getString(Message.DAYS))
                .replace("%day%", " "+messageSender.getString(Message.DAY))
                .replace("%hours%", " "+messageSender.getString(Message.HOURS))
                .replace("%hour%", " "+messageSender.getString(Message.HOUR))
                .replace("%minutes%", " "+messageSender.getString(Message.MINUTES))
                .replace("%minute%", " "+messageSender.getString(Message.MINUTE))
                .replace("%seconds%", " "+messageSender.getString(Message.SECONDS))
                .replace("%second%", " "+messageSender.getString(Message.SECOND))
                .replace("%and%", messageSender.getString(Message.AND));
    }

    public void reCalculateTops(){
        if(this.topsTask != null && !this.topsTask.isCancelled()){
            this.topsTask.cancel();
        }
        automaticallyGenerateTops();
    }


    @Override
    public void reload(boolean deep) {
        FileConfiguration config = plugin.getConfigYaml().getAccess();

        this.ticks = TimeUtils.getTicks(config.getString("config.generate tops time"));
        if(this.ticks < 100){
            this.ticks = 100;
            messageSender.send("&cYou are generating the tops after too little time!");
            messageSender.send("&cThis will cost your server performance.");
            messageSender.send("&cPlease set generate tops time to a value larger than 5s in config. Value set to 5s");
        }
        this.amountTop = config.getInt("config.amount top");
        this.amountWorst = config.getInt("config.amount worst");
        reCalculateTops();
    }

}
