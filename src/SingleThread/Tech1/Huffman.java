package SingleThread.Tech1;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Stream;

public class Huffman {
    private Node rootNode;
    private HashMap<Character,String> codes;
    public static final char EOF = 0;

    public Huffman(){

    }

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

    private void setCodes(Node root, String s){
        if(root.isLeaf()){
            codes.putIfAbsent(root.getCharacter(), s);
            return;
        }
        if(root.hasRight())
            setCodes(root.getrChild(),s+"1");

        if(root.hasLeft())
            setCodes(root.getlChild(),s+"0");
    }

    public HashMap<Character,String> getCodes(){
        return codes;
    }

    public Node getRootNode(){
        return rootNode;
    }

    public static boolean compressFile(String outName, HashMap<Character,String> codes, String inputName){
        File outputFile = new File(outName);
        try {
            outputFile.createNewFile();
            FileOutputStream out = new FileOutputStream(outputFile);
            FileInputStream inputStream = new FileInputStream(new File(inputName));
            String input = new String(inputStream.readAllBytes());
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

        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean decompressFile(String outName, Node root, String inputName){
        File outputFile = new File(outName);
        try {
            outputFile.createNewFile();
            FileOutputStream out = new FileOutputStream(outputFile);
            FileInputStream inputStream = new FileInputStream(new File(inputName));

            String decoded = "";

            byte[] bytes = inputStream.readAllBytes();
            for(byte b: bytes)
                decoded += String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ','0');
            String str = decode(root,decoded);
            out.write(str.getBytes());
            out.close();

        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

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

    public void makeTree(String filePath){
        Scanner reader = null;
        try {
            reader = new Scanner(new File(filePath));
            reader.useDelimiter("");
            HashMap<Character, Integer> charFreq = new HashMap<>();
            while(reader.hasNext()){
                String tmp = reader.next();
                for(char c: tmp.toCharArray()) {
                    if(!charFreq.containsKey(c))
                        System.out.println(c);
                    charFreq.putIfAbsent(c, 0);
                    charFreq.put(c, charFreq.get(c) + 1);
                }
            }
            charFreq.put(EOF,0);
            PriorityQueue<Node> tree = new PriorityQueue<>(charFreq.size(), Comparator.comparingInt(Node::getFrequency));
            charFreq.forEach((character, freq) -> tree.add(new Node(freq,character)));
            while(tree.size()>1){
                Node tmp = tree.poll();
                Node tmp2 = tree.poll();
                tree.add(new Node(tmp.getFrequency()+tmp2.getFrequency(),tmp,tmp2));
            }
            rootNode = tree.poll();
            System.out.println("Made tree, with "+rootNode.getChildCount()+" nodes");

            codes = new HashMap<>(charFreq.size());
            setCodes(rootNode,"");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
