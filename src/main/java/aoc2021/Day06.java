package aoc2021;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Day06 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);

        SchoolOfFish schoolOfFish = new SchoolOfFish(input.stream()
                .collect(groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .map(entry -> new Fish(entry.getValue(), entry.getKey()))
                .collect(toList()));

        return String.valueOf(schoolOfFish.numberOfFishAfterDay(80));
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);

        SchoolOfFish schoolOfFish = new SchoolOfFish(input.stream()
                .collect(groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .map(entry -> new Fish(entry.getValue(), entry.getKey()))
                .collect(toList()));

        return String.valueOf(schoolOfFish.numberOfFishAfterDay(256));
    }

    record Fish(long number, long timer) {
    }

    static class SchoolOfFish {
        private List<Fish> fishes;

        public SchoolOfFish(List<Fish> fishes) {
            this.fishes = fishes;
        }

        private void newDay() {
            fishes = fishes.stream()
                    .flatMap(this::newDay)
                    // combine fishes with the same timer
                    .collect(groupingBy(fish -> fish.timer, summingLong(fish -> fish.number)))
                    .entrySet().stream()
                    .map(entry -> new Fish(entry.getValue(), entry.getKey()))
                    .collect(toList());
        }

        private Stream<Fish> newDay(Fish fish) {
            if (fish.timer > 0) {
                return Stream.of(new Fish(fish.number, fish.timer - 1));
            } else {
                return Stream.of(new Fish(fish.number, 6), new Fish(fish.number, 8));
            }
        }

        public long numberOfFish() {
            return fishes.stream().mapToLong(fish -> fish.number).sum();
        }

        public long numberOfFishAfterDay(int days) {
            IntStream.range(0, days).forEach(i -> newDay());
            return numberOfFish();
        }
    }

    private List<Long> parseInput(List<String> inputRaw) {
        return inputRaw.stream()
                .flatMap(line -> Arrays.stream(line.split(",")).map(Long::parseLong))
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
