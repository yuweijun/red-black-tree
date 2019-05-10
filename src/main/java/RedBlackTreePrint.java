import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                print(tree, "insert " + v + " finished.");
            }
        }

        if (op.equalsIgnoreCase("remove") || op.equalsIgnoreCase("r")) {
            for (int i = 1; i < length - 1; i++) {
                String arg = args[i];
                int v = Integer.parseInt(arg);
                tree.insert(v);
                print(tree, "insert " + v + " finished.");
            }
            int last = Integer.parseInt(args[length - 1]);
            tree.remove(last);
            print(tree, "remove " + last + " finished.");
        }
    }

    static final class NodeInfo {

        RedBlackTreePrint.NodeInfo parent;

        boolean left;

        int column;

        int offsetLeft;

        int offsetRight;

        String text;

        NodeInfo(boolean left, RedBlackTreePrint.NodeInfo parent) {
            this.left = left;
            this.parent = parent;
        }
    }

    public static <K> int size(RedBlackTree.Node<K> node) {
        if (node == null) {
            return 0;
        }

        int s = 1;
        if (node.left != null) {
            s += size(node.left);
        }
        if (node.right != null) {
            s += size(node.right);
        }
        return s;
    }

    public static <K extends Comparable<K>> void print(RedBlackTree<K> tree, String... description) {
        Map<Integer, List<NodeInfo>> maps = new HashMap<>();
        int size = size(tree.root);
        final int initOffset = size * 5;
        traversal(0, initOffset, true, tree.root, null, maps);
        maps.forEach((key, list) -> {
            int position = 0;
            for (NodeInfo info : list) {
                if (info.text != null) {
                    while (position < info.offsetLeft) {
                        position = updateCursorPosition(position, ' ');
                    }
                    if (info.text.endsWith(NIL)) {
                        for (int i = 0; i < info.column; i++) {
                            position = updateCursorPosition(position, ' ');
                        }
                    } else {
                        position = updateCursorPosition(position, '┌');
                        for (int i = 1; i < info.column; i++) {
                            position = updateCursorPosition(position, '─');
                        }
                    }

                    System.out.print(info.text);
                    position += info.text.length();

                    if (!info.text.endsWith(NIL)) {
                        for (int i = 1; i < info.column; i++) {
                            position = updateCursorPosition(position, '─');
                        }
                        position = updateCursorPosition(position, '┐');
                    }
                }
            }
            System.out.println();
        });

        System.out.println(String.join(" ", description));
        for (int i = 0; i < initOffset * 2; i++) {
            System.out.print('.');
        }
        System.out.println("\n");
    }

    private static <K extends Comparable<K>> void traversal(int row, int column, boolean left, RedBlackTree.Node<K> node, NodeInfo parent, Map<Integer, List<NodeInfo>> maps) {
        List<NodeInfo> list = maps.computeIfAbsent(row, ArrayList::new);

        if (node == null) {
            NodeInfo info = new NodeInfo(left, parent);
            info.column = column / 2;
            if (parent != null) {
                info.text = NIL;
                list.add(info);

                updateInfoOffset(left, info, parent);
                traversal(row + 1, info.column, true, null, null, maps);
                traversal(row + 1, info.column, false, null, null, maps);
            }
        } else {
            RedBlackTree.Node<K> leftNode = node.left;
            RedBlackTree.Node<K> rightNode = node.right;
            boolean color = node.color;
            K key = node.key;

            if (parent == null) {
                NodeInfo root = new NodeInfo(left, null);
                String text = String.format("B%s", key);
                root.text = text;
                root.parent = root;
                root.column = column / 2;
                root.offsetLeft = column * 2 / 4;
                root.offsetRight = column * 2 / 4 * 3 + text.length() - 1; // 右节点显示位置左移一个字符
                list.add(root);

                traversal(row + 1, root.column, true, leftNode, root, maps);
                traversal(row + 1, root.column, false, rightNode, root, maps);
            } else {
                NodeInfo info = new NodeInfo(left, parent);
                info.text = String.format("%s%s", color ? "B" : "R", key);
                info.column = column / 2;
                list.add(info);
                updateInfoOffset(left, info, parent);

                traversal(row + 1, info.column, true, leftNode, info, maps);
                traversal(row + 1, info.column, false, rightNode, info, maps);
            }
        }
    }

    private static void updateInfoOffset(boolean left, NodeInfo info, NodeInfo parent) {
        if (left) {
            info.offsetLeft = parent.offsetLeft - info.column;
            info.offsetRight = parent.offsetLeft + info.column + info.text.length() - 1; // 右节点显示位置左移一个字符
        } else {
            info.offsetLeft = parent.offsetRight - info.column;
            info.offsetRight = parent.offsetRight + info.column + info.text.length() - 1; // 右节点显示位置左移一个字符
        }
    }

    private static int updateCursorPosition(int position, char c) {
        System.out.print(c);
        position++;
        return position;
    }

}

