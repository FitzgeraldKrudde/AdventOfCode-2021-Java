package aoc2021;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static aoc2021.Day20.Image.DARK;
import static aoc2021.Day20.Image.LIT;
import static java.util.stream.Collectors.toList;

public class Day20 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        String algorithm = inputRaw.stream()
                .takeWhile(line -> line.length() > 0)
                .collect(Collectors.joining());
        ImageEnhancer imageEnhancer = new ImageEnhancer(algorithm);

        List<String> inputImageLines = inputRaw.stream()
                .dropWhile(line -> line.length() > 0)
                .dropWhile(line -> line.length() == 0)
                .collect(toList());
        Image image = Image.of(inputImageLines);

        int step = 1;
        image = imageEnhancer.applyOn(image, step);
        step++;
        image = imageEnhancer.applyOn(image, step);

        long result = image.nrPixelsLit();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        String algorithm = inputRaw.stream()
                .takeWhile(line -> line.length() > 0)
                .collect(Collectors.joining());
        ImageEnhancer imageEnhancer = new ImageEnhancer(algorithm);

        List<String> inputImageLines = inputRaw.stream()
                .dropWhile(line -> line.length() > 0)
                .dropWhile(line -> line.length() == 0)
                .collect(toList());
        Image image = Image.of(inputImageLines);

        int step = 1;
        while (step<=50){
            image = imageEnhancer.applyOn(image, step);
            step++;
        }

        long result = image.nrPixelsLit();

        return String.valueOf(result);
    }

    record Image(Map<Point, Character> map) {
        public static final char DARK = '.';
        public static final char LIT = '#';

        public static Image of(List<String> input) {
            return new Image(IntStream.range(0, input.size())
                    .boxed()
                    .flatMap(i -> readLine(i, input.get(i)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        private static Stream<Map.Entry<Point, Character>> readLine(int y, String line) {
            return IntStream.range(0, line.length())
                    .mapToObj(x -> new SimpleEntry<>(Point.point(x, y), line.charAt(x)));
        }

        public long nrPixelsLit() {
            return map().values().stream()
                    .filter(c -> c == LIT)
                    .count();
        }

        public String toString() {
            int minX = map.keySet().stream().mapToInt(Point::x).min().getAsInt();
            int maxX = map.keySet().stream().mapToInt(Point::x).max().getAsInt();
            int minY = map.keySet().stream().mapToInt(Point::y).min().getAsInt();
            int maxY = map.keySet().stream().mapToInt(Point::y).max().getAsInt();

            StringBuilder sb = new StringBuilder();
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    sb.append(map.getOrDefault(Point.point(x, y), '.'));
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    record ImageEnhancer(String algorithm) {
        Image applyOn(Image image, int step) {
            Map<Point, Character> newMap = new HashMap<>();

            // get all points and all (new) neighbours
            List<Point> points = image.map().keySet().stream()
                    .flatMap(Point::neighbours)
                    .distinct()
                    .collect(toList());

            points.forEach(point -> newMap.put(point, calculateMode(image.map(), point, step)));

            return new Image(newMap);
        }

        private Character calculateMode(Map<Point, Character> map, Point point, int step) {
            // determine default for new neighbour
            char defaultMode;
            // when algorithm[0] = LIT then alternate between algorithm[0] and algorithm[511] :-)
            if (algorithm.charAt(0) == LIT) {
                if (step % 2 != 0) {
                    defaultMode = algorithm.charAt(511);
                } else {
                    defaultMode = algorithm.charAt(0);
                }
            } else {
                defaultMode = DARK;
            }
            int index = Integer.parseInt(
                    point.neighbours()
                            .map(neighbour -> map.getOrDefault(neighbour, defaultMode))
                            .map(String::valueOf)
                            .collect(Collectors.joining())
                            .replace(DARK, '0')
                            .replace(LIT, '1'), 2);

            return algorithm.charAt(index);
        }
    }

    record Point(int x, int y) {
        Stream<Point> neighbours() {
            return Stream.of(
                    new Point(x - 1, y - 1),
                    new Point(x, y - 1),
                    new Point(x + 1, y - 1),
                    new Point(x - 1, y),
                    new Point(x, y),
                    new Point(x + 1, y),
                    new Point(x - 1, y + 1),
                    new Point(x, y + 1),
                    new Point(x + 1, y + 1));
        }

        public static Point point(int x, int y) {
            return new Point(x, y);
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
