package lol.magix.zombiesv2.goals;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;

import java.util.Set;

public final class BreakBlockGoal extends Goal {
    private static final Set<Block> UNBREAKABLE = Set.of(
            Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL,
            Blocks.END_GATEWAY, Blocks.COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK, Blocks.BARRIER, Blocks.STRUCTURE_BLOCK,
            Blocks.STRUCTURE_VOID, Blocks.ENDER_CHEST, Blocks.NETHER_PORTAL
    );

    private final MobEntity mob;
    private BlockPos targetBlock = BlockPos.ORIGIN;
    private int breakProgress = 0, prevBreakProgress = -1;
    private boolean shouldStop = false;
    private float offsetX, offsetZ;

    public BreakBlockGoal(MobEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        // Validate the mob can execute the goal.
        if (!NavigationConditions.hasMobNavigation(this.mob) ||
                !this.mob.horizontalCollision)
            return false;
        if (!this.mob.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))
            return false;

        var navigator = this.mob.getNavigation();
        var path = navigator.getCurrentPath();
        if (path == null) return false;

        for (var i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); ++i) {
            var node = path.getNode(i);
            this.targetBlock = new BlockPos(node.x, node.y + 1, node.z);

            // Validate the distance.
            if (this.mob.squaredDistanceTo(this.targetBlock.getX(),
                    this.targetBlock.getY(), this.targetBlock.getZ()) > 2.25D)
                continue;
            // Validate the block is breakable.
            var block = this.mob.world.getBlockState(this.targetBlock).getBlock();
            if (!UNBREAKABLE.contains(block)) return true;
        }

        // Check if the mob is standing on a breakable block.
        this.targetBlock = this.mob.getBlockPos().up();
        var block = this.mob.world.getBlockState(this.targetBlock).getBlock();
        return !UNBREAKABLE.contains(block);
    }

    @Override
    public void start() {
        this.shouldStop = false;
        this.offsetX = (float) (this.targetBlock.getX() + 0.5 - this.mob.getX());
        this.offsetZ = (float) (this.targetBlock.getZ() + 0.5 - this.mob.getZ());
    }

    @Override
    public void stop() {
        this.mob.world.setBlockBreakingInfo(this.mob.getId(), this.targetBlock, -1);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= 240 &&
                this.targetBlock.isWithinDistance(this.mob.getPos(), 2.0);
    }

    @Override
    public void tick() {
        var dx = this.targetBlock.getX() + 0.5 - this.mob.getX();
        var value = this.offsetX * dx + this.offsetZ * (this.targetBlock.getZ() + 0.5 - this.mob.getZ());
        if (value < 0.0f) this.shouldStop = true;

        if (this.mob.getRandom().nextInt(20) == 0) {
            this.mob.world.syncWorldEvent(WorldEvents.ZOMBIE_ATTACKS_WOODEN_DOOR, this.targetBlock, 0);
            if (!this.mob.handSwinging) {
                this.mob.swingHand(this.mob.getActiveHand());
            }
        }

        ++this.breakProgress;
        var progress = this.breakProgress / 240 * 10.0f;
        if (progress != this.prevBreakProgress) {
            this.mob.world.setBlockBreakingInfo(this.mob.getId(), this.targetBlock, (int) progress);
            this.prevBreakProgress = (int) progress;
        }

        if (this.breakProgress == 240) {
            this.mob.world.removeBlock(this.targetBlock, false);
            this.mob.world.syncWorldEvent(WorldEvents.ZOMBIE_BREAKS_WOODEN_DOOR, this.targetBlock, 0);
            this.mob.world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, this.targetBlock, Block.getRawIdFromState(this.mob.world.getBlockState(this.targetBlock)));
        }
    }
}
