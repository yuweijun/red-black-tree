/**
 * 代码根据 @link java.util.TreeMap} 的红黑树实现稍做调整
 *
 * @author yu 2019-05-04.
 */
public class RedBlackTree<K extends Comparable<? super K>> {

    // Red-black mechanics
    static final boolean RED = false;
    static final boolean BLACK = true;

    transient Node<K> root;

    static final class Node<K> {
        K key;
        Node<K> left;
        Node<K> right;
        Node<K> parent;
        boolean color = BLACK;

        /**
         * Make a new cell with given key, and parent, and with {@code null} child links, and BLACK color.
         */
        Node(K key, Node<K> parent) {
            this.key = key;
            this.parent = parent;
        }
    }

    /**
     * @return Returns this entry for the given key
     * @throws NullPointerException if the specified key is null
     */
    public final Node<K> search(K key) {
        if (key == null)
            throw new NullPointerException();

        Node<K> p = root;
        while (p != null) {
            int cmp = key.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }

        return null;
    }

    /**
     * @param key key of entry
     * @return the exist entry or new entry with key {@code key}.
     * @throws NullPointerException if the specified key is null
     */
    public Node<K> insert(K key) {
        Node<K> t = root;
        if (t == null) {
            root = new Node<>(key, null);
            return root;
        }

        int cmp;
        Node<K> parent;

        if (key == null)
            throw new NullPointerException();

        do {
            parent = t;
            cmp = key.compareTo(t.key);
            if (cmp < 0)
                t = t.left;
            else if (cmp > 0)
                t = t.right;
            else
                return t;
        } while (t != null);

        Node<K> e = new Node<>(key, parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;

        fixAfterInsertion(e);
        return e;
    }

    /**
     * Returns the successor of the specified Node, or null if no such.
     */
    static <K> Node<K> successor(Node<K> t) {
        if (t == null)
            return null;

        else if (t.right != null) {
            Node<K> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            Node<K> p = t.parent;
            Node<K> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * Balancing operations.
     *
     * Implementations of rebalancings during insertion and deletion are slightly different than the CLR version. Rather
     * than using dummy nilnodes, we use a set of accessors that deal properly with null.  They are used to avoid
     * messiness surrounding nullness checks in the main algorithms.
     */

    private static <K> boolean colorOf(Node<K> p) {
        return (p == null ? BLACK : p.color);
    }

    private static <K> Node<K> parentOf(Node<K> p) {
        return (p == null ? null : p.parent);
    }

    private static <K> void setColor(Node<K> p, boolean c) {
        if (p != null)
            p.color = c;
    }

    private static <K> Node<K> leftOf(Node<K> p) {
        return (p == null) ? null : p.left;
    }

    private static <K> Node<K> rightOf(Node<K> p) {
        return (p == null) ? null : p.right;
    }

    /**
     * <pre>
     * 对红黑树的节点(x)进行左旋转
     *
     * 左旋示意图(对节点 x 进行左旋)：
     *      p                  p
     *     /                  /
     *    x                  y
     *   / \    --(左旋)--   / \
     *  lx  y              x  ry
     *     / \            / \
     *    ly ry          lx ly
     * </pre>
     */
    private void rotateLeft(Node<K> p) {
        if (p != null) {
            Node<K> r = p.right;
            p.right = r.left;
            if (r.left != null)
                r.left.parent = p;
            r.parent = p.parent;
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
        }
    }

    /**
     * <pre>
     * 对红黑树的节点(y)进行右旋转
     *
     * 右旋示意图(对节点 y 进行左旋)：
     *          p                     p
     *         /                     /
     *        y                     x
     *       / \    --(右旋)--      / \
     *      x  ry                 lx  y
     *     / \                       / \
     *    lx rx                     rx ry
     * </pre>
     */
    private void rotateRight(Node<K> p) {
        if (p != null) {
            Node<K> l = p.left;
            p.left = l.right;
            if (l.right != null) l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null)
                root = l;
            else if (p.parent.right == p)
                p.parent.right = l;
            else p.parent.left = l;
            l.right = p;
            p.parent = l;
        }
    }

    /**
     * From CLR
     *
     * insert 节点主要与叔叔节点比较，红黑树的所有插入情形分为以下几种：
     * <pre>
     * 无父节点，新节点为树的根节点
     * 黑父节点
     * 红父红叔
     * 红父黑叔
     * </pre>
     */
    private void fixAfterInsertion(Node<K> x) {
        x.color = RED;

        while (x != null && x != root && x.parent.color == RED) { // 红父
            //                                                    //////////////////////////////////////////////////////
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {   // 父为左子
                Node<K> y = rightOf(parentOf(parentOf(x)));       // 叔为右子
                // uncle is red                                   //////////////////////////////////////////////////////
                if (colorOf(y) == RED) {                          // 情形 1. 红父红叔
                    setColor(parentOf(x), BLACK);                 // 情形 1. 红色上溢父变黑
                    setColor(y, BLACK);                           // 情形 1. 红色上溢叔变黑
                    setColor(parentOf(parentOf(x)), RED);         // 情形 1. 红色上溢祖父变红
                    x = parentOf(parentOf(x));                    // 情形 1. 以祖父为新节点，重新循环
                    // re-balance                                 //////////////////////////////////////////////////////
                } else {                                          // 情形 2. 红父黑叔
                    if (x == rightOf(parentOf(x))) {              // 情形 2. 新节点为左树右子
                        x = parentOf(x);                          // 情形 2. 标记父节点为新节点
                        rotateLeft(x);                            // 情形 2. 将父节点左旋，转化为下面情形 3
                    }                                             //////////////////////////////////////////////////////
                    setColor(parentOf(x), BLACK);                 // 情形 3. 交换祖父节点和父节点颜色，父节点变黑，并退出循环
                    setColor(parentOf(parentOf(x)), RED);         // 情形 3. 祖父节点交红
                    rotateRight(parentOf(parentOf(x)));           // 情形 3. 再以祖父节点右旋，完成
                }
            } else {
                Node<K> y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }

        root.color = BLACK;
    }

    public Node<K> remove(K key) {
        Node<K> p = search(key);
        if (p == null)
            return null;

        deleteNode(p);
        return p;
    }

    /**
     * Delete node p, and then re-balance the tree.
     */
    private void deleteNode(Node<K> p) {
        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        if (p.left != null && p.right != null) {
            Node<K> s = successor(p);                                         // 节点有 2 个子节点时，找到实际删除的后继节点
            p.key = s.key;                                                    // 将后继节点的值复制到原来节点上，原来的节点只是值被删除，节点本身不删除
            p = s;                                                            // 将实际要删除的节点位置指向后继节点位置
        } // p has 2 children                                                 //
        //                                                                    //////////////////////////////////////////////////////////////////
        // Start fixup at replacement node, if it exists.                     //
        Node<K> replacement = (p.left != null ? p.left : p.right);            // 实际删除的节点最多只有一个子节点，并且一定是红色子节点
        if (replacement != null) {                                            // 如果有一个红色子节点
            // Link replacement to parent                                     //
            replacement.parent = p.parent;                                    // 将此节点上移
            if (p.parent == null)                                             //
                root = replacement;                                           //
            else if (p == p.parent.left)                                      // 实际被删除的节点 p 为左子
                p.parent.left = replacement;                                  //
            else                                                              // 实际被删除的节点 p 为右子
                p.parent.right = replacement;                                 //
            //                                                                //
            // Null out links so they are OK to use by fixAfterDeletion.      //
            p.left = p.right = p.parent = null;                               //
            //                                                                //
            // Fix replacement                                                //////////////////////////////////////////////////////////////////
            if (p.color == BLACK)                                             // 删除情形 5. 因为 replacement 是红节点，所以这里 p 的颜色一定是黑色的
                fixAfterDeletion(replacement);                                // 修复只要将此红节点上移并置黑就完成了
        } else if (p.parent == null) { // return if we are the only node.     //////////////////////////////////////////////////////////////////
            root = null;                                                      // 根节点被删除
        } else { //  No children. Use self as phantom replacement and unlink. // 被删除的节点没有子节点时
            if (p.color == BLACK)                                             // 如果是红色的节点，直接删除就完成
                fixAfterDeletion(p);                                          // 如果被删除的是黑色节点，则会破坏红黑树性质 5,需要修复红黑树

            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }

    /**
     * From CLR
     *
     * 删除节点，主要看兄弟节点的颜色，以左子树为例，红黑树的删除情形如下所示：
     * <pre>
     * 删除根节点
     * 红兄，必然是黑父，二黑子
     * 红父黑兄二黑子
     * 黑父黑兄二黑子
     * 黑兄红左子
     * 黑兄红右子
     * </pre>
     */
    private void fixAfterDeletion(Node<K> x) {
        while (x != root && colorOf(x) == BLACK) {                                     // 被实际删除的节点 x 不是 root 节点，并且颜色为黑，这 2 者做为循环退出关键状态
            if (x == leftOf(parentOf(x))) {                                            // x 为左子
                Node<K> sib = rightOf(parentOf(x));                                    // sib 为其兄弟节点，删除操作主要根据此节点颜色调整
                // sibling is red                                                      //////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (colorOf(sib) == RED) {                                             // 情形 1. 红兄，必然对就黑父节点和 2 个黑子节点，需要向兄弟借一个黑节点共享，只要父兄交换颜色并左旋一次
                    setColor(sib, BLACK);                                              // 情形 1. 红兄置黑，一会儿左旋之后共享此黑节点
                    setColor(parentOf(x), RED);                                        // 情形 1. 黑父置红，左旋之后变成左子
                    rotateLeft(parentOf(x));                                           // 情形 1. 红父左旋
                    sib = rightOf(parentOf(x));                                        // 情形 1. 红父的右子是原来兄弟节点的左子，一定是黑的，改变兄弟节点指向，继续修复红黑树
                }                                                                      // 情形 1. 转化为情形 2 或者情形 3，如下说明
                // sibling has two black children                                      //////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) { // 情形 2. 黑兄和 2 个黑侄子，这里实际上又分为黑父和红父，这里其中黑父黑兄带二黑子最复杂
                    setColor(sib, RED);                                                // 情形 2. 黑兄置红，黑色上溢，父节点颜色红变黑结束操作，或者父节点黑加黑，以父节点再下一轮修复循环
                    x = parentOf(x);                                                   // 情形 2.1 如果原来是红父，退出循环并置黑
                    //                                                                 // 情形 2.2 如果是黑父，则父节点作为新的标记删除节点，不做实际删除，继续下一轮红黑树修复循环
                } else {                                                               //////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (colorOf(leftOf(sib)) == RED) {                                 // 情形 3. 黑兄并且其左子为红，需要额外换色旋转，以达到右子为红的结果
                        setColor(leftOf(sib), BLACK);                                  // 情形 3. 黑兄和其红左子交换颜色，左子置黑，红色上溢
                        setColor(sib, RED);                                            // 情形 3. 兄弟节点置黑
                        rotateRight(sib);                                              // 情形 3. 兄弟节点右旋
                        sib = rightOf(parentOf(x));                                    // 情形 3. 原来的红色左侄变黑色兄弟节点，转为情形 4
                    }                                                                  //////////////////////////////////////////////////////////////////////////////////////////////////////////
                    setColor(sib, colorOf(parentOf(x)));                               // 情形 4. 这里兄弟右子肯定为红色，可能是情形 3 转变而来，也可能兄弟右子原来就是红的，此时将原来父节点的颜色设置给兄弟节点
                    setColor(parentOf(x), BLACK);                                      // 情形 4. 父节点置黑
                    setColor(rightOf(sib), BLACK);                                     // 情形 4. 兄弟节点右子置黑
                    rotateLeft(parentOf(x));                                           // 情形 4. 父节点左旋
                    x = root;                                                          // 在情形 3 和 4 中删除操作已经完成，不会再循环，将 x 标记为 root，方便退出循环并最后将 root 置黑
                }
            } else { // symmetric
                Node<K> sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);                                                            // 对应上面情形 3 和 4，还有是最前面有一个红子节点的情形 5，都在这里统一将节点置黑
    }

}
