package net.flytre.aim_plus.mixin;


import net.flytre.aim_plus.AimPlus;
import net.flytre.aim_plus.Logic;
import net.flytre.aim_plus.config.Config;
import net.flytre.aim_plus.config.FirePattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {


    @Shadow
    @Final
    public GameOptions options;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    private int itemUseCooldown;
    @Unique
    private int ticksSinceShot = 0;

    @Unique
    private int burstBullets = 0;

    @Shadow
    protected abstract void doItemUse();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V"))
    public void aim_plus$useItemIfAimed(CallbackInfo ci) {
        ticksSinceShot++;
        AimPlus.ticksToShot--;

        Config config = AimPlus.CONFIG.getConfig();

        if (config.firePattern == FirePattern.BURST) {
            if (ticksSinceShot >= 6 && burstBullets < 3)
                burstBullets = 0;
            if (ticksSinceShot >= 10)
                burstBullets = 0;
        }

        if (!this.options.useKey.isPressed() && config.enabled && config.autoFire && this.itemUseCooldown == 0 && player != null && !player.isUsingItem()) {

            if (config.sniperMode && AimPlus.sneakTime < config.scopeAnimationTicks)
                return;


            if (Logic.TARGETED_ENTITY != null && Logic.TARGETED_ENTITY.getRight() != null && Logic.TARGETED_ENTITY.getRight().lengthSquared() <= (config.sniperMode ? 0.01 : 18)) {
                boolean fire = AimPlus.ticksToShot <= 0;

                if (config.sniperMode && player.getVelocity().lengthSquared() > 0.5)
                    fire = false;

                if (fire && config.firePattern == FirePattern.BURST) {
                    if (burstBullets < 3)
                        burstBullets++;
                    else
                        fire = false;
                }

                if (fire) {
                    this.doItemUse();
                    ticksSinceShot = 0;
                    AimPlus.sneakTime = 0;
                    if (config.firePattern == FirePattern.RPS_1)
                        AimPlus.ticksToShot = 20;
                    if (config.firePattern == FirePattern.RPS_1_5)
                        AimPlus.ticksToShot = 13;
                    if (config.firePattern == FirePattern.RPS_0_8)
                        AimPlus.ticksToShot = 25;
                    if (config.firePattern == FirePattern.BURST && burstBullets >= 3)
                        AimPlus.ticksToShot = 16;
                }
            }
        }
    }
}
