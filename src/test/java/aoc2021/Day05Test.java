package aoc2021;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day05Test {

    @Test
    void doPart1() throws Exception {
        Day day = new Day05();

        assertThat(day.doPart1(day.readInput(getInputFilename()))).isEqualTo("5");
    }

    @Test
    void doPart2() throws Exception {
        Day day = new Day05();

        assertThat(day.doPart2(day.readInput(getInputFilename()))).isEqualTo("12");
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