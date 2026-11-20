package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GcdTest {

    @Test
    @DisplayName("GCD_7_0_covers_initial_x_to_return_and_false_while")
    void gcd_7_0_covers_initialXToReturnAndFalseWhile() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("GCD_12_8_covers_loop_DU_pairs_in_two_iterations")
    void gcd_12_8_coversLoopDuPairsInTwoIterations() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }
}
