import java.util.*;

/**
 * @author yuweijun
 * @since 2019-05-05
 */
public class RedBlackTreePrint {

    private static final String NIL = "NIL";

    /**
     * <pre>
     * 可以在网页上使用 JS 动画查看红黑树的操作动画： http://www.4e00.com/algorithms/SplayTree.html
     * 也可以参考这个文章的红黑树操作截图： http://m.blog.chinaunix.net/uid-26548237-id-3480169.html
     *
     * # insert [1, 2, 3, 4, 5]
     * java RedBlackTreePrint i 1 2 3 4 5
     *
     * # remove last element, such as remove 2 from [1, 2, 3, 4, 5]
     * java RedBlackTreePrint r 1 2 3 4 5 2
     * </pre>
     */
    public static void main(String[] args) {
        int length = args.length;
        if (length < 2) {
            return;
        }

        RedBlackTree<Integer> tree = new RedBlackTree<>();

        String op = args[0];
        if (op.equalsIgnoreCase("insert") || op.equalsIgnoreCase("i")) {
            for (int i = 1; i < length; i++) {
                String arg = args[i];
                int v = Integer.parseInt(arg);
                tree.insert(v);
                new RedBlackTreePrint().print(tree, "insert " + v);
            }
        }

        if (op.equalsIgnoreCase("remove") || op.equalsIgnoreCase("r")) {
            for (int i = 1; i < length - 1; i++) {
                String arg = args[i];
                int v = Integer.parseInt(arg);
                tree.insert(v);
                new RedBlackTreePrint().print(tree, "insert " + v);
            }
            int last = Integer.parseInt(args[length - 1]);
            tree.remove(last);
            new RedBlackTreePrint().print(tree, "remove " + last);
        }
    }

    static final class RedBlackNodeInfo {

        boolean isLeft;
        int column;
        int offsetLeft;
        int offsetRight;
        String text;
        RedBlackNodeInfo parent;
        RedBlackNodeInfo left;
        RedBlackNodeInfo right;

        RedBlackNodeInfo(boolean isLeft, RedBlackNodeInfo parent) {
            this.isLeft = isLeft;
            this.parent = parent;
            if (parent != null) {
                if (isLeft) {
                    parent.left = this;
                } else {
                    parent.right = this;
                }
            }
        }
    }

    private Map<Integer, List<RedBlackNodeInfo>> maps = new HashMap<>();

    public <K extends Comparable<K>> void print(RedBlackTree<K> tree, String... args) {
        if (tree == null || tree.root == null) {
            System.out.printf("%19s%n", NIL);
            return;
        }

        final int initOffset = 8 * maxWidth(tree.root);
        traversal(0, initOffset, true, tree.root, null);

        maps.forEach((key, list) -> {
            int position = 0;
            for (RedBlackNodeInfo info : list) {
                if (info.text != null) {
                    while (position < info.offsetLeft) {
                        position = updateCursorPosition(position, ' ');
                    }
                    if (NIL.equals(info.text)) {
                        for (int i = 0; i < info.column; i++) {
                            position = updateCursorPosition(position, ' ');
                        }
                    } else {
                        if (NIL.equals(info.left.text) && NIL.equals(info.right.text)) {
                            position = updateCursorPosition(position, ' ');
                            for (int i = 1; i < info.column; i++) {
                                position = updateCursorPosition(position, ' ');
                            }
                        } else {
                            position = updateCursorPosition(position, '┌');
                            for (int i = 1; i < info.column; i++) {
                                position = updateCursorPosition(position, '─');
                            }
                        }
                    }

                    if (NIL.equals(info.text)) {
                        // 如果左右子都是 NULL，就不用输出空节点
                        if (info.isLeft) {
                            if (!NIL.equals(info.parent.right.text)) {
                                System.out.print(info.text);
                                position += info.text.length();
                            }
                        } else {
                            if (!NIL.equals(info.parent.left.text)) {
                                System.out.print(info.text);
                                position += info.text.length();
                            }
                        }
                    } else {
                        System.out.print(info.text);
                        position += info.text.length();
                    }

                    if (!NIL.equals(info.text)) {
                        if (!NIL.equals(info.left.text) || !NIL.equals(info.right.text)) {
                            for (int i = 1; i < info.column; i++) {
                                position = updateCursorPosition(position, '─');
                            }
                            position = updateCursorPosition(position, '┐');
                        }
                    }
                }
            }
            System.out.println();
        });

        System.out.println(String.join(" ", args));
        for (int i = 0; i < initOffset * 2; i++) {
            System.out.print('.');
        }
        System.out.println("\n");
    }

    private <K extends Comparable<K>> int maxWidth(RedBlackTree.Node<K> root) {
        if (root == null) return 0;
        Deque<RedBlackTree.Node> deque = new LinkedList<>();
        deque.offer(root);
        int max = 1;
        while (!deque.isEmpty()) {
            Deque<RedBlackTree.Node> next = new LinkedList<>();
            int size = 0;
            while (!deque.isEmpty()) {
                RedBlackTree.Node node = deque.poll();
                if (node.left != null) {
                    size++;
                    next.offer(node.left);
                } else {
                    size++;
                }
                if (node.right != null) {
                    size++;
                    next.offer(node.right);
                } else {
                    size++;
                }
            }
            if (size > max) {
                max = size;
                // next.stream().forEach(node -> System.out.printf("%-4d ", node.val));
                // System.out.println();
            }
            deque = next;
        }

        return max;
    }

    private <K extends Comparable<K>> void traversal(int row, int column, boolean left, RedBlackTree.Node<K> node, RedBlackNodeInfo parent) {
        List<RedBlackNodeInfo> list = maps.computeIfAbsent(row, ArrayList::new);

        if (node == null) {
            RedBlackNodeInfo info = new RedBlackNodeInfo(left, parent);
            info.column = column / 2;
            if (parent != null) {
                info.text = NIL;
                list.add(info);

                updateInfoOffset(left, info, parent);
                traversal(row + 1, info.column, true, null, null);
                traversal(row + 1, info.column, false, null, null);
            }
        } else {
            RedBlackTree.Node<K> leftNode = node.left;
            RedBlackTree.Node<K> rightNode = node.right;
            boolean color = node.color;
            K key = node.key;

            if (parent == null) {
                RedBlackNodeInfo root = new RedBlackNodeInfo(left, null);
                String text = String.format("B%s", key);
                root.text = text;
                root.parent = root;
                root.column = column / 2;
                root.offsetLeft = column * 2 / 4;
                root.offsetRight = column * 2 / 4 * 3 + text.length() - 1; // 右节点显示位置左移一个字符
                list.add(root);

                traversal(row + 1, root.column, true, leftNode, root);
                traversal(row + 1, root.column, false, rightNode, root);
            } else {
                RedBlackNodeInfo info = new RedBlackNodeInfo(left, parent);
                info.text = String.format("%s%s", color ? "B" : "R", key);
                info.column = column / 2;
                list.add(info);
                updateInfoOffset(left, info, parent);

                traversal(row + 1, info.column, true, leftNode, info);
                traversal(row + 1, info.column, false, rightNode, info);
            }
        }
    }

    private void updateInfoOffset(boolean left, RedBlackNodeInfo info, RedBlackNodeInfo parent) {
        if (left) {
            info.offsetLeft = parent.offsetLeft - info.column;
            info.offsetRight = parent.offsetLeft + info.column + info.text.length() - 1; // 右节点显示位置左移一个字符
        } else {
            info.offsetLeft = parent.offsetRight - info.column;
            info.offsetRight = parent.offsetRight + info.column + info.text.length() - 1; // 右节点显示位置左移一个字符
        }
    }

    private int updateCursorPosition(int position, char c) {
        System.out.print(c);
        position++;
        return position;
    }

}
