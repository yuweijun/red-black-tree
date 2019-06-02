import java.util.stream.IntStream;

/**
 * @author yu 2019-05-10.
 */
public class RedBlackTreePrintTest {

    public static void main(String[] args) {
        int[] data = {12, 1, 9, 2, 0, 11, 7, 19, 4, 15, 18, 5, 14, 13, 10, 16, 6, 3, 8, 17};
        RedBlackTree<Integer> tree = new RedBlackTree<>();

        IntStream.of(data).forEach(i -> {
            tree.insert(i);
            new RedBlackTreePrint().print(tree, "insert " + i + " finished.");
        });

        IntStream.of(data).forEach(i -> {
            tree.remove(i);
            new RedBlackTreePrint().print(tree, "delete " + i + " finished.");
        });
    }

}