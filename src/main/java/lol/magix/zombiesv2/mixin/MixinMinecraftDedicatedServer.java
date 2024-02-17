package lol.magix.zombiesv2.mixin;

import lol.magix.zombiesv2.ZombiesV2;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public final class MixinMinecraftDedicatedServer {
    @Inject(method = "setupServer", at = @At("RETURN"))
    public void setupServer(CallbackInfoReturnable<Boolean> cir) {
        ZombiesV2.getInstance().onLoadServer();
    }
}
