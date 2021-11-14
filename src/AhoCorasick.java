import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

public class AhoCorasick {
    private Node keywordTree[];
    private int nodeCount = 0;
    private String patterns[];
    private String text;


    public static class Node {
        int parent;
        int failureLink;
        boolean isLeaf;
        String label;
        char character;
        int ownIndex;
        HashMap<Character, Integer> children; // child node character and its index in keywordTree

        public Node(int parent, int failureLink, boolean isLeaf, char character, int ownIndex) {
            this.parent = parent; // -1 if root, index of parent node in keywordTree
            this.failureLink = failureLink;
            this.isLeaf = isLeaf;
            this.character = character;
            this.ownIndex = ownIndex;
            children = new HashMap<>();
        }
        public boolean hasChild(Character c) {
            return children.containsKey(c);
        }
        public boolean isLeaf() {
            return this.isLeaf;
        }
    }

    public static void main (String[] argv) {
        CliArgs cliArgs = new CliArgs(argv);
        String inputFileName = cliArgs.switchValue("-i");

        AhoCorasick  ahoCorasick = new AhoCorasick();
        String data[] = ahoCorasick.readFile(inputFileName);
        ahoCorasick.setPatterns(data[0]);
        ahoCorasick.setText(data[1]);

//        ahoCorasick.printPatterns();
//        ahoCorasick.printText();
        System.out.println("\nBuild tree");
        System.out.println("----------------------------------------------------------------");
        ahoCorasick.buildKeywordTree(ahoCorasick.patterns);
//        System.out.println("\n\nLeaf Status");
//        ahoCorasick.printLeafStatus();


//        System.out.println("\n\n createFailureLinks");
//        ahoCorasick.createFailureLinks2(0);
//        System.out.println("\n\n Failure Links");
//        ahoCorasick.printFailureLinks();

        // create a queue to be used for breadth-first search while building failure links
        Queue<Integer> queue = ahoCorasick.createBFSQueue();
//        System.out.println("\n");
//        System.out.println("queue size: " + queue.size());

        // create failure links
        while (!queue.isEmpty()) {
            int removedElement = queue.remove();
            ahoCorasick.createFailureLinks(removedElement);
        }
//        ahoCorasick.printFailureLinks();
        ahoCorasick.printKeywordTree();
        System.out.println("\nSearch");
        System.out.println("----------------------------------------------------------------");
        ahoCorasick.search();
    }

    public void createFailureLinks(int index) { // initially call this with 0(root)
        int curParentIndex = this.keywordTree[index].parent;
        if (curParentIndex != -1) {
            int curFailureLink = this.keywordTree[curParentIndex].failureLink;
            char nodeChar = this.keywordTree[index].character;
            while (curParentIndex != -1) {// when you reach the root, set failure link to root
                if (this.keywordTree[curFailureLink].hasChild(nodeChar) && index != this.keywordTree[curFailureLink].children.get(nodeChar)) {
                    this.keywordTree[index].failureLink = this.keywordTree[curFailureLink].children.get(nodeChar);
                    return;
                } else {
                    curFailureLink = this.keywordTree[curFailureLink].failureLink;
                    curParentIndex = this.keywordTree[curFailureLink].parent;
                    // check root's children to find a match, if no match -> set failure link to the root
                    if (curParentIndex == -1) {
                        if (this.keywordTree[curFailureLink].hasChild(nodeChar) && index != this.keywordTree[curFailureLink].children.get(nodeChar)) {
                            this.keywordTree[index].failureLink = this.keywordTree[curFailureLink].children.get(nodeChar);
                        } else {
                            this.keywordTree[index].failureLink = 0; // set failure link to the root
                        }
                        return;
                    }
                }
            }
        }
    }


    public void search() {
        ArrayList<String> patternsList = new ArrayList<>();
        for (String pattern : this.patterns)
            patternsList.add(pattern);

        int curIndex = 0;
        int curCharIndex = 0;
        while (curCharIndex < this.text.length()) {
            // System.out.println("in search in first while");
            //  System.out.println(curCharIndex);
            while (!this.keywordTree[curIndex].isLeaf() && curCharIndex < this.text.length()) {
                // System.out.println("inside second while");
                char curChar = text.charAt(curCharIndex);
                // System.out.println("cur index: " + curIndex);

                if (this.keywordTree[curIndex].parent == -1) { // if it is root
                    if (this.keywordTree[curIndex].hasChild(curChar))
                        curIndex = this.keywordTree[curIndex].children.get(curChar);
                    else
                        curIndex = this.keywordTree[curIndex].failureLink;
                }
                else { // not root
                    if (this.keywordTree[curIndex].hasChild(curChar)) {
                        curIndex = this.keywordTree[curIndex].children.get(curChar);
                    }
                    else {
                        curIndex = this.keywordTree[curIndex].failureLink;
                    }
                }
//                if (this.keywordTree[curIndex].hasChild(curChar)) { // match
//                    if (this.keywordTree[curIndex].character == curChar)
//                        curIndex = this.keywordTree[curIndex].children.get(curChar);
//                }
//                else {
//                    curIndex = this.keywordTree[curIndex].failureLink;
////                }
                curCharIndex++;
            } // pattern found: print index(es) and modify curIndex and curCharIndex if necessary
            // find prefixes and compare them with parameters
            String[] prefixes = calculatePrefixes(this.keywordTree[curIndex].label, this.keywordTree[curIndex].label.length());
            String pureLabel = this.keywordTree[curIndex].label.substring(1);
            for (String prefix : prefixes) {
                if (patternsList.contains(prefix)) {
                    System.out.println("keyword: " + prefix + " - index: " + (curCharIndex - pureLabel.length()));
                }
            }
            curIndex = this.keywordTree[curIndex].failureLink;
            curCharIndex++;
        }
    }

    public String[] calculatePrefixes(String label, int size) {
        String[] prefixes = new String[size];
        String pureLabel = label.substring(1);
        // System.out.println(pureLabel);
        for (int i = 1; i <= pureLabel.length(); ++i) {
            prefixes[i] = pureLabel.substring(0,i);
        }

        return prefixes;
    }

    /**
     * Should run in O(n)
     * */
    public void buildKeywordTree(String[] patterns) {
        int maxSize = 0;
        for (String pattern : patterns)
            maxSize += pattern.length();
        maxSize++; // 1 extra for the root
        this.keywordTree = new Node[maxSize];
        // initialize root separately
        this.keywordTree[0] = new Node(-1, 0, true, '*', 0);
        this.keywordTree[0].label = calculateLabel(this.keywordTree[0]);
        nodeCount++;

        // initialize the children arraylist and parent node index while building the tree
        // do not forget to initialize isLeaf
        for (String curPattern : this.patterns) {
            // change the parent node's isLeaf variable while adding a child to the leaf node
            int current = 0;
            for (Character charInPattern : curPattern.toCharArray()) {
                if (this.keywordTree[current].hasChild(charInPattern)) {
                    current = this.keywordTree[current].children.get(charInPattern);

                } else {
                    //  this.keywordTree[current].children = new HashMap<>();
                    this.keywordTree[current].children.put(charInPattern, nodeCount);
                    // create new node (child)
                    Node newChild = new Node(current, 0, true, charInPattern, nodeCount);
                    // set the previous node's isLeaf to false
                    this.keywordTree[current].isLeaf = false;

                    this.keywordTree[nodeCount] = newChild;
                    newChild.label = calculateLabel(newChild);

                    // update current
                    current = nodeCount;
                    nodeCount++;
                }
            }
        }
    }

    public Queue<Integer> createBFSQueue() {
        Queue<Integer> queue = new LinkedList<>();
        // find max depth
        int maxDepth = 0;
        for (int i = 0; i < nodeCount; ++i) {
            if (maxDepth < this.keywordTree[i].label.length())
                maxDepth = this.keywordTree[i].label.length();
        }

        //System.out.println("max Depth: " + maxDepth);
        // create queue
        for (int i = 0; i <= maxDepth; ++i) {
            for (int j = 0; j < nodeCount; ++j) {
                if (this.keywordTree[j].label.length() == i)
                    queue.add(keywordTree[j].ownIndex);
            }
        }

        return queue;
    }

    public void printKeywordTree() {
        ArrayList<String> patternsList = new ArrayList<>();
        for (String pattern : this.patterns)
            patternsList.add(pattern);

        for (int i = 0; i < nodeCount; ++i) {
            Node curNode = this.keywordTree[i];
            String output = (patternsList.contains(curNode.label)) ? "" : "";
            System.out.println("char: "+
                    curNode.character+
                    " next states: " +
                    curNode.children.values() +
                    " fail state: " + curNode.failureLink +
                    " output: [" +
                    (patternsList.contains(curNode.label.substring(1)) ? curNode.label.substring(1) : "") +
                    "]");
        }
    }


    public String calculateLabel(Node node) {
        String label = "";
        label += node.character;

        int index = node.ownIndex;
//        System.out.println("parent index: " + keywordTree[index].parent);
//        System.out.println("character: " + node.character +" node: " + index ) ;

        int parentIndex = keywordTree[index].parent;
        while (parentIndex != -1) {
            label += keywordTree[parentIndex].character;
            parentIndex = keywordTree[parentIndex].parent;
        }

        // reverse the string to create the label
        byte[] labelBytes = label.getBytes();
        byte[] reversed = new byte[labelBytes.length];

        for (int i = 0; i < labelBytes.length; ++i)
            reversed[i] = labelBytes[labelBytes.length - i - 1];

        //System.out.println("label: " + new String(reversed));

        return new String(reversed);
    }


    public String[] readFile(String fileName) {
        String data[] = new String[2];
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                data[i++] = line;
            }
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
        return data;
    }


    public void setPatterns(String paramPatterns) {
        int totalEmptySpaces = 0;
        char character;

        // count the number of patterns
        for (int i = 0; i < paramPatterns.length(); i++) {
            character = paramPatterns.charAt(i);
            if (character == ' ')
                totalEmptySpaces++;
        }
        totalEmptySpaces++;
        this.patterns = new String[totalEmptySpaces];

        // fill the patterns array
        String pattern = paramPatterns;
        int i = 0;
        while (pattern.indexOf(' ') != -1)  {
            this.patterns[i++] = pattern.substring(0,pattern.indexOf(' '));
            pattern = pattern.substring(pattern.indexOf(' ') + 1);
        }
        this.patterns[i] = pattern;
    }

    public void setText(String text) { this.text = text;}

    public void printText() {
        System.out.println();
        System.out.println("printText: "  + this.text);
    }
    public void printPatterns() {
        System.out.println();
        System.out.println("printPatterns");
        for (String pattern : this.patterns)
            System.out.println(pattern);
    }

    public void printLeafStatus() {
        for (int i = 0; i < nodeCount; ++i) {
            System.out.println("node: " + i + " isLeaf: " + keywordTree[i].isLeaf());
        }
    }

    public void printFailureLinks() {
        for (int i = 0; i < nodeCount; ++i) {
            System.out.println("own index: " + this.keywordTree[i].ownIndex + " fail state: " + this.keywordTree[i].failureLink);
        }
    }

    // -- for building tree
    // search() -- eklemenin yapılacağı node'un pointerını döndürecek
    // addKeyword() // will utilize search():
    // search'ten dönen pointer üzerinden kelimeyi eklemeye devam edecek.
    // build() // will utilize both search() and addKeyword()


    // -- for searching patterns
    // thread (): refer to the slides
}
