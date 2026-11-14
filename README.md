# MyLeveling

NeoForge **1.21.1** progression mod: ranks, Skill/Passive points, six skill branches with actives D–S, quest journal, expanded boss roster, ranked loot, and a Rank S Gate endgame.

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

- **Skill Points (SP):** 1 every **400 XP**
- **Passive Points (PP):** on rank-up
- **End goal:** Rank S → Gate Key + Rank Gate → Gate Monarch

## Bosses (0.6.0)

Legacy: Shadow Scout · Rank Warden · Gate Monarch

**Mini D–C:** Grove Stalker, Dune Scorpion, Frost Howler, Bog Witch, Reef Leviathan, Ash Imp Lord  
**Mid B–A:** Thunder Colossus, Crystal Oracle, Crimson Summoner, Abyss Knight, Sandstorm Empress  
**Large S:** Void Reaper, Infernal Behemoth, Glacial Sovereign  

Unique attacks, telegraphs (windup/cast/dash/summon), phases on mid/large, Russian Solo Leveling-style lines, ranked + unique loot, weak shadow minions. Rare biome encounters + `/myleveling spawn`.

## Controls & commands

- **K** — System GUI · **R / F** — active slots
- `/rank` · `/skills`
- `/myleveling spawn <boss>` · `/myleveling listbosses` · `/myleveling givekey` (op)

## Build

```bash
./gradlew build
```

Jar: `build/libs/myleveling-0.6.0.jar`
