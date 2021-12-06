package aoc2021;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Day05 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<Point> points = inputRaw.stream()
                .flatMap(line -> getPointsFromLine(line, false))
                .toList();

        return String.valueOf(getCountOverlappingPoints(points));
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Point> points = inputRaw.stream()
                .flatMap(line -> getPointsFromLine(line, true))
                .toList();

        return String.valueOf(getCountOverlappingPoints(points));
    }

    public Stream<Point> getPointsFromLine(String line, boolean includeDiagonal) {
        String[] words = line.split("\\s+");
        String[] startString = words[0].split(",");
        String[] endString = words[2].split(",");
        Point start = new Point(Integer.parseInt(startString[0]), Integer.parseInt(startString[1]));
        Point end = new Point(Integer.parseInt(endString[0]), Integer.parseInt(endString[1]));

        if (start.x == end.x) {
            // vertical
            return IntStream.rangeClosed(min(start.y, end.y), max(start.y, end.y))
                    .mapToObj(y -> new Point(start.x, y));
        } else if (start.y == end.y) {
            // horizontal
            return IntStream.rangeClosed(min(start.x, end.x), max(start.x, end.x))
                    .mapToObj(x -> new Point(x, start.y));
        } else {
            //diagonal
            if (includeDiagonal) {
                if (
                        (start.x == (min(start.x, end.x)) && (start.y == min(start.y, end.y))) ||
                                (start.x == (max(start.x, end.x)) && (start.y == max(start.y, end.y)))
                ) {
                    // line is top/left to bottom/right or bottom/right to top/left
                    // navigate top/left to bottom/right
                    return IntStream.rangeClosed(min(start.x, end.x), max(start.x, end.x))
                            .mapToObj(x -> new Point(x, min(start.y, end.y) + x - min(start.x, end.x)));
                } else {
                    // line is bottom/left to top/right or top/right to bottom/left
                    // navigate bottom/left to top/right
                    return IntStream.rangeClosed(min(start.x, end.x), max(start.x, end.x))
                            .mapToObj(x -> new Point(x, max(start.y, end.y) - (x - min(start.x, end.x))));
                }
            } else {
                return Stream.empty();
            }
        }
    }

    private long getCountOverlappingPoints(List<Point> points) {
        return points.stream()
                .collect(groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= 2)
                .count();
    }

    record Point(int x, int y) {
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

        // invoke "main" from the base nl.krudde.aoc2021.Day class
        day.main(filename);
    }
    // @formatter:on
}
