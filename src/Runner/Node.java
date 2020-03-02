package Runner;

public class Node {
    private int frequency;
    private Node lChild, rChild;
    private char character;

    public Node(int frequency, char character) {
        this.frequency = frequency;
        this.character = character;
    }

    public Node(Node lChild, Node rChild){
        this.frequency = lChild.frequency+rChild.frequency;
        this.lChild = lChild;
        this.rChild = rChild;
    }

    public boolean hasLeft(){
        return lChild!=null;
    }

    public boolean hasRight(){
        return rChild!=null;
    }

    public boolean isLeaf(){
        return lChild==null&&rChild==null;
    }

    public int getFrequency() {
        return frequency;
    }

    public char getCharacter() {
        return character;
    }

    public Node getlChild() {
        return lChild;
    }

    public Node getrChild() {
        return rChild;
    }
}
