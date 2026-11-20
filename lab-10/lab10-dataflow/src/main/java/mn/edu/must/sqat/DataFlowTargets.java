package mn.edu.must.sqat;

/**
 * Лаб 10-ын өгөгдлийн урсгалын анализын бай хоёр функц.
 * АНХААР: Кодыг ӨӨРЧЛӨХГҮЙ — мөрийн дугаарууд DU хүснэгтэд ашиглагдана.
 */
public class DataFlowTargets {

    /**
     * Хамгийн их ерөнхий хуваагч (Лекц 10-ын жишээ).
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
     * Ирцийн урамшууллын оноо.
     * attendance: өдөр бүрийн ирц (0 = тасалсан, >0 = ирсэн).
     * threshold: урамшуулал авахад шаардагдах ирсэн өдрийн доод тоо.
     * Дүрэм: ирсэн өдрийн тоо >= threshold бол 5 оноо;
     *        дараалсан ирцийн хамгийн урт цуваа >= 5 бол НЭМЖ 3 оноо.
     * bonus(null, k) болон threshold < 0 үед IllegalArgumentException.
     */
    public static int bonus(int[] attendance, int threshold) {
        if (attendance == null || threshold < 0) {
            throw new IllegalArgumentException("Буруу оролт");
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
