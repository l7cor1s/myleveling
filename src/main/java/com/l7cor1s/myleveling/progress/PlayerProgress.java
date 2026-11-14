package com.l7cor1s.myleveling.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Persistent leveling state: XP, Skill/Passive points, minors, passives, actives, mana, cooldowns.
 */
public record PlayerProgress(
        int xp,
        int skillPoints,
        int passivePoints,
        Map<String, Integer> minorLevels,
        Set<String> selectedPassives,
        Set<String> unlockedActives,
        List<String> equippedActives,
        Map<String, Long> cooldownEnds,
        float mana
) {
    public static final int SKILL_POINT_XP_INTERVAL = 400;
    public static final int MAX_EQUIPPED_ACTIVES = 2;
    public static final float BASE_MAX_MANA = 20.0f;
    public static final int RESET_LEVEL_COST = 5;

    public static final PlayerProgress EMPTY = new PlayerProgress(
            0, 0, 0, Map.of(), Set.of(), Set.of(), List.of(), Map.of(), BASE_MAX_MANA
    );

    public static final Codec<PlayerProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("xp", 0).forGetter(PlayerProgress::xp),
            Codec.INT.optionalFieldOf("skill_points", 0).forGetter(PlayerProgress::skillPoints),
            Codec.INT.optionalFieldOf("passive_points", 0).forGetter(PlayerProgress::passivePoints),
            Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("minor_levels", Map.of()).forGetter(PlayerProgress::minorLevels),
            Codec.STRING.listOf().xmap(PlayerProgress::toSet, PlayerProgress::toList)
                    .optionalFieldOf("selected_passives", Set.of()).forGetter(PlayerProgress::selectedPassives),
            Codec.STRING.listOf().xmap(PlayerProgress::toSet, PlayerProgress::toList)
                    .optionalFieldOf("unlocked_actives", Set.of()).forGetter(PlayerProgress::unlockedActives),
            Codec.STRING.listOf().optionalFieldOf("equipped_actives", List.of()).forGetter(PlayerProgress::equippedActives),
            Codec.unboundedMap(Codec.STRING, Codec.LONG).optionalFieldOf("cooldown_ends", Map.of()).forGetter(PlayerProgress::cooldownEnds),
            Codec.FLOAT.optionalFieldOf("mana", BASE_MAX_MANA).forGetter(PlayerProgress::mana)
    ).apply(instance, PlayerProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerProgress> STREAM_CODEC = StreamCodec.of(
            PlayerProgress::write,
            PlayerProgress::read
    );

    public PlayerProgress {
        minorLevels = Map.copyOf(minorLevels);
        selectedPassives = Set.copyOf(selectedPassives);
        unlockedActives = Set.copyOf(unlockedActives);
        equippedActives = List.copyOf(equippedActives);
        cooldownEnds = Map.copyOf(cooldownEnds);
    }

    public PlayerProgress withXp(int newXp) {
        return copy(newXp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana);
    }

    public PlayerProgress withSkillPoints(int points) {
        return copy(xp, Math.max(0, points), passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana);
    }

    public PlayerProgress addSkillPoints(int amount) {
        return withSkillPoints(skillPoints + Math.max(0, amount));
    }

    public PlayerProgress withPassivePoints(int points) {
        return copy(xp, skillPoints, Math.max(0, points), minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana);
    }

    public PlayerProgress addPassivePoints(int amount) {
        return withPassivePoints(passivePoints + Math.max(0, amount));
    }

    public PlayerProgress withMinors(Map<String, Integer> minors) {
        return copy(xp, skillPoints, passivePoints, minors, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana);
    }

    public PlayerProgress withPassives(Set<String> passives) {
        return copy(xp, skillPoints, passivePoints, minorLevels, passives, unlockedActives, equippedActives, cooldownEnds, mana);
    }

    public PlayerProgress withUnlockedActives(Set<String> actives) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, actives, equippedActives, cooldownEnds, mana);
    }

    public PlayerProgress withEquipped(List<String> equipped) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equipped, cooldownEnds, mana);
    }

    public PlayerProgress withCooldowns(Map<String, Long> cooldowns) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldowns, mana);
    }

    public PlayerProgress withMana(float value) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, Math.max(0, value));
    }

    public boolean hasPassive(String id) {
        return selectedPassives.contains(id);
    }

    public boolean hasActiveUnlocked(String id) {
        return unlockedActives.contains(id);
    }

    public int minorLevel(String id) {
        return minorLevels.getOrDefault(id, 0);
    }

    public String equipped(int slot) {
        if (slot < 0 || slot >= equippedActives.size()) {
            return "";
        }
        return equippedActives.get(slot);
    }

    public boolean isOnCooldown(String activeId, long gameTime) {
        return cooldownEnds.getOrDefault(activeId, 0L) > gameTime;
    }

    public long cooldownRemaining(String activeId, long gameTime) {
        return Math.max(0L, cooldownEnds.getOrDefault(activeId, 0L) - gameTime);
    }

    public PlayerProgress resetPassivesAndActives(int refundedPassivePoints) {
        return copy(xp, skillPoints, refundedPassivePoints, minorLevels, Set.of(), Set.of(), List.of(), Map.of(), mana);
    }

    private static PlayerProgress copy(
            int xp,
            int skillPoints,
            int passivePoints,
            Map<String, Integer> minorLevels,
            Set<String> selectedPassives,
            Set<String> unlockedActives,
            List<String> equippedActives,
            Map<String, Long> cooldownEnds,
            float mana
    ) {
        return new PlayerProgress(
                xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana
        );
    }

    private static void write(RegistryFriendlyByteBuf buf, PlayerProgress progress) {
        ByteBufCodecs.VAR_INT.encode(buf, progress.xp);
        ByteBufCodecs.VAR_INT.encode(buf, progress.skillPoints);
        ByteBufCodecs.VAR_INT.encode(buf, progress.passivePoints);
        ByteBufCodecs.VAR_INT.encode(buf, progress.minorLevels.size());
        progress.minorLevels.forEach((k, v) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, k);
            ByteBufCodecs.VAR_INT.encode(buf, v);
        });
        writeStrings(buf, progress.selectedPassives);
        writeStrings(buf, progress.unlockedActives);
        writeStringList(buf, progress.equippedActives);
        ByteBufCodecs.VAR_INT.encode(buf, progress.cooldownEnds.size());
        progress.cooldownEnds.forEach((k, v) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, k);
            buf.writeLong(v);
        });
        buf.writeFloat(progress.mana);
    }

    private static PlayerProgress read(RegistryFriendlyByteBuf buf) {
        int xp = ByteBufCodecs.VAR_INT.decode(buf);
        int skillPoints = ByteBufCodecs.VAR_INT.decode(buf);
        int passivePoints = ByteBufCodecs.VAR_INT.decode(buf);
        int minorCount = ByteBufCodecs.VAR_INT.decode(buf);
        Map<String, Integer> minors = new HashMap<>();
        for (int i = 0; i < minorCount; i++) {
            minors.put(ByteBufCodecs.STRING_UTF8.decode(buf), ByteBufCodecs.VAR_INT.decode(buf));
        }
        Set<String> passives = readStringSet(buf);
        Set<String> unlocked = readStringSet(buf);
        List<String> equipped = readStringList(buf);
        int cdCount = ByteBufCodecs.VAR_INT.decode(buf);
        Map<String, Long> cds = new HashMap<>();
        for (int i = 0; i < cdCount; i++) {
            cds.put(ByteBufCodecs.STRING_UTF8.decode(buf), buf.readLong());
        }
        float mana = buf.readFloat();
        return new PlayerProgress(xp, skillPoints, passivePoints, minors, passives, unlocked, equipped, cds, mana);
    }

    private static void writeStrings(RegistryFriendlyByteBuf buf, Set<String> values) {
        writeStringList(buf, List.copyOf(values));
    }

    private static void writeStringList(RegistryFriendlyByteBuf buf, List<String> values) {
        ByteBufCodecs.VAR_INT.encode(buf, values.size());
        for (String value : values) {
            ByteBufCodecs.STRING_UTF8.encode(buf, value);
        }
    }

    private static Set<String> readStringSet(RegistryFriendlyByteBuf buf) {
        return Set.copyOf(readStringList(buf));
    }

    private static List<String> readStringList(RegistryFriendlyByteBuf buf) {
        int size = ByteBufCodecs.VAR_INT.decode(buf);
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(ByteBufCodecs.STRING_UTF8.decode(buf));
        }
        return list;
    }

    private static Set<String> toSet(List<String> list) {
        return Set.copyOf(new HashSet<>(list));
    }

    private static List<String> toList(Set<String> set) {
        return List.copyOf(new ArrayList<>(set));
    }
}
