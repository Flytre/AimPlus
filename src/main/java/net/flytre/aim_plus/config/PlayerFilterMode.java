package net.flytre.aim_plus.config;

import net.flytre.aim_plus.AimPlus;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.function.Predicate;

public enum PlayerFilterMode {

    WHITELIST("whitelist", i -> AimPlus.CONFIG.getConfig().canTargetPlayer.getOrDefault(i.getGameProfile().getName(),true)),
    ARMOR_COLOR("armor_color", i -> {
        ItemStack chestSlot = i.getEquippedStack(EquipmentSlot.CHEST);
        if (chestSlot.isEmpty() || !(chestSlot.getItem() instanceof DyeableItem))
            return true;
        int color = ((DyeableItem) chestSlot.getItem()).getColor(chestSlot);
        return !(AimPlus.CONFIG.getConfig().blacklistedColor == Colors.best(new Color(color)));
    });

    private final String name;
    private final Predicate<PlayerEntity> shouldTrack;


    PlayerFilterMode(String name, Predicate<PlayerEntity> shouldTrack) {
        this.name = name;
        this.shouldTrack = shouldTrack;
    }

    public String getKey() {
        return "aim_plus.filter_mode." + name;
    }

    public boolean test(PlayerEntity player) {
        return shouldTrack.test(player);
    }

}
