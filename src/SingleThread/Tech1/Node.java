package SingleThread.Tech1;

public class Node {
    private int frequency;
    private Node parent, lChild, rChild;
    private char character;

    public Node(int frequency, char character) {
        this.frequency = frequency;
        this.character = character;
    }

    public Node(int frequency, Node lChild, Node rChild) {
        this.frequency = frequency;
        this.lChild = lChild;
        this.rChild = rChild;
    }

    public Node(int frequency) {
        this.frequency = frequency;
    }

    public int getChildCount(){
        return 1 + (hasLeft()?lChild.getChildCount():0)+(hasRight()?rChild.getChildCount():0);
    }

    public boolean hasLeft(){
        return lChild!=null;
    }

    public boolean hasRight(){
        return rChild!=null;
    }

    public String printTreeInOrder(){
        return (isLeaf()?" ["+frequency+": "+character+"] ":" ["+frequency+"] ") + (hasLeft()?lChild.printTreeInOrder():"")  + (hasRight()?rChild.printTreeInOrder():"");
    }

    @Override
    public String toString() {
        return "Freq: "+frequency+(isLeaf()?" char: "+character: " has left child: "+hasLeft()+" has right child: "+hasRight());
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

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getlChild() {
        return lChild;
    }

    public void setlChild(Node lChild) {
        this.lChild = lChild;
    }

    public Node getrChild() {
        return rChild;
    }

    public void setrChild(Node rChild) {
        this.rChild = rChild;
    }
}
