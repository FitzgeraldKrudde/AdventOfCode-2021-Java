package aoc2021;

import java.util.Arrays;
import java.util.List;

public class Day15 extends Day {
    @Override
    public String doPart1(List<String> input) {
        Cavern cavern = Cavern.of(input);

        long result = cavern.lowestPathRiskScore();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> input) {
        Cavern cavern = Cavern.of(input);

        long result = cavern.lowestPathRiskScoreExtendedMap();

        return String.valueOf(result);
    }


    record Cavern(int[][] riskLevelMap) {
        private final static int INFINITE = Integer.MAX_VALUE;

        static Cavern of(List<String> input) {
            final int size = input.size();
            int[][] riskLevelMap = new int[size][size];

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    riskLevelMap[y][x] = Character.getNumericValue(input.get(y).charAt(x));
                }
            }

            return new Cavern(riskLevelMap);
        }

        public int[][] extendedMap() {
            int size = riskLevelMap.length;
            int[][] extendedMap = new int[5 * size][5 * size];

            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            int riskLevel = riskLevelMap[j][i] + x + y;
                            if (riskLevel > 9) {
                                riskLevel -= 9;
                            }
                            extendedMap[j + y * size][i + x * size] = riskLevel;
                        }
                    }
                }
            }
            return extendedMap;
        }

        public long lowestPathRiskScoreExtendedMap() {
            return new Cavern(extendedMap()).lowestPathRiskScore();
        }

        public long lowestPathRiskScore() {
            int size = riskLevelMap.length;

            int[][] pathRiskLevel = new int[size][size];
            Arrays.stream(pathRiskLevel).forEach(a -> Arrays.fill(a, INFINITE));

            // set start on 0
            pathRiskLevel[0][0] = 0;

            // make initial calculation based on left->right, top->bottom
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (y == 0 && x == 0) {
                        continue;
                    }
                    int currentStepDistance = riskLevelMap[y][x];
                    int riskLevelLeft = y > 0 ? pathRiskLevel[y - 1][x] : INFINITE;
                    int riskLevelAbove = x > 0 ? pathRiskLevel[y][x - 1] : INFINITE;
                    int min = Math.min(riskLevelAbove, riskLevelLeft);
                    pathRiskLevel[y][x] = min + currentStepDistance;
                }
            }

            // keep updating while improvements are possible
            int mods = INFINITE;
            while (mods > 0) {
                mods = 0;
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        if (x == 0 && y == 0) {
                            continue;
                        }
                        int currentDistance = pathRiskLevel[x][y];
                        int currentStepDistance = riskLevelMap[x][y];
                        int riskLevelLeft = x > 0 ? pathRiskLevel[x - 1][y] + currentStepDistance : INFINITE;
                        int riskLevelAbove = y > 0 ? pathRiskLevel[x][y - 1] + currentStepDistance : INFINITE;
                        int riskLevelRight = x < size - 1 ? pathRiskLevel[x + 1][y] + currentStepDistance : INFINITE;
                        int riskLevelBelow = y < size - 1 ? pathRiskLevel[x][y + 1] + currentStepDistance : INFINITE;
                        if (riskLevelLeft < currentDistance) {
                            pathRiskLevel[x][y] = riskLevelLeft;
                            mods++;
                        }
                        if (riskLevelAbove < currentDistance) {
                            pathRiskLevel[x][y] = riskLevelAbove;
                            mods++;
                        }
                        if (riskLevelRight < currentDistance) {
                            pathRiskLevel[x][y] = riskLevelRight;
                            mods++;
                        }
                        if (riskLevelBelow < currentDistance) {
                            pathRiskLevel[x][y] = riskLevelBelow;
                            mods++;
                        }
                    }
                }
            }
            return pathRiskLevel[size - 1][size - 1];
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
