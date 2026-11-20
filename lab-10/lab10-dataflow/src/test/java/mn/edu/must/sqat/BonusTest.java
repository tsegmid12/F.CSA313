package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusTest {

    @Test
    @DisplayName("ALL_DEFS_score_initial_definition_reaches_bonus_addition")
    void allDefs_initialScoreDefinitionReachesBonusAddition() {
        assertEquals(8, DataFlowTargets.bonus(
                new int[]{1, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("ALL_DEFS_score_base_definition_reaches_bonus_addition")
    void allDefs_baseScoreDefinitionReachesBonusAddition() {
        assertEquals(8, DataFlowTargets.bonus(
                new int[]{1, 1, 1, 1, 1}, 5));
    }
}
