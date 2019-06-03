import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yuweijun
 * @since 2019-06-02
 */
public class BinaryTreePrintTest {

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.left.left = new TreeNode(4);
        root.left.left.right = new TreeNode(5);
        root.right = new TreeNode(6);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(8);
        root.right.right.left = new TreeNode(9);
        root.right.right.right = new TreeNode(10);
        new BinaryTreePrint().print(root);

        new BinaryTreePrint().print(null);

        List<Integer> list = IntStream.rangeClosed(0, 126).boxed().collect(Collectors.toList());
        new BinaryTreePrint().print(new BinaryTreeCodec().deserialize(list.toString()));

        int[] nums = IntStream.rangeClosed(0, 62).toArray();
        BinarySearchCodec codec = new BinarySearchCodec();
        TreeNode node = codec.sortedArrayToBST(nums);
        new BinaryTreePrint().print(node);
        int[] data = codec.bstToSortedArray(node);
        IntStream.of(data).forEach(i -> System.out.printf("%-3d ", i));
    }

}
