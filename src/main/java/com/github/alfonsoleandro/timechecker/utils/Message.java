package com.github.alfonsoleandro.timechecker.utils;

import com.github.alfonsoleandro.mputils.misc.MessageEnum;

public enum Message implements MessageEnum {
    NO_PERMISSION("&cNo permission"),
    UNKNOWN_COMMAND("&cUnknown command"),
    RELOADED("&aPlugin reloaded!"),
    CANNOT_CHECK_CONSOLE("&cCannot check console"),
    NOT_EXIST("&cThat player does not exist"),
    SELF_CHECK("&fYou have been playing for: &e%time%"),
    OTHER_CHECK("&f%player% has been playing in this server for &e%time%"),
    ERROR_CHECKING_SESSION("&cThere has been an error while checking for %player%'s session"),
    SELF_SESSION_CHECK("&fThis session: &e%time%"),
    OTHER_SESSION_CHECK("&f%player%'s session: &e%time%"),
    TOP_LIST("&6&lTOP %amounttop% players by playtime"),
    WORST_LIST("&6&lWORST %amounttop% players by playtime"),
    TOP_PLAYER("&f%pos%) %player%: &c%time%"),
    ERROR_WHILE_GETTING_PLAYER("&cThere has been an error while calculating a player for the top."),
    RECALCULATING_TOPS("&cThere has been an error while calculating a player for the top."),
    WEEKS("weeks"),
    WEEK("week"),
    DAYS("days"),
    DAY("day"),
    HOURS("hours"),
    HOUR("hour"),
    MINUTES("minutes"),
    MINUTE("minute"),
    SECONDS("seconds"),
    SECOND("second"),
    AND("and");


    private final String dflt;

    Message(String dflt){
        this.dflt = dflt;
    }

    @Override
    public String getDefault() {
        return this.dflt;
    }
}