package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BonusTest {

    @Test
    @DisplayName("B1 all-defs: threshold missed; score 49->54->56; streak 46->41")
    void thresholdNotReachedWithLongStreak() {
        int[] attendance = {1, 1, 0, 1, 1, 1, 1, 1};

        assertEquals(3, DataFlowTargets.bonus(attendance, 8));
    }

    @Test
    @DisplayName("B2 all-defs: threshold met; score 51->54->56")
    void thresholdReachedWithLongStreak() {
        int[] attendance = {1, 1, 0, 1, 1, 1, 1, 1};

        assertEquals(8, DataFlowTargets.bonus(attendance, 7));
    }

    @Test
    @DisplayName("B3: present 35->50F; best 37->53F; score 49->56")
    void emptyBelowThreshold() {
        assertEquals(0, DataFlowTargets.bonus(new int[]{}, 1));
    }

    @Test
    @DisplayName("B4: present 35->50T; score 51->56")
    void emptyAtZeroThreshold() {
        assertEquals(5, DataFlowTargets.bonus(new int[]{}, 0));
    }

    @Test
    @DisplayName("B5: best 43->53F; present 40->50F")
    void singlePresentBelowThreshold() {
        assertEquals(0, DataFlowTargets.bonus(new int[]{1}, 2));
    }

    @Test
    @DisplayName("B6: repeated absence; present 35->50T; best 37->53F")
    void allAbsentAtZeroThreshold() {
        assertEquals(5, DataFlowTargets.bonus(new int[]{0, 0}, 0));
    }

    @Test
    @DisplayName("B7: attendance 31->32 null branch")
    void nullAttendanceRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> DataFlowTargets.bonus(null, 0));
    }

    @Test
    @DisplayName("B8: threshold 31->32 negative branch")
    void negativeThresholdRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> DataFlowTargets.bonus(new int[]{}, -1));
    }
    @Test
    @DisplayName("B9 M2 kill: total attendance 6, longest streak 4; expected 5")
    void interruptedStreakMustNotUseTotalAttendance() {
        int[] attendance = {1, 1, 0, 1, 1, 1, 1};

        assertEquals(5, DataFlowTargets.bonus(attendance, 6));
    }

}
