import java.util.*;

/**
 * @author yuweijun
 * @since 2019-05-05
 */
public class BinaryTreePrint {

    private static final String NULL = "N";

    public static void main(String[] args) {
        int length = args.length;
        if (length == 0) {
            return;
        }

        BinaryTreeCodec codec = new BinaryTreeCodec();
        TreeNode root = codec.deserialize(args[0]);
        new BinaryTreePrint().print(root);
    }

    static final class BinaryTreeNodeInfo {

        boolean isLeft;
        int column;
        int offsetLeft;
        int offsetRight;
        String text;
        BinaryTreeNodeInfo parent;
        BinaryTreeNodeInfo left;
        BinaryTreeNodeInfo right;

        BinaryTreeNodeInfo(boolean isLeft, BinaryTreeNodeInfo parent) {
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

    private Map<Integer, List<BinaryTreeNodeInfo>> maps = new HashMap<>();

    private int depth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(depth(root.left), depth(root.right));
    }

    public void print(TreeNode root) {
        if (root == null) {
            System.out.printf("%s%n", NULL);
            return;
        }

        int depth = depth(root);
        int width = 1;
        while (depth-- > 1) {
            width *= 2;
        }

        final int initOffset = width * (width > 0xFF ? 2 : 4);
        traversal(0, initOffset, true, root, null);
        maps.forEach((key, list) -> {
            int position = 0;
            for (BinaryTreeNodeInfo info : list) {
                if (info.text != null) {
                    while (position < info.offsetLeft) {
                        position = updateCursorPosition(position, ' ');
                    }
                    if (NULL.equals(info.text)) {
                        for (int i = 0; i < info.column; i++) {
                            position = updateCursorPosition(position, ' ');
                        }
                    } else {
                        if (NULL.equals(info.left.text) && NULL.equals(info.right.text)) {
                            for (int i = 0; i < info.column; i++) {
                                position = updateCursorPosition(position, ' ');
                            }
                        } else {
                            position = updateCursorPosition(position, '┌');
                            for (int i = 1; i < info.column; i++) {
                                position = updateCursorPosition(position, '─');
                            }
                        }
                    }

                    if (NULL.equals(info.text)) {
                        // 如果左右子都是 NULL，就不用输出空节点
                        if (info.isLeft) {
                            if (!NULL.equals(info.parent.right.text)) {
                                System.out.print(info.text);
                                position += info.text.length();
                            }
                        } else {
                            if (!NULL.equals(info.parent.left.text)) {
                                System.out.print(info.text);
                                position += info.text.length();
                            }
                        }
                    } else {
                        System.out.print(info.text);
                        position += info.text.length();
                    }

                    if (!NULL.equals(info.text)) {
                        if (!NULL.equals(info.left.text) || !NULL.equals(info.right.text)) {
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

        for (int i = 0; i < initOffset * 2; i++) {
            System.out.print('.');
        }
        System.out.println("\n");
    }

    /**
     * null 节点也算一个宽度
     */
    private int maxWidth(TreeNode root) {
        if (root == null) return 0;
        Deque<TreeNode> deque = new LinkedList<>();
        deque.offer(root);
        int max = 1;
        while (!deque.isEmpty()) {
            Deque<TreeNode> next = new LinkedList<>();
            int size = 0;
            while (!deque.isEmpty()) {
                TreeNode node = deque.poll();
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
            }
            deque = next;
        }

        return max;
    }

    private void traversal(int row, int column, boolean left, TreeNode node, BinaryTreeNodeInfo parent) {
        List<BinaryTreeNodeInfo> list = maps.computeIfAbsent(row, ArrayList::new);

        if (node == null) {
            BinaryTreeNodeInfo info = new BinaryTreeNodeInfo(left, parent);
            info.column = column / 2;
            if (parent != null) {
                info.text = NULL;
                list.add(info);

                updateInfoOffset(left, info, parent);
                traversal(row + 1, info.column, true, null, null);
                traversal(row + 1, info.column, false, null, null);
            }
        } else {
            TreeNode leftNode = node.left;
            TreeNode rightNode = node.right;
            int key = node.val;

            if (parent == null) {
                BinaryTreeNodeInfo root = new BinaryTreeNodeInfo(left, null);
                String text = String.format("%d", key);
                root.text = text;
                root.parent = root;
                root.column = column / 2;
                root.offsetLeft = column * 2 / 4;
                root.offsetRight = column * 2 / 4 * 3 + text.length() - 1; // 右节点显示位置左移一个字符
                list.add(root);

                traversal(row + 1, root.column, true, leftNode, root);
                traversal(row + 1, root.column, false, rightNode, root);
            } else {
                BinaryTreeNodeInfo info = new BinaryTreeNodeInfo(left, parent);
                info.text = String.format("%d", key);
                info.column = column / 2;
                list.add(info);
                updateInfoOffset(left, info, parent);

                traversal(row + 1, info.column, true, leftNode, info);
                traversal(row + 1, info.column, false, rightNode, info);
            }
        }
    }

    private void updateInfoOffset(boolean left, BinaryTreeNodeInfo info, BinaryTreeNodeInfo parent) {
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

