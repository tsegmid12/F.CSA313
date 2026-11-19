package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * gcd() all-DU-pairs tests. Line numbers are lines of DataFlowTargets.java.
 * Pair notation: variable:defLine->useLine (15T / 15F = true / false branch of the p-use at line 15).
 */
class GcdTest {

    @Test
    @DisplayName("G1 gcd(7,0)=7: loop body never runs. Covers x:13->20, y:13->15F")
    void g1_zeroIterations_gcd7_0() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("G2 gcd(12,8)=4: 2 iterations. Covers x:13->16, x:17->16, x:17->20, "
            + "y:13->15T, y:13->16, y:13->17, y:18->15T, y:18->15F, y:18->16, y:18->17, tmp:16->18")
    void g2_twoIterations_gcd12_8() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }

    @Test
    @DisplayName("G3 gcd(0,5)=5: 1 iteration (spec example). Covers x:13->16, x:17->20, "
            + "y:13->15T, y:13->16, y:13->17, y:18->15F, tmp:16->18 - nothing new")
    void g3_oneIteration_gcd0_5() {
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }
}
