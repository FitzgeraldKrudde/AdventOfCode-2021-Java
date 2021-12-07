package aoc2021;

import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;

public class Day07 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);

        long min = input.stream().mapToLong(l -> l).min().getAsLong();
        long max = input.stream().mapToLong(l -> l).max().getAsLong();

        // was considering optimisation (starting with average and moving until sweetspot)
        // but not necessary with these small numbers
        long totalFuel = LongStream.rangeClosed(min, max)
                .map(position -> totalFuel(input, position))
                .min()
                .getAsLong();

        return String.valueOf(totalFuel);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);

        long min = input.stream().mapToLong(l -> l).min().getAsLong();
        long max = input.stream().mapToLong(l -> l).max().getAsLong();

        long totalFuel = LongStream.rangeClosed(min, max)
                .map(position -> totalFuelTriangular(input, position))
                .min()
                .getAsLong();

        return String.valueOf(totalFuel);
    }

    private long totalFuel(List<Long> input, long position) {
        return input.stream()
                .mapToLong(l -> abs(position - l))
                .sum();
    }

    private long totalFuelTriangular(List<Long> input, long position) {
        return input.stream()
                .mapToLong(l -> abs(position - l))
                .map(l -> (l * (l + 1) / 2))
                .sum();
    }

    private List<Long> parseInput(List<String> inputRaw) {
        return inputRaw.stream()
                .flatMap(line -> Arrays.stream(line.split(",")))
                .map(Long::valueOf)
                .collect(toList());
    }

    // @formatter:off
    static public void main(String[] args) throws Exception {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        final String filename = clazz.getSimpleName().toLowerCase().replace("day0","day") + ".txt";

        // get the classname
        final String fullClassName = clazz.getCanonicalName();

        // create instance
        Day day=(Day) Class.forName(fullClassName).getDeclaredConstructor().newInstance();

        // invoke "main" from the base Day class
        day.main(filename);
    }
    // @formatter:on
}
