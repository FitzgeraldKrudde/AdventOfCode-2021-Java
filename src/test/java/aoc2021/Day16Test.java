package aoc2021;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class Day16Test {

    @Test
    void doPart1() throws Exception {
        Day day = getDay();

        assertThat(day.doPart1(Collections.singletonList("A0016C880162017C3686B18A3D4780"))).isEqualTo("31");
        assertThat(day.doPart1(Collections.singletonList("620080001611562C8802118E34"))).isEqualTo("12");
        assertThat(day.doPart1(Collections.singletonList("8A004A801A8002F478"))).isEqualTo("16");
        assertThat(day.doPart1(Collections.singletonList("EE00D40C823060"))).isEqualTo("14");
        assertThat(day.doPart1(Collections.singletonList("38006F45291200"))).isEqualTo("9");
        assertThat(day.doPart1(Collections.singletonList("C0015000016115A2E0802F182340"))).isEqualTo("23");
    }

    @Test
    void doPart2() throws Exception {
        Day day = getDay();

        assertThat(day.doPart2(Collections.singletonList("C200B40A82"))).isEqualTo("3");
        assertThat(day.doPart2(Collections.singletonList("04005AC33890"))).isEqualTo("54");
        assertThat(day.doPart2(Collections.singletonList("880086C3E88112"))).isEqualTo("7");
        assertThat(day.doPart2(Collections.singletonList("CE00C43D881120"))).isEqualTo("9");
        assertThat(day.doPart2(Collections.singletonList("D8005AC2A8F0"))).isEqualTo("1");
        assertThat(day.doPart2(Collections.singletonList("F600BC2D8F"))).isEqualTo("0");
        assertThat(day.doPart2(Collections.singletonList("9C005AC2F8F0"))).isEqualTo("0");
        assertThat(day.doPart2(Collections.singletonList("9C0141080250320F1802104A08"))).isEqualTo("1");
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
