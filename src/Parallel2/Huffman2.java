package Parallel2;

import Parallel1.Huffman1;
import Runner.Node;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings("ALL")
public class Huffman2 extends Huffman1 {

    /**
     * Method to generate the Huffman tree in a multithreaded manner by processing portions of the queue at the same time
     * @param filePath The path to the source material
     * @param numThreads The number of threads to execute with
     */
    public void makeTreeParallel(String filePath, int numThreads){
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

            PriorityQueue<Node>[] trees = new PriorityQueue[numThreads];
            for(int i=0;i<trees.length;i++)
                trees[i] = new PriorityQueue<>(charFreq.size(), Comparator.comparingInt(Node::getFrequency));

            List<Node> nodes = new ArrayList<>(charFreq.size());
            charFreq.forEach((character, freq) -> nodes.add(new Node(freq,character)));

            int size = nodes.size()/numThreads;
            for(int i=0;i<numThreads-1;i++) {
                trees[i].addAll(nodes.subList(i * size, Math.min((i + 1) * size, nodes.size())));
            }
            trees[numThreads-1].addAll(nodes.subList((numThreads-1) * size, nodes.size()));

            Future<Node>[] roots = new Future[numThreads];
            ExecutorService exec = Executors.newFixedThreadPool(numThreads);
            for(int i=0;i<numThreads;i++) {
                int finalI = i;
                roots[i] = exec.submit(() -> {
                    while(trees[finalI].size()>1)
                        trees[finalI].add(new Node(trees[finalI].poll(),trees[finalI].poll()));
                    return trees[finalI].poll();
                });
            }

            exec.shutdown();
            PriorityQueue<Node> finalTree = new PriorityQueue<>(charFreq.size(), Comparator.comparingInt(Node::getFrequency));
            for(Future<Node> root: roots)
                finalTree.add(root.get());

            while(finalTree.size()>1)
                finalTree.add(new Node(finalTree.poll(),finalTree.poll()));
            rootNode = finalTree.poll();

            codes = new HashMap<>(charFreq.size());
            setCodes(rootNode,"");

        } catch (FileNotFoundException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
