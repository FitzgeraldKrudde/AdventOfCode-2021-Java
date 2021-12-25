package aoc2021;

import java.util.Arrays;
import java.util.List;

public class Day25 extends Day {
    private final static char FREE = '.';
    private final static char EAST = '>';
    private final static char SOUTH = 'v';

    public void print(char[][] map) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                System.out.print(map[x][y]);
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public String doPart1(List<String> inputRaw) {
        int xlength = inputRaw.get(0).length();
        int ylength = inputRaw.size();
        char[][] map = new char[ylength][xlength];
        Arrays.stream(map).forEach(row -> Arrays.fill(row, FREE));
        for (int y = 0; y < ylength; y++) {
            for (int x = 0; x < xlength; x++) {
                map[y][x] = inputRaw.get(y).charAt(x);
            }
        }
//        print(map);

        long steps = 0;
        while (true) {
            steps++;
            char[][] newMap = new char[ylength][xlength];
            Arrays.stream(newMap).forEach(row -> Arrays.fill(row, FREE));
            // east
            for (int x = 0; x < map.length; x++) {
                for (int y = 0; y < map[0].length; y++) {
                    switch (map[x][y]) {
                        case FREE:
                        case SOUTH:
                            break;
                        case EAST:
                            if (map[x][(y + 1) % map[0].length] == FREE) {
                                newMap[x][(y + 1) % map[0].length] = EAST;
                            } else {
                                newMap[x][y] = EAST;
                            }
                            break;
                    }
                }
            }
            // south
            for (int x = 0; x < map.length; x++) {
                for (int y = 0; y < map[0].length; y++) {
                    switch (map[x][y]) {
                        case FREE:
                        case EAST:
                            break;
                        case SOUTH:
                            if (newMap[(x + 1) % map.length][y] == FREE && map[(x + 1) % map.length][y] != SOUTH) {
                                newMap[(x + 1) % map.length][y] = SOUTH;
                            } else {
                                newMap[x][y] = SOUTH;
                            }
                            break;
                    }
                }
            }
            if (Arrays.deepEquals(map, newMap)) {
                break;
            }
            map = newMap;
        }

        return String.valueOf(steps);
    }

    @Override
    public String doPart2(List<String> inputRaw) {


        long result = 0;

        return String.valueOf(result);
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
