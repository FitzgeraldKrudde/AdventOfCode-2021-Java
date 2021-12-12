package aoc2021;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Day12 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Cave cave = Cave.of(inputRaw);

        List<Path> paths = cave.findPaths("start");

        long result = paths.stream()
                .filter(Path::pathEndsAtEnd)
                .count();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Cave cave = Cave.of(inputRaw);

        List<Path> paths = cave.findPathsAllowingOneSmallCaveTwice("start");

        long result = paths.stream()
                .filter(Path::pathEndsAtEnd)
                .count();

        return String.valueOf(result);
    }

    record Cave(List<Segment> segments) {
        public List<Path> findPaths(String start) {
            return findPaths(new Path(List.of(start)), false);
        }

        public List<Path> findPathsAllowingOneSmallCaveTwice(String start) {
            return findPaths(new Path(List.of(start)), true);
        }

        private List<Path> findPaths(Path currentPath, boolean allowingOneSmallCaveTwice) {
            String point = currentPath.points().get(currentPath.points.size() - 1);
            List<Path> paths = getSegments(point).stream()
                    .filter(segment -> isSegmentAllowed(currentPath, segment, allowingOneSmallCaveTwice))
                    .map(segment -> currentPath.add(segment.to))
                    .collect(toList());

            List<Path> completedPaths = paths.stream()
                    .filter(Path::pathEndsAtEnd)
                    .collect(toList());

            completedPaths.addAll(
                    paths.stream()
                            .filter(path -> !path.pathEndsAtEnd())
                            .flatMap(path -> findPaths(path, allowingOneSmallCaveTwice).stream())
                            .collect(toList()));

            return completedPaths;
        }

        private boolean isSegmentAllowed(Path currentPath, Segment segment, boolean allowingOneSmallCaveTwice) {
            if (!segment.endsInSmallCave()) {
                return true;
            }

            // we are going to a small cave
            if (allowingOneSmallCaveTwice) {
                if (currentPath.someSmallCaveAlreadyVisitedTwice()) {
                    return !currentPath.points.contains(segment.to());
                } else {
                    return true;
                }
            } else {
                return !currentPath.points.contains(segment.to());
            }
        }

        private List<Segment> getSegments(String point) {
            return segments.stream()
                    .filter(segment -> segment.from.equals(point))
                    .collect(toList());
        }

        static Cave of(List<String> input) {
            return new Cave(input.stream()
                    .flatMap(Cave::getSegment)
                    .collect(toList()));
        }

        private static Stream<Segment> getSegment(String line) {
            String[] endpoints = line.split("-");
            String from = endpoints[0];
            String to = endpoints[1];

            // add segment both ways (unless start or end)
            if (from.equals("start") || to.equals("end")) {
                return Stream.of(new Segment(from, to));
            } else if (to.equals("start") || from.equals("end")) {
                return Stream.of(new Segment(to, from));
            }
            return Stream.of(new Segment(from, to), new Segment(to, from));
        }
    }

    record Path(List<String> points) {
        public Path add(String point) {
            Path path = new Path(new ArrayList<>(points));
            path.points.add(point);
            return path;
        }

        public boolean pathEndsAtEnd() {
            return points().get(points().size() - 1).equals("end");
        }

        public List<String> getSmallCaves() {
            return points.stream()
                    .filter(point -> point.toLowerCase().equals(point))
                    .collect(toList());
        }

        public boolean someSmallCaveAlreadyVisitedTwice() {
            List<String> smallCaves = getSmallCaves();
            Set<String> hashSet = new HashSet<>(smallCaves);
            return smallCaves.size() != hashSet.size();
        }
    }

    record Segment(String from, String to) {
        private boolean endsInSmallCave(String point) {
            return point.toLowerCase().equals(point);
        }

        public boolean endsInSmallCave() {
            return endsInSmallCave(to);
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
