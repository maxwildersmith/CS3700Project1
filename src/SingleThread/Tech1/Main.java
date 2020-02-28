package SingleThread.Tech1;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Huffman h = new Huffman();
        h.makeTree("const.txt");
        String encoded = null;
        try {
            Huffman.compressFile("testCompress.huf",h.getCodes(),"const.txt");
            Huffman.decompressFile("cons2.txt",h.getRootNode(),"testCompress.huf");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("Byte test--"+(char)0+"--"+(int)' ');
//        String str = "1100000110001010111100010101110100011111011000000101001000010010001111100101110111101000111110001101111101110110010100011111110010110011011";
//        while(str.length()%8!=0)
//            str+='0';
//        FileOutputStream out = new FileOutputStream(new File("out.txt"));
//
//        int size = 8;
//        while(str.length()>size){
//            out.write(Integer.parseInt(str.substring(0,size),2));
//            str = str.substring(size);
//        }
//        //Integer.parseInt
//        out.write(Integer.parseInt(str,2));
//        out.close();
//        FileInputStream in = new FileInputStream(new File("out.txt"));
//        String decode = "";
//        byte[] bytes = in.readAllBytes();
//        for(byte b: bytes)
//            decode+= String.format("%8s", Integer.toBinaryString(b&0xFF)).replace(' ','0');
//        System.out.println("1100000110001010111100010101110100011111011000000101001000010010001111100101110111101000111110001101111101110110010100011111110010110011011");
//        System.out.println(decode);
    }

    public static void getWordFrequency(){
        Scanner reader = null;
        try {
            reader = new Scanner(new File("const.txt"));

        HashMap<String, Integer> wordFreq = new HashMap<>();
        while(reader.hasNext()){
            String[] tmp = reader.nextLine().trim().split("((?![A-z\'-]).)");
            for(String s: tmp) {
                if(s.trim().length()>0) {
                    wordFreq.putIfAbsent(s, 0);
                    wordFreq.put(s, wordFreq.get(s) + 1);
                }
            }
        }
        PriorityQueue<String> q = new PriorityQueue<>(wordFreq.size(), Comparator.comparingInt(o -> Integer.parseInt(o.split(" ")[2])));
        wordFreq.forEach((s, integer) -> q.add(s+" - "+integer));
        while(!q.isEmpty())
            System.out.println(q.poll());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void getCharFrequency(String filePath){
        Scanner reader = null;
        try {
            reader = new Scanner(new File(filePath));
            HashMap<Character, Integer> charFreq = new HashMap<>();
            while(reader.hasNext()){
                String tmp = reader.nextLine();
                for(char c: tmp.toCharArray()) {
                        charFreq.putIfAbsent(c, 0);
                        charFreq.put(c, charFreq.get(c) + 1);
                }
            }
            PriorityQueue<String> q = new PriorityQueue<>(charFreq.size(), Comparator.comparingInt(o -> Integer.parseInt(o.split(" - ")[1])));
            charFreq.forEach((s, integer) -> q.add(s+" - "+integer));
            while(!q.isEmpty())
                System.out.println(q.poll());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
