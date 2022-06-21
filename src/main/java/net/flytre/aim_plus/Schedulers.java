package net.flytre.aim_plus;

import net.flytre.aim_plus.scope.ScopeStates;
import net.flytre.aim_plus.scope.TemporaryState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Schedulers {

    public static final TemporaryState<ScopeStates> SCOPE_STATE = new TemporaryState<>();
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newScheduledThreadPool(2);
    public static int targetSwitchCooldown = -1;
    public static Entity lastTargetedEntity;

    private static float totalPitch;
    private static float totalYaw;

    public static void startClientTick() {

        if (Logic.TARGETED_ENTITY != null && Logic.TARGETED_ENTITY.getLeft() != null && Logic.TARGETED_ENTITY.getRight() != null) {

            boolean bl = !AimPlus.CONFIG.getConfig().targetSwitchCooldownEnabled;

            if (Logic.TARGETED_ENTITY.getLeft() != lastTargetedEntity) {
                if (targetSwitchCooldown > 0) {
                    targetSwitchCooldown--;
                } else if (targetSwitchCooldown == -1 && !bl) {
                    targetSwitchCooldown = ThreadLocalRandom.current().nextInt(3, 5);
                } else {
                    bl = true;
                }
            }

            bl |= Logic.TARGETED_ENTITY.getLeft() == lastTargetedEntity;

            if (bl) {
                totalPitch = (float) Math.min(ThreadLocalRandom.current().nextDouble(30, 60) * 0.05f, Logic.TARGETED_ENTITY.getRight().y * ThreadLocalRandom.current().nextDouble(0.9, 1.1));
                totalYaw = (float) Math.min(ThreadLocalRandom.current().nextDouble(30, 60), Logic.TARGETED_ENTITY.getRight().x * ThreadLocalRandom.current().nextDouble(0.9, 1.1));
                final ScheduledFuture<?> scheduled2 =
                        SCHEDULER.scheduleAtFixedRate(Schedulers::runClientTick, 1, 1, MILLISECONDS);
                SCHEDULER.schedule(() -> {
                    scheduled2.cancel(false);
                    lastTargetedEntity = Logic.TARGETED_ENTITY.getLeft();
                }, 50, MILLISECONDS);
            }
        }
    }

    private static void runClientTick() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return;
        player.changeLookDirection(totalYaw / 50 * 1 / 0.15, totalPitch / 50 * 1 / 0.15);
        targetSwitchCooldown = -1;
    }

}
