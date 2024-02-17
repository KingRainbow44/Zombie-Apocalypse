package lol.magix.zombiesv2.mixin.mob;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(PiglinBrain.class)
public abstract class MixinPiglinBrain {
    private static final Map<Item, Integer> ITEMS = new HashMap<>(Map.of(
            Items.OBSIDIAN, 2,
            Items.CRYING_OBSIDIAN, 4,
            Items.IRON_NUGGET, 12,
            Items.POTION, 1,
            Items.SPLASH_POTION, 1,
            Items.ARROW, 19,
            Items.SPECTRAL_ARROW, 16,
            Items.IRON_BOOTS, 1,
            Items.IRON_LEGGINGS, 1,
            Items.IRON_CHESTPLATE, 1
    ));

    static {
        // Add additional items.
        ITEMS.put(Items.IRON_HELMET, 1);
        ITEMS.put(Items.IRON_SWORD, 1);
        ITEMS.put(Items.IRON_PICKAXE, 1);
        ITEMS.put(Items.IRON_AXE, 1);
        ITEMS.put(Items.IRON_SHOVEL, 1);
        ITEMS.put(Items.IRON_HOE, 1);
        ITEMS.put(Items.SHIELD, 1);
        ITEMS.put(Items.ENDER_PEARL, 5);
        ITEMS.put(Items.STRING, 15);
        ITEMS.put(Items.GLOWSTONE_DUST, 13);
        ITEMS.put(Items.GOLDEN_CARROT, 12);
    }

    @Inject(method = "getBarteredItem", at = @At("HEAD"), cancellable = true)
    private static void getBarteredItem(PiglinEntity piglin, CallbackInfoReturnable<List<ItemStack>> cir) {
        // Get a random item from the map.
        var item = ITEMS.keySet().toArray(new Item[0])
                [(int) (Math.random() * ITEMS.size())];
        // Get the amount of the item.
        var amount = ITEMS.get(item);
        if (amount != 1)
            amount += ((int) (Math.random() * 6));

        // Check if the item is a potion.
        var itemStack = new ItemStack(item, amount);
        if (item instanceof PotionItem) {
            PotionUtil.setPotion(itemStack, new Potion(
                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 180)
            ));
        }

        if (item instanceof ArmorItem) {
            itemStack.addEnchantment(Enchantments.PROTECTION, 1);
        }

        if (item instanceof MiningToolItem) {
            itemStack.addEnchantment(Enchantments.EFFICIENCY, 1);
        }

        if (item instanceof SwordItem) {
            itemStack.addEnchantment(Enchantments.SHARPNESS, 1);
        }

        // Set the item.
        cir.setReturnValue(List.of(itemStack));
    }
}
