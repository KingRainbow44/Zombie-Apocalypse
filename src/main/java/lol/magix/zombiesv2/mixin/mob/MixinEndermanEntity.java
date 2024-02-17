package lol.magix.zombiesv2.mixin.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanEntity.class)
public abstract class MixinEndermanEntity extends HostileEntity {
    @Shadow public abstract void setTarget(@Nullable LivingEntity target);

    private int ticksAlive = 0;

    protected MixinEndermanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "mobTick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (++this.ticksAlive % 20 == 0)
            this.ticksAlive = 0;
        else return;

        // Try to find a random target in the world.
        if (this.random.nextInt(100) == 0)
            this.setTarget(this.world.getClosestPlayer(this, -1));
    }
}
