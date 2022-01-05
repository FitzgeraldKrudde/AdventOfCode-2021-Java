package aoc2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day22 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<RebootInstruction> rebootInstructions = inputRaw.stream()
                .map(RebootInstruction::of)
                .collect(Collectors.toList());

        // simple approach: keep track of all cubes
        Map<Cube, Boolean> core = new HashMap<>();

        rebootInstructions.stream()
                .filter(rebootInstruction -> rebootInstruction.cuboid.minx >= -50 && rebootInstruction.cuboid.maxx <= 50)
                .forEach(rebootInstruction -> rebootInstruction.cubes()
                        .forEach(cube -> core.put(cube, rebootInstruction.mode())));

        long result = core.values()
                .stream()
                .filter(cube -> cube)
                .count();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {

        List<RebootInstruction> rebootInstructions = inputRaw.stream()
                .map(RebootInstruction::of)
                .collect(Collectors.toList());

        // strategy is to store Cuboids which are on
        // process the reboot instructions and split cuboid where needed (overlap)
        // the result being more (smaller) cuboids
        List<Cuboid> splittedOnCuboids = new ArrayList<>();

        for (RebootInstruction rebootInstruction : rebootInstructions) {
            splittedOnCuboids = splittedOnCuboids.stream()
                    .flatMap(cuboid -> cuboid.splitUntilNoOverlap(rebootInstruction.cuboid()))
                    .collect(Collectors.toList());

            // now we have no overlap and can add the cubeoid from the reboot instruction (when mode wa turning on)
            if (rebootInstruction.mode()) {
                splittedOnCuboids.add(rebootInstruction.cuboid);
            }

        }

        long result = splittedOnCuboids.stream()
                .mapToLong(Cuboid::nrCubes)
                .sum();

        return String.valueOf(result);
    }

    record Cube(int x, int y, int z) {
    }

    record Cuboid(int minx, int maxx, int miny, int maxy, int minz, int maxz) {
        public long nrCubes() {
            long x = Math.abs(maxx - minx + 1);
            long y = Math.abs(maxy - miny + 1);
            long z = Math.abs(maxz - minz + 1);

            return (x != 0 ? x : 1) *
                    (y != 0 ? y : 1) *
                    (z != 0 ? z : 1);
        }

        public boolean hasOverlap(Cuboid other) {
            return Math.max(minx, other.minx) <= Math.min(maxx, other.maxx) &&
                    Math.max(miny, other.miny) <= Math.min(maxy, other.maxy) &&
                    Math.max(minz, other.minz) <= Math.min(maxz, other.maxz);
        }

        public long amountOverlap(Cuboid other) {
            if (!hasOverlap(other)) {
                return 0;
            }

            long x = Math.abs(Math.min(maxx, other.maxx) - Math.max(minx, other.minx)) + 1;
            long y = Math.abs(Math.min(maxy, other.maxy) - Math.max(miny, other.miny)) + 1;
            long z = Math.abs(Math.min(maxz, other.maxz) - Math.max(minz, other.minz)) + 1;

            return x * y * z;
        }

        public Stream<Cuboid> splitUntilNoOverlap(Cuboid other) {
            List<Cuboid> splittedCuboids = new ArrayList<>();
            splittedCuboids.add(this);
            int count = 1;

            splittedCuboids = splittedCuboids.stream()
                    .flatMap(cuboid -> cuboid.splitWhenColliding(other))
                    .collect(Collectors.toList());
            while (splittedCuboids.size() > count) {
                count = splittedCuboids.size();
                splittedCuboids = splittedCuboids.stream()
                        .flatMap(cuboid -> cuboid.splitWhenColliding(other))
                        .collect(Collectors.toList());
            }

            return splittedCuboids.stream();
        }

        public Stream<Cuboid> splitWhenColliding(Cuboid other) {
            if (!hasOverlap(other)) {
                return Stream.of(this);
            }
            if (isContainedIn(other)) {
                return Stream.empty();
            }

            // just split this cuboid based when it collides with the other cubeoid
            // simple approach: just split on one axis
            // if split for multiple axis are needed this method will be called again
            // first try x, then y and then z

            // x axes
            if (Math.max(minx, other.minx) <= Math.min(maxx, other.maxx)) {
                int startOverlap = Math.max(minx, other.minx);
                int endOverlap = Math.min(maxx, other.maxx);
                if (startOverlap > minx) {
                    Cuboid cuboid1 = new Cuboid(minx, startOverlap - 1, miny, maxy, minz, maxz);
                    Cuboid cuboid2 = new Cuboid(startOverlap, maxx, miny, maxy, minz, maxz);
                    return Stream.of(cuboid1, cuboid2);
                }
                if (endOverlap < maxx) {
                    Cuboid cuboid1 = new Cuboid(minx, endOverlap, miny, maxy, minz, maxz);
                    Cuboid cuboid2 = new Cuboid(endOverlap + 1, maxx, miny, maxy, minz, maxz);
                    return Stream.of(cuboid1, cuboid2);
                }
            }

            // y axes
            if (Math.max(miny, other.miny) <= Math.min(maxy, other.maxy)) {
                int startOverlap = Math.max(miny, other.miny);
                int endOverlap = Math.min(maxy, other.maxy);
                if (startOverlap > miny) {
                    Cuboid cuboid1 = new Cuboid(minx, maxx, miny, startOverlap - 1, minz, maxz);
                    Cuboid cuboid2 = new Cuboid(minx, maxx, startOverlap, maxy, minz, maxz);
                    return Stream.of(cuboid1, cuboid2);
                }
                if (endOverlap < maxy) {
                    Cuboid cuboid1 = new Cuboid(minx, maxx, miny, endOverlap, minz, maxz);
                    Cuboid cuboid2 = new Cuboid(minx, maxx, endOverlap + 1, maxy, minz, maxz);
                    return Stream.of(cuboid1, cuboid2);
                }
            }

            // z axes
            if (Math.max(minz, other.minz) <= Math.min(maxz, other.maxz)) {
                int startOverlap = Math.max(minz, other.minz);
                int endOverlap = Math.min(maxz, other.maxz);
                if (startOverlap > minz) {
                    Cuboid cuboid1 = new Cuboid(minx, maxx, miny, maxy, minz, startOverlap - 1);
                    Cuboid cuboid2 = new Cuboid(minx, maxx, miny, maxy, startOverlap, maxz);
                    return Stream.of(cuboid1, cuboid2);
                }
                if (endOverlap < maxz) {
                    Cuboid cuboid1 = new Cuboid(minx, maxx, miny, maxy, minz, endOverlap);
                    Cuboid cuboid2 = new Cuboid(minx, maxx, miny, maxy, endOverlap + 1, maxz);
                    return Stream.of(cuboid1, cuboid2);
                }
            }
            throw new RuntimeException("should not come here..");
        }

        public boolean isContainedIn(Cuboid other) {
            return amountOverlap(other) == nrCubes();
        }

        public Stream<Cube> cubes() {
            return IntStream.rangeClosed(minx, maxx)
                    .boxed()
                    .flatMap(x -> IntStream.rangeClosed(miny, maxy)
                            .boxed()
                            .flatMap(y -> IntStream.rangeClosed(minz, maxz)
                                    .mapToObj(z -> new Cube(x, y, z))
                            )
                    );
        }
    }

    record RebootInstruction(boolean mode, Cuboid cuboid) {
        static RebootInstruction of(String line) {
            // on x=10..12,y=10..12,z=10..12
            boolean mode = false;
            String[] strings = line.split("\\s+");
            if ("on".equals(strings[0])) {
                mode = true;
            }
            String[] coordinatePairs = strings[1].split(",");
            String[] xcoordinates = coordinatePairs[0].split("=")[1].split("\\.\\.");
            int xmin = Integer.parseInt(xcoordinates[0]);
            int xmax = Integer.parseInt(xcoordinates[1]);
            String[] ycoordinates = coordinatePairs[1].split("=")[1].split("\\.\\.");
            int ymin = Integer.parseInt(ycoordinates[0]);
            int ymax = Integer.parseInt(ycoordinates[1]);
            String[] zcoordinates = coordinatePairs[2].split("=")[1].split("\\.\\.");
            int zmin = Integer.parseInt(zcoordinates[0]);
            int zmax = Integer.parseInt(zcoordinates[1]);

            assert xmin <= xmax;
            assert ymin <= ymax;
            assert zmin <= zmax;
            return new RebootInstruction(mode, new Cuboid(xmin, xmax, ymin, ymax, zmin, zmax));
        }

        public Stream<Cube> cubes() {
            return cuboid.cubes();
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
