package aoc2021;

import java.util.ArrayList;
import java.util.List;

public class Day16 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Packet packet = Packet.of(inputRaw.get(0));

        long result = packet.sumVersionNumbers();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Packet packet = Packet.of(inputRaw.get(0));

        long result = packet.calculateExpression();

        return String.valueOf(result);
    }

    static class Packet {
        public static final int TYPE_LITERAL = 4;
        public static final char LENGTH_TYPE_TOTAL_NR_BITS = '0';

        private int version;
        private int type;
        private long value;
        private List<Packet> subPackets;

        public Packet() {
            subPackets = new ArrayList<>();
        }

        public static Packet of(String transmission) {
            Packet packet = new Packet();
            String binaryString = convertHexToBinaryString(transmission);
            packet.fillPacket(binaryString);
            return packet;
        }

        private int fillPacket(String binaryTransmission) {
            int totalPacketsLength = 0;
            version = Integer.valueOf(binaryTransmission.substring(0, 3), 2);
            type = Integer.valueOf(binaryTransmission.substring(3, 6), 2);
            if (type == TYPE_LITERAL) {
                totalPacketsLength = 6 + readValue(binaryTransmission.substring(6));
            } else {
                if (binaryTransmission.charAt(6) == LENGTH_TYPE_TOTAL_NR_BITS) {
                    int bitLength = Integer.valueOf(binaryTransmission.substring(7, 22), 2);
                    String subPacketsBinaryString = binaryTransmission.substring(22, 22 + bitLength);
                    while (subPacketsBinaryString.length() > 0) {
                        Packet packet = new Packet();
                        subPacketsBinaryString = subPacketsBinaryString.substring(packet.fillPacket(subPacketsBinaryString));
                        subPackets.add(packet);
                    }
                    totalPacketsLength = 22 + bitLength;
                } else {
                    totalPacketsLength = 18; // version + type + type length + length header
                    int nrPackets = Integer.valueOf(binaryTransmission.substring(7, 18), 2);
                    String subPacketsBinaryString = binaryTransmission.substring(18);
                    while (nrPackets > 0) {
                        Packet packet = new Packet();
                        int packetLength = packet.fillPacket(subPacketsBinaryString);
                        totalPacketsLength += packetLength;
                        subPacketsBinaryString = subPacketsBinaryString.substring(packetLength);
                        subPackets.add(packet);
                        nrPackets--;
                    }
                }
            }
            return totalPacketsLength;
        }

        private int readValue(final String transmission) {
            int chunks = 0;
            StringBuilder sb = new StringBuilder();

            while (transmission.charAt(5 * chunks) == '1') {
                sb.append(transmission, 5 * chunks + 1, 5 * chunks + 5);
                chunks++;
            }
            sb.append(transmission, 5 * chunks + 1, 5 * chunks + 5);
            chunks++;

            value = Long.valueOf(sb.toString(), 2);
            return chunks * 5;
        }

        private static String convertHexToBinaryString(String hex) {
            return hex
                    .replaceAll("0", "0000")
                    .replaceAll("1", "0001")
                    .replaceAll("2", "0010")
                    .replaceAll("3", "0011")
                    .replaceAll("4", "0100")
                    .replaceAll("5", "0101")
                    .replaceAll("6", "0110")
                    .replaceAll("7", "0111")
                    .replaceAll("8", "1000")
                    .replaceAll("9", "1001")
                    .replaceAll("A", "1010")
                    .replaceAll("B", "1011")
                    .replaceAll("C", "1100")
                    .replaceAll("D", "1101")
                    .replaceAll("E", "1110")
                    .replaceAll("F", "1111");
        }

        public long sumVersionNumbers() {
            return version + subPackets.stream()
                    .mapToLong(Packet::sumVersionNumbers)
                    .sum();
        }

        public long calculateExpression() {
            if (subPackets.size() == 0) {
                return value;
            }

            return switch (type) {
                case 0 -> subPackets.stream()
                        .mapToLong(Packet::calculateExpression)
                        .reduce(0L, Long::sum);
                case 1 -> subPackets.stream()
                        .mapToLong(Packet::calculateExpression)
                        .reduce(1L, (l1, l2) -> l1 * l2);
                case 2 -> subPackets.stream()
                        .mapToLong(Packet::calculateExpression)
                        .reduce(Long.MAX_VALUE, Math::min);
                case 3 -> subPackets.stream()
                        .mapToLong(Packet::calculateExpression)
                        .reduce(Long.MIN_VALUE, Math::max);
                case 5 -> subPackets.get(0).calculateExpression() > subPackets.get(1).calculateExpression() ? 1 : 0;
                case 6 -> subPackets.get(0).calculateExpression() < subPackets.get(1).calculateExpression() ? 1 : 0;
                case 7 -> subPackets.get(0).calculateExpression() == subPackets.get(1).calculateExpression() ? 1 : 0;
                default -> throw new RuntimeException("invalid type");
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
