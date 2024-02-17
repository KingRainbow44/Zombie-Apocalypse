package lol.magix.zombiesv2.methods;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface MethodsAxeItem {
    void onAttack(PlayerEntity entity, ItemStack item);
}
