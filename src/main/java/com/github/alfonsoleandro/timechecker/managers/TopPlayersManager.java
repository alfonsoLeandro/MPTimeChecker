package com.github.alfonsoleandro.timechecker.managers;

import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.time.TimeUtils;
import com.github.alfonsoleandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class TopPlayersManager extends Reloadable {

    private final TimeChecker plugin;
    private final MessageSender<TimeChecker.Message> messageSender;
    private final LinkedHashMap<OfflinePlayer, String> topPlayers = new LinkedHashMap<>();
    private final LinkedHashMap<OfflinePlayer, String> worstPlayers = new LinkedHashMap<>();
    private int amountTop;
    private int amountWorst;

    public TopPlayersManager(TimeChecker plugin){
        super(plugin);
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
        generateTops();
    }

    private void generateTops(){
        Bukkit.broadcastMessage("CALCULATING TOPS");
        new BukkitRunnable() {
            @Override
            public void run() {
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
                Bukkit.broadcastMessage("TOPS CALCULATED");

            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Sends a top of the best players by playtime.
     */
    public void sendTop(CommandSender sender) {
        this.messageSender.send(sender, TimeChecker.Message.TOP_LIST,
                "%amounttop%", String.valueOf(amountTop));
        int j = 1;

        for (OfflinePlayer player : topPlayers.keySet()) {
            this.messageSender.send(sender, TimeChecker.Message.TOP_PLAYER,
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
        this.messageSender.send(sender, TimeChecker.Message.WORST_LIST,
                "%amountworst%", String.valueOf(amountWorst));
        int j = 1;

        for (OfflinePlayer player : worstPlayers.keySet()) {
            this.messageSender.send(sender, TimeChecker.Message.TOP_PLAYER,
                    "%player%", player.getName() + "",
                    "%time%", worstPlayers.get(player),
                    "%pos%", String.valueOf(j));
            j++;
        }

    }

    /**
     * Translates and amount of ticks into days, hours and minutes.
     * @param ticks The amount of ticks to translate
     * @return A string with an h,m and s format.
     */
    public String getTime(long ticks){
        return TimeUtils.getTimeString(ticks)
                .replace("%weeks%", " "+messageSender.getString(TimeChecker.Message.WEEKS))
                .replace("%week%", " "+messageSender.getString(TimeChecker.Message.WEEK))
                .replace("%days%", " "+messageSender.getString(TimeChecker.Message.DAYS))
                .replace("%day%", " "+messageSender.getString(TimeChecker.Message.DAY))
                .replace("%hours%", " "+messageSender.getString(TimeChecker.Message.HOURS))
                .replace("%hour%", " "+messageSender.getString(TimeChecker.Message.HOUR))
                .replace("%minutes%", " "+messageSender.getString(TimeChecker.Message.MINUTES))
                .replace("%minute%", " "+messageSender.getString(TimeChecker.Message.MINUTE))
                .replace("%seconds%", " "+messageSender.getString(TimeChecker.Message.SECONDS))
                .replace("%second%", " "+messageSender.getString(TimeChecker.Message.SECOND))
                .replace("%and%", " "+messageSender.getString(TimeChecker.Message.AND));
    }


    @Override
    public void reload(boolean deep) {
        this.amountTop = plugin.getConfigYaml().getAccess().getInt("config.amount top");
        this.amountWorst = plugin.getConfigYaml().getAccess().getInt("config.amount worst");
        generateTops();
    }

}
