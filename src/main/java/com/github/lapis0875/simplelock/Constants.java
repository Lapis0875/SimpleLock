package com.github.lapis0875.simplelock;

import net.kyori.adventure.text.format.TextColor;

public class Constants {
    public static final String PLUGIN_NAME = "";
    // Commands
    public static final String CMD_LOCK = "lock";
    public static final String CMD_UNLOCK = "unlock";
    public static final String CMD_ALLOW = "allow";
    public static final String CMD_DENY = "deny";
    public static final String CMD_LOCK_VIEW = "view";
    // Lock Meta
    public static final String LOCK_KEY = "objectlock";
    public static final String LOCK_OWNER_NAME = "ownerName";
    public static final String LOCK_OWNER_UUID = "ownerUUID";
    public static final String LOCK_ALLOWED_PLAYERS = "allowedPlayers";
    public static final String ALLOWED_PLAYER_BASE = "player";
    public static String ALLOWED_PLAYER(int index) {
        return ALLOWED_PLAYER_BASE + index;
    }
    // Colors
    public static final TextColor INFO = TextColor.color(171, 242, 0);
    public static final TextColor ERROR = TextColor.color(200, 0, 0);
}
