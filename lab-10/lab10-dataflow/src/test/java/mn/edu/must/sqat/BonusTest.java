package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BonusTest {

    // Stage 1: the minimum all-defs set. The two cases are necessary because
    // score=0 (line 49) and score=5 (line 51) are distinct definitions.
    @Test
    @DisplayName("ALL_DEFS_score_initial_definition_reaches_bonus_addition")
    void allDefs_initialScoreDefinitionReachesBonusAddition() {
        assertEquals(3, DataFlowTargets.bonus(
                new int[]{1, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("ALL_DEFS_score_base_definition_reaches_bonus_addition")
    void allDefs_baseScoreDefinitionReachesBonusAddition() {
        assertEquals(8, DataFlowTargets.bonus(
                new int[]{1, 1, 1, 1, 1}, 5));
    }

    // Stage 2: additional tests for predicate outcomes, loop paths, kills,
    // and definition-use pairs not exercised by the minimum all-defs set.
    @Test
    @DisplayName("DU_invalid_null_attendance_takes_guard_true")
    void invalidNullAttendanceTakesGuardTrue() {
        assertThrows(IllegalArgumentException.class,
                () -> DataFlowTargets.bonus(null, 0));
    }

    @Test
    @DisplayName("DU_invalid_negative_threshold_takes_guard_true")
    void invalidNegativeThresholdTakesGuardTrue() {
        assertThrows(IllegalArgumentException.class,
                () -> DataFlowTargets.bonus(new int[0], -1));
    }

    @Test
    @DisplayName("DU_empty_attendance_covers_zero_iterations_and_threshold_true")
    void emptyAttendanceCoversZeroIterationsAndThresholdTrue() {
        assertEquals(5, DataFlowTargets.bonus(new int[0], 0));
    }

    @Test
    @DisplayName("DU_all_absent_covers_else_reset_and_no_bonus")
    void allAbsentCoversElseResetAndNoBonus() {
        assertEquals(0, DataFlowTargets.bonus(new int[]{0, 0, 0}, 1));
    }

    @Test
    @DisplayName("DU_mixed_attendance_covers_streak_kill_and_restart")
    void mixedAttendanceCoversStreakKillAndRestart() {
        assertEquals(5, DataFlowTargets.bonus(
                new int[]{1, 1, 0, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("DU_long_streak_covers_best_update_and_additional_score")
    void longStreakCoversBestUpdateAndAdditionalScore() {
        assertEquals(8, DataFlowTargets.bonus(
                new int[]{1, 1, 1, 1, 1, 1}, 6));
    }

    @Test
    @DisplayName("DU_short_streak_covers_best_false_and_base_score_only")
    void shortStreakCoversBestFalseAndBaseScoreOnly() {
        assertEquals(5, DataFlowTargets.bonus(
                new int[]{1, 1, 1, 0, 1}, 4));
    }
    @Test
    @DisplayName("DU_M2_kill_best_must_track_longest_consecutive_streak")
    void duM2KillBestMustTrackLongestConsecutiveStreak() {
        // Five total present days but no streak reaches five.
        assertEquals(0, DataFlowTargets.bonus(
                new int[]{1, 1, 0, 1, 1, 1}, 6));
    }

}
