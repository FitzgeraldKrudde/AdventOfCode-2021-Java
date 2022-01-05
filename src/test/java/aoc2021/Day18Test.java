package aoc2021;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Stream;

import static aoc2021.Day18.Snailfish;
import static org.assertj.core.api.Assertions.assertThat;

class Day18Test {

    @Test
    void explode() {
        Snailfish snailfish;

        snailfish = Snailfish.of("[[[[[9,8],1],2],3],4]");
        assertThat(snailfish.findExplodingNode().get().toString()).isEqualTo("[9,8]");
        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[[[0,9],2],3],4]");

        snailfish = Snailfish.of("[7,[6,[5,[4,[3,2]]]]]");
        assertThat(snailfish.findExplodingNode().get().toString()).isEqualTo("[3,2]");
        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[7,[6,[5,[7,0]]]]");

        snailfish = Snailfish.of("[[6,[5,[4,[3,2]]]],1]");
        assertThat(snailfish.findExplodingNode().get().toString()).isEqualTo("[3,2]");
        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[6,[5,[7,0]]],3]");

        snailfish = Snailfish.of("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]");
        assertThat(snailfish.findExplodingNode().get().toString()).isEqualTo("[7,3]");
        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]");

        snailfish = Snailfish.of("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]");
        assertThat(snailfish.findExplodingNode().get().toString()).isEqualTo("[3,2]");
        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[3,[2,[8,0]]],[9,[5,[7,0]]]]");

    }

    @Test
    public void reduce() {
        Snailfish snailfish = Snailfish.of("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]");

        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[[[0,7],4],[7,[[8,4],9]]],[1,1]]");
        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[[[0,7],4],[15,[0,13]]],[1,1]]");
        assertThat(snailfish.split()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]");
        assertThat(snailfish.split()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]");
        assertThat(snailfish.explode()).isTrue();
        assertThat(snailfish.toString()).isEqualTo("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]");
    }

    @Test
    public void sum() {
        assertThat(Stream.of("[1,1]","[2,2]","[3,3]","[4,4]")
                .map(Snailfish::of)
                .reduce(Snailfish.of("[]"), Snailfish::sum)
                .toString())
                .isEqualTo("[[[[1,1],[2,2]],[3,3]],[4,4]]");

        assertThat(Stream.of("[1,1]","[2,2]","[3,3]","[4,4]","[5,5]")
                .map(Snailfish::of)
                .reduce(Snailfish.of("[]"), Snailfish::sum)
                .toString())
                .isEqualTo("[[[[3,0],[5,3]],[4,4]],[5,5]]");

        assertThat(Stream.of("[1,1]","[2,2]","[3,3]","[4,4]","[5,5]","[6,6]")
                .map(Snailfish::of)
                .reduce(Snailfish.of("[]"), Snailfish::sum)
                .toString())
        .isEqualTo("[[[[5,0],[7,4]],[5,5]],[6,6]]");

        assertThat(Stream.of("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
                        "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
                        "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
                        "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
                        "[7,[5,[[3,8],[1,4]]]]",
                        "[[2,[2,2]],[8,[8,1]]]",
                        "[2,9]",
                        "[1,[[[9,3],9],[[9,0],[0,7]]]]",
                        "[[[5,[7,4]],7],1]",
                        "[[[[4,2],2],6],[8,7]]"
                        )
                .map(Snailfish::of)
                .reduce(Snailfish.of("[]"), Snailfish::sum)
                .toString())
                .isEqualTo("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]");
    }

    @Test
    void doPart1() throws Exception {
        Day day = getDay();

        assertThat(day.doPart1(day.readInput(getInputFilename()))).isEqualTo("4140");
    }

    @Test
    void doPart2() throws Exception {
        Day day = getDay();

        assertThat(day.doPart2(day.readInput(getInputFilename()))).isEqualTo("3993");
    }

    // @formatter:off
    private String getInputFilename() {
        // get our class
        final Class<?> clazz = new Object() {}.getClass().getEnclosingClass();

        // construct filename with input
        return clazz.getSimpleName().toLowerCase().replace("test","").replace("day0", "day") + ".txt";
        // @formatter:on
    }

    private Day getDay() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // get our Test class
        final Class<?> clazz = new Object() {
        }.getClass().getEnclosingClass();

        // get the classname of the class under test
        final String fullClassName = clazz.getCanonicalName().replace("Test", "");

        // create instance
        return (Day) Class.forName(fullClassName).getDeclaredConstructor().newInstance();
    }
    // @formatter:on
}
