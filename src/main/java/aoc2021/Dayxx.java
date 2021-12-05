package aoc2021;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Dayxx extends Day {

    // @formatter:off
    static public void main(String[] args) throws Exception {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        final String filename = clazz.getSimpleName().toLowerCase().replace("day0","day") + ".txt";

        // invoke "main" from the base nl.krudde.aoc2021.Day class
        new Dayxx().main(filename);
        // @formatter:on
    }

    @Override
    public String doPart1(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);


        long result = 0;

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);


        long result = 0;

        return String.valueOf(result);
    }

    private List<Long> parseInput(List<String> inputRaw) {
        return inputRaw.stream()
                .map(Long::valueOf)
                .collect(toList());
    }
}
