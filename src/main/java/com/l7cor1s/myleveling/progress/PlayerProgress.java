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
 * Persistent leveling / quest / endgame state.
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
        float mana,
        Set<String> activeQuests,
        Set<String> completedQuests,
        Map<String, Integer> questProgress,
        long dailyQuestDay,
        boolean monarchDefeated
) {
    public static final int SKILL_POINT_XP_INTERVAL = 400;
    public static final int MAX_EQUIPPED_ACTIVES = 2;
    public static final float BASE_MAX_MANA = 20.0f;
    public static final int RESET_LEVEL_COST = 5;

    public static final PlayerProgress EMPTY = new PlayerProgress(
            0, 0, 0, Map.of(), Set.of(), Set.of(), List.of(), Map.of(), BASE_MAX_MANA,
            Set.of(), Set.of(), Map.of(), 0L, false
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
            Codec.FLOAT.optionalFieldOf("mana", BASE_MAX_MANA).forGetter(PlayerProgress::mana),
            Codec.STRING.listOf().xmap(PlayerProgress::toSet, PlayerProgress::toList)
                    .optionalFieldOf("active_quests", Set.of()).forGetter(PlayerProgress::activeQuests),
            Codec.STRING.listOf().xmap(PlayerProgress::toSet, PlayerProgress::toList)
                    .optionalFieldOf("completed_quests", Set.of()).forGetter(PlayerProgress::completedQuests),
            Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("quest_progress", Map.of()).forGetter(PlayerProgress::questProgress),
            Codec.LONG.optionalFieldOf("daily_quest_day", 0L).forGetter(PlayerProgress::dailyQuestDay),
            Codec.BOOL.optionalFieldOf("monarch_defeated", false).forGetter(PlayerProgress::monarchDefeated)
    ).apply(instance, PlayerProgress::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerProgress> STREAM_CODEC = StreamCodec.of(
            PlayerProgress::write, PlayerProgress::read
    );

    public PlayerProgress {
        minorLevels = Map.copyOf(minorLevels);
        selectedPassives = Set.copyOf(selectedPassives);
        unlockedActives = Set.copyOf(unlockedActives);
        equippedActives = List.copyOf(equippedActives);
        cooldownEnds = Map.copyOf(cooldownEnds);
        activeQuests = Set.copyOf(activeQuests);
        completedQuests = Set.copyOf(completedQuests);
        questProgress = Map.copyOf(questProgress);
    }

    public PlayerProgress withXp(int newXp) {
        return copy(newXp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress withSkillPoints(int points) {
        return copy(xp, Math.max(0, points), passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress addSkillPoints(int amount) {
        return withSkillPoints(skillPoints + Math.max(0, amount));
    }

    public PlayerProgress withPassivePoints(int points) {
        return copy(xp, skillPoints, Math.max(0, points), minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress addPassivePoints(int amount) {
        return withPassivePoints(passivePoints + Math.max(0, amount));
    }

    public PlayerProgress withMinors(Map<String, Integer> minors) {
        return copy(xp, skillPoints, passivePoints, minors, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress withPassives(Set<String> passives) {
        return copy(xp, skillPoints, passivePoints, minorLevels, passives, unlockedActives, equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress withUnlockedActives(Set<String> actives) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, actives, equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress withEquipped(List<String> equipped) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equipped, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress withCooldowns(Map<String, Long> cooldowns) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldowns, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress withMana(float value) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, Math.max(0, value), activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    public PlayerProgress withQuests(Set<String> active, Set<String> completed, Map<String, Integer> progress, long day) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana, active, completed, progress, day, monarchDefeated);
    }

    public PlayerProgress withMonarchDefeated(boolean value) {
        return copy(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives, equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, value);
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

    public int questCount(String questId) {
        return questProgress.getOrDefault(questId, 0);
    }

    public PlayerProgress resetPassivesAndActives(int refundedPassivePoints) {
        return copy(xp, skillPoints, refundedPassivePoints, minorLevels, Set.of(), Set.of(), List.of(), Map.of(), mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    private static PlayerProgress copy(
            int xp, int skillPoints, int passivePoints,
            Map<String, Integer> minorLevels, Set<String> selectedPassives, Set<String> unlockedActives,
            List<String> equippedActives, Map<String, Long> cooldownEnds, float mana,
            Set<String> activeQuests, Set<String> completedQuests, Map<String, Integer> questProgress,
            long dailyQuestDay, boolean monarchDefeated
    ) {
        return new PlayerProgress(xp, skillPoints, passivePoints, minorLevels, selectedPassives, unlockedActives,
                equippedActives, cooldownEnds, mana, activeQuests, completedQuests, questProgress, dailyQuestDay, monarchDefeated);
    }

    private static void write(RegistryFriendlyByteBuf buf, PlayerProgress p) {
        ByteBufCodecs.VAR_INT.encode(buf, p.xp);
        ByteBufCodecs.VAR_INT.encode(buf, p.skillPoints);
        ByteBufCodecs.VAR_INT.encode(buf, p.passivePoints);
        writeIntMap(buf, p.minorLevels);
        writeStrings(buf, p.selectedPassives);
        writeStrings(buf, p.unlockedActives);
        writeStringList(buf, p.equippedActives);
        ByteBufCodecs.VAR_INT.encode(buf, p.cooldownEnds.size());
        p.cooldownEnds.forEach((k, v) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, k);
            buf.writeLong(v);
        });
        buf.writeFloat(p.mana);
        writeStrings(buf, p.activeQuests);
        writeStrings(buf, p.completedQuests);
        writeIntMap(buf, p.questProgress);
        buf.writeLong(p.dailyQuestDay);
        buf.writeBoolean(p.monarchDefeated);
    }

    private static PlayerProgress read(RegistryFriendlyByteBuf buf) {
        int xp = ByteBufCodecs.VAR_INT.decode(buf);
        int sp = ByteBufCodecs.VAR_INT.decode(buf);
        int pp = ByteBufCodecs.VAR_INT.decode(buf);
        Map<String, Integer> minors = readIntMap(buf);
        Set<String> passives = readStringSet(buf);
        Set<String> unlocked = readStringSet(buf);
        List<String> equipped = readStringList(buf);
        int cdCount = ByteBufCodecs.VAR_INT.decode(buf);
        Map<String, Long> cds = new HashMap<>();
        for (int i = 0; i < cdCount; i++) {
            cds.put(ByteBufCodecs.STRING_UTF8.decode(buf), buf.readLong());
        }
        float mana = buf.readFloat();
        Set<String> activeQ = readStringSet(buf);
        Set<String> doneQ = readStringSet(buf);
        Map<String, Integer> qProg = readIntMap(buf);
        long day = buf.readLong();
        boolean monarch = buf.readBoolean();
        return new PlayerProgress(xp, sp, pp, minors, passives, unlocked, equipped, cds, mana, activeQ, doneQ, qProg, day, monarch);
    }

    private static void writeIntMap(RegistryFriendlyByteBuf buf, Map<String, Integer> map) {
        ByteBufCodecs.VAR_INT.encode(buf, map.size());
        map.forEach((k, v) -> {
            ByteBufCodecs.STRING_UTF8.encode(buf, k);
            ByteBufCodecs.VAR_INT.encode(buf, v);
        });
    }

    private static Map<String, Integer> readIntMap(RegistryFriendlyByteBuf buf) {
        int n = ByteBufCodecs.VAR_INT.decode(buf);
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(ByteBufCodecs.STRING_UTF8.decode(buf), ByteBufCodecs.VAR_INT.decode(buf));
        }
        return map;
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
