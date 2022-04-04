package regalloc;

public class Edge {
    public Vertex v1;
    public Vertex v2;

    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean contains(Vertex v) {
        return v == v1 || v == v2;
    }

    @Override
    public boolean equals(Object o) {
        Edge e = (Edge) o;
        if(v1.name == e.v1.name)
            return v2.name == e.v2.name;
        if(v1.name == e.v2.name)
            return v2.name == e.v1.name;
        return false;
    }

    @Override
    public int hashCode() {
        return v1.hashCode() * v2.hashCode();
    }

}