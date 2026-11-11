package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ScholarshipCalculatorTest {
    private final ScholarshipCalculator calculator = new ScholarshipCalculator();

    @Test
    void letterGradeReturnsAForExcellentScore() {
        assertEquals("A", calculator.letterGrade(95));
    }

    @Test
    void letterGradeReturnsBForGoodScore() {
        assertEquals("B", calculator.letterGrade(85));
    }

    @Test
    void gpaCalculatesAverageFromTwoScores() {
        assertEquals(3.5, calculator.gpa(new double[] {95, 85}));
    }

    @Test
    void scholarshipReturnsFullAwardForHighGpaAndCredits() {
        assertEquals(100, calculator.scholarship(3.9, 16, false));
    }

    @Test
    void scholarshipReturnsPartialAwardForQualifyingGpa() {
        assertEquals(50, calculator.scholarship(3.0, 10, false));
    }

    @Test
    void letterGradeReturnsCForAverageScore() {
        assertEquals("C", calculator.letterGrade(75));
    }

    @Test
    void letterGradeReturnsDForPassingScoreAboveBoundary() {
        assertEquals("D", calculator.letterGrade(65));
    }

    @Test
    void letterGradeReturnsFForLowScore() {
        assertEquals("F", calculator.letterGrade(50));
    }

    @Test
    void letterGradeRejectsNegativeScore() {
        assertThrows(IllegalArgumentException.class, () -> calculator.letterGrade(-1));
    }

    @Test
    void letterGradeRejectsScoreAboveMaximum() {
        assertThrows(IllegalArgumentException.class, () -> calculator.letterGrade(101));
    }

    @Test
    void gpaHandlesAllGradeBranches() {
        assertEquals(2.0, calculator.gpa(new double[] {95, 85, 75, 65, 50}));
    }

    @Test
    void gpaRejectsNullScores() {
        assertThrows(IllegalArgumentException.class, () -> calculator.gpa(null));
    }

    @Test
    void gpaRejectsEmptyScores() {
        assertThrows(IllegalArgumentException.class, () -> calculator.gpa(new double[0]));
    }

    @Test
    void gpaLoopBoundaryZeroItemsThrows() {
        assertThrows(IllegalArgumentException.class, () -> calculator.gpa(new double[0]));
    }

    @Test
    void gpaLoopBoundaryOneItem() {
        assertEquals(4.0, calculator.gpa(new double[] {95}));
    }

    @Test
    void gpaLoopBoundaryTwoOrMoreItems() {
        assertEquals(2.5, calculator.gpa(new double[] {95, 85, 75, 65}));
    }

    @Test
    void scholarshipReturnsZeroForViolation() {
        assertEquals(0, calculator.scholarship(4.0, 30, true));
    }

    @Test
    void scholarshipFallsThroughWhenFullAwardCreditsAreInsufficient() {
        assertEquals(70, calculator.scholarship(3.9, 14, false));
    }

    @Test
    void scholarshipFallsThroughWhenPartialAwardCreditsAreInsufficient() {
        assertEquals(50, calculator.scholarship(3.4, 11, false));
    }

    @Test
    void scholarshipReturnsZeroBelowMinimumGpa() {
        assertEquals(0, calculator.scholarship(2.9, 20, false));
    }

    @Test
    void scholarshipRejectsNegativeGpa() {
        assertThrows(IllegalArgumentException.class, () -> calculator.scholarship(-0.1, 10, false));
    }

    @Test
    void scholarshipRejectsGpaAboveMaximum() {
        assertThrows(IllegalArgumentException.class, () -> calculator.scholarship(4.1, 10, false));
    }

    @Test
    void scholarshipRejectsNegativeCredits() {
        assertThrows(IllegalArgumentException.class, () -> calculator.scholarship(3.0, -1, false));
    }

    @Test
    void letterGradeBoundary60IsD() {
        assertEquals("D", calculator.letterGrade(60));
    }

    @Test
    void letterGradeBoundary59Point99IsF() {
        assertEquals("F", calculator.letterGrade(59.99));
    }

    @Test
    void letterGradeBoundary69Point99IsD() {
        assertEquals("D", calculator.letterGrade(69.99));
    }

    @Test
    void letterGradeBoundaryZeroIsF() {
        assertEquals("F", calculator.letterGrade(0));
    }

    @Test
    void letterGradeBoundary100IsA() {
        assertEquals("A", calculator.letterGrade(100));
    }

    @Test
    void letterGradeBoundary89Point99IsB() {
        assertEquals("B", calculator.letterGrade(89.99));
    }

    @Test
    void letterGradeBoundary90IsA() {
        assertEquals("A", calculator.letterGrade(90));
    }
}
