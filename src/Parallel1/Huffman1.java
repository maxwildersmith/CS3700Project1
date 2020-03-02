package Parallel1;

import SingleThread.Huffman;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("ALL")
public class Huffman1 extends Huffman {
    /**
     * Static function to compress a text file using Huffman encoding in a multi threaded manner
     * @param outName Output file name/path
     * @param codes The HashMap of codes to use
     * @param inputName Input file name/path
     * @param numThreads The number of threads to use
     */
    public static void compressFileParallel(String outName, HashMap<Character,String> codes, String inputName, int numThreads){
        File outputFile = new File(outName);
        File inputFile = new File(inputName);
        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            char[] input = new String(new FileInputStream(inputFile).readAllBytes()).toCharArray();

            ExecutorService e = Executors.newFixedThreadPool(numThreads);
            Future<String>[] results = new Future[numThreads];

            final int section = input.length/(numThreads-1);

            for(int i=0;i<numThreads;i++){
                int finalI = i;
                results[i] = e.submit(() -> encodeParallel(codes,input, finalI*section,(finalI+1)*section));
            }
            e.shutdown();
            String str = "";
            for(Future<String> result: results)
                str+=result.get();
            str+=codes.get(EOF);

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
     * Helper method to encode the a string into a binary string using a given set of codes.
     * @param codes The HashMap of codes to use
     * @param message The message to encode
     * @param start The starting index of the message for this iteration of the function
     * @param end The ending index of the message
     * @return The binary string that can be decoded with a Huffman tree
     * @throws Exception Exception if the set of codes was not meant for this message and the message contains characters not in the tree
     */
    public static String encodeParallel(HashMap<Character,String> codes, char[] message, int start, int end) throws Exception {
        String output = "";
        for(int i=start;i<end&&i<message.length;i++) {
            if(!codes.containsKey(message[i]))
                throw new Exception("'"+message[i]+"' Does not exist in the Huffman tree, only use characters in the original text");
            output += codes.get(message[i]);
        }
        return output;
    }

}
