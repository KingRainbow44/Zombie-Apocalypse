package lol.magix.zombiesv2.mixin;

import lol.magix.zombiesv2.ZombiesV2;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld {
    @Shadow @Final private ServerWorldProperties worldProperties;
    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract void setTimeOfDay(long timeOfDay);

    @Shadow @Final @Mutable
    private boolean shouldTickTime;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo callback) {
        this.shouldTickTime = false;
    }

    @Inject(method = "tickTime", at = @At("HEAD"))
    public void tickTime(CallbackInfo callback) {
        var timeManager = ZombiesV2.getInstance().getTimeManager();
        var time = timeManager.internalTimeToMinecraftTime();

        // Set the world's time.
        this.worldProperties.setTime(time);
        this.worldProperties.getScheduledEvents().processEvents(this.server, time);
        this.setTimeOfDay(time);
    }
}
