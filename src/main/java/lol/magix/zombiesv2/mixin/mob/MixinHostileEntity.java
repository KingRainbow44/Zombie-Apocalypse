package lol.magix.zombiesv2.mixin.mob;

import lol.magix.zombiesv2.ZombiesV2;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HostileEntity.class)
public abstract class MixinHostileEntity extends LivingEntity {
    protected MixinHostileEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        ZombiesV2.getMinecraftServer().getPlayerManager()
                .broadcast(this.getDamageTracker().getDeathMessage(), false);
    }

    @Override
    public void tick() {
        super.tick();

        // Check if there is a nearby player.
        if (this.world.getClosestPlayer(
                this, 50) == null) {
            this.discard();
        }
    }
}
