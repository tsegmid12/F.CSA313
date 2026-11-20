package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 10 - Section B4: bonus() data-flow tests.
 *
 * Minimum all-defs suite: allDefs_T1 and allDefs_T2.
 * The remaining DU_* tests exercise reachable definition-use pairs and
 * predicate outcomes. M2Kill_bestUpdateUsesStreak is a separate mutation-kill
 * regression test, not part of the DU-pairs minimum.
 *
 * Source line references:
 * attendance@31 -> predicates@32 and loop@38
 * threshold@31  -> predicates@32 and @50
 * present@35/@40 -> uses@40 and predicate@50
 * streak@36/@41/@46 -> uses@41 and predicate@42
 * best@37/@43 -> predicates@42 and @53
 * a@38 -> predicate@39
 * score@49/@51/@54 -> uses@54 and @56
 */
public class BonusTest {

    // =========================================================================
    // ALL-DEFS phase: minimum 2 tests.
    // T1 reaches every definition except score@49; T2 reaches score@49 -> use@54.
    // =========================================================================

    @Test
    @DisplayName("AllDefs-T1: present threshold met and streak reset/recovery => score=8")
    void allDefs_T1_allDefinitionsExceptInitialScore() {
        // present=6 >= 5 and longest streak=5 => score=8.
        // Includes streak@46 -> streak@41 after a missed day.
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 0, 1, 1, 1, 1, 1}, 5));
    }

    @Test
    @DisplayName("AllDefs-T2: threshold not met but streak bonus applies => score=3")
    void allDefs_T2_initialScoreReachesBonusUse() {
        // present=5 < 6: score@49 survives to score+3 at line 54.
        // best=5: score@54 reaches return at line 56.
        assertEquals(3, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 6));
    }

    // =========================================================================
    // ALL-DU-PAIRS phase tests
    // Additional tests to cover all DU pairs not yet covered above.
    // =========================================================================

    @Test
    @DisplayName("DUPairs-T4: null attendance => IllegalArgumentException [pair: attendance(31->32 p-use T)]")
    void DU_attendance_def31_use32_null() {
        assertThrows(IllegalArgumentException.class,
            () -> DataFlowTargets.bonus(null, 3));
    }

    @Test
    @DisplayName("DUPairs-T5: threshold<0 => IllegalArgumentException [pair: threshold(31->32 p-use T)]")
    void DU_threshold_def31_use32_negative() {
        assertThrows(IllegalArgumentException.class,
            () -> DataFlowTargets.bonus(new int[]{1}, -1));
    }

    @Test
    @DisplayName("DUPairs-T6: empty attendance, threshold=0 => score=5 [pair: attendance(31->38 p-use F=empty loop)]")
    void DU_attendance_def31_use38_emptyLoop() {
        // attendance=[] => for loop never entered; present=0>=0 => score=5
        // Covers: attendance def@31 -> use@38 p-use (loop condition, FALSE path - empty array)
        assertEquals(5, DataFlowTargets.bonus(new int[]{}, 0));
    }

    @Test
    @DisplayName("DUPairs-T7: attendance has elements, loop entered [pair: attendance(31->38 p-use T)]")
    void DU_attendance_def31_use38_loopEntered() {
        // Covers: attendance def@31 -> use@38 p-use TRUE (non-empty, loop is entered)
        assertEquals(0, DataFlowTargets.bonus(new int[]{0}, 1));
    }

    @Test
    @DisplayName("DUPairs-T8: streak reset value is read by next increment [pair: streak def@46->use@41]")
    void DU_streak_def46_use41_afterReset() {
        // attendance=[1,0,1], threshold=10
        // iter1: a=1>0 => present=1,streak=1,best=1(1>0)
        // iter2: a=0 => streak=0 (def@46)
        // iter3: a=1>0 => present=2,streak=1, 1>1? no => best=1
        // Covers: streak def@46 -> use@41 c-use (reset value read by next streak increment)
        assertEquals(0, DataFlowTargets.bonus(new int[]{1, 0, 1}, 10));
    }

    @Test
    @DisplayName("DUPairs-T9: streak reset does NOT carry to next streak update (streak def@46->use@41) [pair: streak(46->41)]")
    void DU_streak_def46_use41_nextPresent() {
        // attendance=[1,0,1,1], threshold=5
        // iter3: streak was reset to 0 (def@46 in iter2)
        // iter3: a=1>0 => streak=0+1=1 (use of streak def@46 at line 41)
        // Covers: streak def@46 -> use@41 (streak+1 uses previously-reset streak value)
        assertEquals(0, DataFlowTargets.bonus(new int[]{1, 0, 1, 1}, 5));
    }

    @Test
    @DisplayName("DUPairs-T10: present def@40 -> use@40 chain (auto-use in expr) [pair: present(40->40 c-use in next iter)]")
    void DU_present_def40_use40_chain() {
        // attendance=[1,1,1], threshold=4
        // iter1: present=0+1=1; iter2: present=1+1=2; iter3: present=2+1=3
        // present def@40 used in next iteration's line 40 (present+1)
        // Covers: present def@40 -> use@40 (c-use, present+1 uses previously-defined present)
        assertEquals(0, DataFlowTargets.bonus(new int[]{1, 1, 1}, 4));
    }

    @Test
    @DisplayName("DUPairs-T11: present def@40 -> use@50 p-use TRUE [pair: present(40->50 T)]")
    void DU_present_def40_use50_TRUE() {
        // attendance=[1,1,1], threshold=2
        // present=3 >= threshold=2 => TRUE branch at line 50
        // Covers: present def@40 -> use@50 p-use TRUE
        assertEquals(5, DataFlowTargets.bonus(new int[]{1, 1, 1}, 2));
    }

    @Test
    @DisplayName("DUPairs-T12: present def@40 -> use@50 p-use FALSE [pair: present(40->50 F)]")
    void DU_present_def40_use50_FALSE() {
        // attendance=[1], threshold=5
        // present=1 < threshold=5 => FALSE branch at line 50
        // Covers: present def@40 -> use@50 p-use FALSE
        assertEquals(0, DataFlowTargets.bonus(new int[]{1}, 5));
    }

    @Test
    @DisplayName("DUPairs-T13: threshold def@31 -> use@50 p-use TRUE [pair: threshold(31->50 T)]")
    void DU_threshold_def31_use50_TRUE() {
        // threshold=1, present=1 => 1>=1 TRUE
        assertEquals(5, DataFlowTargets.bonus(new int[]{1}, 1));
    }

    @Test
    @DisplayName("DUPairs-T14: threshold def@31 -> use@50 p-use FALSE [pair: threshold(31->50 F)]")
    void DU_threshold_def31_use50_FALSE() {
        // threshold=10, present=1 => FALSE
        assertEquals(0, DataFlowTargets.bonus(new int[]{1}, 10));
    }

    @Test
    @DisplayName("DUPairs-T15: best def@37 -> use@53 p-use FALSE (best<5) [pair: best(37->53 F)]")
    void DU_best_def37_use53_FALSE() {
        // best=0 (never updated since all zero), best<5 => FALSE at line 53
        // Also: present=0, threshold=0 => score=5 at line 51
        assertEquals(5, DataFlowTargets.bonus(new int[]{0}, 0));
    }

    @Test
    @DisplayName("DUPairs-T16: best def@43 -> use@42 p-use (streak vs best in next iteration) [pair: best(43->42)]")
    void DU_best_def43_use42() {
        // attendance=[1,1,1,1,1,1], threshold=1
        // iter1: streak=1>best(0) => best=1 (def@43)
        // iter2-6: streak=2,3,4,5,6; each time streak>best => best updated (def@43 reused via use@42)
        // best def@43 -> use@42 (p-use, streak>best in later iterations)
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1, 1}, 1));
    }

    @Test
    @DisplayName("DUPairs-T17: best def@43 -> use@53 p-use TRUE (best>=5) [pair: best(43->53 T)]")
    void DU_best_def43_use53_TRUE() {
        // attendance=[1,1,1,1,1], threshold=10
        // best=5 >= 5 => TRUE at line 53; score=0+3=3
        assertEquals(3, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 10));
    }

    @Test
    @DisplayName("DUPairs-T18: a def@38 -> use@39 p-use FALSE (a<=0) [pair: a(38->39 F)]")
    void DU_a_def38_use39_FALSE() {
        // a=0 => FALSE branch at line 39
        assertEquals(5, DataFlowTargets.bonus(new int[]{0}, 0));
    }

    @Test
    @DisplayName("DUPairs-T19: a def@38 -> use@39 p-use TRUE (a>0) [pair: a(38->39 T)]")
    void DU_a_def38_use39_TRUE() {
        // a=1 => TRUE branch at line 39
        assertEquals(5, DataFlowTargets.bonus(new int[]{1}, 1));
    }

    @Test
    @DisplayName("DUPairs-T20: score def@51 -> use@54 c-use (score+3) [pair: score(51->54)]")
    void DU_score_def51_use54() {
        // present>=threshold AND best>=5 => score=5 then score=5+3=8
        // score def@51 -> use@54 (c-use in score+3)
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 1));
    }

    @Test
    @DisplayName("DUPairs-T21: score def@49 -> use@56 direct (no branch taken) [pair: score(49->56)]")
    void DU_score_def49_use56_direct() {
        // present=0 < threshold=1 => score stays 0; best=0 < 5 => score stays 0 => return 0
        // score def@49 -> use@56 (c-use, score returned with no modification)
        assertEquals(0, DataFlowTargets.bonus(new int[]{0}, 1));
    }

    @Test
    @DisplayName("DUPairs-T22: streak def@41 -> use@41 chain (streak+1 uses prior streak) [pair: streak(41->41)]")
    void DU_streak_def41_use41_chain() {
        // Multiple consecutive a>0 => streak grows each iter
        // iter1: streak=0+1=1(def@41); iter2: streak=1+1=2 (use of def@41 at line 41)
        // Covers: streak def@41 -> use@41
        assertEquals(5, DataFlowTargets.bonus(new int[]{1, 1}, 2));
    }

    @Test
    @DisplayName("DUPairs-T23: streak def@41 -> use@42 p-use TRUE (streak>best) [pair: streak(41->42 T)]")
    void DU_streak_def41_use42_TRUE() {
        // streak=1 > best=0 => TRUE at line 42
        assertEquals(5, DataFlowTargets.bonus(new int[]{1}, 1));
    }

    @Test
    @DisplayName("DUPairs-T24: streak def@41 -> use@42 p-use FALSE (streak<=best) [pair: streak(41->42 F)]")
    void DU_streak_def41_use42_FALSE() {
        // Need streak <= best: attendance=[1,0,1] threshold=0
        // iter1: streak=1>0=best => best=1; iter2: streak=0 (def@46);
        // iter3: streak=1 (def@41); 1>1? FALSE at line 42
        // Covers: streak def@41 -> use@42 p-use FALSE
        assertEquals(5, DataFlowTargets.bonus(new int[]{1, 0, 1}, 0));
    }

    @Test
    @DisplayName("DUPairs-T25: score def@51->use@56 (no streak bonus) and score def@49->use@51 [pairs: score(51->56) no54]")
    void DU_score_def51_use56_noStreakBonus() {
        // present>=threshold, but best<5 => score=5 (def@51) -> return 5 (use@56, skips line 54)
        assertEquals(5, DataFlowTargets.bonus(new int[]{1, 1, 1}, 3));
    }

    @Test
    @DisplayName("DUPairs-T26: streak def@46->use@41 and best def@37->use@42 TRUE (after reset) [streak reset scenario]")
    void DU_streakReset_bestZero_newStreak_greaterThanBest() {
        // attendance=[0,1], threshold=0
        // iter1: a=0 => streak=0 (def@46); iter2: a=1>0 => streak=0+1=1(def@41), 1>0=best => best=1(def@43)
        // Covers: streak def@46->use@41 (the value 0 is used in streak+1)
        //         best def@37->use@42 TRUE (0 from def@37 used in comparison, TRUE)
        assertEquals(5, DataFlowTargets.bonus(new int[]{0, 1}, 1));
    }

    @Test
    @DisplayName("M2-kill: best update must use streak, not present")
    void M2Kill_bestUpdateUsesStreak() {
        // Correct: present=6, longest streak=4 => score 5.
        // M2 (best=present) incorrectly makes best=6 and returns 8.
        assertEquals(5, DataFlowTargets.bonus(new int[]{1, 1, 0, 1, 1, 1, 1}, 0));
    }
}
