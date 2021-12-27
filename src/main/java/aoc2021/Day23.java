package aoc2021;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static aoc2021.Day23.Point.point;

public class Day23 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Burrow burrow = Burrow.of(inputRaw);
        Point.fillCache(burrow.map.keySet());

        long result = burrow.solve().get();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        BurrowPart2 burrow = BurrowPart2.of(inputRaw);
        Point.fillCache(burrow.map.keySet());
        burrow.extendSideroomForPart2();
        Point.fillCache(burrow.map.keySet());
//        System.out.println("burrow = " + burrow);

        long result = burrow.solve().get();

        return String.valueOf(result);
    }

    record Point(int x, int y) {
        private static Point[][] cachePoints;

        public static void fillCache(Set<Point> points) {
            int maxX = points.stream().max(Comparator.comparingInt(Point::x)).get().x();
            int maxY = points.stream().max(Comparator.comparingInt(Point::y)).get().y();
            cachePoints = new Point[maxX + 1][maxY + 1];
            points.forEach(point -> cachePoints[point.x()][point.y()] = point);
        }

        public static Point point(int x, int y) {
//            return new Point(x, y);
            return cachePoints[x][y];
        }
    }

    record Move(Character amphipod, Point from, Point to) {
        public long energy() {
            int steps = from.y() - 1 + // vertical distance 'from' to hallway
                    Math.abs(from().x - to.x()) + // horizontal distance
                    to.y() - 1; // vertical distance 'to' to hallway
            return (long) steps * switch (amphipod) {
                case 'A' -> 1;
                case 'B' -> 10;
                case 'C' -> 100;
                case 'D' -> 1000;
                default -> throw new RuntimeException("siderooms");
            };
        }
    }

    static class Burrow {
        public final static char WALL = '#';
        public final static char OPENSPACE = '.';
        protected static final List<Point> SIDEROOM_A = Arrays.asList(new Point(3, 2), new Point(3, 3));
        protected static final List<Point> SIDEROOM_B = Arrays.asList(new Point(5, 2), new Point(5, 3));
        protected static final List<Point> SIDEROOM_C = Arrays.asList(new Point(7, 2), new Point(7, 3));
        protected static final List<Point> SIDEROOM_D = Arrays.asList(new Point(9, 2), new Point(9, 3));

        protected Map<Point, Character> map;
        protected List<Point> hallway;
        protected long spentEnergy;

        public static Map<Map<Point, Character>, Long> mapsInProgress = new HashMap<>();

        public Burrow(Map<Point, Character> map, List<Point> hallway, long spentEnergy) {
            this.map = map;
            this.hallway = hallway;
            this.spentEnergy = spentEnergy;
        }

        public Optional<Long> solve() {
            if (finished()) {
                return Optional.of(spentEnergy);
            }

            // do we have this map already with lower spent energy
            Long currentLowestEnergy = mapsInProgress.get(map);
            if (currentLowestEnergy != null) {
                if (currentLowestEnergy < spentEnergy) {
                    return Optional.empty();
                }
            }
            // add our map as it does not exist yet or has lower energy
            mapsInProgress.put(map, spentEnergy);

            // try the moves to own sideroom first
            List<Move> moves = map.entrySet().stream()
                    .filter(this::isAmphipod)
                    .filter(entry -> amphipodIsInHallway(entry.getKey()) ||
                            (amphipodIsInNotOwnSideroom(entry.getValue(), entry.getKey())) && spotsAboveInTheSideroomAreFree(entry.getKey()))
                    .map(entry -> findMoveToSideRoom(entry.getValue(), entry.getKey()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            if (moves.isEmpty()) {
                // moves to the hallway
                moves = map.entrySet().stream()
                        .filter(this::isAmphipod)
                        .filter(entry -> !amphipodIsInHallway(entry.getKey()))
                        .filter(entry -> !amphipodIsInFinalSpotInOwnSideroom(entry.getValue(), entry.getKey()))
                        .flatMap(entry -> movesToHallway(entry.getValue(), entry.getKey()).stream())
                        .collect(Collectors.toList());
            }

            // check if no moves
            if (moves.isEmpty()) {
                return Optional.empty();
            }

            Stream<Move> streamMoves;
            if (spentEnergy == 0) {
                streamMoves = moves.parallelStream();
            } else {
                streamMoves = moves.stream();
            }
            return streamMoves
                    .map(move -> createNewBurrow(move).solve())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .min(Comparator.comparingLong(l -> l))
                    .stream()
                    .findFirst();
        }

        protected Burrow createNewBurrow(Move move) {
            return new Burrow(applyMove(map, move), hallway, spentEnergy + move.energy());
        }

        private boolean amphipodIsInNotOwnSideroom(Character amphipod, Point point) {
            return sideroomsXPos(amphipod) != point.x();
        }

        private boolean spotsAboveInTheSideroomAreFree(Point point) {
            // check spots above are free
            for (int y = point.y() - 1; y > 1; y--) {
                if (map.get(point(point.x(), y)) != OPENSPACE) {
                    return false;
                }
            }
            return true;
        }

        private boolean isAmphipod(Map.Entry<Point, Character> entry) {
            char c = entry.getValue();
            return c == 'A' || c == 'B' || c == 'C' || c == 'D';
        }

        private boolean amphipodIsInHallway(Point point) {
            return point.y() == 1;
        }

        protected Map<Point, Character> applyMove(Map<Point, Character> map, Move move) {
            Map<Point, Character> newMap = new HashMap<>(map);
            newMap.put(move.from(), OPENSPACE);
            newMap.put(move.to(), move.amphipod);
            return newMap;
        }

        public List<Point> siderooms(char amphipod) {
            return switch (amphipod) {
                case 'A' -> SIDEROOM_A;
                case 'B' -> SIDEROOM_B;
                case 'C' -> SIDEROOM_C;
                case 'D' -> SIDEROOM_D;
                default -> throw new RuntimeException("siderooms");
            };
        }

        public boolean amphipodIsInFinalSpotInOwnSideroom(char amphipod, Point point) {
            if (siderooms(amphipod).contains(point)) {
                // amphipod is in final spot when below are all same amphipods
                return siderooms(amphipod).stream()
                        .filter(p -> p.y() > point.y())
                        .allMatch(p -> map.get(p) == amphipod);
            }
            return false;
        }

        public List<Move> movesToHallway(char amphipod, Point point) {
            // check when moving from lower point in side room if upper points is free
            for (int y = point.y(); y > 1; y--) {
                if (map.get(point(point.x(), y)) != OPENSPACE && map.get(point(point.x(), y)) != amphipod) {
                    return Collections.emptyList();
                }
            }

            List<Move> moves = new ArrayList<>();
            boolean blocked = false;
            Point to;
            // try left in the hallway
            for (int i = point.x() - 1; !blocked && i > 0; i--) {
                to = point(i, 1);
                if (map.get(to) != OPENSPACE) {
                    blocked = true;
                } else if (i != 3 && i != 5 && i != 7 && i != 9) {
                    moves.add(new Move(amphipod, point, to));
                }
            }

            blocked = false;
            // try right in the hallway
            for (int j = point.x() + 1; !blocked && j < 12; j++) {
                to = point(j, 1);
                if (map.get(to) != OPENSPACE) {
                    blocked = true;
                } else if (j != 3 && j != 5 && j != 7 && j != 9) {
                    moves.add(new Move(amphipod, point, to));
                }
            }

            return moves;
        }

        public Optional<Move> findMoveToSideRoom(char amphipod, Point fromPoint) {
            // check that sideroom has no other amphipods
            if (siderooms(amphipod).stream()
                    .anyMatch(p -> map.get(p) != OPENSPACE && map.get(p) != amphipod)) {
                return Optional.empty();
            }

            int sideroomXPos = sideroomsXPos(amphipod);

            // check path through hallway to sideroom
            for (int x = Math.min(fromPoint.x(), sideroomXPos) + 1; x < Math.max(fromPoint.x(), sideroomXPos); x++) {
                if (map.get(point(x, 1)) != OPENSPACE) {
                    return Optional.empty();
                }
            }

            // prefer lower spot if multiple available
            Optional<Point> to = siderooms(amphipod).stream()
                    .filter(p -> map.get(p) == OPENSPACE)
                    .max(Comparator.comparingInt(Point::y));
            return to.map(toPoint -> new Move(amphipod, fromPoint, toPoint));
        }

        private int sideroomsXPos(char amphipod) {
            return switch (amphipod) {
                case 'A' -> 3;
                case 'B' -> 5;
                case 'C' -> 7;
                case 'D' -> 9;
                default -> throw new RuntimeException("siderooms");
            };
        }

        public static Burrow of(List<String> input) {
            Map<Point, Character> map = new HashMap<>();
            for (int y = 1; y < 4; y++) {
                for (int x = 1; x < input.get(y).length(); x++) {
                    char c = input.get(y).charAt(x);
                    if (c != WALL && c != ' ') {
                        map.put(new Point(x, y), c);
                    }
                }
            }
            List<Point> hallway = new ArrayList<>();
            for (int x = 1; x < input.get(1).length(); x++) {
                hallway.add(new Point(x, 1));
            }
            return new Burrow(map, hallway, 0);
        }

        private long maxX() {
            return map.keySet().stream()
                    .mapToLong(Point::x)
                    .max()
                    .getAsLong();
        }

        private long maxY() {
            return map.keySet().stream()
                    .mapToLong(Point::y)
                    .max()
                    .getAsLong();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y <= maxY(); y++) {
                for (int x = 0; x <= maxX(); x++) {
                    sb.append(map.getOrDefault(point(x, y), ' '));
                }
                sb.append('\n');
            }
            sb.append("spentEnergy = " + spentEnergy);
            return sb.toString();
        }

        public boolean finished() {
            return map.get(point(3, 2)) == 'A' &&
                    map.get(point(3, 3)) == 'A' &&
                    map.get(point(5, 2)) == 'B' &&
                    map.get(point(5, 3)) == 'B' &&
                    map.get(point(7, 2)) == 'C' &&
                    map.get(point(7, 3)) == 'C' &&
                    map.get(point(9, 2)) == 'D' &&
                    map.get(point(9, 3)) == 'D';
        }
    }

    static class BurrowPart2 extends Burrow {
        protected static final List<Point> SIDEROOM_A = Arrays.asList(new Point(3, 2), new Point(3, 3), new Point(3, 4), new Point(3, 5));
        protected static final List<Point> SIDEROOM_B = Arrays.asList(new Point(5, 2), new Point(5, 3), new Point(5, 4), new Point(5, 5));
        protected static final List<Point> SIDEROOM_C = Arrays.asList(new Point(7, 2), new Point(7, 3), new Point(7, 4), new Point(7, 5));
        protected static final List<Point> SIDEROOM_D = Arrays.asList(new Point(9, 2), new Point(9, 3), new Point(9, 4), new Point(9, 5));

        public BurrowPart2(Map<Point, Character> map, List<Point> hallway, long spentEnergy) {
            super(map, hallway, spentEnergy);
        }

        public static BurrowPart2 of(List<String> input) {
            Burrow burrow = Burrow.of(input);
            return new BurrowPart2(burrow.map, burrow.hallway, burrow.spentEnergy);
        }

        @Override
        protected BurrowPart2 createNewBurrow(Move move) {
            return new BurrowPart2(applyMove(map, move), hallway, spentEnergy + move.energy());
        }

        public List<Point> siderooms(char amphipod) {
            return switch (amphipod) {
                case 'A' -> SIDEROOM_A;
                case 'B' -> SIDEROOM_B;
                case 'C' -> SIDEROOM_C;
                case 'D' -> SIDEROOM_D;
                default -> throw new RuntimeException("siderooms");
            };
        }

        public void extendSideroomForPart2() {
            // insert in the middle of the sideroom
            // #D#C#B#A#
            // #D#B#A#C#

            // first move the current bottom of the sideroom below
            map.put(new Point(3, 5), map.get(new Point(3, 3)));
            map.put(new Point(5, 5), map.get(new Point(5, 3)));
            map.put(new Point(7, 5), map.get(new Point(7, 3)));
            map.put(new Point(9, 5), map.get(new Point(9, 3)));

            // insert the new amphipods
            map.put(new Point(3, 3), 'D');
            map.put(new Point(5, 3), 'C');
            map.put(new Point(7, 3), 'B');
            map.put(new Point(9, 3), 'A');

            map.put(new Point(3, 4), 'D');
            map.put(new Point(5, 4), 'B');
            map.put(new Point(7, 4), 'A');
            map.put(new Point(9, 4), 'C');
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
