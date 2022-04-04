package regalloc;

import java.util.Set;

public class VertexEdgesPair {
    public Vertex vertex;
    public Set<Edge> edges;

    public VertexEdgesPair(Vertex v, Set<Edge> e) {
        vertex = v;
        edges = e;
    }
}
