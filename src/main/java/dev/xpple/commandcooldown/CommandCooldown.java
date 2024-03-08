package dev.xpple.commandcooldown;

import com.google.common.cache.CacheBuilder;
import dev.xpple.betterconfig.api.ModConfigBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommandCooldown implements DedicatedServerModInitializer {
    private static final Map<UUID, Set<String>> activeCooldowns = new HashMap<>();

    @Override
    public void onInitializeServer() {
        new ModConfigBuilder("commandcooldown", Configs.class).build();
    }

    public static boolean checkCooldown(UUID uuid, String command) {
        Integer cooldown = Configs.cooldowns.get(command);
        if (cooldown == null) {
            return true;
        }

        Set<String> cooldowns = activeCooldowns.putIfAbsent(uuid, createExpiringSet(command, cooldown));
        if (cooldowns == null) {
            return true;
        }

        if (cooldowns.contains(command)) {
            return false;
        }
        cooldowns.add(command);
        return true;
    }

    private static Set<String> createExpiringSet(String command, int cooldown) {
        Set<String> cooldowns = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(cooldown)).<String, Boolean>build().asMap());
        cooldowns.add(command);
        return cooldowns;
    }
}
