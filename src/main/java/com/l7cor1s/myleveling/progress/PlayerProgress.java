package com.l7cor1s.myleveling.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * All persistent player leveling data in one attachment.
 */
public record PlayerProgress(int xp, int skillPoints, Set<String> unlockedSkills) {
    public static final PlayerProgress EMPTY = new PlayerProgress(0, 0, Set.of());

    public static final Codec<PlayerProgress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("xp", 0).forGetter(PlayerProgress::xp),
            Codec.INT.optionalFieldOf("skill_points", 0).forGetter(PlayerProgress::skillPoints),
            Codec.STRING.listOf()
                    .xmap(PlayerProgress::toSet, PlayerProgress::toList)
                    .optionalFieldOf("unlocked_skills", Set.of())
                    .forGetter(PlayerProgress::unlockedSkills)
    ).apply(instance, PlayerProgress::new));

    public PlayerProgress {
        unlockedSkills = Set.copyOf(unlockedSkills);
    }

    public PlayerProgress withXp(int newXp) {
        return new PlayerProgress(Math.max(0, newXp), skillPoints, unlockedSkills);
    }

    public PlayerProgress withSkillPoints(int points) {
        return new PlayerProgress(xp, Math.max(0, points), unlockedSkills);
    }

    public PlayerProgress addSkillPoints(int amount) {
        return withSkillPoints(skillPoints + Math.max(0, amount));
    }

    public PlayerProgress withUnlocked(Set<String> skills) {
        return new PlayerProgress(xp, skillPoints, skills);
    }

    public boolean hasSkill(String skillId) {
        return unlockedSkills.contains(skillId);
    }

    private static Set<String> toSet(List<String> list) {
        return Set.copyOf(new HashSet<>(list));
    }

    private static List<String> toList(Set<String> set) {
        return List.copyOf(new ArrayList<>(set));
    }
}
