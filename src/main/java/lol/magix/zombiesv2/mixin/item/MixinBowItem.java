package lol.magix.zombiesv2.mixin.item;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.methods.MethodsItem;
import lol.magix.zombiesv2.methods.MethodsItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BowItem.class)
public abstract class MixinBowItem extends RangedWeaponItem implements MethodsItem {
    public MixinBowItem(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onAttack(PlayerEntity user) {
        var item = user.getStackInHand(Hand.MAIN_HAND);
        var customItem = (MethodsItemStack) (Object) item;
        if (customItem.getCooldown() > 0) return;

        // Check the game state.
        var difficulty = ZombiesV2.getInstance()
                .getGameState().difficulty;
        if (difficulty < 50) return;

        float damage = 8, velocity = 1.5f, speed = 0.5f, times = 1;

        damage *= difficulty / 55f;

        // Account for enchantments.
        var enchantments = EnchantmentHelper.get(item);
        if (enchantments.containsKey(Enchantments.POWER)) {
            damage += enchantments.get(Enchantments.POWER) * 0.5f;
        }

        if (enchantments.containsKey(Enchantments.PUNCH)) {
            velocity += enchantments.get(Enchantments.PUNCH) * 0.5f;
        }

        if (enchantments.containsKey(Enchantments.UNBREAKING)) {
            speed -= enchantments.get(Enchantments.UNBREAKING) * 0.07f;
        }

        if (enchantments.containsKey(Enchantments.MENDING)) {
            damage /= 1.2f;
            times = 3;
        }

        if (enchantments.containsKey(Enchantments.INFINITY)) {
            speed -= 0.1f;
        } else {
            var arrowType = user.getArrowType(item);
            if (arrowType.isEmpty()) return;

            arrowType.decrement(Math.round(times));
        }

        // Check for strength.
        if (user.hasStatusEffect(StatusEffects.STRENGTH)) {
            damage *= (user.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + .5);
        }

        this.shoot(user, damage,     velocity, times);
        customItem.setCooldown(speed);
    }

    /**
     * Shoots an arrow from the player's eye.
     * @param user The player to shoot the arrow from.
     * @param damage The damage the arrow will deal.
     * @param range The range the arrow will travel.
     */
    private void shoot(PlayerEntity user, float damage, float range, float times) {
        var world = user.world;
        var basePos = user.getEyePos();
        for (int i = 0; i < times; i++) {
            var arrow = new ArrowEntity(world, user);
            arrow.setVelocity(user, user.getPitch(), user.getYaw(),
                    0.0f, .5f + range, 1.0f);
            arrow.setDamage(damage);

            if (i > 0) {
                var pos = basePos.rotateY(i == 1 ? 0.05f : -0.05f);
                arrow.setPos(pos.x, pos.y, pos.z);
            }

            user.world.spawnEntity(arrow);
        }

        user.world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0f, 1.0f / (world.getRandom().nextFloat()
                        * 0.4f + 1.2f) + 1 * 0.5f);
    }
}
