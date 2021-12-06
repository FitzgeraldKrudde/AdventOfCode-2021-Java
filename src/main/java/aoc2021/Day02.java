package aoc2021;

import java.util.List;

public class Day02 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        long forward = countMoves(inputRaw, "forward");
        long up = countMoves(inputRaw, "up");
        long down = countMoves(inputRaw, "down");

        return String.valueOf(forward * (down - up));
    }

    private long countMoves(List<String> inputRaw, String direction) {
        return inputRaw.stream()
                .filter(line -> line.startsWith(direction))
                .map(line -> line.split("\\s+")[1])
                .mapToLong(Long::valueOf)
                .sum();
    }

    @Override
    public String doPart2(List<String> inputRaw) {

        Submarine submarine = new Submarine(new Position(0, 0, 0));
        inputRaw.stream()
                .forEach(submarine::move);

        return String.valueOf(submarine.position.horizontal() * submarine.position.depth());
    }

    record Position(long horizontal, long depth, long aim) {
    }

    static class Submarine {
        private Position position;

        Submarine(Position position) {
            this.position = position;
        }

        public void move(String instruction) {
            String[] words = instruction.split("\\s+");
            String direction = words[0];
            long moves = Long.parseLong(words[1]);
            position = switch (direction) {
                case "up" -> new Position(position.horizontal, position.depth, position.aim - moves);
                case "down" -> new Position(position.horizontal, position.depth, position.aim + moves);
                case "forward" -> new Position(position.horizontal + moves, position.depth + (position.aim * moves), position.aim);
                default -> throw new RuntimeException("unknown instruction");
            };
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

        // invoke "main" from the base nl.krudde.aoc2021.Day class
        day.main(filename);
    }
    // @formatter:on
}
