package regalloc;

import com.sun.source.tree.BreakTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ChaitinAlgo {
    Stack<VertexEdgesPair> modifications = new Stack<>();

    public Graph run(List<Node> nodes) {
        Graph graph = new Graph(nodes);
        //Reduce as much as you can
        while(true) {
            Vertex v = graph.getSubKVertex();
            if(v == null) break;

            VertexEdgesPair pair = graph.removeVertex(v);
            modifications.push(pair);
        }
        System.out.println("Spilled size: " + graph.vertices);
        //TODO SPILLING GOES HERE
        while(!modifications.isEmpty()) {
            graph.addAndColorVertex(modifications.pop());
        }
        return graph;
    }
}
