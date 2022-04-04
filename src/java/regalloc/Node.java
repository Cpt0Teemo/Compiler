package regalloc;

import gen.asm.Instruction;
import gen.asm.Register;

import java.util.HashSet;
import java.util.Set;

public class Node {
    public static int increment = 0;

    public Set<Node> next;
    public Set<Node> prev;
    public int label;
    public Instruction instruction;

    public Set<Register> liveIN;
    public Set<Register> liveOUT;

    public Node(Instruction instruction) {
        next = new HashSet<>();
        prev = new HashSet<>();
        label = increment++;
        this.instruction = instruction;

        liveIN = new HashSet<>();
        liveOUT = new HashSet<>();
    }

    public void print() {
        String n = "";
        String p = "";
        String IN = "";
        String OUT = "";
        for (Node next: next) { n += next.label + " "; }
        for (Node prev: prev) { p += prev.label + " "; }
        for (Register reg: liveIN) { if(reg.isVirtual()) IN += ((Register.Virtual)reg).name + " "; }
        for (Register reg: liveOUT) { if(reg.isVirtual()) OUT += ((Register.Virtual)reg).name + " "; }
        System.out.println(label + " |" + instruction.opcode.toString() + "| {n: " + n + "p: " + p + "} IN: " + IN + "OUT: " + OUT);
    }
}
