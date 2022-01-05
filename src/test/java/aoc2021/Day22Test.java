package aoc2021;

import aoc2021.Day22.Cuboid;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class Day22Test {

    @Test
    void doPart1() throws Exception {
        Day day = getDay();

        assertThat(day.doPart1(day.readInput("day22_1.txt"))).isEqualTo("39");
    }

    @Test
    void doPart2() throws Exception {
        Day day = getDay();

        assertThat(day.doPart2(day.readInput("day22_1.txt"))).isEqualTo("39");
        assertThat(day.doPart2(day.readInput("day22_2.txt"))).isEqualTo("2758514936282235");
    }

    @Test
    void hasOverlap() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 0);
        assertThat(cube1.hasOverlap(cube1)).isTrue();

        Cuboid cube2 = new Cuboid(5, 7, 6, 8, 0, 0);
        assertThat(cube1.hasOverlap(cube2)).isTrue();

        Cuboid cube3 = new Cuboid(5, 7, 16, 18, 0, 0);
        assertThat(cube1.hasOverlap(cube3)).isFalse();

        Cuboid instruction4 = new Cuboid(5, 7, 6, 8, 1, 1);
        assertThat(cube1.hasOverlap(instruction4)).isFalse();
    }

    @Test
    void amountOverlap() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(0, 10, 0, 10, 0, 5);
        assertThat(cube1.amountOverlap(cube2)).isEqualTo(726);

        Cuboid cube3 = new Cuboid(-10, 5, 0, 10, 0, 10);
        assertThat(cube1.amountOverlap(cube3)).isEqualTo(726);

        Cuboid cube4 = new Cuboid(10, 20, 10, 20, 10, 20);
        assertThat(cube1.amountOverlap(cube4)).isEqualTo(1);
    }

    @Test
    public void splitNoOverlap() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(0, 10, 0, 10, -1, -5);
        List<Cuboid> cuboidList = cube1.splitWhenColliding(cube2).collect(Collectors.toList());

        assertThat(cuboidList.size()).isEqualTo(1);
        assertThat(cuboidList.contains(cube1)).isTrue();
    }

    @Test
    public void splitOnce() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(0, 10, 0, 10, 0, 5);
        List<Cuboid> cuboidList = cube1.splitWhenColliding(cube2).collect(Collectors.toList());

        assertThat(cuboidList.size()).isEqualTo(2);
        assertThat(cuboidList.contains(new Cuboid(0, 10, 0, 10, 0, 5))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(0, 10, 0, 10, 6, 10))).isTrue();
    }

    @Test
    public void splitMultiple() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(0, 5, 0, 5, 0, 5);
        List<Cuboid> cuboidList = cube1.splitUntilNoOverlap(cube2).collect(Collectors.toList());

        assertThat(cuboidList.size()).isEqualTo(3);
        assertThat(cuboidList.contains(new Cuboid(0, 5, 0, 5, 6, 10))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(0, 5, 6, 10, 0, 10))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(6, 10, 0, 10, 0, 10))).isTrue();
    }

    @Test
    public void splitOnPoint() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(10, 10, 10, 10, 10, 10);
        List<Cuboid> cuboidList = cube1.splitUntilNoOverlap(cube2).collect(Collectors.toList());

        assertThat(cuboidList.size()).isEqualTo(3);
        assertThat(cuboidList.contains(new Cuboid(0, 9, 0, 10, 0, 10))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(10, 10, 0, 9, 0, 10))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(10, 10, 10, 10, 0, 9))).isTrue();
    }

    @Test
    public void splitOnLine() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(0, 10, -10, 0, -10, 0);
        List<Cuboid> cuboidList = cube1.splitUntilNoOverlap(cube2).collect(Collectors.toList());

        assertThat(cuboidList.size()).isEqualTo(2);
        assertThat(cuboidList.contains(new Cuboid(0, 10, 0, 0, 1, 10))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(0, 10, 1, 10, 0, 10))).isTrue();
    }

    @Test
    public void splitComplex() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(5, 15, 5, 15, 5, 15);
        List<Cuboid> cuboidList = cube1.splitUntilNoOverlap(cube2).collect(Collectors.toList());

        assertThat(cuboidList.size()).isEqualTo(3);
        assertThat(cuboidList.contains(new Cuboid(0, 4, 0, 10, 0, 10))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(5, 10, 0, 4, 0, 10))).isTrue();
        assertThat(cuboidList.contains(new Cuboid(5, 10, 5, 10, 0, 4))).isTrue();
    }


    @Test
    public void isContained() {
        Cuboid cube1 = new Cuboid(0, 10, 0, 10, 0, 10);
        Cuboid cube2 = new Cuboid(0, 10, 0, 10, 0, 5);
        assertThat(cube2.isContainedIn(cube1)).isTrue();
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
