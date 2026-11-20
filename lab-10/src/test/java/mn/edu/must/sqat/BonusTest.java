package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BonusTest {

    @Test
    @DisplayName("all-defs empty: attendance34, present35, streak36, best37, score50")
    void emptyAttendanceCoversInitialDefinitions() {
        assertEquals(0, DataFlowTargets.bonus(new int[0], 1));
    }

    @Test
    @DisplayName("all-defs present: a38, present41, streak42, best44, score52")
    void onePresentDayCoversPositiveDefinitions() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {1}, 1));
    }

    @Test
    @DisplayName("du-pairs fivePresent: streak44, best44, score55")
    void fivePresentDaysCoverBestUpdateAndBonus() {
        assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 1));
    }

    @Test
    @DisplayName("du-pairs brokenStreak: else46 kills streak and restarts it")
    void brokenStreakCoversElseKill() {
        assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 0, 1, 1, 1, 1, 1}, 4));
    }

    @Test
    @DisplayName("du-pairs thresholdFalse: present51 and score56")
    void thresholdFalseAndLongStreakUseScore() {
        assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("du-pairs thresholdZero: score52 and best54 false path")
    void zeroThresholdCoversImmediateThresholdBranch() {
        assertEquals(5, DataFlowTargets.bonus(new int[0], 0));
    }

    @Test
    @DisplayName("mutation-M2-kill: present count differs from best streak")
    void separatedPresentDaysDoNotEarnStreakBonus() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 0, 1, 1, 1}, 5));
    }

    @Test
    @DisplayName("invalid inputs: attendance null and negative threshold")
    void invalidInputsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 0));
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[0], -1));
    }
}
