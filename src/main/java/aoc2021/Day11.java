package aoc2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Day11 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Cavern cavern = Cavern.of(inputRaw);

        long result = cavern.doSteps(100);

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Cavern cavern = Cavern.of(inputRaw);

        long result = LongStream.iterate(1, i -> i + 1)
                .filter(step -> cavern.allOctopusesFlashed(cavern.doStep()))
                .findFirst()
                .getAsLong();

        return String.valueOf(result);
    }

    record Point(int x, int y) {
    }

    record Cavern(Map<Point, Integer> map) {
        List<Point> neighbours(Point point) {
            return Stream.of(
                            new Point(point.x() - 1, point.y() - 1),
                            new Point(point.x(), point.y() - 1),
                            new Point(point.x() + 1, point.y() - 1),
                            new Point(point.x() + 1, point.y()),
                            new Point(point.x() + 1, point.y() + 1),
                            new Point(point.x(), point.y() + 1),
                            new Point(point.x() - 1, point.y() + 1),
                            new Point(point.x() - 1, point.y())
                    )
                    .filter(this::isPointOnMap)
                    .collect(toList());
        }

        public boolean isPointOnMap(Point point) {
            return map.containsKey(point);
        }

        public long doSteps(int steps) {
            return LongStream.range(0, steps)
                    .map(l -> doStep())
                    .sum();
        }

        public long doStep() {
            // increase energy level of all octopuses
            map.keySet().forEach(p -> map.put(p, map.get(p) + 1));

            // keep checking for new flashing octopuses (energy level > 9)
            List<Point> alreadyFlashedOctopuses = new ArrayList<>();
            List<Point> flashingOctopuses = getNewFlashingOctopuses(alreadyFlashedOctopuses);
            while (flashingOctopuses.size() > 0) {
                flashingOctopuses.stream()
                        .flatMap(p -> neighbours(p).stream())
                        .forEach(neighbour -> map.put(neighbour, map.get(neighbour) + 1));
                alreadyFlashedOctopuses.addAll(flashingOctopuses);
                flashingOctopuses = getNewFlashingOctopuses(alreadyFlashedOctopuses);
            }

            // set the energy level of the flashed octopuses to 0
            alreadyFlashedOctopuses.forEach(p -> map.put(p, 0));

            return alreadyFlashedOctopuses.size();
        }

        private List<Point> getNewFlashingOctopuses(List<Point> alreadyFlashedOctopuses) {
            return map.entrySet().stream()
                    .filter(entry -> !alreadyFlashedOctopuses.contains(entry.getKey()))
                    .filter(entry -> entry.getValue() > 9)
                    .map(Map.Entry::getKey)
                    .collect(toList());
        }

        static Cavern of(List<String> inputRaw) {
            final int lines = inputRaw.size();
            final int lineLength = inputRaw.get(0).length();
            Map<Point, Integer> map = new HashMap<>();

            for (int y = 0; y < lines; y++) {
                for (int x = 0; x < lineLength; x++) {
                    map.put(new Point(x, y), Character.getNumericValue(inputRaw.get(y).charAt(x)));
                }
            }

            return new Cavern(map);
        }

        public boolean allOctopusesFlashed(long flashingOctopuses) {
            return flashingOctopuses == map.size();
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
