package lol.magix.zombiesv2.mixin.block;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BedBlock.PART;
import static net.minecraft.block.HorizontalFacingBlock.FACING;

@Mixin(BedBlock.class)
public final class MixinBedBlock {
    @Inject(method = "isBedWorking", at = @At("RETURN"), cancellable = true)
    private static void isBedWorking(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) {
            cir.setReturnValue(ActionResult.CONSUME); return;
        }
        if (state.get(PART) != BedPart.HEAD && !(state = world.getBlockState(pos = pos.offset(state.get(FACING)))).isOf((BedBlock) (Object) this)) {
            cir.setReturnValue(ActionResult.CONSUME); return;
        }

        world.removeBlock(pos, false);
        BlockPos blockPos = pos.offset(state.get(FACING).getOpposite());
        if (world.getBlockState(blockPos).isOf((BedBlock) (Object) this)) {
            world.removeBlock(blockPos, false);
        }
        world.createExplosion(null, DamageSource.badRespawnPoint(), null,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                6.0f, false, Explosion.DestructionType.BREAK);
        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
