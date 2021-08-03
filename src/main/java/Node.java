import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

import java.util.Iterator;
import java.util.stream.Stream;

public class Node implements org.graphstream.graph.Node {
    public Node(){
        super();
    };

    @Override
    public Graph getGraph() {
        return null;
    }

    @Override
    public int getDegree() {
        return 0;
    }

    @Override
    public int getOutDegree() {
        return 0;
    }

    @Override
    public int getInDegree() {
        return 0;
    }

    @Override
    public Edge getEdgeToward(String s) {
        return null;
    }

    @Override
    public Edge getEdgeFrom(String s) {
        return null;
    }

    @Override
    public Edge getEdgeBetween(String s) {
        return null;
    }

    @Override
    public Edge getEdge(int i) {
        return null;
    }

    @Override
    public Edge getEnteringEdge(int i) {
        return null;
    }

    @Override
    public Edge getLeavingEdge(int i) {
        return null;
    }

    @Override
    public Iterator<org.graphstream.graph.Node> getBreadthFirstIterator() {
        return null;
    }

    @Override
    public Iterator<org.graphstream.graph.Node> getBreadthFirstIterator(boolean b) {
        return null;
    }

    @Override
    public Iterator<org.graphstream.graph.Node> getDepthFirstIterator() {
        return null;
    }

    @Override
    public Iterator<org.graphstream.graph.Node> getDepthFirstIterator(boolean b) {
        return null;
    }

    @Override
    public Stream<Edge> edges() {
        return null;
    }

    @Override
    public Edge getEdgeToward(org.graphstream.graph.Node node) {
        return null;
    }

    @Override
    public Edge getEdgeToward(int i) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Edge getEdgeFrom(org.graphstream.graph.Node node) {
        return null;
    }

    @Override
    public Edge getEdgeFrom(int i) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Edge getEdgeBetween(org.graphstream.graph.Node node) {
        return null;
    }

    @Override
    public Edge getEdgeBetween(int i) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Object getFirstAttributeOf(String... strings) {
        return null;
    }

    @Override
    public <T> T getAttribute(String s, Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T getFirstAttributeOf(Class<T> aClass, String... strings) {
        return null;
    }

    @Override
    public boolean hasAttribute(String s) {
        return false;
    }

    @Override
    public boolean hasAttribute(String s, Class<?> aClass) {
        return false;
    }

    @Override
    public Stream<String> attributeKeys() {
        return null;
    }

    @Override
    public void clearAttributes() {

    }

    @Override
    public void setAttribute(String s, Object... objects) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public int getAttributeCount() {
        return 0;
    }
}
