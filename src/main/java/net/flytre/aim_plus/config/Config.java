package net.flytre.aim_plus.config;

import com.google.gson.annotations.SerializedName;
import net.flytre.aim_plus.AimPlus;
import net.flytre.flytre_lib.api.config.ConfigEventAcceptor;
import net.flytre.flytre_lib.api.config.annotation.Button;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;
import net.flytre.flytre_lib.api.config.annotation.Populator;
import net.flytre.flytre_lib.api.config.reference.block.BlockReference;
import net.flytre.flytre_lib.api.config.reference.block.ConfigBlock;
import net.flytre.flytre_lib.api.config.reference.entity.ConfigEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;
import java.util.function.BiFunction;

@DisplayName("config.aim_plus")
public class Config implements ConfigEventAcceptor {

    public boolean enabled = true;

    public PresetMode mode = PresetMode.MONSTERS;

    @SerializedName("fire_pattern")
    public FirePattern firePattern = FirePattern.AUTO;

    @SerializedName("sniper_mode")
    @Description("When enabled, requires scope, no movement, and being on target to shoot. Overrides crouch threshold as 0 and stops the user from moving when on target and able to fire.")
    public boolean sniperMode = false;

    @SerializedName("crouch_threshold")
    public int crouchThreshold = 20;

    @SerializedName("manual_precision_needed_for_aim_adjustment")
    @DisplayName("config.aim_plus.manual_precision_needed")
    public ManualPrecision manualPrecision = ManualPrecision.SOME;


    @SerializedName("player_filter_mode")
    public PlayerFilterMode playerFilterMode = PlayerFilterMode.WHITELIST;

    @SerializedName("blacklisted_color")
    public Colors blacklistedColor = Colors.RED;

    @SerializedName("max_distance")
    public double maxDistance = 80;

    @SerializedName("bullet_speed")
    @Description("How much lean to add to shots to account for bullet speed")
    public BulletSpeed bulletSpeed = BulletSpeed.INSTANT;


    public boolean headshots = true;


    @SerializedName("use_distance_as_targeting_priority_factor")
    @DisplayName("config.aim_plus.use_distance")
    public boolean useDistanceAsPriorityFactor = true;

    @SerializedName("whitelisted_mobs")
    public Set<ConfigEntity> whitelistedMobs = new HashSet<>();


    @SerializedName("use_health_as_targeting_priority_factor")
    @DisplayName("config.aim_plus.use_health")
    public boolean useHealthAsPriorityFactor = true;


    @SerializedName("dropoff_adjustment")
    public DropoffAdjustment dropoffAdjustment = DropoffAdjustment.NONE;


    @SerializedName("auto_fire")
    public boolean autoFire = true;


    @SerializedName("target_switch_cooldown")
    public boolean targetSwitchCooldownEnabled = false;

    @SerializedName("scope_animation_time")
    @Description("How many ticks to wait after scoping before shooting if require scope is enabled")
    public int scopeAnimationTicks = 3;

    @Populator(PlayerPopulator.class)
    @Button(function = PlayerBlacklister.class, translationKey = "config.aim_plus.blacklist_nearby")
    @SerializedName("can_target_player")
    public Map<String, Boolean> canTargetPlayer = new HashMap<>();

    public boolean isWhitelisted(EntityType<?> type) {
        return ConfigEntity.contains(whitelistedMobs, type,MinecraftClient.getInstance().world);
    }


    @SerializedName("ignored_blocks")
    @Description("Blocks that bullets can travel through or break")
    public Set<ConfigBlock> ignoredBlocks = Set.of(new BlockReference(Blocks.IRON_BARS),new BlockReference(Blocks.GLASS),new BlockReference(Blocks.GLASS_PANE));

    @Override
    public void onServerStatusChanged() {
        canTargetPlayer.clear();
        enabled = false;
        AimPlus.CONFIG.save(this);
    }

    //Populate the player map with all known players on the server
    public static class PlayerPopulator implements BiFunction<ClientWorld, ClientPlayerEntity, Map<?, ?>> {

        @Override
        public Map<String, Boolean> apply(ClientWorld world, ClientPlayerEntity player) {
            List<String> keys = world.getPlayers().stream().map(i -> i.getGameProfile().getName()).toList();
            Map<String, Boolean> result = new HashMap<>();
            for (String key : keys)
                result.put(key, true);
            return result;
        }
    }

    //Blacklists nearby players from being tracked
    public static class PlayerBlacklister implements Runnable {

        @Override
        public void run() {

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.world == null || client.player == null)
                return;

            Config config = AimPlus.CONFIG.getConfig();

            List<PlayerEntity> players = client.world.getPlayers(TargetPredicate.DEFAULT, client.player, client.player.getBoundingBox().expand(15));
            players.forEach(i -> config.canTargetPlayer.put(i.getGameProfile().getName(), false));
        }
    }
}
