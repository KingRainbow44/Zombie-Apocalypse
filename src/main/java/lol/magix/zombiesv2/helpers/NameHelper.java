package lol.magix.zombiesv2.helpers;

import lol.magix.zombiesv2.ZombiesV2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface NameHelper {
    // List of random names.
    List<String> NAMES = new ArrayList<>();

    /**
     * Loads all the names from a resource.
     */
    static void loadNames() {
        try (var resource = ZombiesV2.class.getResourceAsStream("/names.txt")) {
            if (resource == null) return; // Resource not found.
            var content = new String(resource.readAllBytes());

            // Parse the names.
            var names = List.of(content.split("\n"));
            names.forEach(name -> NAMES.add(name.trim().split(",")[0]));

            ZombiesV2.getLogger().info("Loaded names!");
        } catch (IOException ignored) { }
    }

    /**
     * Gets a random name.
     * @return The random name.
     */
    static String zombieName() {
        return NAMES.get((int) Math.floor(Math.random() * NAMES.size()));
    }
}
