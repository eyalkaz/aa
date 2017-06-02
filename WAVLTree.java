
/**
 * WAVLTree
 * <p>
 * An implementation of a WAVL Tree with distinct integer keys and info
 */
//students and id:
//username: eyalkazula ID:209133693 name: Eyal Kazula
//username: yuvalyehudab ID:203769609 name: Yuval_barak

public class WAVLTree {
    // there is one virtual Node in all the tree - all linked to him
    private final WAVLNode virtualNode = new WAVLNode(-1, null, false, -1);
    private WAVLNode root;
    private WAVLNode minimum;

    public WAVLTree() {
        //empty tree
        this.root = this.virtualNode;
        minimum=this.virtualNode;
    }

    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */
    public boolean empty() {
        //O(1)
        //if the root is virtaul its means the tree is empty
        return !this.root.isReal;
    }

    /**
     * public String search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     */
    public String search(int k) {
        //O(logn)
        //simple bst search
        WAVLNode temp = this.root;
        while (temp.isReal) {
            if (temp.key == k) {
                return temp.value;
            }
            if (temp.key > k) {
                temp = temp.left;
            } else {
                temp = temp.right;
            }

        }
        return null;
    }

    /**
     * public int insert(int k, String i)
     * <p>
     * inserts an item with key k and info i to the WAVL tree. the tree must
     * remain valid (keep its invariants). returns the number of rebalancing
     * operations, or 0 if no rebalancing operations were necessary. returns -1
     * if an item with key k already exists in the tree.
     */
    public int insert(int k, String i) {
        // O(logn)
        int balancing = 0;
        WAVLNode c;
        WAVLNode temp = new WAVLNode(k, i);// rank temp=0
        temp.right = this.virtualNode;
        temp.left = this.virtualNode;
        // empty tree
        if (this.empty()) {
            this.root = temp;
            this.minimum=temp;
            return 0;
        }
        WAVLNode newParent = findInsertParent(k);
        // if already exist
        if (!newParent.isReal) {
            return -1;
        }
        temp.parent = newParent;
        if(temp.key<this.minimum.getKey()){
            this.minimum=temp;
        }

        // case 1 parent is leaf

        if (isLeaf(newParent)) {
            if (k > newParent.key) {
                newParent.right = temp;
            } else {
                newParent.left = temp;
            }
            c = newParent;
            while (c != null) {
                c.subTreeSize++;
                c = c.parent;
            }
            // do balance if need
            balancing = balanceInsert(newParent, 0);

        } else {
            // case 2 parent is unary no need to balance
            if (!newParent.left.isReal) {
                newParent.left = temp;
            } else {
                newParent.right = temp;
            }
            // update size
            while (newParent != null) {
                newParent.subTreeSize++;
                newParent = newParent.parent;
            }
        }

        return balancing;
    }

    /*
     * @pre x==y.left||x==y.right
     */
    private void rotate(WAVLNode x, WAVLNode y) {
        //O(1)
        WAVLNode c;
        if (this.root == y) {
            this.root = x;
        } else {

            if (y.parent.right == y) {
                y.parent.right = x;
            } else {
                y.parent.left = x;
            }
        }
        x.parent = y.parent;
        y.parent = x;

        if (x == y.left) {
            c = x.left;
            y.left = x.right;
            if (x.right.isReal) {
                x.right.parent = y;
            }
            x.right = y;
        } else {
            c = x.right;
            y.right = x.left;
            if (x.left.isReal) {
                x.left.parent = y;
            }
            x.left = y;
        }
        y.subTreeSize = y.right.subTreeSize + y.left.subTreeSize + 1;
        x.subTreeSize = y.subTreeSize + c.subTreeSize + 1;

    }

    private int balanceInsert(WAVLNode newParent, int balancing) {
        //w.c O(logn)
        if (newParent != null) {
            // we didnt promoted root

            if (newParent.rank - newParent.left.rank == 0) {
                if (newParent.rank - newParent.right.rank == 1) {
                    // newParent is 0/1 need promote
                    newParent.rank++;
                    balancing = balanceInsert(newParent.parent, balancing)+1;
                } else {
                    // newParent is 0/2 node
                    if (newParent.left.rank - newParent.left.right.rank == 2) {
                        // case 2 in presentation need single rotation
                        rotate(newParent.left, newParent);
                        newParent.rank--;
                        balancing += 1;
                    } else {
                        // case 3 presentation double rotation
                        newParent.left.right.rank++;
                        newParent.left.rank--;
                        rotate(newParent.left.right, newParent.left);
                        rotate(newParent.left, newParent);
                        newParent.rank--;
                        balancing += 2;

                    }

                }
            } else {
                if (newParent.rank - newParent.right.rank == 0) {
                    if (newParent.rank - newParent.left.rank == 1) {
                        // new parent is 1/0 need promote
                        newParent.rank++;
                        balancing = balanceInsert(newParent.parent,
                                balancing )+1;
                    } else {
                        // new parent is 2/0 node
                        if (newParent.right.rank - newParent.right.left.rank == 2) {
                            // one roteation
                            rotate(newParent.right, newParent);
                            newParent.rank--;
                            balancing += 1;

                        } else {
                            // double rotate
                            newParent.right.left.rank++;
                            newParent.right.rank--;
                            rotate(newParent.right.left, newParent.right);
                            rotate(newParent.right, newParent);
                            newParent.rank--;
                            balancing += 2;
                        }

                    }
                }

            }
        }
        return balancing;

    }

    private boolean isLeaf(WAVLNode temp) {
        //O(1)
        //check if temp is leaf
        if (!temp.right.isReal && !temp.left.isReal) {
            return true;
        }
        return false;
    }

    // @pre !this.empty()
    // @post return new parent of k and virtual node if k already exist
    private WAVLNode findInsertParent(int k) {
        //O(logn)
        WAVLNode temp = this.root;
        WAVLNode child;
        if (k == temp.key) {
            return this.virtualNode;
        }
        if (k > temp.key) {
            child = temp.right;
        } else {
            child = temp.left;
        }
        while (child.isReal) {
            temp = child;
            if (k == child.key) {
                return this.virtualNode;
            }
            if (k > child.key) {
                child = child.right;
            } else {
                child = child.left;
            }

        }
        return temp;
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there; the tree
     * must remain valid (keep its invariants). returns the number of
     * rebalancing operations, or 0 if no rebalancing operations were needed.
     * returns -1 if an item with key k was not found in the tree.
     */
    public int delete(int k) {
        //O(logn)
        int balancing = 0;
        WAVLNode deleteNode = findDeleteNode(k);
        if (!deleteNode.isReal) {
            return -1;
        }
        if(this.minimum==deleteNode){
                this.minimum = this.findSuccessor(deleteNode);
            if(this.minimum==deleteNode){
                this.minimum=this.virtualNode;
            }
        }
        balancing = deleteThisNode(deleteNode);
        return balancing;
    }

    private int deleteThisNode(WAVLNode deleteNode) {
        WAVLNode successor, temp;
        int balancing = 0;

        if (deleteNode.right.isReal && deleteNode.left.isReal) {
            // inner node
            // replace with succsessor and delete succsessor

            successor = findSuccessor(deleteNode);
            balancing += deleteThisNode(successor);
            successor.rank = deleteNode.rank;
            successor.right = deleteNode.right;
            successor.left = deleteNode.left;
            successor.parent = deleteNode.parent;
            successor.subTreeSize = deleteNode.subTreeSize;
            deleteNode.right.parent = successor;
            deleteNode.left.parent = successor;
            if (this.root == deleteNode) {
                this.root = successor;
            } else {
                if (deleteNode.parent.right == deleteNode) {
                    deleteNode.parent.right = successor;
                } else {
                    deleteNode.parent.left = successor;
                }
            }


        } else {
            // leaf or unary

            if (isLeaf(deleteNode)) {
                // leaf
                if (this.root == deleteNode) {
                    this.root = this.virtualNode;
                } else {
                    if (deleteNode.parent.right == deleteNode) {
                        // right leaf
                        deleteNode.parent.right = this.virtualNode;
                    } else {
                        // left leaf
                        deleteNode.parent.left = this.virtualNode;
                    }
                }
            } else {
                // unary
                WAVLNode child = null;
                if (this.root == deleteNode) {
                    // delete the root
                    if (deleteNode.right.isReal) {
                        child = deleteNode.right;
                    } else {
                        child = deleteNode.left;
                    }

                    this.root = child;
                } else {
                    // not root connect child to parent and balance parent

                    if (deleteNode.parent.right == deleteNode) {
                        // deletedNode > deletedNode.parent

                        if (deleteNode.right.isReal) {
                            // right child unary
                            deleteNode.parent.right = deleteNode.right;
                            child = deleteNode.right;

                        } else {
                            // left child unary
                            deleteNode.parent.right = deleteNode.left;
                            child = deleteNode.left;
                        }

                    } else {
                        // deletedNode < deletedNode.parent
                        if (deleteNode.right.isReal) {
                            // right child unary
                            deleteNode.parent.left = deleteNode.right;
                            child = deleteNode.right;

                        } else {
                            // left child unary
                            deleteNode.parent.left = deleteNode.left;
                            child = deleteNode.left;
                        }

                    }

                }
                child.parent = deleteNode.parent;
            }
            temp = deleteNode;
            while (temp != null) {
                temp.subTreeSize--;
                temp = temp.parent;
            }
            balancing = balanceDelete(deleteNode.parent, balancing);
        }

        return balancing;

    }

    private int balanceDelete(WAVLNode node, int balancing) {
        //w.c O(logn)
        if (node == null) {
            // we promoted root
            return balancing;
        }
        if (isLeaf(node)) {
            if (node.rank == 1) {
                // 2,2 leaf need demote
                node.rank--;
                balancing = balanceDelete(node.parent, balancing )+1;
            }
        } else {
            if (node.rank - node.left.rank == 3) {
                if (node.rank - node.right.rank == 2) {
                    // 3,2 node need demote
                    node.rank--;
                    balancing = balanceDelete(node.parent, balancing )+1;
                } else {
                    // 3,1 node
                    if (node.right.rank - node.right.right.rank == 2
                            && node.right.rank - node.right.left.rank == 2) {
                        // case 2 presentation double demote
                        node.rank--;
                        node.right.rank--;
                        balancing = balanceDelete(node.parent, balancing )+2;

                    } else {
                        if (node.right.rank - node.right.right.rank == 1) {
                            // case 3 rotate
                            node.right.rank++;
                            rotate(node.right, node);
                            node.rank--;
                            balancing += 1;
                            if (isLeaf(node)
                                    && node.rank - node.right.rank == 2) {
                                // 2,2 leaf after roteation
                                node.rank--;
                                balancing += 1;

                            }

                        } else {
                            // case 4 double rotate
                            node.right.rank--;
                            node.right.left.rank += 2;
                            rotate(node.right.left, node.right);
                            rotate(node.right, node);
                            node.rank -= 2;
                            balancing += 2;

                        }
                    }
                }
            }

        }
        if (node.rank - node.right.rank == 3) {
            if (node.rank - node.left.rank == 2) {
                // 2,3 node demote
                node.rank--;
                balancing = balanceDelete(node.parent, balancing )+1;
            } else {
                // 1,3 node
                if (node.left.rank - node.left.right.rank == 2
                        && node.left.rank - node.left.left.rank == 2) {
                    // case 2 presentation double demote
                    node.rank--;
                    node.left.rank--;
                    balancing = balanceDelete(node.parent, balancing )+2;
                } else {
                    if (node.left.rank - node.left.left.rank == 1) {
                        // case 3 rotate
                        node.left.rank++;
                        rotate(node.left, node);
                        node.rank--;

                        balancing += 1;
                        if (isLeaf(node) && node.rank - node.left.rank == 2) {
                            // 2,2 leaf after roteation
                            node.rank--;
                            balancing += 1;

                        }
                    } else {
                        // case 4 double rotate
                        node.left.rank--;
                        node.left.right.rank += 2;
                        rotate(node.left.right, node.left);
                        rotate(node.left, node);
                        node.rank -= 2;

                        balancing += 2;

                    }
                }
            }
        }
        return balancing;
    }

    // return node to delete if not found return this virtual node
    private WAVLNode findDeleteNode(int k) {
        //o(logn)
        WAVLNode temp = this.root;
        while (temp.isReal) {
            if (k == temp.key) {
                return temp;
            }
            if (k > temp.key) {
                temp = temp.right;
            } else {
                temp = temp.left;
            }
        }
        return temp;
    }


    // @post return succssesor
    private WAVLNode findSuccessor(WAVLNode temp) {
        //O(logn)
        if(temp.right.isReal) {
            temp = temp.right;
            while (temp.left.isReal) {
                temp = temp.left;
            }
            return temp;
        }
        else{
            while(temp!=this.root){
                if(temp.parent.left==temp){
                    //we found the succsessor
                    return temp.parent;
                }
                else{
                    temp=temp.parent;
                }
            }
        }
        return temp;

    }

    /**
     * public String min()
     * <p>
     * Returns the info of the item with the smallest key in the tree, or null
     * if the tree is empty
     */
    public String min() {
        //O(1)
        if (this.empty()) {
            return null;

        }
     return this.minimum.value;
    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree, or null if
     * the tree is empty
     */
    public String max() {
        //O(logn)
        // go on the right branch
        if (this.empty()) {
            return null;

        }
        WAVLNode temp = this.root;
        while (temp.right.isReal) {
            temp = temp.right;
        }
        return temp.value;
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree, or an empty
     * array if the tree is empty.
     */
    public int[] keysToArray() {
        //O(n)
        int[] arr = new int[this.root.subTreeSize];
        if (this.empty()) {
            return arr;
        }
        // using an array instead of int so it will change deep in the recursion
        int[] index = {0};
        WAVLNode[] arrayNodes = inOrderWalk(
                new WAVLNode[this.root.subTreeSize], index, this.root);
        for (int i = 0; i < arrayNodes.length; i++) {
            arr[i] = arrayNodes[i].key;
        }

        return arr;
    }

    // @preCondition temp!=this.virtualNode
    private WAVLNode[] inOrderWalk(WAVLNode[] array, int[] index, WAVLNode temp) {
        //O(n)
        // scan the tree in order and return an array of all the nodes
        if (temp.left.isReal) {
            this.inOrderWalk(array, index, temp.left);
        }
        array[index[0]] = temp;
        index[0]++;
        if (temp.right.isReal) {
            this.inOrderWalk(array, index, temp.right);
        }
        return array;

    }

    /**
     * public String[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree, sorted by their
     * respective keys, or an empty array if the tree is empty.
     */
    public String[] infoToArray() {
        //O(n)
        String[] arr = new String[this.root.subTreeSize];
        if (this.empty()) {
            return arr;
        }
        int[] index = {0};
        WAVLNode[] arrayNodes = inOrderWalk(
                new WAVLNode[this.root.subTreeSize], index, this.root);
        for (int i = 0; i < arrayNodes.length; i++) {
            arr[i] = arrayNodes[i].value;
        }
        return arr;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     * <p>
     * precondition: none postcondition: none
     */
    public int size() {
        return this.root.subTreeSize;
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root WAVL node, or null if the tree is empty
     * <p>
     * precondition: none postcondition: none
     */
    public IWAVLNode getRoot() {
        if (this.empty()) {
            return null;
        }
        return this.root;
    }

    /**
     * public int select(int i)
     * <p>
     * Returns the value of the i'th smallest key (return -1 if tree is empty)
     * Example 1: select(1) returns the value of the node with minimal key
     * Example 2: select(size()) returns the value of the node with maximal key
     * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the
     * value of the node minimal node's successor
     * <p>
     * precondition: size() >= i > 0 postcondition: none
     */
    public String select(int i) {
    	//o(log i)
    	if(this.empty()){
    		return null;
    	}
    	return selectNode(i).getValue();
    }

	/**
	 * @param i
	 */
	private IWAVLNode selectNode(int i) {
		WAVLNode tmpRoot = this.minimum;
		if (i == 1){
			return tmpRoot;
		}
    	boolean goingUp = true;
		while (goingUp){
    		int numOfElementsBefore = tmpRoot.parent.getSubtreeSize();
    		if (numOfElementsBefore < i){ 
    			tmpRoot = tmpRoot.parent;
    		}
    		else if(numOfElementsBefore >= i){
    			tmpRoot=tmpRoot.parent;
    			goingUp = false;
    		}
    	}
		while (true){
    		int numOfElementsBefore = tmpRoot.getLeft().getSubtreeSize() + 1;
    		if (numOfElementsBefore == i){ //we need this element
    			return tmpRoot;
    		}
    		else if(numOfElementsBefore > i){ //its in the left 
    			tmpRoot = tmpRoot.left;
    		}
    		else if (numOfElementsBefore < i) {
    			i = i - numOfElementsBefore;
    			tmpRoot = tmpRoot.right;
    		}
		}
	}
   

    /**
     * public interface IWAVLNode ! Do not delete or modify this - otherwise all
     * tests will fail !
     */
    public interface IWAVLNode {
        public int getKey(); // returns node's key (for virtuval node return -1)

        public String getValue(); // returns node's value [info] (for virtuval
        // node return null)

        public IWAVLNode getLeft(); // returns left child (if there is no left
        // child return null)

        public IWAVLNode getRight(); // returns right child (if there is no
        // right child return null)

        public boolean isRealNode(); // Returns True if this is a non-virtual
        // WAVL node (i.e not a virtual leaf or
        // a sentinal)

        public int getSubtreeSize(); // Returns the number of real nodes in this
        // node's subtree (Should be implemented
        // in O(1))


    }

    /**
     * public class WAVLNode
     * <p>
     * If you wish to implement classes other than WAVLTree (for example
     * WAVLNode), do it in this file, not in another file. This class can and
     * must be modified. (It must implement IWAVLNode)
     */
    public class WAVLNode implements IWAVLNode {
        private boolean isReal;
        private int key;
        private int subTreeSize = 1;
        private String value;
        private WAVLNode right = null;
        private WAVLNode left = null;
        private WAVLNode parent = null;
        private int rank;

        public WAVLNode(int key, String val) {
            this.key = key;
            this.value = val;
            this.isReal = true;
            this.rank = 0;
            this.parent = null;

        }

        // use this to create virtual node
        public WAVLNode(int key, String val, boolean isReal, int rank) {
            this.key = key;
            this.value = val;
            this.isReal = isReal;
            this.rank = -1;
            this.parent = null;
            this.subTreeSize = 0;

        }

        public int getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public IWAVLNode getLeft() {
            return this.left;
        }

        public IWAVLNode getRight() {
            return this.right;
        }

        // Returns True if this is a non-virtual WAVL node (i.e not a virtual
        // leaf or a sentinal)
        public boolean isRealNode() {
            return this.isReal;
        }

        public int getSubtreeSize() {
            return this.subTreeSize;
        }


    }

}
