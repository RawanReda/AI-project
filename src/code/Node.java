package code;

public class Node {
    Node parent;
    String operator; // operator applied to generate this node

    public boolean goalTest () {
        return false;
    }
}
