package net.flytre.aim_plus.mixin;


import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Accessor("thirdPerson")
    void setThirdPerson(boolean val);
}
