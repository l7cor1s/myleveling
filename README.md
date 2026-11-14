# MyLeveling

NeoForge **1.21.1** progression mod: ranks, Skill/Passive points, six skill branches with actives D–S, quest journal, bosses, ranked loot, and a Rank S Gate endgame.

Works standalone. Soft bonuses when Create / Iron's Spells / Epic Fight / Apotheosis / MCA are present.

## Progression

| Rank | XP | Passive Points |
|------|----|----------------|
| E | 0 | — |
| D | 5 000 | 1 |
| C | 20 000 | 2 |
| B | 60 000 | 2 |
| A | 150 000 | 3 |
| S | 400 000 | 3 |

- **Skill Points (SP):** 1 every **400 XP** → small permanent upgrades
- **Passive Points (PP):** on rank-up → passives (1 of 2 per rank/branch) and actives
- **End goal:** reach Rank S, open a Rank Gate with a Gate Key, defeat the Gate Monarch

## Skills & HUD

- Actives on every rank D–S per branch (max **2** equipped)
- Left-center HUD icons (R/F) with cooldowns; quest progress listed below
- GUI (**K**): Quests tab + branches + SP shop + reset
- Example ult: **Hammer Strike** (Therizla-style AoE + knockback + slow)

## Quests

Daily, repeatable, chain, boss, mob, gather, villager/MCA soft quests. Accept / turn-in from the Quests tab. Rewards: XP, PP, ranked loot.

## Bosses & loot

- Mini: Shadow Scout (D–C) · Mid: Rank Warden (B–A) · Final: Gate Monarch (S+)
- Ranked weapons / armor / accessories E–S with bleed, lifesteal, pierce, block/reflect, accessory effects

## Controls & commands

- **K** — System GUI · **R / F** — active slots
- `/rank` · `/skills`
- `/myleveling spawn <scout|warden|monarch>` · `/myleveling givekey` (op)

## Build

```bash
./gradlew build
```

Jar: `build/libs/myleveling-0.5.0.jar`
