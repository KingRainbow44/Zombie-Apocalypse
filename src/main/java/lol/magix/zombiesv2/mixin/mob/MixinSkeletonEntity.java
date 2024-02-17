package lol.magix.zombiesv2.mixin.mob;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.helpers.DifficultyHelper;
import lol.magix.zombiesv2.helpers.NameHelper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkeletonEntity.class)
public abstract class MixinSkeletonEntity extends AbstractSkeletonEntity {
    protected MixinSkeletonEntity(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        // Obtain a valid target for the mob.
        var target = this.getTarget();
        if (!(target instanceof PlayerEntity))
            target = this.world.getClosestPlayer(this.getX(), this.getY(),
                    this.getZ(), -1, true);
        if (target == null) return;

        // Check if the mob has a target.
        if (this.getTarget() == null) {
            // If the mob doesn't have a target, set the target to the player.
            this.setTarget(this.world.getClosestPlayer(this, -1));
            // Check if the mob has a target.
            if (this.getTarget() == null) {
                // If the mob doesn't have a target, return.
                return;
            }
        }

        // Look at the target.
        this.lookAtEntity(target, 360, 360);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(3, new FleeEntityGoal<WolfEntity>(this, WolfEntity.class, 6.0f, 1.0, 1.2));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        try {
            DifficultyHelper.applyArmor(this); // Apply armor to the entity.
            DifficultyHelper.applyWeapon(this, new ItemStack(Items.BOW)); // Apply a weapon to the entity.
        } catch (Exception ignored) {}
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setCustomName(Text.of(NameHelper.zombieName()));
        this.setCustomNameVisible(true);

        var attribute = this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE);
        if (attribute != null) attribute.setBaseValue(Double.MAX_VALUE);

        var gameDifficulty = Math.round(ZombiesV2.getInstance().getGameState().difficulty);
        if (gameDifficulty >= 10) {
            attribute = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (attribute != null) attribute.setBaseValue(switch (gameDifficulty) {
                case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 -> 0.24;
                case 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 -> 0.25;
                case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 -> 0.26;
                case 41, 42, 43, 44, 45, 46, 47, 48, 49, 50 -> 0.27;
                case 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 -> 0.28;
                case 61, 62, 63, 64, 65, 66, 67, 68, 69, 70 -> 0.29;
                case 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 -> 0.3;
                case 81, 82, 83, 84, 85, 86, 87, 88, 89, 90 -> 0.31;
                case 91, 92, 93, 94, 95, 96, 97, 98, 99, 100 -> 0.32;
                default -> (gameDifficulty / 100f) * 0.32;
            });
        }

        if (gameDifficulty >= 40) {
            attribute = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                attribute.setBaseValue(attribute.getBaseValue() * (gameDifficulty / 40f));
                this.setHealth((float) attribute.getBaseValue());
            }
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
}
