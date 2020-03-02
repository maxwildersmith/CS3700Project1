package Runner;

import Parallel1.Huffman1;
import Parallel2.Huffman2;
import Parallel3.Huffman3;
import SingleThread.Huffman;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        singleThread();
        tech1();
        tech2();
        tech3();
    }

    /**
     * Runs and times the encoding with a single thread
     */
    private static void singleThread(){
        System.out.println("\n\nTesting single thread Huffman file encoding....");

        Huffman h = new Huffman();
        long total, sTime = total = System.currentTimeMillis();
        h.makeTree("const.txt");
        System.out.printf("Made tree in %dms\n",System.currentTimeMillis()-sTime);
        try {
            sTime = System.currentTimeMillis();
            Huffman.compressFile("testCompress.huf",h.getCodes(),"const.txt");
            System.out.printf("Compressed file in %dms\n",System.currentTimeMillis()-sTime);

            sTime = System.currentTimeMillis();
            Huffman.decompressFile("cons2.txt",h.getRootNode(),"testCompress.huf");
            System.out.printf("Decompressed file in %dms\n",System.currentTimeMillis()-sTime);
            System.out.println("Files are the same: "+checkConsistency(new File("cons2.txt"),new File("const.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total time taken: "+(System.currentTimeMillis()-total)+"ms");
    }

    /**
     * Runs and times the encoding with a multithreaded approach to file encoding
     */
    private static void tech1(){
        System.out.println("\n\nTesting technique 1 with multithreaded file encoding....");

        Huffman1 h = new Huffman1();
        long total, sTime = total = System.currentTimeMillis();
        h.makeTree("const.txt");
        System.out.printf("Made tree in %dms\n",System.currentTimeMillis()-sTime);
        try {
            sTime = System.currentTimeMillis();
            Huffman1.compressFileParallel("testCompress.huf",h.getCodes(),"const.txt",Runtime.getRuntime().availableProcessors());
            System.out.printf("Compressed file in %dms\n",System.currentTimeMillis()-sTime);

            sTime = System.currentTimeMillis();
            Huffman1.decompressFile("cons2.txt",h.getRootNode(),"testCompress.huf");
            System.out.printf("Decompressed file in %dms\n",System.currentTimeMillis()-sTime);
            System.out.println("Files are the same: "+checkConsistency(new File("cons2.txt"),new File("const.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total time taken: "+(System.currentTimeMillis()-total)+"ms");
    }

    /**
     * Runs and times the encoding with multithreaded tree creation and encoding
     */
    private static void tech2(){
        System.out.println("\n\nTesting technique 2 with multithreaded tree creation....");

        Huffman2 h = new Huffman2();
        long total, sTime = total = System.currentTimeMillis();
        h.makeTreeParallel("const.txt",Runtime.getRuntime().availableProcessors());
        System.out.printf("Made tree in %dms\n",System.currentTimeMillis()-sTime);
        try {
            sTime = System.currentTimeMillis();
            Huffman2.compressFileParallel("testCompress.huf",h.getCodes(),"const.txt",Runtime.getRuntime().availableProcessors());
            System.out.printf("Compressed file in %dms\n",System.currentTimeMillis()-sTime);

            sTime = System.currentTimeMillis();
            Huffman2.decompressFile("cons2.txt",h.getRootNode(),"testCompress.huf");
            System.out.printf("Decompressed file in %dms\n",System.currentTimeMillis()-sTime);
            System.out.println("Files are the same: "+checkConsistency(new File("cons2.txt"),new File("const.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total time taken: "+(System.currentTimeMillis()-total)+"ms");
    }

    /**
     * Runs and times the encoding with a multithreaded character counting, tree creation and file encoding
     */
    private static void tech3(){
        System.out.println("\n\nTesting technique 3 with multithreaded tree creation and frequency counting....");

        Huffman3 h = new Huffman3();
        long total, sTime = total = System.currentTimeMillis();
        h.makeTreeParallel2("const.txt",Runtime.getRuntime().availableProcessors());
        System.out.printf("Made tree in %dms\n",System.currentTimeMillis()-sTime);
        try {
            sTime = System.currentTimeMillis();
            Huffman3.compressFileParallel("testCompress.huf",h.getCodes(),"const.txt",Runtime.getRuntime().availableProcessors());
            System.out.printf("Compressed file in %dms\n",System.currentTimeMillis()-sTime);

            sTime = System.currentTimeMillis();
            Huffman3.decompressFile("cons2.txt",h.getRootNode(),"testCompress.huf");
            System.out.printf("Decompressed file in %dms\n",System.currentTimeMillis()-sTime);
            System.out.println("Files are the same: "+checkConsistency(new File("cons2.txt"),new File("const.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total time taken: "+(System.currentTimeMillis()-total)+"ms");
    }

    /**
     * Helper function to check that the uncompressed file matches the original byte by byte
     * @param fileA The original file to check against
     * @param fileB The decompressed file to check
     * @return True if they are the same, false otherwise
     */
    public static boolean checkConsistency(File fileA, File fileB){
        try {
            byte[] a = new FileInputStream(fileA).readAllBytes();
            byte[] b = new FileInputStream(fileB).readAllBytes();
            if(a.length!=b.length)
                return false;
            for(int i=0;i<a.length;i++)
                if(a[i]!=b[i])
                    return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
