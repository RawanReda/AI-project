package code;

public class Node {
    Node parent;
    String operator; // operator applied to generate this node

    int noOfExpandedNodes;

    public boolean goalTest () {
        return false;
    }
}
