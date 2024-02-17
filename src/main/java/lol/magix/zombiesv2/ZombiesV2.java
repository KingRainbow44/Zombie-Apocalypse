package lol.magix.zombiesv2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lol.magix.zombiesv2.helpers.NameHelper;
import lol.magix.zombiesv2.managers.DifficultyManager;
import lol.magix.zombiesv2.managers.TimeManager;
import lol.magix.zombiesv2.methods.MethodsAxeItem;
import lol.magix.zombiesv2.methods.MethodsItem;
import lol.magix.zombiesv2.methods.MethodsPlayerEntity;
import lol.magix.zombiesv2.methods.MethodsZombieEntity;
import lol.magix.zombiesv2.states.GameState;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.*;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.biome.SpawnSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public final class ZombiesV2 implements DedicatedServerModInitializer {
    @Getter private static ZombiesV2 instance;
    @Getter private static final Logger logger =
            LoggerFactory.getLogger("Zombies");
    @Getter private static final Gson gson =
            new GsonBuilder().setPrettyPrinting().create();

    @Getter @Setter
    private static MinecraftServer minecraftServer;

    /**
     * Creates an identifier using the mod's namespace.
     * @param name The name of the identifier.
     * @return The identifier.
     */
    public static Identifier id(String name) {
        return new Identifier("zombies2", name);
    }

    @Getter private final TimeManager timeManager = new TimeManager();
    @Getter private final DifficultyManager difficultyManager = new DifficultyManager();
    @Getter private GameState gameState = new GameState();
    @Getter private ScoreboardObjective gameInfo;

    private int mobCount = 0;

    @Override
    public void onInitializeServer() {
        instance = this; // Set the mod's instance.

        this.increaseSpawns(); // Add the modifications to the biomes.
        this.registerListeners(); // Register the event listeners.
        this.loadDatabase(); // Loads the JSON database from the server.

        ZombiesV2.logger.info("ZombiesV2 has been initialized!");
    }

    /**
     * Invoked when the dedicated server loads.
     */
    public void onLoadServer() {
        // Create a scoreboard objective for game info.
        var scoreboard = minecraftServer.getScoreboard();
        if (!scoreboard.containsObjective("gameInfo"))
            this.gameInfo = scoreboard.addObjective("gameInfo",
                    ScoreboardCriterion.DUMMY,
                    Text.of("Game Info"),
                    ScoreboardCriterion.RenderType.INTEGER
            );
        else this.gameInfo = scoreboard.getObjective("gameInfo");

        // Create a scoreboard for deaths.
        if (!scoreboard.containsObjective("deaths"))
            scoreboard.addObjective("deaths",
                    ScoreboardCriterion.DEATH_COUNT,
                    Text.of("Deaths"),
                    ScoreboardCriterion.RenderType.INTEGER
            );

        this.timeManager.initialize(); // Initialize the time manager.
        this.difficultyManager.initialize(); // Initialize the difficulty manager.

        // Load the names from the resource.
        NameHelper.loadNames();
    }

    /**
     * Invoked when the dedicated server ticks.
     */
    public void onTick() {
        this.timeManager.tick(); // Tick the time manager.
        this.difficultyManager.tick(); // Tick the difficulty manager.
    }

    /**
     * Invoked when the dedicated server is shutting down.
     */
    public void onShutdown() {
        this.timeManager.shutdown(); // Shutdown the time manager.
        this.difficultyManager.shutdown(); // Shutdown the difficulty manager.

        this.saveDatabase(); // Saves the JSON database to the server.
    }

    /**
     * Adds additional spawn rules to biomes.
     */
    private void increaseSpawns() {
        BiomeModifications
                .create(ZombiesV2.id("general"))
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.all(), context -> {
                    context.getSpawnSettings().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(
                            Objects.requireNonNull(Registry.ENTITY_TYPE.get(RegistryKey.of(
                                    Registry.ENTITY_TYPE_KEY, new Identifier("minecraft:zombie")))),
                            300, 100, 100));
                    context.getSpawnSettings().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(
                            Objects.requireNonNull(Registry.ENTITY_TYPE.get(RegistryKey.of(
                                    Registry.ENTITY_TYPE_KEY, new Identifier("minecraft:skeleton")))),
                            150, 100, 100));
                    context.getSpawnSettings().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(
                            Objects.requireNonNull(Registry.ENTITY_TYPE.get(RegistryKey.of(
                                    Registry.ENTITY_TYPE_KEY, new Identifier("minecraft:enderman")))),
                            80, 5, 15));
                    context.getSpawnSettings().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(
                            Objects.requireNonNull(Registry.ENTITY_TYPE.get(RegistryKey.of(
                                    Registry.ENTITY_TYPE_KEY, new Identifier("minecraft:drowned")))),
                            300, 100, 100));
                    context.getSpawnSettings().addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(
                            Objects.requireNonNull(Registry.ENTITY_TYPE.get(RegistryKey.of(
                                    Registry.ENTITY_TYPE_KEY, new Identifier("minecraft:piglin")))),
                            50, 1, 5));
                });

        try {
            // Change the way zombies spawn.
            SpawnRestriction.register(EntityType.ZOMBIE, SpawnRestriction.Location.ON_GROUND,
                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this::zombieSpawnPredicate);
            SpawnRestriction.register(EntityType.DROWNED, SpawnRestriction.Location.ON_GROUND,
                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this::zombieSpawnPredicate);
        } catch (Exception ignored) { }
    }

    /**
     * Registers the event listeners.
     */
    private void registerListeners() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            handler.player.sendMessage(Text.of("have fun in hell!!"));
            this.timeManager.onPlayerJoin(handler.player);
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof MethodsPlayerEntity player) {
                player.onPlayerDeath();
            }

            if (entity instanceof ZombieEntity ||
                    entity instanceof SkeletonEntity ||
                    entity instanceof PiglinEntity ||
                    entity instanceof EndermanEntity) {
                mobCount--;
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
            if (entity instanceof MethodsZombieEntity zombie) {
                zombie.setupEquipment(this.gameState.difficulty >= 80);
                zombie.assignId();
            }
        }));

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            var holdingItem = player.getStackInHand(hand);
            var progress = player.getAttackCooldownProgress(0.5f);
            if (holdingItem.getItem() instanceof MethodsAxeItem item &&
                    progress >= 0.9f && this.gameState.difficulty >= 70)
                item.onAttack(player, holdingItem);
            if (holdingItem.getItem() instanceof MethodsItem item &&
                    progress >= 0.9f && this.gameState.difficulty >= 70)
                item.onAttack(player);
            return ActionResult.PASS;
        });
    }

    /**
     * Loads the JSON database from the server.
     */
    private void loadDatabase() {
        var databaseFile = new File("zombies.json");

        try {
            if (this.checkDatabase(databaseFile)) return;

            // Read the data from the file.
            this.gameState = ZombiesV2.gson.fromJson(new FileReader(databaseFile), GameState.class);
            if (this.gameState == null) this.gameState = new GameState();

            ZombiesV2.logger.debug("Successfully loaded the zombies.json file!");
        } catch (IOException ignored) { }

        // Check the player state folder.
        var playerStateFolder = new File("players");
        if (!playerStateFolder.exists() && !playerStateFolder.mkdirs()) {
            ZombiesV2.logger.error("Failed to create the players folder!");
        }
    }

    /**
     * Saves the JSON database to the file system.
     */
    private void saveDatabase() {
        var databaseFile = new File("zombies.json");
        try {
            if (this.checkDatabase(databaseFile)) return;

            // Write the data to the file.
            var data = ZombiesV2.gson.toJson(this.gameState);
            Files.write(databaseFile.toPath(), data.getBytes());

            ZombiesV2.logger.debug("Successfully saved the zombies.json file!");
        } catch (IOException ignored) { }
    }

    /**
     * Validates the database file.
     * @param databaseFile The database file.
     * @return Whether the database file is valid.
     * @throws IOException If an I/O error occurs.
     */
    private boolean checkDatabase(File databaseFile) throws IOException {
        if (!databaseFile.exists() && !databaseFile.createNewFile()) {
            ZombiesV2.logger.error("Failed to create the zombies.json file!");
            return true;
        }

        if (!databaseFile.canRead()) {
            ZombiesV2.logger.error("Failed to read the zombies.json file!");
            return true;
        }

        if (!databaseFile.canWrite()) {
            ZombiesV2.logger.error("Failed to write to the zombies.json file!");
            return true;
        }
        return false;
    }

    /**
     * The custom predicate for spawning zombies.
     * @param type The type of entity.
     * @param world The world.
     * @param spawnReason The reason for spawning.
     * @param pos The position.
     * @param random The random.
     * @return Whether the entity can spawn.
     */
    private boolean zombieSpawnPredicate(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (world.getDifficulty() == Difficulty.PEACEFUL ||
                !HostileEntity.canMobSpawn(type, world, spawnReason, pos, random))
            return false;

        // Check for nearby players.
        var player = world.getClosestPlayer(pos.getX(), pos.getY(),
                pos.getZ(), 50, false);
        if (player == null) return false;

        // Check the mob count.
        if (this.mobCount >= Math.max(400,
                this.gameState.difficulty * 32))
            return false; this.mobCount++;

        var underground = ZombiesV2.isUnderground(world, pos);
        return (ZombiesV2.getInstance().getTimeManager().isDay() && underground) ||
                (ZombiesV2.getInstance().getTimeManager().isNight() && !underground);
    }

    /**
     * Checks if the position is underground.
     * @param world The world.
     * @param pos The position.
     * @return Whether the position is underground.
     */
    private static boolean isUnderground(ServerWorldAccess world, BlockPos pos) {
        for (var y = pos.getY(); y <= world.getTopY(); y++) {
            if (!world.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).isAir())
                return true;
        }

        return false;
    }
}
