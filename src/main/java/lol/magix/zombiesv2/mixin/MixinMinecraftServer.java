package lol.magix.zombiesv2.mixin;

import lol.magix.zombiesv2.ZombiesV2;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public final class MixinMinecraftServer {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo callback) {
        ZombiesV2.setMinecraftServer((MinecraftServer) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callback) {
        ZombiesV2.getInstance().onTick();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo callback) {
        ZombiesV2.getInstance().onShutdown();
    }
}