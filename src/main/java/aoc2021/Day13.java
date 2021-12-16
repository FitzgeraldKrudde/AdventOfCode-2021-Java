package aoc2021;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Day13 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        TransparantPaper transparantPaper = TransparantPaper.of(inputRaw);
        List<FoldInstruction> foldInstructions = getFoldInstructions(inputRaw);

        long result = transparantPaper.fold(foldInstructions.get(0)).dots().size();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        TransparantPaper transparantPaper = TransparantPaper.of(inputRaw);
        List<FoldInstruction> foldInstructions = getFoldInstructions(inputRaw);

        for (FoldInstruction foldInstruction : foldInstructions) {
            transparantPaper = transparantPaper.fold(foldInstruction);
        }

        transparantPaper.print();

        return String.valueOf(0);
    }

    private List<FoldInstruction> getFoldInstructions(List<String> inputRaw) {
        return inputRaw.stream()
                .filter(line -> line.startsWith("fold"))
                .map(FoldInstruction::of)
                .collect(toList());
    }

    record Point(int x, int y) {
        public static Point of(String line) {
            String[] numbers = line.split(",");
            return new Point(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
        }
    }

    record FoldInstruction(char axis, int coordinate) {
        public static FoldInstruction of(String line) {
            String instruction = line.split("\\s+")[2];
            return new FoldInstruction(instruction.charAt(0), Integer.parseInt(instruction.substring(2)));
        }
    }

    record TransparantPaper(Set<Point> dots) {
        static TransparantPaper of(List<String> input) {
            Set<Point> dots = input.stream()
                    .filter(line -> line.length() > 0)
                    .filter(line -> Character.isDigit(line.charAt(0)))
                    .map(Point::of)
                    .collect(Collectors.toSet());

            return new TransparantPaper(dots);
        }

        public TransparantPaper fold(FoldInstruction foldInstruction) {
            if (foldInstruction.axis() == 'x') {
                Set<Point> newDots = dots.stream()
                        .filter(dot -> dot.x() < foldInstruction.coordinate())
                        .collect(Collectors.toSet());

                dots.stream()
                        .filter(dot -> dot.x() > foldInstruction.coordinate())
                        .forEach(dot -> newDots.add(new Point(foldInstruction.coordinate() - (dot.x() - foldInstruction.coordinate()), dot.y)));

                return new TransparantPaper(newDots);
            } else {
                Set<Point> newDots = dots.stream()
                        .filter(dot -> dot.y() < foldInstruction.coordinate())
                        .collect(Collectors.toSet());

                dots.stream()
                        .filter(dot -> dot.y() > foldInstruction.coordinate())
                        .forEach(dot -> newDots.add(new Point(dot.x(), foldInstruction.coordinate() - (dot.y() - foldInstruction.coordinate()))));

                return new TransparantPaper(newDots);
            }
        }

        public void print() {
            int maxX = dots.stream().mapToInt(Point::x).max().getAsInt();
            int maxY = dots.stream().mapToInt(Point::y).max().getAsInt();
            for (int y = 0; y <= maxY; y++) {
                for (int x = 0; x <= maxX; x++) {
                    if (x % 5 == 0) {
                        System.out.print("   ");
                    }
                    if (dots.contains(new Point(x, y))) {
                        System.out.print('#');
                    } else
                        System.out.print('.');
                }
                System.out.println();
            }
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
