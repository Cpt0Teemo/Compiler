package regalloc;

import com.sun.source.tree.BreakTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ChaitinAlgo {
    Stack<VertexEdgesPair> modifications = new Stack<>();
    Stack<VertexEdgesPair> spilling = new Stack<>();

    public Graph run(List<Node> nodes) {
        Graph graph = new Graph(nodes);
        //Reduce as much as you can
        while(true) {
            Vertex v = graph.getSubKVertex();
            if(v == null) {
                if(graph.vertices.isEmpty()) break;
                //Spilling
                v = graph.getHighestDegreeVertex();
                VertexEdgesPair pair = graph.removeVertex(v);
                spilling.push(pair);
                continue;
            }

            VertexEdgesPair pair = graph.removeVertex(v);
            modifications.push(pair);
        }
        while(!modifications.isEmpty()) {
            graph.addAndColorVertex(modifications.pop());
        }
        while(!spilling.isEmpty()) {
            graph.addSpilledVertex(spilling.pop());
        }
        return graph;
    }
}
