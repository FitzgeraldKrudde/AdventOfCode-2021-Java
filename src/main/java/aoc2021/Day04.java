package aoc2021;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Day04 extends Day {

    // @formatter:off
    static public void main(String[] args) throws Exception {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        final String filename = clazz.getSimpleName().toLowerCase().replace("day0","day") + ".txt";

        // invoke "main" from the base nl.krudde.aoc2021.Day class
        new Day04().main(filename);
        // @formatter:on
    }

    @Override
    public String doPart1(List<String> inputRaw) {
        BingoSystem bingoSystem = parseInput(inputRaw);

        return String.valueOf(bingoSystem.getScorePart1());
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        BingoSystem bingoSystem = parseInput(inputRaw);

        return String.valueOf(bingoSystem.getScorePart2());
    }

    private BingoSystem parseInput(List<String> inputRaw) {
        List<Long> draws = Arrays.stream(inputRaw.get(0).split(","))
                .map(Long::parseLong)
                .collect(toList());
        List<BingoGame> bingoGames = new ArrayList<>();

        List<String> rows = new ArrayList<>();
        for (int i = 2; i < inputRaw.size(); i++) {
            String line = inputRaw.get(i);
            if (line.length() == 0) {
                bingoGames.add(new BingoGame(rows));
                rows.clear();
            } else {
                rows.add(line);
            }
        }
        // add the last board
        bingoGames.add(new BingoGame(rows));

        return new BingoSystem(bingoGames, draws);
    }

    record BingoSystem(List<BingoGame> boards, List<Long> draws) {
        public long getScorePart1() {
            Entry<BingoGame, List<Long>> bingo = getBingo();
            return bingo.getKey().score(bingo.getValue());
        }

        public Entry<BingoGame, List<Long>> getBingo() {
            List<Long> draws = new ArrayList<>(this.draws);
            List<Long> drawn = new ArrayList<>();
            Optional<BingoGame> winningBoard;

            do {
                drawn.add(draws.remove(0));
                winningBoard = boards.stream()
                        .filter(bingoGame -> bingoGame.getBingoRowIfAny(drawn).isPresent())
                        .findFirst();
            } while (winningBoard.isEmpty());

            return new SimpleEntry<>(winningBoard.get(), drawn);
        }


        public long getScorePart2() {
            Entry<BingoGame, List<Long>> bingo;

            while (boards.size() > 1) {
                boards.remove(getBingo().getKey());
            }

            // one board left
            bingo = getBingo();

            return bingo.getKey().score(bingo.getValue());
        }
    }

    static class BingoGame {
        private final List<List<Long>> rows;

        BingoGame(List<String> lines) {
            // get the horizontal rows
            rows = lines.stream()
                    .map(line -> Arrays.stream(line.trim().split("\\s+"))
                            .map(Long::parseLong)
                            .collect(toList()))
                    .collect(toList());

            //append the vertical rows as new rows for convenience
            List<List<Long>> verticalRows =
                    IntStream.range(0, rows.size())
                            .mapToObj(i ->
                                    rows.stream()
                                            .map(row -> row.get(i))
                                            .collect(toList())
                            ).toList();
            rows.addAll(verticalRows);
        }

        public Optional<List<Long>> getBingoRowIfAny(List<Long> draws) {
            return rows.stream()
                    .filter(draws::containsAll)
                    .findFirst();
        }

        public long score(List<Long> draws) {
            // divide the sum by 2 as we doubled the rows
            // (as convenience I added all vertical rows)
            return draws.get(draws.size() - 1) * (rows.stream()
                    .flatMap(Collection::stream)
                    .filter(l -> !draws.contains(l))
                    .mapToLong(Long::valueOf)
                    .sum() / 2);

        }
    }
}
