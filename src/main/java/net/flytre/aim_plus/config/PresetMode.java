package net.flytre.aim_plus.config;

import net.flytre.aim_plus.AimPlus;
import net.flytre.flytre_lib.api.config.annotation.MemberLocalizationFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Predicate;

public enum PresetMode {
    MOBS("mobs", i -> i instanceof MobEntity),
    MONSTERS("monsters", i -> i instanceof Monster),
    PLAYERS("players", i -> i instanceof PlayerEntity && AimPlus.CONFIG.getConfig().playerFilterMode.test((PlayerEntity) i)),
    CUSTOM("custom", i -> AimPlus.CONFIG.getConfig().isWhitelisted(i.getType()) && (!(i instanceof PlayerEntity) || PLAYERS.test(i)));

    private final String name;
    private final Predicate<Entity> predicate;

    PresetMode(String name, Predicate<Entity> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    public boolean test(Entity e) {
        return predicate.test(e);
    }

    @MemberLocalizationFunction
    public String getKey() {
        return "aim_plus.mode." + name;
    }
}
