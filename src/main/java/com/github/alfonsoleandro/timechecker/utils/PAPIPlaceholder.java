package com.github.alfonsoleandro.timechecker.utils;

import com.github.alfonsoleandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.timechecker.managers.TopPlayersManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIPlaceholder extends PlaceholderExpansion {

    private final TimeChecker plugin;
    private final TopPlayersManager topPlayersManager;

    public PAPIPlaceholder(TimeChecker plugin){
        this.plugin = plugin;
        this.topPlayersManager = plugin.getTopPlayersManager();
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
            return this.topPlayersManager.getTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));

            //%MPTimeChecker_session%
        }else if(identifier.equalsIgnoreCase("session")){
            FileConfiguration players = plugin.getPlayersYaml().getAccess();

            if(!players.contains("players." + player.getName()))
                return plugin.getConfigYaml().getAccess().getString("config.messages.error checking session");

            //Get session ticks
            long ticks = (System.currentTimeMillis() - players.getLong("players." + player.getName())) / 50;

            return this.topPlayersManager.getTime(ticks);

        }else if(identifier.contains("TOP")){
            int place = Integer.parseInt(identifier.replace("TOP", ""));
            return this.topPlayersManager.getTopTime(place);

        }else if(identifier.contains("WORST")){
            int place = Integer.parseInt(identifier.replace("WORST", ""));
            return this.topPlayersManager.getWorstTime(place);


        }

        // We return null if an invalid placeholder (f.e. %somePlugin_placeholder3%)
        // was provided
        return null;
    }
}

