package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * bonus() data-flow tests. Line numbers are lines of DataFlowTargets.java.
 * Pair notation: variable:defLine->useLine (42T / 42F = true / false branch of the p-use at line 42).
 */
class BonusTest {

    // ---------------- Stage 1: ALL-DEFS (2 tests) ----------------

    @Test
    @DisplayName("B01 all-defs: present>=threshold and best>=5, with an absent day inside the streak run. "
            + "Reaches a use for every def except score@49 (killed by score@51)")
    void b01_longStreakWithBreak_thr3() {
        assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 0, 1}, 3));
    }

    @Test
    @DisplayName("B02 all-defs: present<threshold and best<5. Reaches score:49->56")
    void b02_belowThreshold_thr5() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {1, 1, 1}, 5));
    }

    // ---------------- Stage 2: ALL-DU-PAIRS (tests added on top of B01, B02) ----------------

    @Test
    @DisplayName("B03 null attendance throws. Covers attendance:31->32T")
    void b03_nullAttendance_throws() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 3));
    }

    @Test
    @DisplayName("B04 negative threshold throws. Covers threshold:31->32T")
    void b04_negativeThreshold_throws() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[] {1}, -1));
    }

    @Test
    @DisplayName("B05 empty array, threshold 0: loop never runs, present@35 reaches 50T. "
            + "Covers present:35->50T, best:37->53F, score:51->56")
    void b05_emptyAttendance_thr0() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {}, 0));
    }

    @Test
    @DisplayName("B06 empty array, threshold 1. Covers present:35->50F")
    void b06_emptyAttendance_thr1() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {}, 1));
    }

    @Test
    @DisplayName("B07 present<threshold but best>=5: score@49 reaches the c-use at 54. Covers score:49->54")
    void b07_belowThresholdLongStreak() {
        assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 10));
    }

    // ---------------- Stage 3: test added to kill mutant M2 (best = present) ----------------

    @Test
    @DisplayName("B08 kills M2: 5 present days but the longest streak is only 3 -> no streak bonus. "
            + "Adds no new DU pair; checks that best holds the streak, not the present count")
    void b08_fivePresentLongestStreakThree() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 0, 1, 1, 1}, 1));
    }
}
