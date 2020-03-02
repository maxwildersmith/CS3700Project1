package Parallel3;

import Parallel2.Huffman2;
import Runner.Node;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("ALL")
public class Huffman3 extends Huffman2 {

    /**
     * Method to generate the Huffman tree in a multithreaded manner by the characters in sections at the same time
     * @param filePath The path to the source material
     * @param numThreads The number of threads to execute with
     */
    public void makeTreeParallel2(String filePath, int numThreads){
        try {
            byte[] data = Files.readAllBytes(Paths.get(filePath));
            ExecutorService e = Executors.newFixedThreadPool(numThreads);
            HashMap<Character, Integer>[] charFreqs = new HashMap[numThreads];

            int charSize = data.length/numThreads;
            for(int i=0;i<numThreads-1;i++) {
                charFreqs[i] = new HashMap<>();
                int finalI = i;
                e.execute(() -> {
                    for(int start=0;start<charSize;start++){
                        charFreqs[finalI].putIfAbsent((char)data[finalI*charSize+start], 0);
                        charFreqs[finalI].put((char)data[finalI*charSize+start], charFreqs[finalI].get((char)data[finalI*charSize+start]) + 1);
                    }
                });
            }
            charFreqs[numThreads-1] = new HashMap<>();
            e.execute(() -> {
                for(int start=0;start<charSize;start++){
                    charFreqs[numThreads-1].putIfAbsent((char)data[(numThreads-1)*charSize+start], 0);
                    charFreqs[numThreads-1].put((char)data[(numThreads-1)*charSize+start], charFreqs[numThreads-1].get((char)data[(numThreads-1)*charSize+start]) + 1);
                }
            });
            e.shutdown();

            for(int i=1;i<charFreqs.length;i++){
                charFreqs[i].forEach((character, integer) -> charFreqs[0].merge(character, integer, Integer::sum));
            }

            charFreqs[0].put(EOF,0);
            
            PriorityQueue<Node>[] trees = new PriorityQueue[numThreads];
            for(int i=0;i<trees.length;i++)
                trees[i] = new PriorityQueue<>(charFreqs[0].size(), Comparator.comparingInt(Node::getFrequency));

            List<Node> nodes = new ArrayList<>(charFreqs[0].size());
            charFreqs[0].forEach((character, freq) -> nodes.add(new Node(freq,character)));

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
            PriorityQueue<Node> finalTree = new PriorityQueue<>(charFreqs[0].size(), Comparator.comparingInt(Node::getFrequency));
            for(Future<Node> root: roots)
                finalTree.add(root.get());

            while(finalTree.size()>1)
                finalTree.add(new Node(finalTree.poll(),finalTree.poll()));
            rootNode = finalTree.poll();

            codes = new HashMap<>(charFreqs[0].size());
            setCodes(rootNode,"");

        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
