package lol.magix.zombiesv2.mixin.item;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.methods.MethodsAxeItem;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public abstract class MixinAxeItem extends MiningToolItem implements MethodsAxeItem {
    protected MixinAxeItem(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onAttack(PlayerEntity entity, ItemStack item) {
        var damage = switch (Registry.ITEM.getId(item.getItem()).toString()) {
            default -> throw new IllegalStateException("Unknown axe type.");
            case "minecraft:wooden_axe" -> 7;
            case "minecraft:stone_axe" -> 12;
            case "minecraft:gold_axe" -> 17;
            case "minecraft:iron_axe" -> 21;
            case "minecraft:diamond_axe" -> 26;
            case "minecraft:netherite_axe" -> 34;
        };
        if (damage > 21) damage *= ZombiesV2.getInstance()
                .getGameState().difficulty / 35f;

        var nearbyEntities = entity.world.getEntitiesByClass(LivingEntity.class,
                entity.getBoundingBox().expand(3),
                (nearbyEntity) -> nearbyEntity != entity);

        // Check for strength.
        if (entity.hasStatusEffect(StatusEffects.STRENGTH)) {
            damage *= (entity.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + .5);
        }

        int finalDamage = damage;
        nearbyEntities.forEach(nearby -> {
            if (!(nearby instanceof PlayerEntity)) {
                nearby.damage(DamageSource.player(entity), finalDamage);
            }
        });
    }
}
