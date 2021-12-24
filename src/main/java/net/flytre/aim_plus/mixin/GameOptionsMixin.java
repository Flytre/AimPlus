package net.flytre.aim_plus.mixin;

import net.flytre.aim_plus.AimPlus;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {


    @Inject(method = "write", at = @At("HEAD"))
    public void aim_plus$write(CallbackInfo ci) {
        AimPlus.CONFIG.save(AimPlus.CONFIG.getConfig());
    }

}
