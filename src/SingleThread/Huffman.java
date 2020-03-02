package SingleThread;

import Runner.Node;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

@SuppressWarnings("ALL")
public class Huffman {
    public Node rootNode;
    public HashMap<Character,String> codes;
    public static final char EOF = 0;

    /**
     * Helper function to print all the codes for the characters
     * @param root The root node of the Huffman tree
     * @param s A string used internally, pass the empty string ""
     */
    public static void printCodes(Node root, String s){
        if(root.isLeaf()){
            System.out.println(root.getCharacter()+": "+s);
            return;
        }
        if(root.hasRight())
            printCodes(root.getrChild(),s+"1");

        if(root.hasLeft())
            printCodes(root.getlChild(),s+"0");
    }

    /**
     * Function to set the HashMap of codes for each character
     * @param root The root node of the Huffman tree
     * @param s A string used internally, pass the empty string ""
     */
    public void setCodes(Node root, String s){
        if(root.isLeaf()){
            codes.putIfAbsent(root.getCharacter(), s);
            return;
        }
        if(root.hasRight())
            setCodes(root.getrChild(),s+"1");

        if(root.hasLeft())
            setCodes(root.getlChild(),s+"0");
    }

    /**
     * Method to get the HashMap of codes
     * @return The HashMap of codes
     */
    public HashMap<Character,String> getCodes(){
        return codes;
    }

    /**
     * Method to get the root node
     * @return The root node of the Huffman tree
     */
    public Node getRootNode(){
        return rootNode;
    }

    /**
     * Static function to compress a text file using Huffman encoding in a single threaded manner
     * @param outName Output file name/path
     * @param codes The HashMap of codes to use
     * @param inputName Input file name/path
     */
    public static void compressFile(String outName, HashMap<Character,String> codes, String inputName){
        File outputFile = new File(outName);
        File inputFile = new File(inputName);
        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            String input = new String(new FileInputStream(inputFile).readAllBytes());
            String str = encode(codes,input);
            while(str.length()%8!=0)
                str+='0';

            int size = 8;
            while(str.length()>size){
                out.write(Integer.parseInt(str.substring(0,size),2));
                str = str.substring(size);
            }
            out.write(Integer.parseInt(str,2));
            out.close();
            System.out.printf("Compressed file of %d bytes to size of %d bytes for %.4f%% compression\n",
                    inputFile.length(),outputFile.length(),100-100.*outputFile.length()/inputFile.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Static function to compress a text file using Huffman encoding in a single threaded manner
     * @param outName Output file name/path
     * @param codes The HashMap of codes to use
     * @param inputName Input file name/path
     * @return True if the operation succeeded, false otherwise
     */
    public static boolean decompressFile(String outName, Node root, String inputName){
        File outputFile = new File(outName);
        File inputFile = new File(inputName);
        try {
            FileOutputStream out = new FileOutputStream(outputFile);

            String decoded = "";

            byte[] bytes = new FileInputStream(inputFile).readAllBytes();
            for(byte b: bytes)
                decoded += String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ','0');
            String str = decode(root,decoded);
            out.write(str.getBytes());
            out.close();
            System.out.printf("Decompressed file of %d bytes to uncompressed size of %d bytes for %.4f%% compression\n",
                    inputFile.length(),outputFile.length(),100-100.*inputFile.length()/outputFile.length());

        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Helper method to encode the a string into a binary string using a given set of codes.
     * @param codes The HashMap of codes to use
     * @param message The message to encode
     * @return The binary string that can be decoded with a Huffman tree
     * @throws Exception Exception if the set of codes was not meant for this message and the message contains characters not in the tree
     */
    public static String encode(HashMap<Character,String> codes, String message) throws Exception {
        char[] messageChars = message.toCharArray();
        String output = "";
        for(char c: messageChars) {
            if(!codes.containsKey(c))
                throw new Exception("'"+c+"' Does not exist in the Huffman tree, only use characters in the original text");
            output += codes.get(c);
        }
        return output+codes.get(EOF);
    }

    /**
     * Helper method to convert a binary string into a character string with a certain Huffman tree
     * @param root The root of the Huffman tree
     * @param message The message to decode
     * @return The character string of the message
     */
    public static String decode(Node root, String message){
        String output = "";
        char[] messageChars = message.toCharArray();

        Node tmp = root;
        for(char c: messageChars){
            if (c=='0')
                tmp = tmp.getlChild();
            else if(c=='1')
                tmp = tmp.getrChild();
            if(tmp.isLeaf()){
                if(tmp.getCharacter()==EOF)
                    break;
                output+=tmp.getCharacter();
                tmp = root;
            }
        }
        return output;
    }

    /**
     * Method to create a Huffman tree with a single thread.
     * @param filePath The String of the path to the file to generate a tree from.
     */
    public void makeTree(String filePath){
        Scanner reader = null;
        try {
            reader = new Scanner(new File(filePath));
            reader.useDelimiter("");
            HashMap<Character, Integer> charFreq = new HashMap<>();
            while(reader.hasNext()){
                String tmp = reader.next();
                for(char c: tmp.toCharArray()) {
                    charFreq.putIfAbsent(c, 0);
                    charFreq.put(c, charFreq.get(c) + 1);
                }
            }
            charFreq.put(EOF,0);
            PriorityQueue<Node> tree = new PriorityQueue<>(charFreq.size(), Comparator.comparingInt(Node::getFrequency));
            charFreq.forEach((character, freq) -> tree.add(new Node(freq,character)));
            while(tree.size()>1)
                tree.add(new Node(tree.poll(),tree.poll()));

            rootNode = tree.poll();

            codes = new HashMap<>(charFreq.size());
            setCodes(rootNode,"");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
