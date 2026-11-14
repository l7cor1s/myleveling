# MyLeveling

NeoForge **1.21.1** progression mod: ranks, Skill/Passive points, 6 skill branches, actives with HUD cooldowns.

Works standalone. Soft bonuses when Create / Iron's Spells / Epic Fight / Apotheosis are present.

## Progression

| Rank | XP | Passive Points |
|------|----|----------------|
| E | 0 | — |
| D | 5 000 | 1 |
| C | 20 000 | 2 |
| B | 60 000 | 2 |
| A | 150 000 | 3 |
| S | 400 000 | 3 |

- **Skill Points (SP):** 1 every **400 XP** → small permanent upgrades (HP, speed, luck, damage, mining…)
- **Passive Points (PP):** on rank-up → choose passives (1 of 2 per rank/branch) and unlock actives
- Mob XP tiers: Normal / Strong / Boss (datapack tags + health fallbacks)

## Branches

Melee · Ranged · Magic · Engineering · Survival · Hunter

Each branch: passives D→S (pick one of two) + actives at C and A.

## Controls

- **K** — System GUI
- **R / F** — Active slots 1 / 2 (HUD above hotbar shows cooldown)
- Reset button in GUI (10 gold **or** 5 levels) refunds PP

## Build

```bash
./gradlew build
```

Jar: `build/libs/myleveling-0.4.0.jar`
