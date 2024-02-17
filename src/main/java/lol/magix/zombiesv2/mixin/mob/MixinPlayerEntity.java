package lol.magix.zombiesv2.mixin.mob;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.methods.MethodsItem;
import lol.magix.zombiesv2.methods.MethodsPlayerEntity;
import lol.magix.zombiesv2.states.PlayerState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements MethodsPlayerEntity {
    @Shadow public abstract Text getDisplayName();

    private PlayerState state = new PlayerState();

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        var name = this.getDisplayName().getString().toLowerCase();
        var file = new File("players", name + ".json");

        try {
            if (this.checkPlayerDatabase(file)) return;
            this.state = ZombiesV2.getGson().fromJson(new FileReader(file), PlayerState.class);
            if (this.state == null) this.state = new PlayerState();
        } catch (IOException ignored) { }
    }

    @Inject(method = "remove", at = @At("RETURN"))
    public void remove(CallbackInfo ci) {
        var name = this.getDisplayName().getString().toLowerCase();
        var file = new File("players", name + ".json");

        try {
            if (this.checkPlayerDatabase(file)) return;
            var data = ZombiesV2.getGson().toJson(this.state);
            Files.write(file.toPath(), data.getBytes());
        } catch (IOException ignored) { }
    }

    @Override
    public void onPlayerDeath() {
        if (this.state != null) this.state.deaths++; // Increase state.
        ZombiesV2.getInstance().getGameState().difficulty++; // Increase difficulty.
    }

    @Inject(method = "onKilledOther", at = @At("RETURN"))
    public void onKilledOther(ServerWorld world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && this.state != null) this.state.kills++; // Increase state.
    }

    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);

        if (hand == Hand.MAIN_HAND) {
            var item = this.getMainHandStack().getItem();
            if (item instanceof MethodsItem customItem)
                customItem.onAttack((PlayerEntity) (Object) this);
        }
    }

    /**
     * Validates the player's database file.
     * @param databaseFile The database file.
     * @return Whether the database file is valid.
     * @throws IOException If an I/O error occurs.
     */
    private boolean checkPlayerDatabase(File databaseFile) throws IOException {
        if (!databaseFile.exists() && !databaseFile.createNewFile()) {
            ZombiesV2.getLogger().error("Failed to create the players/" + this.getDisplayName() + ".json file!");
            return true;
        }

        if (!databaseFile.canRead()) {
            ZombiesV2.getLogger().error("Failed to read the players/" + this.getDisplayName() + ".json file!");
            return true;
        }

        if (!databaseFile.canWrite()) {
            ZombiesV2.getLogger().error("Failed to write to the players/" + this.getDisplayName() + ".json file!");
            return true;
        }
        return false;
    }
}
