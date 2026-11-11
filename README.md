# MyLeveling

NeoForge 1.21.1 mod: player ranks and combat XP.

## Features (stage 1)

- Ranks: **E → D → C → B → A → S**
- XP from killing mobs (scales with mob max health)
- Rank promotes automatically at XP thresholds
- Rank & XP persist across death (data attachments)
- Chat message on rank-up
- `/rank` — show current rank, XP, and progress to next rank

### XP thresholds

| Rank | Required XP |
|------|-------------|
| E    | 0           |
| D    | 100         |
| C    | 350         |
| B    | 800         |
| A    | 1600        |
| S    | 3200        |

## Build

```bash
./gradlew build
```

Jar output: `build/libs/myleveling-0.1.0.jar`

## Run (dev)

```bash
./gradlew runClient
./gradlew runServer
```

## Project layout

```
src/main/java/com/l7cor1s/myleveling/
  MyLeveling.java              # mod entrypoint
  attachment/ModAttachments.java
  rank/Rank.java
  rank/PlayerRankData.java
  event/MobKillXpHandler.java
  command/RankCommand.java
```
