import java.util.ArrayList;
import java.util.List;

/**
 * @author yuweijun
 * @since 2019-06-03
 */
public class BinarySearchCodec {

    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums == null || nums.length == 0) return null;
        return sortedArrayToBST(nums, 0, nums.length);
    }

    private TreeNode sortedArrayToBST(int[] nums, int left, int right) {
        if (left == right) return null;

        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = sortedArrayToBST(nums, left, mid);
        root.right = sortedArrayToBST(nums, mid + 1, right);
        return root;
    }

    public int[] bstToSortedArray(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        bstToSortedArray(root, list);
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    private void bstToSortedArray(TreeNode root, List<Integer> list) {
        if (root == null) return;
        bstToSortedArray(root.left, list);
        list.add(root.val);
        bstToSortedArray(root.right, list);
    }

}
