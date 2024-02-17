package lol.magix.zombiesv2.mixin.item;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.methods.MethodsItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SwordItem.class)
public abstract class MixinSwordItem extends ToolItem implements MethodsItem {
    public MixinSwordItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onAttack(PlayerEntity user) {
        // Check the game state.
        var difficulty = ZombiesV2.getInstance()
                .getGameState().difficulty;
        if (difficulty < 30) return;

        // Check the player's attack cooldown.
        var progress = user.getAttackCooldownProgress(0.5f);
        if (progress < 0.9f) return;

        var item = user.getStackInHand(Hand.MAIN_HAND);
        var type = item.getItem();

        // Check enchantments.
        var enchantments = EnchantmentHelper.get(item);
        if (enchantments.size() < 1) return;

        item.damage(1, user, (player) -> player.sendToolBreakStatus(user.getActiveHand()));

        float damage, range;
        switch (Registry.ITEM.getId(type).toString()) {
            default -> throw new IllegalStateException("Unknown sword type.");
            case "minecraft:wooden_sword" -> { damage = 4; range = 4; }
            case "minecraft:stone_sword" -> { damage = 8; range = 7; }
            case "minecraft:gold_sword" -> { damage = 13; range = 12; }
            case "minecraft:iron_sword" -> { damage = 18; range = 14; }
            case "minecraft:diamond_sword" -> { damage = 22; range = 16; }
            case "minecraft:netherite_sword" -> { damage = 30; range = 23; }
        }

        damage *= difficulty / 45f;

        // Account for enchantments.
        if (enchantments.containsKey(Enchantments.SHARPNESS)) {
            damage += enchantments.get(Enchantments.SHARPNESS) * 1.25f;
        }

        if (enchantments.containsKey(Enchantments.SMITE)) {
            damage += enchantments.get(Enchantments.SMITE) * 2f;
        }

        if (enchantments.containsKey(Enchantments.KNOCKBACK)) {
            range *= enchantments.get(Enchantments.KNOCKBACK) + 1;
        }

        // Check for strength.
        if (user.hasStatusEffect(StatusEffects.STRENGTH)) {
            damage *= (user.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + .5);
        }

        this.shoot(user, damage, range);
    }

    /**
     * Shoots a particle beam from the player's eyes to the block they're looking at.
     * @param player The player to shoot the beam from.
     * @param damage The damage to deal to the block.
     */
    private void shoot(PlayerEntity player, float damage, float range) {
        var particleLocation = player.getEyePos();
        var world = (ServerWorld) player.getEntityWorld();

        var offset = player.getRotationVector().multiply(0.5f);
        for(int i = 0; i <= range; i++) {
            // Damage Entities
            for (var entity : player.getEntityWorld().getOtherEntities(
                    player, Box.of(player.getPos(), range, range, range))) {
                if (!(entity instanceof LivingEntity living)) continue;

                /* Define the bounding box of the particle.
                        We will use 0.25 here, since the particle is moving 0.5 blocks each time.
                        That means the particle won't miss very small entities like chickens or bats,
                          as the particle bounding box covers 1/2 of the movement distance.
                         */
                var particleMinVector = new Vec3d(
                        particleLocation.getX() - 0.25,
                        particleLocation.getY() - 0.25,
                        particleLocation.getZ() - 0.25);
                var particleMaxVector = new Vec3d(
                        particleLocation.getX() + 0.25,
                        particleLocation.getY() + 0.25,
                        particleLocation.getZ() + 0.25);

                // Now use a spigot API call to determine if the particle is inside the entity's hitbox
                var boundingBox = entity.getBoundingBox();
                if (boundingBox.contains(particleMinVector) ||
                        boundingBox.contains(particleMaxVector)) {
                    if (living instanceof PlayerEntity) continue;
                    living.damage(DamageSource.player(player), damage);

                    // Get nearby entities and damage them.
                    for (var nearbyEntity : player.getEntityWorld().getOtherEntities(
                            player, living.getBoundingBox().expand(5))) {
                        if (!(nearbyEntity instanceof LivingEntity livingEntity)) continue;
                        if (livingEntity instanceof PlayerEntity) continue;
                        livingEntity.damage(DamageSource.player(player), damage);
                    }
                }
            }

            // Now we add the direction vector offset to the particle's current location
            particleLocation = particleLocation.add(offset);

            // Display the particle in the new location
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                    particleLocation.getX(), particleLocation.getY(),
                    particleLocation.getZ(), 1, 0, 0, 0, 0);
        }
    }
}
