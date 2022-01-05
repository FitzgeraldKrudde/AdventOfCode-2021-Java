package aoc2021;

import java.util.List;

public class Day17 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        TargetArea targetArea = TargetArea.of(inputRaw.get(0));
        int maxy = 0;
        for (int x = 1; x < targetArea.minx(); x++) {
            for (int y = 0; y < 1000; y++) {
                ProbePosition probePosition = new ProbePosition(0, 0, x, y, 0);
                while (!targetArea.inTargetArea(probePosition) && !targetArea.passedTargetArea(probePosition)) {
                    probePosition = probePosition.step();
                }
                if (targetArea.inTargetArea(probePosition)) {
                    if (probePosition.maxY() > maxy) {
                        maxy = probePosition.maxY;
                    }
                }
            }
        }

        long result = maxy;

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        TargetArea targetArea = TargetArea.of(inputRaw.get(0));
        int count = 0;
        for (int xVelocity = 1; xVelocity <= targetArea.maxx; xVelocity++) {
            for (int yVelocity = -100; yVelocity < 100; yVelocity++) {
                ProbePosition probePosition = new ProbePosition(0, 0, xVelocity, yVelocity, 0);
                while (!targetArea.inTargetArea(probePosition) && !targetArea.passedTargetArea(probePosition)) {
                    probePosition = probePosition.step();
                }
                if (targetArea.inTargetArea(probePosition)) {
                    count++;
                }
            }
        }
        long result = count;

        return String.valueOf(result);
    }

    record ProbePosition(int x, int y, int xVelocity, int yVelocity, int maxY) {
        ProbePosition step() {
            int newYmax = y + yVelocity > maxY ? y + yVelocity : maxY;
            int newXVelocity = 0;
            if (xVelocity != 0) {
                newXVelocity = xVelocity > 0 ? xVelocity - 1 : xVelocity + 1;
            }
            return new ProbePosition(x + xVelocity, y + yVelocity, newXVelocity, yVelocity - 1, newYmax);
        }
    }

    record TargetArea(int minx, int maxx, int miny, int maxy) {
        static TargetArea of(String line) {
            // target area: x=20..30, y=-10..-5
            String[] strings = line.replace(",", "").split("\\s+");
            String[] xcoordinates = strings[2].split("=")[1].split("\\.\\.");
            int xmin = Integer.parseInt(xcoordinates[0]);
            int xmax = Integer.parseInt(xcoordinates[1]);
            String[] ycoordinates = strings[3].split("=")[1].split("\\.\\.");
            int ymin = Integer.parseInt(ycoordinates[1]);
            int ymax = Integer.parseInt(ycoordinates[0]);

            return new TargetArea(xmin, xmax, ymin, ymax);
        }

        boolean inTargetArea(ProbePosition probePosition) {
            return probePosition.x >= minx &&
                    probePosition.x <= maxx &&
                    probePosition.y <= miny &&
                    probePosition.y >= maxy;
        }

        boolean passedTargetArea(ProbePosition probePosition) {
            return probePosition.x > maxx || probePosition.y < maxy;
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
