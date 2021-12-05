package aoc2021;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Day03 extends Day {

    // @formatter:off
    static public void main(String[] args) throws Exception {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        final String filename = clazz.getSimpleName().toLowerCase().replace("_0", "_") + ".txt";

        // invoke "main" from the base nl.krudde.aoc2021.Day class
        new Day03().main(filename);
        // @formatter:on
    }

    @Override
    public String doPart1(List<String> inputRaw) {
        Report report = new Report(inputRaw);

        return String.valueOf(report.powerConsumption());
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Report report = new Report(inputRaw);

        return String.valueOf(report.getOxygenGeneratorRating() * report.getCO2ScrubberRating());
    }

    static class Report {
        private final List<String> bitStrings;
        private final int nrBits;

        public Report(List<String> lines) {
            bitStrings = lines;
            nrBits = lines.get(0).length();
        }

        public long powerConsumption() {
            StringBuilder mostCommon = new StringBuilder();
            StringBuilder leastCommon = new StringBuilder();
            for (int i = 0; i < nrBits; i++) {
                if (isMostCommon(bitStrings, i, '1')) {
                    mostCommon.append("1");
                    leastCommon.append("0");
                } else {
                    mostCommon.append("0");
                    leastCommon.append("1");
                }
            }
            long gammaRate = Long.parseLong(mostCommon.toString(), 2);
            long epsilonRate = Long.parseLong(leastCommon.toString(), 2);

            return gammaRate * epsilonRate;
        }

        private boolean isMostCommon(List<String> lines, int bitposition, char c) {
            String line = getVertical(lines, bitposition);
            return 2 * countOccurrences(line, c) >= line.length();
        }

        private long countOccurrences(String s, char c) {
            return s.chars().
                    filter(ch -> ch == c)
                    .count();
        }

        public List<String> filterOnValueInPosition(List<String> strings, int position, char value) {
            return strings.stream()
                    .filter(s -> s.charAt(position) == value)
                    .collect(toList());
        }

        private String getVertical(List<String> lines, int position) {
            return lines.stream()
                    .map(line -> line.charAt(position))
                    .map(Object::toString)
                    .collect(Collectors.joining());
        }

        public long getOxygenGeneratorRating() {
            List<String> copyOfBits = new ArrayList<>(bitStrings);

            int bitPosition = 0;
            while (copyOfBits.size() > 1) {
                if (isMostCommon(copyOfBits, bitPosition, '1')) {
                    copyOfBits = filterOnValueInPosition(copyOfBits, bitPosition, '1');
                } else {
                    copyOfBits = filterOnValueInPosition(copyOfBits, bitPosition, '0');
                }
                bitPosition++;
            }

            return Integer.parseInt(copyOfBits.get(0), 2);
        }

        public long getCO2ScrubberRating() {
            List<String> copyOfBits = new ArrayList<>(bitStrings);

            int bitPosition = 0;
            while (copyOfBits.size() > 1) {
                if (isMostCommon(copyOfBits, bitPosition, '1')) {
                    copyOfBits = filterOnValueInPosition(copyOfBits, bitPosition, '0');
                } else {
                    copyOfBits = filterOnValueInPosition(copyOfBits, bitPosition, '1');
                }
                bitPosition++;
            }

            return Integer.parseInt(copyOfBits.get(0), 2);
        }

    }
}
