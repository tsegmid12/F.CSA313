package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 10 - Section A: GCD all-DU-pairs coverage tests.
 *
 * DU pairs covered (file line numbers):
 *   x: def@13, use@16(c), use@17(c), use@20(c)
 *   y: def@13, use@15(p), use@16(c), use@18(c)
 *   y: def@18, use@15(p), use@16(c), use@18(c)
 *   tmp: def@16, use@18(c)
 *   x: def@17, use@16(c), use@17(c), use@20(c)
 *
 * Test method naming: DU_<var>_def<defLine>_use<useLine>[_<suffix>]
 */
public class GcdTest {

    // -------------------------------------------------------------------------
    // DU pairs for variable x:
    //   (x, def@13, use@20)  -- y==0 on first check, loop never entered
    //   (x, def@13, use@16)  -- loop entered at least once, x used at line 16
    //   (x, def@13, use@17)  -- loop entered, x=y at line 17
    //   (x, def@17, use@16)  -- after first iteration, new x used at line 16
    //   (x, def@17, use@17)  -- multiple iterations, x keeps being reassigned
    //   (x, def@17, use@20)  -- last iteration, x returned
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DU x def@13 use@20: y=0 at entry, skip loop, return x [pair: x(13->20)]")
    void DU_x_def13_use20_skipLoop() {
        // y=0 => while(y!=0) false immediately => return x (line 20)
        // Covers: x def@13 -> use@20 (c-use)
        //         y def@13 -> use@15 p-use FALSE branch
        assertEquals(7, DataFlowTargets.gcd(7, 0));
        assertEquals(0, DataFlowTargets.gcd(0, 0));
    }

    @Test
    @DisplayName("DU x def@13 use@16+17 and y def@13 use@15(T)+16+18: one iteration [pairs: x(13->16,17) y(13->15,16,18)]")
    void DU_x_def13_use16_17_and_y_def13_use15_16_18_oneIteration() {
        // gcd(4, 2): iter1: tmp=4%2=0, x=2, y=0 => loop exits, return 2
        // Covers: x def@13 -> use@16 (c-use, tmp=x%y)
        //         x def@13 -> use@17 (c-use, x=y)
        //         y def@13 -> use@15 p-use TRUE branch
        //         y def@13 -> use@16 (c-use, tmp=x%y)
        //         y def@13 -> use@18 (c-use, y=tmp)
        //         tmp def@16 -> use@18 (c-use, y=tmp)
        assertEquals(2, DataFlowTargets.gcd(4, 2));
    }

    @Test
    @DisplayName("DU tmp def@16 use@18 and y def@18 use@15 TRUE: multi-iteration [pairs: tmp(16->18) y(18->15,16,18)]")
    void DU_tmp_def16_use18_and_y_def18_uses_multiIteration() {
        // gcd(12, 8): iter1: tmp=4, x=8, y=4; iter2: tmp=0, x=4, y=0 => return 4
        // Covers: tmp def@16 -> use@18
        //         y def@18 -> use@15 p-use TRUE (iteration 2 condition check)
        //         y def@18 -> use@16 (c-use in iteration 2)
        //         y def@18 -> use@18 (c-use, re-assigned in iteration 2)
        //         x def@17 -> use@16 (c-use, x used in next tmp=x%y)
        //         x def@17 -> use@17 (c-use, x=y, next iteration)
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }

    @Test
    @DisplayName("DU y def@18 use@15 FALSE and x def@17 use@20: last-iter exit [pairs: y(18->15 F) x(17->20)]")
    void DU_y_def18_use15_FALSE_and_x_def17_use20_lastIter() {
        // gcd(9, 3): iter1: tmp=0, x=3, y=0 => loop exits, return x (line 20)
        // Covers: y def@18 -> use@15 p-use FALSE (exit condition after iteration)
        //         x def@17 -> use@20 (c-use, return x)
        assertEquals(3, DataFlowTargets.gcd(9, 3));
    }

    @Test
    @DisplayName("DU x def@17 use@17 multi-iter: three iterations [pair: x(17->17)]")
    void DU_x_def17_use17_threeIter() {
        // gcd(8, 6): iter1: tmp=2,x=6,y=2; iter2: tmp=0,x=2,y=0 => return 2
        // Need 3+ iterations for x(def@17)->use@17:
        // gcd(13, 8): iter1: tmp=5,x=8,y=5; iter2: tmp=3,x=5,y=3; iter3: tmp=2,x=3,y=2;
        //             iter4: tmp=1,x=2,y=1; iter5: tmp=0,x=1,y=0 => return 1
        // Covers: x def@17 -> use@17 (x=y again in a later iteration)
        assertEquals(1, DataFlowTargets.gcd(13, 8));
    }

    @Test
    @DisplayName("Edge: gcd(0,5) where x=0 at entry [pair: x(13->16) with x=0]")
    void DU_x0_entry() {
        // gcd(0,5): iter1: tmp=0%5=0, x=5, y=0 => return 5
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }
}
