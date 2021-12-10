package aoc2021;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class Day10 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        long result = inputRaw.stream()
                .map(Line::new)
                .mapToLong(Line::getSyntaxErrorScore)
                .sum();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Line> incompleteLines = inputRaw.stream()
                .map(Line::new)
                .filter(Line::isIncomplete)
                .collect(toList());

        List<Long> scores = incompleteLines.stream()
                .map(Line::getIncompletenessScore)
                .collect(toList());

        long result = scores.stream()
                .sorted()
                .skip(scores.size() / 2)
                .limit(1)
                .findFirst().get();

        return String.valueOf(result);
    }

    record Line(String line) {
        static Pattern corruptPattern1 = Pattern.compile("\\((\\s*)[)}>\\]]");
        static Pattern corruptPattern2 = Pattern.compile("\\{(\\s*)[)>\\]]");
        static Pattern corruptPattern3 = Pattern.compile("\\[(\\s*)[)>}]");
        static Pattern corruptPattern4 = Pattern.compile("\\<(\\s*)[)}\\]]");
        static List<Pattern> patterns = Arrays.asList(corruptPattern1, corruptPattern2, corruptPattern3, corruptPattern4);

        private long getNrReplacedChars(String s) {
            return s.chars().filter(c -> c == ' ').count();
        }

        public long getSyntaxErrorScore() {
            String copy = replaceCorrectChunks();
            // try to apply the mismatch patterns
            Optional<Matcher> matcher = patterns.stream()
                    .map(pattern -> pattern.matcher(copy))
                    .filter(Matcher::find)
                    .findFirst();

            return matcher.map(value -> getScoreForCorruptChar(copy.charAt(value.start() + value.group().length() - 1))).orElse(0L);
        }

        private String replaceCorrectChunks() {
            String copy = line;
            long nrReplacedChars;
            // substitute all matching pairs with spaces
            do {
                nrReplacedChars = getNrReplacedChars(copy);
                copy = copy
                        .replaceAll("\\((\\s*)\\)", " $1 ")
                        .replaceAll("\\[(\\s*)]", " $1 ")
                        .replaceAll("\\{(\\s*)}", " $1 ")
                        .replaceAll("<(\\s*)>", " $1 ");
            }
            while (getNrReplacedChars(copy) != nrReplacedChars);

            return copy;
        }

        private long getScoreForCorruptChar(char c) {
            return switch (c) {
                case ')' -> 3;
                case ']' -> 57;
                case '}' -> 1197;
                case '>' -> 25137;
                default -> throw new RuntimeException("invalid char: " + c);
            };
        }

        public boolean isIncomplete() {
            return getSyntaxErrorScore() == 0;
        }

        private long getIncompletenessScore() {
            List<Long> scores = new StringBuilder(replaceCorrectChunks().replaceAll("\\s*", "")).reverse().chars()
                    .mapToLong(c -> getScoreMatchingCloseCharacter((char) c))
                    .boxed()
                    .collect(toList());

            AtomicLong score = new AtomicLong(0);
            scores.forEach(s -> score.set(5 * score.get() + s));
            return score.get();
        }

        private long getScoreMatchingCloseCharacter(char c) {
            return switch (c) {
                case '(' -> 1;
                case '[' -> 2;
                case '{' -> 3;
                case '<' -> 4;
                default -> throw new RuntimeException("invalid char: " + c);
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

        // invoke "main" from the base Day class
        day.main(filename);
    }
    // @formatter:on
}
