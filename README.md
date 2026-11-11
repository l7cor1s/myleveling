# MyLeveling

NeoForge **1.21.1** mod: slow rank progression, mob XP tiers, and a skill tree.

Soft-compatible with Create, Iron's Spells 'n Spellbooks, Epic Fight, Apotheosis, MCA Reborn, Farmer's Delight, Kaleidoscope — **no hard dependencies**.

## Features

### Ranks & XP
- Ranks: **E → D → C → B → A → S** (high thresholds)
- XP only from combat kills (not players / villagers / MCA)
- Tiers:
  - **Normal** — base 6 XP
  - **Strong** — base 28 + health/10 (tag or HP ≥ 40)
  - **Boss** — base 250 + health/4 (tag, `Boss` interface, Warden, or HP ≥ 200)
- Pack makers can extend `#myleveling:bosses`, `#myleveling:strong_mobs`, `#myleveling:no_xp`

| Rank | XP | Skill points on reach |
|------|----|------------------------|
| E | 0 | — |
| D | 5 000 | 2 |
| C | 20 000 | 2 |
| B | 60 000 | 3 |
| A | 150 000 | 3 |
| S | 400 000 | 4 |

### Skill tree
Branches: **Melee**, **Magic** (ISS-themed), **Engineering** (Create-themed), **Survival**.

- Spend points with `/skills unlock <id>`
- Effects use unique attribute modifiers (stack with Epic Fight / Apotheosis)
- Progress (XP, points, unlocked skills) persists via data attachments (`copyOnDeath`)

### System GUI
- Press **K** to open the Solo Leveling–style skill tree
- Glowing rank, XP bar, skill points, 4 vertical branches
- Hover for details, click to unlock (validated on server)

### Commands
- `/rank` — rank, XP, skill points
- `/skills` / `/skills list [branch]` — tree overview
- `/skills info <id>` — skill details
- `/skills unlock <id>` — unlock a skill

## Build

```bash
./gradlew build
```

Jar: `build/libs/myleveling-0.3.0.jar`

## Layout

```
com.l7cor1s.myleveling/
  MyLeveling.java
  attachment/ ModAttachments
  progress/   PlayerProgress
  rank/       Rank, PlayerRankData
  xp/         MobTier, MobXpCalculator
  skill/      Skills catalog + unlock API
  event/      XP + skill sync
  command/    /rank, /skills
  compat/     soft ModList checks
  tag/        entity type tags
```
