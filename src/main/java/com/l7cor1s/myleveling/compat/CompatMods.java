package com.l7cor1s.myleveling.compat;

import net.neoforged.fml.ModList;

/**
 * Soft presence checks. MyLeveling never hard-depends on pack mods.
 */
public final class CompatMods {
    public static final String CREATE = "create";
    public static final String IRONS_SPELLBOOKS = "irons_spellbooks";
    public static final String EPIC_FIGHT = "epicfight";
    public static final String APOTHEOSIS = "apotheosis";
    public static final String MCA = "mca";
    public static final String FARMERS_DELIGHT = "farmersdelight";
    public static final String KALEIDOSCOPE_COOKERY = "kaleidoscope_cookery";

    private CompatMods() {
    }

    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static void logDetected() {
        logIfPresent(CREATE, "Create");
        logIfPresent(IRONS_SPELLBOOKS, "Iron's Spells 'n Spellbooks");
        logIfPresent(EPIC_FIGHT, "Epic Fight");
        logIfPresent(APOTHEOSIS, "Apotheosis");
        logIfPresent(MCA, "MCA Reborn");
        logIfPresent(FARMERS_DELIGHT, "Farmer's Delight");
        logIfPresent(KALEIDOSCOPE_COOKERY, "Kaleidoscope Cookery");
    }

    private static void logIfPresent(String modId, String displayName) {
        if (isLoaded(modId)) {
            com.l7cor1s.myleveling.MyLeveling.LOGGER.info("Compat: detected {} ({})", displayName, modId);
        }
    }
}
