package com.leonardobishop.quests.common.quest;

import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgressFile;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuestCooldownModeTest {

    @Test
    void defaultCooldownModeIsCompletion() {
        final Quest quest = new Quest.Builder("default").build();

        assertEquals(Quest.CooldownMode.COMPLETION, quest.getCooldownMode());
    }

    @Test
    void cooldownModeFromConfigValueAcceptsAcceptanceOnly() {
        assertEquals(Quest.CooldownMode.ACCEPTANCE, Quest.CooldownMode.fromConfigValue("acceptance"));
        assertEquals(Quest.CooldownMode.ACCEPTANCE, Quest.CooldownMode.fromConfigValue(" AcCePtAnCe "));
        assertEquals(Quest.CooldownMode.COMPLETION, Quest.CooldownMode.fromConfigValue("completion"));
        assertEquals(Quest.CooldownMode.COMPLETION, Quest.CooldownMode.fromConfigValue("started"));
        assertEquals(Quest.CooldownMode.COMPLETION, Quest.CooldownMode.fromConfigValue(null));
    }

    @Test
    void completionModeCooldownUsesCompletionDate() {
        final long now = System.currentTimeMillis();
        final Quest quest = cooldownQuest("completion", Quest.CooldownMode.COMPLETION);
        final QuestProgressFile questProgressFile = progressFileWith(
                quest,
                now - minutes(30),
                true,
                now - minutes(2)
        );

        final long remaining = questProgressFile.getCooldownFor(quest);

        assertTrue(remaining > minutes(7));
        assertTrue(remaining <= minutes(8) + TimeUnit.SECONDS.toMillis(5));
    }

    @Test
    void acceptanceModeCooldownUsesStartedDate() {
        final long now = System.currentTimeMillis();
        final Quest quest = cooldownQuest("acceptance", Quest.CooldownMode.ACCEPTANCE);
        final QuestProgressFile questProgressFile = progressFileWith(
                quest,
                now - minutes(2),
                true,
                now - minutes(1)
        );

        final long remaining = questProgressFile.getCooldownFor(quest);

        assertTrue(remaining > minutes(7));
        assertTrue(remaining <= minutes(8) + TimeUnit.SECONDS.toMillis(5));
    }

    @Test
    void acceptanceModeCooldownDoesNotApplyUntilCompleted() {
        final long now = System.currentTimeMillis();
        final Quest quest = cooldownQuest("incomplete", Quest.CooldownMode.ACCEPTANCE);
        final QuestProgressFile questProgressFile = progressFileWith(
                quest,
                now - minutes(2),
                false,
                0L
        );

        assertEquals(-1L, questProgressFile.getCooldownFor(quest));
    }

    private static Quest cooldownQuest(final String id, final Quest.CooldownMode cooldownMode) {
        return new Quest.Builder(id)
                .withCooldownEnabled(true)
                .withCooldown(10)
                .withCooldownMode(cooldownMode)
                .build();
    }

    private static QuestProgressFile progressFileWith(final Quest quest, final long startedDate, final boolean completed, final long completionDate) {
        final UUID playerUUID = UUID.randomUUID();
        final QuestProgressFile questProgressFile = new QuestProgressFile(null, playerUUID);
        questProgressFile.addQuestProgress(new QuestProgress(null, quest.getId(), playerUUID, false, startedDate, completed, completed, completionDate, false));
        return questProgressFile;
    }

    private static long minutes(final long minutes) {
        return TimeUnit.MINUTES.toMillis(minutes);
    }
}
