package Model;

public interface Edge<T extends Node> {
    public void setStartNode(T node);
    public T getStartNode();
    public void setEndNode(T node);
    public T getEndNode();
}
