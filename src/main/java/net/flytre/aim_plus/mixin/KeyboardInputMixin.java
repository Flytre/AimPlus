package net.flytre.aim_plus.mixin;


import net.flytre.aim_plus.AimPlus;
import net.flytre.aim_plus.Logic;
import net.flytre.aim_plus.config.Config;
import net.flytre.aim_plus.scope.ScopeStates;
import net.flytre.aim_plus.scope.TemporaryState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.flytre.aim_plus.Schedulers.SCOPE_STATE;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(method = "tick", at = @At("TAIL"))
    public void aim_plus$autoSneak(boolean slowDown, CallbackInfo ci) {
        Config config = AimPlus.CONFIG.getConfig();
        PlayerEntity player = MinecraftClient.getInstance().player;


        if (config.enabled && player != null) {
            if (Logic.TARGETED_ENTITY != null && Logic.TARGETED_ENTITY.getLeft() != null && (config.sniperMode || player.distanceTo(Logic.TARGETED_ENTITY.getLeft()) > config.crouchThreshold) && AimPlus.ticksToShot < 6) {

                if (SCOPE_STATE.getState() == ScopeStates.NO_TARGET)
                    SCOPE_STATE.enterState(ScopeStates.UNSCOPED, TemporaryState.UntilDateRule.randomMillisecondsFromNow(100, 200));


                if (!SCOPE_STATE.isInState())
                    SCOPE_STATE.enterState(ScopeStates.SCOPED, TemporaryState.UntilDateRule.randomMillisecondsFromNow(100, 200));

            } else {

                if (!SCOPE_STATE.isInState())
                    SCOPE_STATE.enterState(ScopeStates.NO_TARGET, new TemporaryState.InfiniteTimeRule());
            }

            if (SCOPE_STATE.getState() == ScopeStates.SCOPED || SCOPE_STATE.getState() == ScopeStates.UNSCOPED)
                this.sneaking = this.sneaking || SCOPE_STATE.getState() == ScopeStates.SCOPED;

            if (config.sniperMode && Logic.TARGETED_ENTITY != null && Logic.TARGETED_ENTITY.getLeft() != null && AimPlus.ticksToShot < 3) {
                this.pressingForward = false;
                this.pressingBack = false;
                this.pressingLeft = false;
                this.pressingRight = false;
                this.movementForward = 0;
                this.movementSideways = 0;
                this.jumping = false;
            }

        }
    }
}
