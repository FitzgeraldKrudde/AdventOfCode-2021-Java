package aoc2021;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day04Test {

    @Test
    void doPart1() throws Exception {
        Day day = new Day04();

        assertThat(day.doPart1(day.readInput(getInputFilename()))).isEqualTo("4512");
    }

    @Test
    void doPart2() throws Exception {
        Day day = new Day04();

        assertThat(day.doPart2(day.readInput(getInputFilename()))).isEqualTo("1924");
    }

    // @formatter:off
    private String getInputFilename() {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        return clazz.getSimpleName().toLowerCase().replace("test","").replace("day0", "day") + ".txt";
        // @formatter:on
    }
}