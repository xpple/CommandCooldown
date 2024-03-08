package dev.xpple.commandcooldown;

import dev.xpple.betterconfig.api.Config;

import java.util.HashMap;
import java.util.Map;

public class Configs {
    @Config
    public static Map<String, Integer> cooldowns = new HashMap<>();
}
