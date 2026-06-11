# Previous Changes

## Cooldown Mode Support

Commit: `99ee10d Add cooldown mode support for quests`

This change added a per-quest cooldown mode option:

```yaml
options:
  cooldown:
    enabled: true
    time: 1440
    mode: completion # completion or acceptance
```

Supported values:

- `completion`: cooldown timing starts from the existing quest completion timestamp.
- `acceptance`: cooldown timing starts from the quest started timestamp.

The default remains `completion`, so existing quest configs keep their previous behavior unless they explicitly set `mode: acceptance`.

## Behavior

- `completion` mode preserves the original Quests behavior.
- `acceptance` mode uses the quest's `startedDate`, but only blocks replay after the quest has been completed.
- Quest completion no longer overwrites `startedDate`, so acceptance-mode cooldowns keep the original acceptance timestamp.
- No storage migration is needed because `startedDate` is already persisted.

## Changed Files

- `common/src/main/java/com/leonardobishop/quests/common/quest/Quest.java`
- `common/src/main/java/com/leonardobishop/quests/common/player/questprogressfile/QuestProgressFile.java`
- `bukkit/src/main/java/com/leonardobishop/quests/bukkit/config/BukkitQuestsLoader.java`
- `bukkit/src/main/java/com/leonardobishop/quests/bukkit/questcontroller/NormalQuestController.java`

## Validation

- `git show --stat HEAD` showed the cooldown-mode commit as 6 files changed, 48 insertions, and 9 deletions.
- A Java 21 downgraded jar was tested successfully on-server after the feature was added.

## Notes

- This note documents only the cooldown-mode feature.
- It intentionally excludes the temporary local NuVotifier compile dependency workaround.
