import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yuweijun
 * @since 2019-06-02
 */
public class BinaryTreeCodec {

    public String serialize(TreeNode root) {
        if (root == null) return null;
        Deque<TreeNode> list = new LinkedList<>();
        List<Integer> values = new ArrayList<>();
        list.offer(root);
        while (!list.isEmpty()) {
            Deque<TreeNode> deque = new LinkedList<>();
            while (!list.isEmpty()) {
                TreeNode node = list.pollFirst();
                if (node != null) {
                    values.add(node.val);

                    deque.offer(node.left);
                    deque.offer(node.right);
                } else {
                    values.add(null);
                }
            }
            list = deque;
        }

        Deque<Integer> results = new LinkedList<>();
        for (int i = values.size() - 1; i >= 0; i--) {
            Integer v = values.get(i);
            if (v != null || results.size() > 0) results.offerFirst(v);
        }
        return results.toString();
    }

    public TreeNode deserialize(String data) {
        if (data == null) return null;
        if (!data.startsWith("[") || !data.endsWith("]")) throw new IllegalArgumentException("data format error");

        data = data.substring(1, data.length() - 1);
        String[] sources = data.split(",");
        LinkedList<Integer> values = new LinkedList<>();
        for (String source : sources) {
            if (source.trim().equals("null")) {
                values.add(null);
            } else {
                values.add(Integer.valueOf(source.trim()));
            }
        }

        Deque<TreeNode> deque = new LinkedList<>();
        TreeNode root = new TreeNode(values.poll());
        deque.offer(root);
        while (!values.isEmpty()) {
            TreeNode node = deque.pollFirst();
            Integer lv = values.pollFirst();
            Integer rv = values.pollFirst();
            if (lv != null) {
                TreeNode left = new TreeNode(lv);
                node.left = left;
                deque.offer(left);
            }
            if (rv != null) {
                TreeNode right = new TreeNode(rv);
                node.right = right;
                deque.offer(right);
            }
        }

        return root;
    }

}
