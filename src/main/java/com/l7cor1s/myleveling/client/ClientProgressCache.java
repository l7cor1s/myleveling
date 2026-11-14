package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.progress.PlayerProgress;

/**
 * Client-side mirror of player progress. Safe to reference from network handlers;
 * contains no Minecraft client-only types.
 */
public final class ClientProgressCache {
    private static PlayerProgress progress = PlayerProgress.EMPTY;

    private ClientProgressCache() {
    }

    public static void set(PlayerProgress value) {
        progress = value == null ? PlayerProgress.EMPTY : value;
    }

    public static PlayerProgress get() {
        return progress;
    }

    public static void clear() {
        progress = PlayerProgress.EMPTY;
    }
}
