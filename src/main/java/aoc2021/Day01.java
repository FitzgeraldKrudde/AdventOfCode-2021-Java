package aoc2021;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Day01 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);

        Optional<Long> optionalPreviousDepth = Optional.empty();
        int count = 0;
        for (long l : input) {
            if (optionalPreviousDepth.isPresent()) {
                if (l > optionalPreviousDepth.get()) {
                    count++;
                }
            }
            optionalPreviousDepth = Optional.of(l);
        }

        return String.valueOf(count);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);

        int count = 0;
        for (int i = 0; i < input.size(); i++) {
            if (i + 3 < input.size()) {
                if (input.get(i + 3) > input.get(i)) {
                    count++;
                }
            }
        }

        return String.valueOf(count);
    }

    private List<Long> parseInput(List<String> inputRaw) {
        return inputRaw.stream()
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
