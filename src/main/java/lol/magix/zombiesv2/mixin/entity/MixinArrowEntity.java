package lol.magix.zombiesv2.mixin.entity;

import lol.magix.zombiesv2.ZombiesV2;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArrowEntity.class)
public abstract class MixinArrowEntity extends PersistentProjectileEntity {
    protected MixinArrowEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        var position = blockHitResult.getBlockPos();

        var difficulty = ZombiesV2.getInstance()
                .getGameState().difficulty;

        // Check the shooter of the arrow.
        if (difficulty >= 50 && this.getOwner() instanceof SkeletonEntity) {
            // Make a small explosion where the arrow landed.
            this.world.createExplosion(this,
                    position.getX(), position.getY(), position.getZ(), difficulty * 0.1f,
                    true, Explosion.DestructionType.BREAK);
        }

        // Remove the arrow.
        this.discard();
    }

    @Override
    public byte getPierceLevel() {
        return 3;
    }
}
