package lol.magix.zombiesv2.mixin.item;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.methods.MethodsItemStack;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoeItem.class)
public abstract class MixinHoeItem extends MiningToolItem {
    protected MixinHoeItem(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Check the game state.
        var difficulty = ZombiesV2.getInstance()
                .getGameState().difficulty;
        if (difficulty < 10) return TypedActionResult.fail(user.getStackInHand(hand));

        var item = user.getStackInHand(Hand.MAIN_HAND);
        var customItem = (MethodsItemStack) (Object) item;
        if (customItem.getCooldown() > 0)
            return TypedActionResult.fail(item);

        var type = item.getItem();

        if (!user.getAbilities().creativeMode) {
            ItemStack stack = null;
            for (int i = 0; i < user.getInventory().size(); ++i) {
                stack = user.getInventory().getStack(i);
                if (stack.getItem() == Items.ROTTEN_FLESH)
                    break;
            }

            if (stack != null && !stack.isEmpty()) {
                stack.decrement(1);
            } else {
                return TypedActionResult.fail(item);
            }
        }

        float damage, range, speed;
        switch (Registry.ITEM.getId(type).toString()) {
            default -> throw new IllegalStateException("Unknown hoe type.");
            case "minecraft:wooden_hoe" -> { damage = 3; range = 10; speed = 0.55f; }
            case "minecraft:stone_hoe" -> { damage = 5; range = 15; speed = 0.53f; }
            case "minecraft:gold_hoe" -> { damage = 7; range = 24; speed = 0.51f; }
            case "minecraft:iron_hoe" -> { damage = 12; range = 32; speed = 0.47f; }
            case "minecraft:diamond_hoe" -> { damage = 19; range = 40; speed = 0.43f; }
            case "minecraft:netherite_hoe" -> { damage = 27; range = 47; speed = 0.4f; }
        }

        damage *= difficulty / 40f;

        // Account for enchantments.
        var enchantments = EnchantmentHelper.get(item);
        if (enchantments.containsKey(Enchantments.EFFICIENCY)) {
            speed -= enchantments.get(Enchantments.EFFICIENCY) * 0.02f;
        }

        if (enchantments.containsKey(Enchantments.UNBREAKING)) {
            damage += enchantments.get(Enchantments.UNBREAKING) * 3.3f;
        }

        if (enchantments.containsKey(Enchantments.MENDING)) {
            range += 8;
        }

        // Check for strength.
        if (user.hasStatusEffect(StatusEffects.STRENGTH)) {
            damage *= (user.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + .5);
        }

        this.shoot(user, damage, range);
        customItem.setCooldown(speed);

        return TypedActionResult.success(item);
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
