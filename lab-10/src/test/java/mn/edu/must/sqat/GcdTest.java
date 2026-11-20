package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GcdTest {

    @Test
    @DisplayName("gcd(7,0): y13-F and x13-return20")
    void zeroSecondArgumentCoversDirectReturnPairs() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("gcd(12,8): x13/y13/tmp16 and first-loop DU pairs")
    void twoIterationsCoverInitialDefinitions() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }

    @Test
    @DisplayName("gcd(0,5): y13-to-tmp16 and x17-to-return20")
    void zeroFirstArgumentCoversAlternativeRemainderPath() {
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }
}
