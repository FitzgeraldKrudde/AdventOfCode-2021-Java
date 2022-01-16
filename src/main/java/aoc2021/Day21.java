package aoc2021;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;

public class Day21 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Game game = Game.of(inputRaw);
        game.play();

        long result = game.score();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        GameWithQuantumDie game = GameWithQuantumDie.of(inputRaw);
        game.play();

        long result = game.score();

        return String.valueOf(result);
    }

    static class Game {
        private final DeterministicDie deterministicDie;
        private int positionPlayer1;
        private int positionPlayer2;
        private long scorePlayer1 = 0;
        private long scorePlayer2 = 0;

        static Game of(List<String> lines) {
            int pos1 = Integer.parseInt(lines.get(0).split(":")[1].trim());
            int pos2 = Integer.parseInt(lines.get(1).split(":")[1].trim());

            return new Game(new DeterministicDie(), pos1, pos2);
        }

        public Game(DeterministicDie deterministicDie, int positionPlayer1, int positionPlayer2) {
            this.deterministicDie = deterministicDie;
            this.positionPlayer1 = positionPlayer1;
            this.positionPlayer2 = positionPlayer2;
        }

        public void play() {
            while (true) {
                int roll = deterministicDie.roll() + deterministicDie.roll() + deterministicDie.roll();
                int move = roll % 10;
                positionPlayer1 += move;
                if (positionPlayer1 > 10) {
                    positionPlayer1 -= 10;
                }
                scorePlayer1 += positionPlayer1;
                if (scorePlayer1 >= 1000) {
                    break;
                }

                roll = deterministicDie.roll() + deterministicDie.roll() + deterministicDie.roll();
                move = roll % 10;
                positionPlayer2 += move;
                if (positionPlayer2 > 10) {
                    positionPlayer2 -= 10;
                }
                scorePlayer2 += positionPlayer2;
                if (scorePlayer2 >= 1000) {
                    break;
                }
            }
        }

        public long score() {
            return Math.min(scorePlayer1, scorePlayer2) * deterministicDie.getRolls();
        }
    }

    static class GameWithQuantumDie {
        // keep a list of game states: the player position/score and for the number of universes it occurs
        private List<ScoreAndNrUniverses> scoreAndNrUniversesList;

        public GameWithQuantumDie(List<ScoreAndNrUniverses> scoreAndNrUniversesList) {
            this.scoreAndNrUniversesList = scoreAndNrUniversesList;
        }

        static GameWithQuantumDie of(List<String> lines) {
            int pos1 = Integer.parseInt(lines.get(0).split(":")[1].trim());
            int pos2 = Integer.parseInt(lines.get(1).split(":")[1].trim());

            return new GameWithQuantumDie(List.of(new ScoreAndNrUniverses(new PlayerPositionScores(new PlayerPositionScore(pos1, 0), new PlayerPositionScore(pos2, 0)), 1)));
        }

        public void play() {
            do {
                // based on the current game state:
                // - roll for both players (for unfinished games)
                // - combine the resulting game states
                scoreAndNrUniversesList = scoreAndNrUniversesList.stream()
                        .flatMap(scoreAndNrUniverses -> rollQuantumDie(scoreAndNrUniverses, true))
                        .collect(Collectors.toMap(ScoreAndNrUniverses::playerPositionScores,
                                ScoreAndNrUniverses::nrUniverses,
                                Long::sum))
                        .entrySet()
                        .stream()
                        .map(entry -> new ScoreAndNrUniverses(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());

                scoreAndNrUniversesList = scoreAndNrUniversesList.stream()
                        .flatMap(scoreAndNrUniverses -> rollQuantumDie(scoreAndNrUniverses, false))
                        .collect(Collectors.toMap(ScoreAndNrUniverses::playerPositionScores,
                                ScoreAndNrUniverses::nrUniverses,
                                Long::sum))
                        .entrySet()
                        .stream()
                        .map(entry -> new ScoreAndNrUniverses(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());

                // until all games are finished
            } while (!scoreAndNrUniversesList.stream().allMatch(ScoreAndNrUniverses::isGameFinished));
        }

        private Stream<ScoreAndNrUniverses> rollQuantumDie(ScoreAndNrUniverses scoreAndNrUniverses, boolean player1) {
            if (scoreAndNrUniverses.isGameFinished()) {
                return Stream.of(scoreAndNrUniverses);
            }

            // 3 rolls means 3x3x3=27 outcomes
            return IntStream.rangeClosed(1, 3)
                    .boxed()
                    .flatMap(roll1 -> IntStream.rangeClosed(1, 3)
                            .boxed()
                            .flatMap(roll2 -> IntStream.rangeClosed(1, 3)
                                    .mapToObj(roll3 -> processRollForPlayer(scoreAndNrUniverses, player1, roll1 + roll2 + roll3))
                            )
                    );
        }

        private ScoreAndNrUniverses processRollForPlayer(ScoreAndNrUniverses scoreAndNrUniverses, boolean player1, int roll) {
            if (player1) {
                return new ScoreAndNrUniverses(new PlayerPositionScores(scoreAndNrUniverses.playerPositionScores().player1().roll(roll),
                        scoreAndNrUniverses.playerPositionScores().player2()),
                        scoreAndNrUniverses.nrUniverses());
            } else {
                return new ScoreAndNrUniverses(new PlayerPositionScores(scoreAndNrUniverses.playerPositionScores().player1(),
                        scoreAndNrUniverses.playerPositionScores().player2().roll(roll)),
                        scoreAndNrUniverses.nrUniverses());
            }
        }

        public long score() {
            long scorePlayer1 = scoreAndNrUniversesList.stream()
                    .filter(scoreAndNrUniverses -> scoreAndNrUniverses.playerPositionScores.player1().score >= 21)
                    .map(ScoreAndNrUniverses::nrUniverses)
                    .reduce(0L, Long::sum);

            long scorePlayer2 = scoreAndNrUniversesList.stream()
                    .filter(scoreAndNrUniverses -> scoreAndNrUniverses.playerPositionScores.player2().score >= 21)
                    .map(ScoreAndNrUniverses::nrUniverses)
                    .reduce(0L, Long::sum);

            return max(scorePlayer1, scorePlayer2);
        }
    }

    record ScoreAndNrUniverses(PlayerPositionScores playerPositionScores, long nrUniverses) {
        public boolean isGameFinished() {
            return playerPositionScores.player1().score() >= 21 || playerPositionScores.player2().score() >= 21;
        }
    }

    record PlayerPositionScores(PlayerPositionScore player1, PlayerPositionScore player2) {
    }

    record PlayerPositionScore(int position, long score) {
        public PlayerPositionScore roll(int roll) {
            int newPosition = position + roll;
            if (newPosition > 10) {
                newPosition -= 10;
            }
            long newScore = score + newPosition;

            return new PlayerPositionScore(newPosition, newScore);
        }
    }

    static class DeterministicDie {
        private int nr = 0;
        private long rolls = 0;

        public long getRolls() {
            return rolls;
        }

        public int roll() {
            ++rolls;
            if (nr == 100) {
                nr = 1;
            } else {
                nr++;
            }
            return nr;
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
