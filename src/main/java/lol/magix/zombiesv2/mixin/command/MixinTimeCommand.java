package lol.magix.zombiesv2.mixin.command;

import lol.magix.zombiesv2.ZombiesV2;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TimeCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TimeCommand.class)
public final class MixinTimeCommand {
    @Inject(method = "executeSet", at = @At("HEAD"))
    private static void executeSet(ServerCommandSource source, int time, CallbackInfoReturnable<Integer> cir) {
        // Set the time for our difficulty manager.
        ZombiesV2.getInstance().getTimeManager().setCurrentTime(time);
    }

    @Inject(method = "executeQuery", at = @At("HEAD"))
    private static void executeQuery(ServerCommandSource source, int time, CallbackInfoReturnable<Integer> cir) {
        // Reload the game state.
        ZombiesV2.getInstance().getGameState().reload();
    }
}
