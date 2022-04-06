package regalloc;

import gen.asm.Register;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private final int k = ChaitinRegAlloc.registers.length;
    public List<Vertex> vertices;
    public HashSet<Edge> edges;
    private static HashMap<String, Vertex> registers;


    public Graph(List<Node> nodes) {
        vertices = new ArrayList<>();
        edges = new HashSet<Edge>();
        registers = new HashMap<>();
        for (Node node: nodes) {
            List<Register> regs = node.instruction.registers();
            for(Register register: regs) {
                if(!register.isVirtual() || registers.containsKey(((Register.Virtual)register).name)) continue;
                Vertex vertex = new Vertex((Register.Virtual)register);
                registers.put(vertex.name, vertex);
                vertices.add(vertex);
            }
        }
        for (Node node: nodes) {
            for (Register reg1: node.liveIN) {
                for (Register reg2 : node.liveIN) {
                    if (!reg1.equals(reg2)) {
                        Vertex v1 = registers.get(((Register.Virtual) reg1).name);
                        Vertex v2 = registers.get(((Register.Virtual) reg2).name);
                        edges.add(new Edge(v1, v2));
                    }
                }
            }
            for (Register reg1: node.liveOUT) {
                for (Register reg2 : node.liveOUT) {
                    if (!reg1.equals(reg2)) {
                        Vertex v1 = registers.get(((Register.Virtual) reg1).name);
                        Vertex v2 = registers.get(((Register.Virtual) reg2).name);
                        edges.add(new Edge(v1, v2));
                    }
                }
            }
        }

    }

    public Graph(List<Vertex> vertices, HashSet<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public VertexEdgesPair removeVertex(Vertex v) {
        vertices.remove(v);
        Set<Edge> edgesToRemove = edges.stream().filter(x -> x.contains(v)).collect(Collectors.toSet());
        edges.removeAll(edgesToRemove);
        return new VertexEdgesPair(v, edgesToRemove);
    }

    public void addAndColorVertex(VertexEdgesPair pair) {
        vertices.add(pair.vertex);
        edges.addAll(pair.edges);

        List<Integer> usedColors = pair.edges.stream().mapToInt(x -> {
            if(pair.vertex.equals(x.v1))
                return x.v2.color;
            else
                return x.v1.color;
        }).boxed().collect(Collectors.toList());
        int inc = 0;
        for(int i = 1; i <= k; i++) { //Assumes all are colored
            if(!usedColors.contains(i)) {
                pair.vertex.color = i;
                System.out.println("Register: " + pair.vertex.name + " Color: " + i);
                break;
            }
            inc++;

        }
    }

    public void addSpilledVertex(VertexEdgesPair pair) {
        vertices.add(pair.vertex);
        edges.addAll(pair.edges);
    }

    public Vertex getSubKVertex() {
        for(Vertex v: vertices) {
            if(getDegree(v) < k)
                return v;
        }
        return null;
    }

    public Vertex getHighestDegreeVertex() {
        HashMap<String, Integer> degrees = new HashMap<>();
        for(Vertex v: vertices) {
            degrees.put(v.name, 0);
        }
        for(Edge e: edges) {
            degrees.put(e.v1.name, degrees.get(e.v1.name) + 1);
            degrees.put(e.v2.name, degrees.get(e.v2.name) + 1);
        }
        int max = 0;
        String maxName = "";
        for(Map.Entry<String, Integer> e: degrees.entrySet()) {
            if(e.getValue() > max) {
                max = e.getValue();
                maxName = e.getKey();
            }
        }
        return registers.get(maxName);
    }

    public int getDegree(Vertex v) {
        return (int) edges.stream().filter(x -> x.contains(v)).count();
    }
}
