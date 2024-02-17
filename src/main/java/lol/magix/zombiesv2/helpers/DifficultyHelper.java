package lol.magix.zombiesv2.helpers;

import lol.magix.zombiesv2.ZombiesV2;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;

import javax.annotation.Nullable;
import java.util.List;

public interface DifficultyHelper {
    Random random = Random.create();

    /**
     * Calculates an explosion power from the difficulty.
     * @return The explosion power.
     */
    static float getExplosionPower() {
        var difficulty = ZombiesV2.getInstance()
                .getGameState().difficulty;

        return switch ((int) Math.floor(difficulty / 10f)) {
            default -> 8f;
            case 0 -> 0.2f;
            case 1 -> 0.4f;
            case 2 -> 0.6f;
            case 3 -> 1f;
            case 4 -> 2f;
            case 5 -> 3f;
            case 6 -> 4f;
            case 7 -> 5f;
            case 8 -> 6f;
            case 9 -> 7f;
        };
    }

    /**
     * Applies armor to the entity.
     */
    static void applyArmor(LivingEntity entity) {
        var difficulty = ZombiesV2.getInstance().getDifficultyManager().getDifficulty();
        var level = ZombiesV2.getInstance().getGameState().difficulty;

        var enchantmentLevel = random.nextInt(difficulty.getArmorEnchantmentRange().getRight() -
                difficulty.getArmorEnchantmentRange().getLeft() + 1) + difficulty.getArmorEnchantmentRange().getLeft() - 1;

        var fullEnchanted = random.nextInt(100) <= difficulty.getHasEnchantedArmorFull();
        var enchantedArmor = random.nextInt(100) <= difficulty.getHasEnchantedArmor();

        if (random.nextInt(100) <= difficulty.getHasArmor()) {
            if (random.nextInt(100) <= difficulty.getHasFullArmor()) {
                // Apply full armor.
                entity.equipStack(EquipmentSlot.HEAD, DifficultyHelper.armorPiece(level, 0));
                entity.equipStack(EquipmentSlot.CHEST, DifficultyHelper.armorPiece(level, 1));
                entity.equipStack(EquipmentSlot.LEGS, DifficultyHelper.armorPiece(level, 2));
                entity.equipStack(EquipmentSlot.FEET, DifficultyHelper.armorPiece(level, 3));

                // Enchant the armor.
                if (enchantedArmor && fullEnchanted) {
                    var armorInventory = (List<ItemStack>) entity.getArmorItems();
                    for (var i = 0; i < 4; i++) {
                        var armorPiece = armorInventory.get(i);
                        if (armorPiece.isEmpty()) continue;

                        armorPiece.addEnchantment(Enchantments.PROTECTION, enchantmentLevel);
                    }
                }
            } else {
                var slot = random.nextBetween(0, 3);
                var armor = DifficultyHelper.armorPiece(level, slot);
                if (enchantedArmor)
                    armor.addEnchantment(Enchantments.PROTECTION, enchantmentLevel);

                entity.equipStack(switch (slot) {
                    default -> EquipmentSlot.MAINHAND;
                    case 0 -> EquipmentSlot.HEAD;
                    case 1 -> EquipmentSlot.CHEST;
                    case 2 -> EquipmentSlot.LEGS;
                    case 3 -> EquipmentSlot.FEET;
                }, armor);
            }
        }
    }

    /**
     * Creates an item stack from the level and piece.
     * @param level The level.
     * @param piece The piece.
     * @return The item stack.
     */
    static ItemStack armorPiece(long level, int piece) {
        if (level < 10) return switch (piece) {
            default -> throw new RuntimeException("Invalid piece.");
            case 0 -> new ItemStack(Items.LEATHER_HELMET);
            case 1 -> new ItemStack(Items.LEATHER_CHESTPLATE);
            case 2 -> new ItemStack(Items.LEATHER_LEGGINGS);
            case 3 -> new ItemStack(Items.LEATHER_BOOTS);
        };

        if (level < 40) return switch (piece) {
            default -> throw new RuntimeException("Invalid piece.");
            case 0 -> new ItemStack(Items.IRON_HELMET);
            case 1 -> new ItemStack(Items.IRON_CHESTPLATE);
            case 2 -> new ItemStack(Items.IRON_LEGGINGS);
            case 3 -> new ItemStack(Items.IRON_BOOTS);
        };

        if (level < 70) return switch (piece) {
            default -> throw new RuntimeException("Invalid piece.");
            case 0 -> new ItemStack(Items.DIAMOND_HELMET);
            case 1 -> new ItemStack(Items.DIAMOND_CHESTPLATE);
            case 2 -> new ItemStack(Items.DIAMOND_LEGGINGS);
            case 3 -> new ItemStack(Items.DIAMOND_BOOTS);
        };

        return switch (piece) {
            default -> throw new RuntimeException("Invalid piece.");
            case 0 -> new ItemStack(Items.NETHERITE_HELMET);
            case 1 -> new ItemStack(Items.NETHERITE_CHESTPLATE);
            case 2 -> new ItemStack(Items.NETHERITE_LEGGINGS);
            case 3 -> new ItemStack(Items.NETHERITE_BOOTS);
        };
    }

    /**
     * Applies a weapon to the entity.
     * @param entity The entity.
     */
    static void applyWeapon(LivingEntity entity) {
        applyWeapon(entity, null);
    }

    /**
     * Applies a weapon to the entity.
     * @param entity The entity.
     * @param weapon Override the weapon.
     */
    static void applyWeapon(LivingEntity entity, @Nullable ItemStack weapon) {
        var difficulty = ZombiesV2.getInstance().getDifficultyManager().getDifficulty();
        var isEnchanted = random.nextInt(100) <= difficulty.getHasEnchantedWeapon();
        var enchantmentLevel = random.nextInt(difficulty.getWeaponEnchantmentRange().getRight() -
                difficulty.getWeaponEnchantmentRange().getLeft() + 1) + difficulty.getWeaponEnchantmentRange().getLeft() - 1;

        var itemStack = weapon == null ? DifficultyHelper.weapon(difficulty.getWeaponType()) : weapon;
        if (isEnchanted) {
            var damageEnchantment = Enchantments.SHARPNESS;
            var fireEnchantment = Enchantments.FIRE_ASPECT;
            var knockEnchantment = Enchantments.KNOCKBACK;

            if (itemStack.getItem() == Items.BOW) {
                damageEnchantment = Enchantments.POWER;
                fireEnchantment = Enchantments.FLAME;
                knockEnchantment = Enchantments.PUNCH;
            }

            switch (enchantmentLevel) {
                case 0 -> { }
                case 1, 2 -> itemStack.addEnchantment(damageEnchantment, 1);
                case 3 -> itemStack.addEnchantment(damageEnchantment, 2);
                case 4 -> {
                    itemStack.addEnchantment(damageEnchantment, 2);
                    itemStack.addEnchantment(fireEnchantment, 1);
                    itemStack.addEnchantment(knockEnchantment, 1);
                }
                case 5 -> itemStack.addEnchantment(damageEnchantment, 3);
                case 6 -> {
                    itemStack.addEnchantment(damageEnchantment, 3);
                    itemStack.addEnchantment(fireEnchantment, 1);
                    itemStack.addEnchantment(knockEnchantment, 1);
                }
                case 7, 8 -> {
                    itemStack.addEnchantment(damageEnchantment, 4);
                    itemStack.addEnchantment(fireEnchantment, 1);
                    itemStack.addEnchantment(knockEnchantment, 1);
                }
                case 9 -> {
                    itemStack.addEnchantment(damageEnchantment, 5);
                    itemStack.addEnchantment(fireEnchantment, 1);
                    itemStack.addEnchantment(knockEnchantment, 2);
                }
                case 10 -> {
                    itemStack.addEnchantment(damageEnchantment, 5);
                    itemStack.addEnchantment(fireEnchantment, 2);
                    itemStack.addEnchantment(knockEnchantment, 2);
                }
            }
        }

        entity.equipStack(EquipmentSlot.MAINHAND, itemStack);
    }

    /**
     * Creates an item stack from the type of tool.
     * @param toolType The type of tool.
     * @return The item stack.
     */
    static ItemStack weapon(int toolType) {
        return switch (toolType) {
            default -> throw new RuntimeException("Invalid tool type.");
            case 1 -> new ItemStack(Items.WOODEN_SWORD);
            case 2 -> new ItemStack(Items.STONE_SWORD);
            case 3 -> new ItemStack(Items.IRON_SWORD);
            case 4 -> new ItemStack(Items.DIAMOND_SWORD);
            case 5 -> new ItemStack(Items.NETHERITE_SWORD);
        };
    }
}
