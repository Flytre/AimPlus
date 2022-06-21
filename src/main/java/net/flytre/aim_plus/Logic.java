package net.flytre.aim_plus;

import net.flytre.aim_plus.config.Config;
import net.flytre.aim_plus.mixin.CameraAccessor;
import net.flytre.flytre_lib.api.base.util.EntityUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class Logic {
    private static final List<Entity> USEFUL_ENTITIES = new ArrayList<>();
    public static Pair<Entity, Vec2f> TARGETED_ENTITY;

    /**
     * Get the optimal change in yaw and pitch the source needs to do to face the target
     */
    public static Vec2f getOptimalVector(Entity source, Entity target, boolean actual) {
        MinMaxVec2f mm2f = getLookingNeeded(source, target, actual);
        float minYaw = MathHelper.wrapDegrees(mm2f.min.x), maxYaw = MathHelper.wrapDegrees(mm2f.max.x), minPitch = MathHelper.wrapDegrees(mm2f.min.y), maxPitch = MathHelper.wrapDegrees(mm2f.max.y);

        float sourceYaw = MathHelper.wrapDegrees(source.getYaw()), sourcePitch = MathHelper.wrapDegrees(source.getPitch());


        float deltaPitch, deltaYaw;

        boolean betweenYaw = minYaw < maxYaw ? (sourceYaw >= minYaw && sourceYaw <= maxYaw) : (sourceYaw >= minYaw || sourceYaw <= maxYaw);
        boolean betweenPitch = sourcePitch >= minPitch && sourcePitch <= maxPitch;

        if (!betweenYaw) {
            float distMin = MathHelper.abs(MathHelper.wrapDegrees(minYaw - sourceYaw));
            float distMax = MathHelper.abs(MathHelper.wrapDegrees(maxYaw - sourceYaw));
            deltaYaw = distMin < distMax ? MathHelper.wrapDegrees(minYaw - sourceYaw) : MathHelper.wrapDegrees(maxYaw - sourceYaw);
        } else
            deltaYaw = 0.0F;

        if (!betweenPitch) {
            float distMin = MathHelper.abs(MathHelper.wrapDegrees(minPitch - sourcePitch));
            float distMax = MathHelper.abs(MathHelper.wrapDegrees(maxPitch - sourcePitch));
            deltaPitch = distMin < distMax ? MathHelper.wrapDegrees(minPitch - sourcePitch) : MathHelper.wrapDegrees(maxPitch - sourcePitch);
        } else
            deltaPitch = 0.0F;

        return new Vec2f(deltaYaw, deltaPitch);

    }

    private static float horizontalDistanceTo(Entity entity, Entity entity2) {
        float f = (float) (entity2.getX() - entity.getX());
        float h = (float) (entity2.getZ() - entity.getZ());
        return MathHelper.sqrt(f * f + h * h);
    }


    /**
     * Get the yaw and pitch values needed to face towards the target from the source
     */
    public static Vec2f getYawPitchBetween(Vec3d source, Vec3d target) {


        Vec3d diff = target.subtract(source);

        double dist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);

        float yaw = (float) ((Math.atan2(diff.z, diff.x) * 180.0D / Math.PI) - 90.0F);
        float pitch = (float) -(Math.atan2(diff.y, dist) * 180.0D / Math.PI);

        return new Vec2f(yaw, pitch);
    }

    public static MinMaxVec2f getLookingNeeded(Entity source, Entity target, boolean actual) {
        Config config = AimPlus.CONFIG.getConfig();

        double dist = horizontalDistanceTo(source, target);

        double leanTicks = dist / config.bulletSpeed.getSpeed();

        Vec3d sourceVector = source.getPos().add(0, source.getEyeHeight(source.getPose()), 0);
        Vec2f min = new Vec2f(1000, 1000), max = new Vec2f(-1000, -1000);

        Box targetBox = target.getBoundingBox();
        targetBox = targetBox.offset(target.getVelocity().multiply(leanTicks));


        double heightInc = actual ? config.dropoffAdjustment.getYIncrease(dist) : 0;
        double minY = targetBox.minY + heightInc;
        double maxY = targetBox.maxY + heightInc;


        double m = MathHelper.clamp(0.2 + dist / 60 * 0.3, 0.2, 0.48);

        double minYPlus = config.headshots ? targetBox.getYLength() * 0.9 : targetBox.getYLength() * m;
        double maxYMinus = config.headshots ? targetBox.getYLength() * 0.1 : targetBox.getYLength() * m;


        for (int i = 0; i < 8; i++) {
            double x = i % 2 == 0 ? targetBox.minX + targetBox.getXLength() * m : targetBox.maxX - targetBox.getXLength() * m;
            double y = (i / 2) % 2 == 0 ? (minY + minYPlus) : (maxY - maxYMinus);

            double z = (i / 4) % 2 == 0 ? targetBox.minZ + targetBox.getZLength() * m : targetBox.maxZ - targetBox.getZLength() * m;
            Vec3d targetVector = new Vec3d(x, y, z);


            Vec2f attempt = getYawPitchBetween(sourceVector, targetVector);
            if (attempt.x < min.x)
                min = new Vec2f(attempt.x, min.y);
            if (attempt.x > max.x)
                max = new Vec2f(attempt.x, max.y);
            if (attempt.y < min.y)
                min = new Vec2f(min.x, attempt.y);
            if (attempt.y > max.y)
                max = new Vec2f(max.x, attempt.y);
        }
        return new MinMaxVec2f(min, max);
    }


    /**
     * Update the entities that are valid and visible
     */
    public static void updateVisibleEntities(MatrixStack matrices, float partialTicks, Camera camera, Matrix4f projection, Frustum capturedFrustum) {
        MinecraftClient client = MinecraftClient.getInstance();
        final Entity cameraEntity = camera.getFocusedEntity() != null ? camera.getFocusedEntity() : client.player; //possible fix for Optifine
        assert cameraEntity != null : "Camera Entity must not be null!";
        Vec3d cameraPos = camera.getPos();
        boolean val = camera.isThirdPerson();
        ((CameraAccessor) camera).setThirdPerson(true);
        final Frustum frustum;
        if (capturedFrustum != null) {
            frustum = capturedFrustum;
        } else {
            frustum = new Frustum(matrices.peek().getPositionMatrix(), projection);
            frustum.setPosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
        }
        USEFUL_ENTITIES.clear();
        StreamSupport
                .stream(client.world.getEntities().spliterator(), false)
                .filter(entity -> entity instanceof LivingEntity &&
                        entity != cameraEntity &&
                        entity.isAlive() &&
                        entity.shouldRender(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ()) &&
                        (entity.ignoreCameraFrustum || frustum.isVisible(entity.getBoundingBox()) || entity.distanceTo(cameraEntity) < 6) &&
                        !entity.isInvisible())
                .forEach(USEFUL_ENTITIES::add);
        ((CameraAccessor) camera).setThirdPerson(val);
    }


    /**
     * Get the rotation vector for a given pitch and yaw
     */
    public static Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    public static void updateTargetedEntity() {
        TARGETED_ENTITY = getTargetEntity();
    }


    /**
     * "MAIN" method to find the target entity and the change in yaw and pitch from the player
     */
    public static Pair<Entity, Vec2f> getTargetEntity() {
        Config config = AimPlus.CONFIG.getConfig();
        ClientPlayerEntity source = MinecraftClient.getInstance().player;

        float minDist = Float.MAX_VALUE;
        Entity closest = null;
        Vec2f finalF = null;


        if (source == null)
            return new Pair<>(null, null);

        for (Entity entity : USEFUL_ENTITIES) {
            if (!config.mode.test(entity))
                continue;

            Vec2f acc = getOptimalVector(source, entity, true);
            Vec2f ideal = getOptimalVector(source, entity, false);

            if (Math.abs(acc.x) > config.manualPrecision.getVal() || Math.abs(acc.y) > config.manualPrecision.getVal() * 1.5)
                continue;

            Vec2f current = source.getRotationClient();
            source.setYaw(source.getYaw() + ideal.x);
            source.setPitch(source.getPitch() + ideal.y);
            Set<Entity> result = EntityUtils.getEntitiesLookedAt(source, config.maxDistance,config.ignoredBlocks);
            source.setYaw(current.y);
            source.setPitch(current.x);

            if (!result.contains(entity))
                continue;

            float dist = MathHelper.sqrt(ideal.x * ideal.x + ideal.y * ideal.y);

            if (config.useDistanceAsPriorityFactor)
                dist += source.distanceTo(entity);

            float compareDist = dist;
            if (config.useHealthAsPriorityFactor && entity instanceof LivingEntity && closest instanceof LivingEntity) {


                double percent = ((LivingEntity) entity).getHealth() / ((LivingEntity) closest).getHealth();
                compareDist = (float) MathHelper.clamp(1 + (percent - 1) / 1.8f, 0.25f, 4f);
            }

            if (config.sniperMode && source.distanceTo(entity) < 150) //favor nearby enemies quite strongly
                compareDist *= 0.3;

            if (Logic.TARGETED_ENTITY != null && entity == Logic.TARGETED_ENTITY.getLeft()) {
                dist *= config.targetSwitchCooldownEnabled ? 0.2 : 0.85;
            }

            if (compareDist < minDist) {
                closest = entity;
                minDist = dist;
                finalF = acc;
            }
        }

        return new Pair<>(closest, finalF);
    }


    private record MinMaxVec2f(Vec2f min, Vec2f max) {

    }
}
