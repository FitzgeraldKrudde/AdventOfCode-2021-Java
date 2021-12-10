package aoc2021;

import java.util.*;
import java.util.stream.Collectors;

public class Day09 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        HeightMap heightMap = parseInput(inputRaw);

        long result = heightMap.getLowestPoints().stream()
                .mapToLong(heightMap::getRiskLevel)
                .sum();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        HeightMap heightMap = parseInput(inputRaw);

        List<Long> basinsSizes = heightMap.getBasinsSizes(heightMap.getLowestPoints());

        return String.valueOf(basinsSizes.stream()
                .sorted()
                .skip(basinsSizes.size() - 3)
                .reduce(1L, (l1, l2) -> l1 * l2));
    }

    record Point(int x, int y) {
        List<Point> neighbours() {
            return Arrays.asList(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1));
        }
    }

    record HeightMap(Map<Point, Integer> map) {
        public int getHeight(Point point) {
            return map.get(point);
        }

        public boolean isInMap(Point point) {
            return map.containsKey(point);
        }

        public boolean isLowestPoint(Point point) {
            return point.neighbours().stream()
                    .filter(this::isInMap)
                    .allMatch(neighbour -> getHeight(point) < getHeight(neighbour));
        }

        public List<Point> getLowestPoints() {
            return map().keySet().stream()
                    .filter(this::isLowestPoint)
                    .collect(Collectors.toList());
        }

        public long getRiskLevel(Point p) {
            return getHeight(p) + 1;
        }

        public List<Long> getBasinsSizes(List<Point> points) {
            return points.stream()
                    .map(this::getBasinSize)
                    .collect(Collectors.toList());
        }

        private long getBasinSize(Point point) {
            List<Point> basin = new ArrayList<>();
            List<Point> newBasinNeighbours = new ArrayList<>();

            // start with given point
            newBasinNeighbours.add(point);
            while (newBasinNeighbours.size() > 0) {
                basin.addAll(newBasinNeighbours);
                newBasinNeighbours = newBasinNeighbours.stream()
                        .flatMap(p -> p.neighbours().stream()
                                .filter(this::isInMap)
                                .filter(n -> getHeight(n) > getHeight(p))
                                .filter(n -> getHeight(n) < 9)
                                .filter(n -> !basin.contains(n)))
                        .distinct()
                        .collect(Collectors.toList());
            }
            return basin.size();
        }
    }

    private HeightMap parseInput(List<String> inputRaw) {
        final int lines = inputRaw.size();
        final int lineLength = inputRaw.get(0).length();
        Map<Point, Integer> heightMap = new HashMap<>();

        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < lineLength; x++) {
                heightMap.put(new Point(x, y), Character.getNumericValue(inputRaw.get(y).charAt(x)));
            }
        }

        return new HeightMap(heightMap);
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
