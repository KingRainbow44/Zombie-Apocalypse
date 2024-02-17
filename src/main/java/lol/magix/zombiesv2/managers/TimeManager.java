package lol.magix.zombiesv2.managers;

import lol.magix.zombiesv2.ZombiesV2;
import lol.magix.zombiesv2.states.GameState;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.awt.*;

/**
 * Manages the day and night cycle in-game.
 */
public final class TimeManager {
    // private static final int DAY_LENGTH = 6000; // 5 minutes
    // private static final int NIGHT_LENGTH = 24000; // 20 minutes
    private static final int DAY_LENGTH = 3000; // 2.5 minutes
    private static final int NIGHT_LENGTH = 12000; // 10 minutes
    private static final int DAY_NIGHT_LENGTH = DAY_LENGTH + NIGHT_LENGTH;

    private GameState gameState = new GameState();
    private CommandBossBar bossbar;

    /**
     * Called when the mod initializes.
     */
    public void initialize() {
        var world = ZombiesV2.getMinecraftServer().getWorld(RegistryKey.of(
                Registry.WORLD_KEY, new Identifier("minecraft", "overworld")));
        if (world == null) {
            ZombiesV2.getLogger().warn("Failed to get the world!"); return;
        }

        this.gameState = ZombiesV2.getInstance().getGameState();
        this.bossbar = ZombiesV2.getMinecraftServer().getBossBarManager().add(ZombiesV2.id("bossbar_display"), Text.of("yk this is a test"));

        // Initialize the bossbar.
        this.bossbar.setThickenFog(false);
        this.bossbar.setDarkenSky(false);
        this.bossbar.setDragonMusic(false);
    }

    /**
     * Testing overload method.
     * @param days The days.
     * @param time The time.
     */
    public void initialize(int days, int time) {
        this.gameState.setDayCount(days);
        this.gameState.setTime(time);
    }

    /**
     * Called when the server performs a tick.
     */
    public void tick() {
        var currentTime = this.gameState.time++;

        if (currentTime >= DAY_NIGHT_LENGTH) {
            this.gameState.setTime(0);
            this.gameState.dayCount++;
            this.gameState.difficulty++;
        }

        if (this.isDay()) {
            if (this.bossbar.getColor() != BossBar.Color.YELLOW) this.bossbar.setColor(BossBar.Color.YELLOW);
            this.bossbar.setPercent(currentTime / (float) DAY_LENGTH);
        } else if (this.isNight()){
            if (this.bossbar.getColor() != BossBar.Color.BLUE) this.bossbar.setColor(BossBar.Color.BLUE);
            this.bossbar.setPercent((currentTime - DAY_LENGTH) / (float) NIGHT_LENGTH);
        }
        this.bossbar.setName(Text.of("Day: " + this.gameState.dayCount + "  |  Difficulty: " + Math.round(this.gameState.difficulty)));
    }

    /**
     * Called when the server shuts down.
     */
    public void shutdown() {

    }

    /**
     * Sets the current internal time.
     * @param time The time.
     */
    public void setCurrentTime(int time) {
        // Increase the difficulty if the time is set to 0.
        if (ZombiesV2.getInstance().getTimeManager().isNight()
                && time < TimeManager.DAY_LENGTH)
            this.gameState.difficulty++;

        this.gameState.setTime(time);
    }

    /**
     * Utility method for getting the amount of days passed.
     * @return Integer representation.
     */
    public int getDaysPassed() {
        return this.gameState.getDayCount();
    }

    /**
     * Utility method for getting the current time.
     * @return Integer duration in Minecraft ticks (20t = 1s).
     */
    public int getCurrentTime() {
        return this.gameState.getTime();
    }

    /**
     * Utility method for checking if it is currently day.
     * @return Boolean: true if it is day
     */
    public boolean isDay() {
        return this.getCurrentTime() < DAY_LENGTH;
    }

    /**
     * Utility method for checking if it is currently night.
     * @return Boolean: true if it is night
     */
    public boolean isNight() {
        return this.getCurrentTime() >= DAY_LENGTH;
    }

    /**
     * Converts the internal time to Minecraft time.
     * @return The Minecraft time.
     */
    public long internalTimeToMinecraftTime() {
        if (this.isDay()) {
            return Math.round(this.getCurrentTime() * 4);
        } else if (this.isNight()) {
            return Math.round(Math.floor(DAY_LENGTH * 4 + ((getCurrentTime() - DAY_LENGTH))));
        } else {
            // This **should** never be reached.
            // However, when do things actually ever go our way.
            throw new IllegalStateException("Neither day nor night?????!?!?!?!?!?!?!?!?!?!?!?!?!?!");
        }
    }

    /**
     * Invoked when the player joins the server.
     * @param player The player.
     */
    public void onPlayerJoin(ServerPlayerEntity player) {
        this.bossbar.addPlayer(player);
    }
}
