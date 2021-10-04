package com.github.alfonsoleandro.timechecker.utils;

import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.timechecker.TimeChecker;
import com.github.alfonsoleandro.timechecker.managers.TopPlayersManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PAPIPlaceholder extends PlaceholderExpansion {

    private final TimeChecker plugin;
    private final TopPlayersManager topPlayersManager;
    private final MessageSender<Message> messageSender;

    public PAPIPlaceholder(TimeChecker plugin){
        this.plugin = plugin;
        this.topPlayersManager = plugin.getTopPlayersManager();
        this.messageSender = plugin.getMessageSender();
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
        identifier = identifier.toLowerCase(Locale.ENGLISH);


        // %MPTimeChecker_check%
        if(identifier.equalsIgnoreCase("check")){
            return this.topPlayersManager.getTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE));

            //%MPTimeChecker_session%
        }else if(identifier.equalsIgnoreCase("session")) {
            FileConfiguration players = plugin.getPlayersYaml().getAccess();

            if(!players.contains("players." + player.getName()))
                return plugin.getConfigYaml().getAccess().getString("config.messages.error checking session");

            //Get session ticks
            long ticks = (System.currentTimeMillis() - players.getLong("players." + player.getName())) / 50;

            return this.topPlayersManager.getTime(ticks);


        }else if(identifier.contains("name_top")){
            int place;
            try {
                place = Integer.parseInt(identifier.replace("name_top", ""));
            }catch (NumberFormatException e){
                return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
            }
            OfflinePlayer bPlayer = topPlayersManager.getTopPlayer(place);

            if(bPlayer == null) return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);

            return bPlayer.getName();

        }else if(identifier.contains("time_top")){
            int place;
            try {
                place = Integer.parseInt(identifier.replace("time_top", ""));
            }catch (NumberFormatException e){
                return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
            }
            OfflinePlayer bPlayer = topPlayersManager.getTopPlayer(place);

            if(bPlayer == null) return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);

            return this.topPlayersManager.getTopTime(bPlayer);


        }else if(identifier.contains("top")){
            int place;
            try {
                place = Integer.parseInt(identifier.replace("top", ""));
            }catch (NumberFormatException e){
                return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
            }
            OfflinePlayer bPlayer = topPlayersManager.getTopPlayer(place);

            if(bPlayer == null) return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);

            return this.messageSender.getString(Message.TOP_PLAYER,
                    "%pos%", String.valueOf(place),
                    "%player%", bPlayer.getName(),
                    "%time%", this.topPlayersManager.getTopTime(bPlayer));


        }else if(identifier.contains("name_worst")){
            int place;
            try {
                place = Integer.parseInt(identifier.replace("name_worst", ""));
            }catch (NumberFormatException e){
                return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
            }
            OfflinePlayer wPlayer = topPlayersManager.getWorstPlayer(place);

            if(wPlayer == null) return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);

            return wPlayer.getName();


        }else if(identifier.contains("time_worst")){
            int place;
            try {
                place = Integer.parseInt(identifier.replace("time_worst", ""));
            }catch (NumberFormatException e){
                return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
            }
            OfflinePlayer wPlayer = topPlayersManager.getTopPlayer(place);

            if(wPlayer == null) return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);

            return this.topPlayersManager.getWorstTime(wPlayer);


        }else if(identifier.contains("worst")){
            int place;
            try {
                place = Integer.parseInt(identifier.replace("worst", ""));
            }catch (NumberFormatException e){
                return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);
            }
            OfflinePlayer bPlayer = topPlayersManager.getWorstPlayer(place);

            if(bPlayer == null) return this.messageSender.getString(Message.ERROR_WHILE_GETTING_PLAYER);

            return this.messageSender.getString(Message.TOP_PLAYER,
                    "%pos%", String.valueOf(place),
                    "%player%", bPlayer.getName(),
                    "%time%", this.topPlayersManager.getWorstTime(bPlayer));


        }

        // We return null if an invalid placeholder (f.e. %somePlugin_placeholder3%)
        // was provided
        return null;
    }
}

