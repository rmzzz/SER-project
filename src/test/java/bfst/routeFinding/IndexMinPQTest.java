package bfst.routeFinding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IndexMinPQTest {

    IndexMinPQ pq = new IndexMinPQ();

    @Test
    void insert() {
        IndexMinPQ thispq = pq;
        thispq.insert(1, 5);

        Assertions.assertEquals(5, pq.getFromKeys(1));
    }

    @Test
    void delMin() {
        String exception = "";
        try {
            pq.delMin();
        } catch (Exception e) {
            exception = e.getMessage();
        }
        Assertions.assertEquals("Priority queue underflow", exception);

        IndexMinPQ thispq = pq;

        thispq.insert(3, 2.5);
        thispq.insert(4, 1.4);
        thispq.insert(5, 5.5);

        Assertions.assertEquals(4L, pq.delMin());
    }

    @Test
    void decreaseKey() {
        String exception = "";
        IndexMinPQ thispq = pq;
        thispq.insert(3, 4.2);

        try {
            thispq.decreaseKey(3, 4.2);
        } catch (Exception e) {
            exception = e.getMessage();
        }

        Assertions.assertEquals("Calling decreaseKey() with a key equal to the key in the priority queue", exception);

        try {
            thispq.decreaseKey(3, 5.2);
        } catch (Exception e) {
            exception = e.getMessage();
        }

        Assertions.assertEquals("Calling decreaseKey() with a key strictly greater than the key in the priority queue", exception);

    }

    @Test
    void greater() {
        IndexMinPQ thispq = pq;
        thispq.insert(3, 5.6);
        thispq.insert(4, 7.6);

        boolean expected;
        boolean actual;
        expected = true;
        actual = thispq.greater(2, 1);
        Assertions.assertEquals(expected, actual);

        expected = false;
        actual = thispq.greater(1, 2);
        Assertions.assertEquals(expected, actual);

        expected = false;
        actual = thispq.greater(1, 1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void exch() {
        IndexMinPQ thispq = pq;
        thispq.insert(3, 5.6);
        thispq.insert(4, 7.6);

        thispq.exch(1,2);

        long expected;
        long actual;
        expected = 4;
        actual = thispq.getFromPq(1);
        Assertions.assertEquals(expected, actual);

        expected = 3;
        actual = thispq.getFromPq(2);
        Assertions.assertEquals(expected, actual);

        expected = 1;
        actual = thispq.getFromQp(4);
        Assertions.assertEquals(expected, actual);

        expected = 2;
        actual = thispq.getFromQp(3);
        Assertions.assertEquals(expected, actual);

    }
}