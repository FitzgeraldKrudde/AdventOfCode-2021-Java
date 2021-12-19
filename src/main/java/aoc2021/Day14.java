package aoc2021;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class Day14 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Polymer polymer = Polymer.of(inputRaw);

        return String.valueOf(polymer.applyRules(10));
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Polymer polymer = Polymer.of(inputRaw);

        return String.valueOf(polymer.applyRules(40));

    }

    record Polymer(String polymer, Map<String, String> rules) {
        static Polymer of(List<String> input) {
            Map<String, String> rules = input.stream()
                    .dropWhile(line -> line.length() > 0)
                    .dropWhile(line -> line.length() == 0)
                    .map(Polymer::readRule)
                    .collect(toMap(Entry::getKey, Entry::getValue));

            return new Polymer(input.get(0), rules);
        }

        static private Entry<String, String> readRule(String line) {
            String[] words = line.split("\\s+");
            return new SimpleEntry<>(words[0], words[2]);
        }

        public long applyRules(int times) {
            String start = polymer.substring(0, 1);
            String end = polymer.substring(polymer.length() - 1);

            // break the polymer into pairs
            Map<String, Long> pairCounter = IntStream.range(0, polymer.length() - 1)
                    .mapToObj(i -> polymer.substring(i, i + 2))
                    .collect(groupingBy(Function.identity(), counting()));

            for (int time = 0; time < times; time++) {
                pairCounter = pairCounter.entrySet().stream()
                        .flatMap(this::breakMatchingPair)
                        .collect(groupingBy(Entry::getKey, summingLong(Entry::getValue)));
            }

            return calculateScore(pairCounter, start, end);
        }

        private Stream<Entry<String, Long>> breakMatchingPair(Entry<String, Long> entry) {
            if (rules.containsKey(entry.getKey())) {
                String value = rules.get(entry.getKey());
                return Stream.of(new SimpleEntry<>(entry.getKey().charAt(0) + value, entry.getValue()),
                        new SimpleEntry<>(value + entry.getKey().charAt(1), entry.getValue()));
            } else {
                return Stream.of(entry);
            }
        }

        private long calculateScore(Map<String, Long> pairCounter, String start, String end) {
            Map<String, Long> occurrences =
                    Stream.concat(
                                    Stream.of(new SimpleEntry<>(start, 1L), new SimpleEntry<>(end, 1L)),
                                    pairCounter.entrySet().stream().flatMap(this::splitPairCounter))
                            .collect(groupingBy(Entry::getKey, summingLong(Entry::getValue)));

            // due to pair splitting everything is counted twice so divide by 2
            long min = occurrences.values().stream().mapToLong(l -> l / 2).min().getAsLong();
            long max = occurrences.values().stream().mapToLong(l -> l / 2).max().getAsLong();

            return max - min;
        }

        private Stream<Entry<String, Long>> splitPairCounter(Entry<String, Long> entry) {
            return Stream.of(new SimpleEntry<>(entry.getKey().substring(0, 1), entry.getValue()),
                    new SimpleEntry<>(entry.getKey().substring(1, 2), entry.getValue()));
        }
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
