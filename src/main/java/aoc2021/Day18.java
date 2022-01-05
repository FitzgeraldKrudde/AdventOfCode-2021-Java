package aoc2021;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Day18 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        long result = inputRaw.stream()
                .map(Snailfish::of)
                .reduce(Snailfish.of("[]"), Snailfish::sum)
                .magnitude();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Snailfish> listSnailFish = inputRaw.stream()
                .map(Snailfish::of)
                .collect(Collectors.toList());

        long result = listSnailFish.stream()
                .flatMap(snailFish1 -> listSnailFish.stream().map(snailfish2 -> new SimpleEntry<>(snailFish1, snailfish2)))
                .filter(entry -> !entry.getKey().equals(entry.getValue()))
                .map(entry -> entry.getKey().sum(entry.getValue()))
                .mapToLong(Snailfish::magnitude)
                .max()
                .getAsLong();

        return String.valueOf(result);
    }

    record Snailfish(Node root) {
        static Snailfish of(String line) {
            if ("[]".equals(line)) {
                return new Snailfish(new Node(null, null, null, 0, true));
            } else {
                Node root = new Node(null, null, null, 0, false);
                processLine(root, line, 0);

                return new Snailfish(root);
            }
        }

        static private int processLine(Node parent, String line, int i) {
            switch (line.charAt(i)) {
                case '[':
                    Node nodeLeft = new Node(parent, null, null, 0, false);
                    parent.left = nodeLeft;
                    int leftEnd = processLine(nodeLeft, line, i + 1);

                    Node nodeRight = new Node(parent, null, null, 0, false);
                    parent.right = nodeRight;
                    int rightEnd = processLine(nodeRight, line, leftEnd + 2);
                    return rightEnd + 1;
                default:
                    if (Character.isDigit(line.charAt(i))) {
                        // make parent a leaf node
                        parent.value = Character.getNumericValue(line.charAt(i));
                        parent.isLeafNode = true;
                        return i;
                    } else {
                        throw new RuntimeException("failed case " + line + " " + i);
                    }
            }
        }

        public boolean explode() {
            return findExplodingNode().map(this::explodeNode).isPresent();
        }

        public Optional<Node> findExplodingNode() {
            return findExplodingNode(root, 0);
        }

        public Optional<Node> findExplodingNode(Node node, int depth) {
            depth++;
            if (depth > 4 && node.left != null && node.left.isLeafNode() && node.right != null && node.right.isLeafNode()) {
                return Optional.of(node);
            } else {
                Optional<Node> explodingNode = Optional.empty();
                if (node.left != null) {
                    explodingNode = findExplodingNode(node.left, depth);
                }
                if (explodingNode.isEmpty()) {
                    if (node.right != null) {
                        explodingNode = findExplodingNode(node.right, depth);
                    }
                }
                return explodingNode;
            }
        }

        public Snailfish explodeNode(Node node) {
            addToFirstValue(node.parent, node, node.left.value, true, true);
            addToFirstValue(node.parent, node, node.right.value, false, true);

            node.value = 0;
            node.left = null;
            node.right = null;
            node.isLeafNode = true;

            return this;
        }

        public boolean addToFirstValue(Node node, Node referringNode, long value, boolean goLeft, boolean goingUp) {
            if (node.isLeafNode()) {
                node.value += value;
                return true;
            }

            if (node.isRootNode()) {
                if (goLeft) {
                    if (!node.left.equals(referringNode)) {
                        return addToFirstValue(node.left, node, value, goLeft, false);
                    } else return false;
                } else {
                    if (!node.right.equals(referringNode)) {
                        return addToFirstValue(node.right, node, value, goLeft, false);
                    } else {
                        return false;
                    }
                }
            }

            if (goLeft) {
                if (goingUp) {
                    if (!node.left.equals(referringNode)) {
                        if (addToFirstValue(node.left, node, value, goLeft, false)) {
                            return true;
                        }
                    }
                    return addToFirstValue(node.parent, node, value, goLeft, true);
                } else {
                    if (addToFirstValue(node.right, node, value, goLeft, false)) {
                        return true;
                    } else {
                        return addToFirstValue(node.left, node, value, goLeft, false);
                    }
                }
            } else {
                if (goingUp) {
                    if (!node.right.equals(referringNode)) {
                        if (addToFirstValue(node.right, node, value, goLeft, false)) {
                            return true;
                        }
                    }
                    return addToFirstValue(node.parent, node, value, goLeft, true);
                } else {
                    if (addToFirstValue(node.left, node, value, goLeft, false)) {
                        return true;
                    } else {
                        return addToFirstValue(node.right, node, value, goLeft, false);
                    }
                }
            }
        }

        public boolean split() {
            return findSplittingNode(root).map(this::splitNode).isPresent();
        }

        public Optional<Node> findSplittingNode(Node node) {
            Optional<Node> splittingNode = Optional.empty();
            if (node.left != null) {
                if (node.left.isLeafNode() && node.left.value >= 10) {
                    splittingNode = Optional.of(node.left);
                } else {
                    splittingNode = findSplittingNode(node.left);
                    if (splittingNode.isEmpty()) {
                        if (node.right.isLeafNode() && node.right.value >= 10) {
                            splittingNode = Optional.of(node.right);
                        } else {
                            splittingNode = findSplittingNode(node.right);
                        }
                    }
                }
            }
            return splittingNode;
        }

        public Snailfish splitNode(Node node) {
            Node left = new Node(node, null, null, node.value / 2, true);
            Node right = new Node(node, null, null, (node.value + 1) / 2, true);

            node.value = 0;
            node.left = left;
            node.right = right;
            node.isLeafNode = false;

            return this;
        }

        public Snailfish sum(Snailfish otherSnailfish) {
            if (this.root.isLeafNode()) {
                return Snailfish.of(otherSnailfish.toString());
            }
            if (otherSnailfish.root.isLeafNode()) {
                return Snailfish.of(this.toString());
            }

            // make copies as we reduce the SnailFish :-)
            Snailfish copyThis = Snailfish.of(this.toString());
            Snailfish copyOther = Snailfish.of(otherSnailfish.toString());
            Node newRoot = new Node(null, copyThis.root, copyOther.root, 0, false);
            copyThis.root.parent = newRoot;
            copyOther.root.parent = newRoot;

            return new Snailfish(newRoot).reduce();
        }

        public Snailfish reduce() {
            while (explode() || split()) {
            }

            return this;
        }

        public long magnitude() {
            while (!root.isLeafNode()) {
                collapseNodes(root);
            }
            return root().value;
        }

        private void collapseNodes(Node node) {
            if (!collapseSingleNode(node)) {
                if (node.left != null) {
                    collapseNodes(node.left);
                }
                if (node.right != null) {
                    collapseNodes(node.right);
                }
            }
        }

        private boolean collapseSingleNode(Node node) {
            if (node.left != null && node.left.isLeafNode() && node.right != null && node.right.isLeafNode()) {
                node.value = 3 * node.left.value + 2 * node.right.value;
                node.left = null;
                node.right = null;
                node.isLeafNode = true;

                return true;
            }

            return false;
        }

        @Override
        public String toString() {
            return root.toString();
        }
    }

    static class Node {
        Node parent;
        Node left;
        Node right;
        long value;
        boolean isLeafNode;

        public boolean isRootNode() {
            return parent == null;
        }

        public boolean isLeafNode() {
            return isLeafNode;
        }

        public String toString() {
            if (isLeafNode()) {
                return String.valueOf(value);
            } else {
                return "[" + left + ',' + right + ']';
            }
        }

        public Node(Node parent, Node left, Node right, long value, boolean isLeafNode) {
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.value = value;
            this.isLeafNode = isLeafNode;
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
