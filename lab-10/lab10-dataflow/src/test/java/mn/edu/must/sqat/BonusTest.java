package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 10 - Section B4: bonus() all-defs then all-DU-pairs tests.
 *
 * bonus() variables and their defs (file line numbers):
 *   attendance: def@31 (param), use@32(p), use@38(p)
 *   threshold:  def@31 (param), use@32(p), use@50(p)
 *   present:    def@35 (init=0), def@40 (present+1), use@40(c), use@50(p), use@54(c) -- wait, use@50
 *   streak:     def@36 (init=0), def@41 (streak+1), def@46 (streak=0), use@41(c), use@42(p), use@54(c) -- wait
 *   best:       def@37 (init=0), def@43 (best=streak), use@42(p), use@53(p), use@54(c)
 *   a:          def@38 (for-each assigns a), use@39(p)
 *   score:      def@49 (init=0), def@51 (score=5), def@54 (score=score+3), use@51(c), use@54(c), use@56(c)
 *
 * ALL-DEFS: must reach a use from every def node.
 *   present def@35 -> use@40 (or use@50)   -- need any test where loop runs OR present>=threshold
 *   present def@40 -> use@50               -- need present to be incremented and reach line 50
 *   streak  def@36 -> use@41 (or use@42)   -- any test entering loop with a>0
 *   streak  def@41 -> use@42               -- test where a>0 so streak incremented
 *   streak  def@46 -> use@42 or use@41     -- test with a<=0 after a>0 (streak reset, then a>0 again)
 *   best    def@37 -> use@42               -- test where loop runs (best initially 0, check streak>best)
 *   best    def@43 -> use@53               -- test where streak>best happened (best updated, then checked @53)
 *   a       def@38 -> use@39               -- any non-empty attendance array
 *   score   def@49 -> use@56 (via 51 or 54) -- any test
 *   score   def@51 -> use@54 or use@56     -- present>=threshold
 *   score   def@54 -> use@56              -- best>=5 path
 *
 * Minimum all-defs: 3 tests cover all defs.
 *   T1: covers streak def@46 (must have a<=0 entry), and also streak def@36->use@41 if followed by a>0
 *   T2: covers best def@43 (need streak>best), score def@54 (need best>=5)
 *   T3: covers score def@51 (present>=threshold) without best>=5
 */
public class BonusTest {

    // =========================================================================
    // ALL-DEFS phase tests
    // These 3 tests cover all def nodes.
    // =========================================================================

    @Test
    @DisplayName("AllDefs-T1: all-zero attendance, threshold=0 => score=5 [defs: present35, streak36, best37, a38, score49, score51]")
    void allDefs_T1_allZero() {
        // attendance=[0,0,0], threshold=0
        // Loop: a=0 each time => streak stays 0 (def@46 each iter), best stays 0
        // present=0 >= threshold=0 => score=5 (def@51)
        // best=0 < 5 => score stays 5
        // Covers: present def@35->use@50, streak def@36->use@41(first iter check via @42),
        //         streak def@46->use@42(next iter), best def@37->use@42,
        //         a def@38->use@39, score def@49->use@51->use@56, score def@51->use@56
        assertEquals(5, DataFlowTargets.bonus(new int[]{0, 0, 0}, 0));
    }

    @Test
    @DisplayName("AllDefs-T2: long streak>=5, threshold met => score=8 [defs: present40, streak41, best43, score54]")
    void allDefs_T2_longStreak() {
        // attendance=[1,1,1,1,1], threshold=3
        // present=5 >= threshold=3 => score=5; best=5 >= 5 => score=8
        // Covers: present def@40->use@50, streak def@41->use@42,
        //         best def@43->use@53, score def@54->use@56
        assertEquals(8, DataFlowTargets.bonus(new int[]{1, 1, 1, 1, 1}, 3));
    }

    @Test
    @DisplayName("AllDefs-T3: threshold not met, short streak => score=0 [covers score def@49 not overwritten path check]")
    void allDefs_T3_noBonus() {
        // attendance=[1,1], threshold=5
        // present=2 < threshold=5 => score stays 0 (def@49->use@56 direct)
        // best=2 < 5 => no streak bonus
        assertEquals(0, DataFlowTargets.bonus(new int[]{1, 1}, 5));
    }

    // =========================================================================
`r`n}
