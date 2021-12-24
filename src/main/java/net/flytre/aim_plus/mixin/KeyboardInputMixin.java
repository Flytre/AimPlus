package net.flytre.aim_plus.mixin;


import net.flytre.aim_plus.AimPlus;
import net.flytre.aim_plus.Logic;
import net.flytre.aim_plus.Schedulers;
import net.flytre.aim_plus.config.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At("TAIL"))
    public void aim_plus$autoSneak(boolean slowDown, CallbackInfo ci) {
        Config config = AimPlus.CONFIG.getConfig();
        PlayerEntity player = MinecraftClient.getInstance().player;
        Schedulers.ScopeState state = Schedulers.scopeState;


        if (config.enabled && player != null) {
            if (Logic.TARGETED_ENTITY != null && Logic.TARGETED_ENTITY.getLeft() != null && player.distanceTo(Logic.TARGETED_ENTITY.getLeft()) > config.crouchThreshold) {

                if (state.enabled() == null) {
                    state = new Schedulers.ScopeState(ThreadLocalRandom.current().nextInt(100, 200 + 1), false, true);
                } else {
                    if (!state.enabled() && state.cooldown() <= 0)
                        state = new Schedulers.ScopeState(ThreadLocalRandom.current().nextInt(100, 200 + 1), true, false);

                    this.sneaking = state.enabled();
                }
            } else if (state.enabled() != null) {
                state = state.withShouldDecrement(true);
                this.sneaking = state.enabled();
                if (state.cooldown() <= 0)
                    state = new Schedulers.ScopeState(0, null, false);
            }

        }

        Schedulers.scopeState = state;

    }
}
