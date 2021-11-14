import java.io.*;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

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
        ArrayList<Integer> children;

        public Node(int parent, int failureLink, boolean isLeaf, String label, char character) {
            this.parent = parent; // -1 if root
            this.failureLink = failureLink;
            this.isLeaf = isLeaf;
            this.label = label;
            this.character = character;
            children = null;
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
        this.keywordTree[0] = new Node(-1, 0, true, "", ' ');
        nodeCount++;

        // initialize the children arraylist and parent node index while building the tree
        // do not forget to initialize isLeaf
        for (int i = 0; i < this.patterns.length; ++i) {
            // change the parent node's isLeaf variable while adding a child to the leaf node
            int current = 0;
            for ( // every char in pattern[i]) {

                while ())
            }
        }
    }


    /**
     * Should run in O(n)
     * */
    public void createFailureLinks() {
        // use this.keywordTree here
    }


    public String[] readFile(String fileName) {
        String data[] = new String[2];
        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
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


    // -- for building tree
    // search() -- eklemenin yapılacağı node'un pointerını döndürecek
    // addKeyword() // will utilize search():
                    // search'ten dönen pointer üzerinden kelimeyi eklemeye devam edecek.
    // build() // will utilize both search() and addKeyword()


    // -- for searching patterns
    // thread (): refer to the slides
}
