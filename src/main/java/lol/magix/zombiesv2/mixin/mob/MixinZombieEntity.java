package lol.magix.zombiesv2.mixin.mob;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.helpers.DifficultyHelper;
import lol.magix.zombiesv2.helpers.NameHelper;
import lol.magix.zombiesv2.methods.MethodsZombieEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ZombieEntity.class)
public abstract class MixinZombieEntity extends HostileEntity implements MethodsZombieEntity {
    @Shadow private int ticksUntilWaterConversion;
    private static final Set<Block> UNBREAKABLE = Set.of(
            Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL,
            Blocks.END_GATEWAY, Blocks.COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK, Blocks.BARRIER, Blocks.STRUCTURE_BLOCK,
            Blocks.STRUCTURE_VOID, Blocks.ENDER_CHEST, Blocks.NETHER_PORTAL,
            Blocks.IRON_DOOR, Blocks.IRON_TRAPDOOR, Blocks.IRON_BARS, Blocks.IRON_BLOCK,
            Blocks.REINFORCED_DEEPSLATE, Blocks.WAXED_COPPER_BLOCK
    );

    private boolean overpowered = false;
    private boolean toweringUp = false;
    private long ticksAlive = 0L;

    protected MixinZombieEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Attempts to break the block at the given position.
     * @param block The position of the block to break.
     */
    private void tryToBreak(BlockPos block) {
        var blockState = this.world.getBlockState(block);
        if (UNBREAKABLE.contains(blockState.getBlock()) && !this.overpowered) return;

        // Check if the entity is in the nether.
        if (this.world.getDimension().piglinSafe() &&
                blockState.getBlock() == Blocks.COBBLESTONE &&
                !this.overpowered) return;

        this.world.breakBlock(block, false, this); // Break the block.
    }

    /**
     * Attempts to place a block at the target position.
     * @param target The position to place the block at.
     * @param block The block to place.
     */
    private void tryToPlace(BlockPos target, BlockState block) {
        var blockState = this.world.getBlockState(target);
        if (UNBREAKABLE.contains(blockState.getBlock()) && !this.overpowered) return;

        this.world.setBlockState(target, block); // Place the block.
    }

    /**
     * Attempts to apply velocity to make the mob jump.
     */
    private void velocityJump() {
        var currentVelocity = this.getVelocity();
        this.setVelocity(currentVelocity.x,
                this.getJumpVelocity() + 0.1, currentVelocity.z);
    }

    /**
     * Validates the mob's position to build up.
     * @return True if the mob is in a valid position to build up.
     */
    private boolean isNearby(int distance) {
        // Check if the mob is within 10 blocks of the target horizontally.
        var target = this.getTarget();
        if (target == null) return false;

        var targetPos = target.getBlockPos().withY(0);
        var mobPos = this.getBlockPos().withY(0);
        return mobPos.getSquaredDistance(targetPos) <= distance;
    }

    /**
     * Returns the mob's target block.
     * @return A block coordinate.
     */
    private BlockPos getTargetBlock() {
        var direction = this.getHorizontalFacing();
        var x = this.getX() + direction.getOffsetX();
        var y = this.getY() + direction.getOffsetY();
        var z = this.getZ() + direction.getOffsetZ();
        return new BlockPos(x, y, z);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        this.ticksAlive++;

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

        // Check if the mob is overpowered.
        if (this.overpowered && this.random.nextInt(3000) == 0) {
            // Teleport the mob within a radius of 5 blocks around the target.
            // If it's a solid block, oh well... not worth the effort.

            var targetPos = target.getBlockPos();
            var tarX = targetPos.getX();
            var tarY = targetPos.getY();
            var tarZ = targetPos.getZ();

            var x = tarX + 5 * Math.sin(this.random.nextInt(360));
            var z = tarZ + 5 * Math.sin(this.random.nextInt(360));
            this.teleport(x, tarY, z);
        }

        // Check if the mob should tower up.
        if (this.toweringUp && this.ticksAlive % 10 == 0) {
            var upBlock1 = world.getBlockState(this.getBlockPos().up()).getBlock(); // Get the block above the mob.
            var upBlock2 = world.getBlockState(this.getBlockPos().up(1)).getBlock(); // Get the block above the mob.
            if (upBlock1 == Blocks.AIR && upBlock2 == Blocks.AIR) {
                this.velocityJump(); // Make the mob jump.
                this.tryToPlace(this.getBlockPos().down(), // Place a block below the mob.
                        Blocks.DIRT.getDefaultState());

                // Check if the mob is within distance.
                if (this.world.getClosestPlayer(this, 6) == null) return;
                // Check if the mob should explode.
                if (this.random.nextInt(50) == 0 && toweringUp) {
                    // Create a non-breaking explosion.
                    world.createExplosion(this, this.getX(), this.getY(), this.getZ(),
                            DifficultyHelper.getExplosionPower(), this.overpowered ?
                            Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
                }
            } else {
                this.tryToBreak(this.getBlockPos().up(1));
                this.tryToBreak(this.getBlockPos().up(2));
            }
        }

        // Determine if the mob should attack the target.
        if (this.random.nextInt(20) != 0 ||
                this.ticksAlive % 20 == 0) return;

        var world = this.world;

        // Check if the mob is near the target.
        if (this.world.getClosestPlayer(this, 5) != null) return;

        // Determine the direction the mob should attack.
        if (target.getY() == this.getY()) { // Mob should attack horizontally.
            if (this.toweringUp) this.toweringUp = false;

            // Determine if the mob should break blocks in front of it.
            var position = this.getTargetBlock();

            // Check if the targeted block is air.
            var block = world.getBlockState(position);
            if (!block.isAir()) this.tryToBreak(position); // If the targeted block is not air, break it.
            // Check if the block below the target is air.
            var belowBlock = world.getBlockState(position.up());
            if (!belowBlock.isAir()) this.tryToBreak(position.up()); // If the block below the target is not air, break it.

            // Get the direction the mob should bridge towards.
            var targetBlock = this.getTargetBlock().down();

            // Check if the targeted block is air.
            var targetBlockState = world.getBlockState(position);
            if (targetBlockState.isAir()) {
                // If the targeted block is air, place a block.
                this.tryToPlace(targetBlock, world.getBlockState(this.getBlockPos().down(1)));
            }
        } else if (target.getY() > this.getY()) { // Mob should attempt to elevate.
            // Determine if the mob is surrounded by blocks.
            var upBlock = world.getBlockState(this.getBlockPos().up());
            var downBlock = world.getBlockState(this.getBlockPos().down());
            if (!upBlock.isAir() && !downBlock.isAir()) { // Surrounded by blocks.
                // Get the current block.
                var currentBlock = this.getBlockPos();
                // Mob should attempt to pillar up.
                this.tryToBreak(this.getBlockPos().up(1)); // Attempt to break the block above the mob.
                this.velocityJump(); // Jump.
                this.tryToPlace(currentBlock, this.world.getBlockState(currentBlock.down())); // Set the current block to the block below.
            } else if (this.isNearby(10)) { // Not surrounded by blocks.
                this.toweringUp = true; // Set the mob to tower up.
            }
        } else { // Mob should attempt to descend.
            if (this.toweringUp) this.toweringUp = false;

            // Check if there is ground below the block the mob is standing on.
            var belowBlock = world.getBlockState(this.getBlockPos().down());
            if (!belowBlock.isAir()) {
                // If there is no ground below the block the mob is standing on, break the block below the mob.
                this.tryToBreak(this.getBlockPos().down());
            }
        }
    }

    @Override
    public void assignId() {
        this.setCustomName(Text.of(NameHelper.zombieName()));
        this.setCustomNameVisible(true);
    }

    @Override
    public void setupEquipment(boolean overpowered) {
        this.overpowered = overpowered;

        try {
            DifficultyHelper.applyArmor(this); // Apply armor to the entity.
            DifficultyHelper.applyWeapon(this); // Apply a weapon to the entity.
        } catch (Exception ignored) {}
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    public void dropEquipment(CallbackInfo ci) {
        ci.cancel(); // Cancel the default dropEquipment method.
    }

    @Inject(method = "initCustomGoals", at = @At("HEAD"), cancellable = true)
    protected void initCustomGoals(CallbackInfo ci) {
        ci.cancel(); // Cancel the original method.

        this.goalSelector.add(1, new ZombieAttackGoal((ZombieEntity) (Object) this, 1.2, false));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Inject(method = "initAttributes", at = @At("RETURN"))
    protected void initAttributes(CallbackInfo ci) {
        var attribute = this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE);
        if (attribute != null) attribute.setBaseValue(Double.MAX_VALUE);

        var difficulty = Math.round(ZombiesV2.getInstance().getGameState().difficulty);
        if (difficulty >= 10) {
            attribute = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (attribute != null) attribute.setBaseValue(switch (difficulty) {
                case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 -> 0.24;
                case 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 -> 0.25;
                case 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 -> 0.26;
                case 41, 42, 43, 44, 45, 46, 47, 48, 49, 50 -> 0.27;
                case 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 -> 0.28;
                case 61, 62, 63, 64, 65, 66, 67, 68, 69, 70 -> 0.29;
                case 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 -> 0.3;
                case 81, 82, 83, 84, 85, 86, 87, 88, 89, 90 -> 0.31;
                case 91, 92, 93, 94, 95, 96, 97, 98, 99, 100 -> 0.32;
                default -> (difficulty / 100f) * 0.32;
            });
        }

        if (difficulty >= 40) {
            attribute = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                attribute.setBaseValue(attribute.getBaseValue() * (difficulty / 40f));
                this.setHealth((float) attribute.getBaseValue());
            }
        }
    }

     @Inject(method = "convertInWater", at = @At("HEAD"), cancellable = true)
     public void convertInWater(CallbackInfo ci) {
        ci.cancel(); this.ticksUntilWaterConversion = 0;
     }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
    }
}
