package com.github.alfonsoleandro.timechecker.utils;

import com.github.alfonsoleandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.mputils.time.TimeUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PAPIPlaceholder extends PlaceholderExpansion {

    private final TimeChecker plugin;

    public PAPIPlaceholder(TimeChecker plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public @NotNull String getIdentifier(){
        return "MPTimeChecker";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){
        if(player == null){
            return "";
        }


        // %MPTimeChecker_check%
        if(identifier.equalsIgnoreCase("check")){
            return getTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));

        //%MPTimeChecker_session%
        }else if(identifier.equalsIgnoreCase("session")){
            FileConfiguration players = plugin.getPlayersYaml().getAccess();

            if(!players.contains("players." + player.getName()))
                return plugin.getConfigYaml().getAccess().getString("config.messages.error checking session");

            //Get session ticks
            long ticks = (System.currentTimeMillis() - players.getLong("players." + player.getName())) / 50;

            return getTime(ticks);

        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
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
}