package aoc2021;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DayxxTest {

    @Test
    void doPart1() throws Exception {
        Day day = new Dayxx();

        assertThat(day.doPart1(day.readInput(getInputFilename()))).isEqualTo("0");
    }

    @Test
    void doPart2() throws Exception {
        Day day = new Dayxx();

        assertThat(day.doPart2(day.readInput(getInputFilename()))).isEqualTo("0");
    }

    // @formatter:off
    private String getInputFilename() {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        return clazz.getSimpleName().toLowerCase().replace("test","").replace("_0", "_") + ".txt";
        // @formatter:on
    }
}