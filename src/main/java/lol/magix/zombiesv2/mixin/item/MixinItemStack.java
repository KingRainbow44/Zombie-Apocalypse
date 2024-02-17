package lol.magix.zombiesv2.mixin.item;

import lol.magix.zombiesv2.methods.MethodsItemStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements MethodsItemStack {
    private float cooldown = 0;

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    public void onInventoryTick(CallbackInfo ci) {
        if (cooldown > 0) cooldown -= 0.05f;
    }
}
