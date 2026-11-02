package game.quest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestDefinitionAndSessionTest {
    private QuestDefinition definition(int limit, boolean ordered) {
        QuestScenario scenario = setup -> {};
        QuestConsequence consequence = new QuestConsequence() {
            public String onSuccess(QuestWorldContext world) { return "ok"; }
            public String onFailure(QuestWorldContext world) { return "failed"; }
        };
        return new QuestDefinition("Unit-test contract", 10, 20, 30, limit,
                ordered, scenario, consequence);
    }

    @Test void invalidContractsAreRejected() {
        QuestDefinition valid = definition(3, true);
        assertEquals(3, valid.turnLimit());
        assertThrows(IllegalArgumentException.class,
                () -> new QuestDefinition(" ", 10, 20, 30, 3, false,
                        valid.scenario(), valid.consequence()));
        assertThrows(IllegalArgumentException.class,
                () -> new QuestDefinition("Bad fee", -1, 20, 30, 3, false,
                        valid.scenario(), valid.consequence()));
        assertThrows(IllegalArgumentException.class, () -> definition(0, false));
    }

    @Test void deadlineAllowsAllAdvertisedTurns() {
        for (int limit : new int[]{1, 3, 45}) {
            QuestSession session = new QuestSession(definition(limit, false));
            assertEquals(limit, session.turnsRemaining());
            session.tick(); // engine's initial arena tick: no playable action yet
            assertEquals(limit, session.turnsRemaining());
            for (int i = 1; i < limit; i++) {
                session.tick();
                assertEquals(limit - i, session.turnsRemaining());
                assertFalse(session.hasExpired());
            }
            session.tick();
            assertEquals(0, session.turnsRemaining());
            assertTrue(session.hasExpired());
            session.tick();
            assertEquals(0, session.turnsRemaining(), "Timer may not go negative");
        }
    }

    @Test void orderedAndUnorderedObjectivesBehaveDifferently() {
        QuestObjectiveGround first = new QuestObjectiveGround(null, null, "First");
        QuestObjectiveGround second = new QuestObjectiveGround(null, null, "Second");
        QuestSession ordered = new QuestSession(definition(5, true));
        ordered.addObjective(first);
        ordered.addObjective(second);
        assertEquals(2, ordered.totalObjectives());
        assertTrue(ordered.contains(first));
        assertFalse(ordered.isNextObjective(second));
        assertTrue(ordered.isNextObjective(first));
        ordered.completeObjective();
        assertTrue(ordered.isNextObjective(second));
        assertTrue(ordered.isFinalObjective());
        ordered.completeObjective();
        assertTrue(ordered.isComplete());
        assertThrows(IllegalStateException.class, ordered::completeObjective);

        QuestSession unordered = new QuestSession(definition(5, false));
        unordered.addObjective(first);
        unordered.addObjective(second);
        assertTrue(unordered.isNextObjective(second));
    }
}

