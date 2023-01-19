package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		TrieNode root = new TrieNode(null,null,null);
		TrieNode ptr = root;
		TrieNode past = ptr;
		for(int i = 0;i<allWords.length;i++){
			ptr = root;
			past = ptr;
			if(i == 0){
				Indexes tem = new Indexes(i,(short)(0),(short)(allWords[i].length()-1));
				TrieNode t = new TrieNode(tem,null,null);
				root.firstChild = t;
			}
			else{
				boolean isNew = true;
				boolean hasCommon = false;
				String now = allWords[i];
				ptr = ptr.firstChild;
				while(!hasCommon||ptr.firstChild!=null){
					Character com = now.charAt(0);
					if(com.equals(allWords[ptr.substr.wordIndex].charAt(ptr.substr.startIndex))) {
						isNew = false;
						hasCommon = true;
						int j;
						for(j = 0 ;j< (ptr.substr.endIndex+1)-ptr.substr.startIndex ; j++){
							Character sss = now.charAt(j);
							if(!sss.equals(allWords[ptr.substr.wordIndex].charAt(j+ptr.substr.startIndex))){
								break;
							}
							if(j==(ptr.substr.endIndex+1)-ptr.substr.startIndex-1){
								j = j+1;
								break;
							}
						}
						if (ptr.firstChild != null) {
							isNew = true;
							hasCommon = false;
							now = now.substring(j,now.length());
							past = ptr;
							ptr = ptr.firstChild;
						}
					}
					else{
						if(isNew==true&&hasCommon==false&&ptr.sibling==null){
							break;
						}
						if(ptr.sibling!=null) {
							past = ptr;
							ptr = ptr.sibling;
						}
					}

				}
				if(isNew==true&&hasCommon==false&&ptr.sibling==null){

					Indexes tem = new Indexes(i,(short)(allWords[i].indexOf(now)),(short)(allWords[i].length()-1));
					ptr.sibling= new TrieNode(tem,null,null);
				}
				else{
					int j;
					for(j = 0 ;j< (ptr.substr.endIndex+1)-ptr.substr.startIndex ; j++){
						Character sss = now.charAt(j);
						if(!sss.equals(allWords[ptr.substr.wordIndex].charAt(j+ptr.substr.startIndex))){
							break;
						}
					}
					Indexes newTop = new Indexes(ptr.substr.wordIndex,(short)(ptr.substr.startIndex),(short)(ptr.substr.startIndex+j-1));
					Indexes newLeft = new Indexes(ptr.substr.wordIndex,(short)(ptr.substr.startIndex+j),(short)(ptr.substr.endIndex));
					Indexes newRight = new Indexes(i,(short)(ptr.substr.startIndex+j),(short)(allWords[i].length()-1));
					if(past.firstChild!=null){
						past.firstChild = new TrieNode(newTop,null,null);
						past.firstChild.firstChild = new TrieNode(newLeft,null,null);
						past.firstChild.firstChild.sibling = new TrieNode(newRight,null,null);
						past.firstChild.sibling=ptr.sibling;
					}
					else{
						past.sibling = new TrieNode(newTop,null,null);
						past.sibling.firstChild = new TrieNode(newLeft,null,null);
						past.sibling.firstChild.sibling = new TrieNode(newRight,null,null);
						past.sibling.sibling = ptr.sibling;
					}

				}


			}
		}
		return root;
	}
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		/** COMPLETE THIS METHOD **/
		TrieNode ptr = root.firstChild, ptr1 = ptr;;
		ArrayList<TrieNode> a = new ArrayList<TrieNode>();
		boolean t = false;
		while(t != true) {
			ptr1 = ptr;
			while(ptr1 != null && t != true){
					for(int i = 0; i <= ptr1.substr.endIndex; i++) {
						if(allWords[ptr1.substr.wordIndex].substring(0, i+1).equals(prefix)) {
							t = true;
							break;
						}
					}
					if(ptr1 != null && t != true) {
					ptr1 = ptr1.sibling;
					}
			}
			ptr = ptr.firstChild;
		}
		if(ptr1.firstChild == null) {
			a.add(ptr1);
			return a;
		}
		ptr1 = ptr1.firstChild;
		ptr = ptr1;
		while(ptr != null) {
			ptr1 = ptr;
			while(ptr1!=null){
				if((int)(ptr1.substr.endIndex) == (allWords[ptr1.substr.wordIndex].length()-1)) {
					a.add(ptr1);
				}
				ptr1 = ptr1.sibling;
			}
			ptr = ptr.firstChild;
		}
		return a;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
