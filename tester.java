public class tester {

	public static void main(String[] args) {
		WAVLTree tree = new WAVLTree();
		tree.init();
		System.out.println(tree.empty());
		int []arr=tree.keysToArray();
		String[]arr2=tree.infoToArray();
		for(int i=0;i<arr.length;i++){
			System.out.print(arr[i]+" ");
					
		}
		System.out.println("");
		for(int i=0;i<arr2.length;i++){
			System.out.print(arr2[i]+" ");
					
		}
		System.out.println("");
		System.out.println(tree.max());
		System.out.println(tree.min());
		System.out.println(tree.search(-1));

	}

}
