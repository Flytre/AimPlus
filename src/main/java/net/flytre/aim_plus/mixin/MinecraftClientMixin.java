package net.flytre.aim_plus.mixin;


import net.flytre.aim_plus.AimPlus;
import net.flytre.aim_plus.config.Config;
import net.flytre.aim_plus.Logic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow
    protected abstract void doItemUse();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V"))
    public void aim_plus$useItemIfAimed(CallbackInfo ci) {
        Config config = AimPlus.CONFIG.getConfig();
        if (!this.options.keyUse.isPressed() && config.enabled && config.autoFire && this.itemUseCooldown == 0 && player != null && !player.isUsingItem()) {

            if (config.onlyShootOnceScoped && AimPlus.sneakTime < 3)
                return;

            if (Logic.TARGETED_ENTITY != null && Logic.TARGETED_ENTITY.getRight() != null && Logic.TARGETED_ENTITY.getRight().lengthSquared() < 18)
                this.doItemUse();
        }
    }
}
