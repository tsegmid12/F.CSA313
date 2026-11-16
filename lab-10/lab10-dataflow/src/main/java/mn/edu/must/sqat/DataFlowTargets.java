package mn.edu.must.sqat;

/**
 * Lab 10 DataFlow targets.
 * CAUTION: Do not modify source code.
 */
public class DataFlowTargets {

    /**
     * Greatest common divisor (Lecture 10 example).
     * gcd(12, 8) = 4, gcd(7, 0) = 7, gcd(0, 5) = 5.
     */
    public static int gcd(int x, int y) {
        int tmp;
        while (y != 0) {
            tmp = x % y;
            x = y;
            y = tmp;
        }
        return x;
    }

    /**
     * Attendance bonus score.
     * attendance: daily attendance array (0 = absent, >0 = present).
     * threshold: minimum present days required for base bonus.
     * Rule: if present days >= threshold => 5 points;
     *       if longest consecutive streak >= 5 => ADD 3 points.
     * bonus(null, k) or threshold < 0 throws IllegalArgumentException.
     */
    public static int bonus(int[] attendance, int threshold) {
        if (attendance == null || threshold < 0) {
            throw new IllegalArgumentException("Invalid input");
        }
        int present = 0;
        int streak = 0;
        int best = 0;
        for (int a : attendance) {
            if (a > 0) {
                present = present + 1;
                streak = streak + 1;
                if (streak > best) {
                    best = streak;
                }
            } else {
                streak = 0;
            }
        }
        int score = 0;
        if (present >= threshold) {
            score = 5;
        }
        if (best >= 5) {
            score = score + 3;
        }
        return score;
    }
}