package lol.magix.zombiesv2.states;

import lol.magix.zombiesv2.ZombiesV2;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a stage of the game.
 */
@Getter @Setter
public final class GameState {
    public int dayCount = 1, time = 0;
    public int difficulty = 1;

    public Map<String, PlayerState> playerStates = new HashMap<>();

    /**
     * Reloads the current game state from the file.
     */
    public void reload() {
        var databaseFile = new File("zombies.json");
        if (!databaseFile.exists()) return;

        try {
            // Read the data from the file.
            var fileState = ZombiesV2.getGson().fromJson(new FileReader(databaseFile), GameState.class);
            if (fileState == null) return;

            // Set the data.
            this.dayCount = fileState.dayCount;
            this.time = fileState.time;
            this.difficulty = fileState.difficulty;
            this.playerStates = fileState.playerStates;

            ZombiesV2.getLogger().debug("Successfully reloaded the game state!");
        } catch (IOException ignored) { }
    }
}
