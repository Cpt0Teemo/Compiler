package regalloc;

import gen.asm.*;

import java.util.*;

public class ChaitinRegAlloc implements AssemblyPass {

    public final static ChaitinRegAlloc INSTANCE = new ChaitinRegAlloc();

    public static Register[] registers = {Register.Arch.t0, Register.Arch.t1, Register.Arch.t2, Register.Arch.t3, Register.Arch.t4, Register.Arch.t5, Register.Arch.t6, Register.Arch.t7, Register.Arch.t8, Register.Arch.t9, Register.Arch.s0, Register.Arch.s1, Register.Arch.s2, Register.Arch.s3, Register.Arch.s4};
    public static Register[] spilledRegs = {Register.Arch.s5, Register.Arch.s6, Register.Arch.s7};
    HashMap<Register, Integer> regToInt = new HashMap<Register, Integer>();
    final Map<Register, Register> vrToAr = new HashMap<>();

    HashMap<Node, String> labelPointers = new HashMap<>();
    HashMap<String, Node> labelMap = new HashMap<>();

    @Override
    public AssemblyProgram apply(AssemblyProgram program) {
        List<List<Node>> CFGs = buildCFGs(program);
        for(List<Node> CFG: CFGs) {
            Graph graph = new ChaitinAlgo().run(CFG);
            for(Vertex v: graph.vertices) {
                regToInt.put(v.register, v.color);
            }
        }
        return run(program);
    }

    private List<Node> buildCFG(List<AssemblyItem> items) {
        List<Node> nodes = new ArrayList<>();
        List<String> labels = null;
        //First pass: Creates nodes and link labels with nodes
        for(AssemblyItem item: items) {
            if(item instanceof Instruction) {
                Node node = new Node((Instruction)item);
                nodes.add(node);
                if(labels != null) {
                    labels.forEach(label -> labelMap.put(label, node));
                    labels = null;
                }
                storeBranches(node);
            } else if (item instanceof Label) {
                if(labels == null)
                    labels = new ArrayList<>();
                labels.add(((Label) item).name);
            }
        }

        //Second pass: Connects nodes together with Next
        for(int i = 0; i < nodes.size()-1; i++) {
            Node node = nodes.get(i);
            if(labelPointers.containsKey(node)) { //If node jumps somewhere
                String label = labelPointers.get(node);
                Node nextNode = labelMap.get(label);
                node.next.add(nextNode);
                if(node.instruction instanceof Instruction.BinaryBranch || node.instruction instanceof Instruction.UnaryBranch) {
                    node.next.add(nodes.get(i+1));
                }
            } else { // Normal instruction
                node.next.add(nodes.get(i+1));
            }
        }

        // Third pass: link nodes backwards
        for(Node node: nodes) {
            node.next.forEach(x -> x.prev.add(node));
            node.instruction.uses().forEach(register -> { if(register.isVirtual()) node.liveIN.add(register);}); // LIVENESS rule#1
        }

        // Fourth pass: liveness algorithm
        boolean hasChanged = true;
        while (hasChanged) {
            hasChanged = false;
            for (Node node : nodes) {
                // Rule 2
                for (Register reg: node.liveIN) {
                    if(reg.isVirtual()) {
                        for (Node prevNode : node.prev) {
                            hasChanged = prevNode.liveOUT.add(reg) || hasChanged;
                        }
                    }
                }

                // Rule 3
                Register defReg = node.instruction.def();
                for(Register reg: node.liveOUT) {
                    if (reg != defReg && reg.isVirtual()) {
                        hasChanged = node.liveIN.add(reg) ||  hasChanged;
                    }
                }
            }
        }
        nodes.forEach(x -> x.print());
        return nodes;
    }

    private void storeBranches(Node node) {
        if(node.instruction instanceof Instruction.BinaryBranch) {
            Instruction.BinaryBranch branch = (Instruction.BinaryBranch) node.instruction;
            labelPointers.put(node, branch.label.name);
        } else if(node.instruction instanceof Instruction.UnaryBranch) {
            Instruction.UnaryBranch branch = (Instruction.UnaryBranch) node.instruction;
            labelPointers.put(node, branch.label.name);
        } else if(node.instruction instanceof Instruction.Jump) {
            Instruction.Jump branch = (Instruction.Jump) node.instruction;
            labelPointers.put(node, branch.label.name);
        }
    }

    private List<List<Node>> buildCFGs(AssemblyProgram program) {
        List<List<Node>> CFGs = new ArrayList<>();
        for(AssemblyProgram.Section section: program.sections) {
            if(section.type == AssemblyProgram.Section.Type.DATA)
                continue;

            CFGs.add(buildCFG(section.items));
        }
        return CFGs;
    }

    private static Map<Register.Virtual, Label>  collectVirtualRegisters(AssemblyProgram.Section section) {
        final Map<Register.Virtual, Label> vrMap = new HashMap<>();

        section.items.forEach(item ->
                item.accept(new AssemblyItemVisitor() {
                    public void visitComment(Comment comment) {}
                    public void visitLabel(Label label) {}
                    public void visitDirective(Directive directive) {}

                    public void visitInstruction(Instruction insn) {
                        insn.registers().forEach(reg -> {
                            if (reg instanceof Register.Virtual) {
                                Register.Virtual vr = (Register.Virtual) reg;
                                Label l = Label.create(vr.toString());
                                vrMap.put(vr, l);
                            }
                        });
                    }
                }));
        return vrMap;
    }

    private void emitInstructionWithoutVirtualRegister(Instruction insn, Map<Register.Virtual, Label> vrMap, AssemblyProgram.Section section) {

        section.emit("Original instruction: "+insn);

        final Stack<Register> freeTempRegs = new Stack<>();
        final Stack<Register> freeSpilledRegs = new Stack<>();
        freeTempRegs.addAll(Arrays.asList(registers));
        freeSpilledRegs.addAll(Arrays.asList(spilledRegs));

        // creates a map from virtual register to temporary architecture register for all registers appearing in the instructions
        insn.registers().forEach(reg -> {
            if (reg.isVirtual() && regToInt.get(reg) != 0) {
                Register tmp = freeTempRegs.get(regToInt.get(reg)-1);
                vrToAr.put(reg, tmp);
            } else if (reg.isVirtual()) {
                Register tmp = freeSpilledRegs.pop();
                Label label = vrMap.get(reg);
                vrToAr.put(reg, tmp);
            }
        });

        // load the values of any spilled registers
        insn.uses().forEach(reg -> {
            if (reg.isVirtual() && regToInt.get(reg) == 0) {
                Register tmp = vrToAr.get(reg);
                Label label = vrMap.get(reg);
                section.emit(OpCode.LA, tmp, label);
                section.emit(OpCode.LW, tmp, tmp, 0);
            }
        });

        // emit new instructions where all virtual register have been replaced by architectural ones
        section.emit(insn.rebuild(vrToAr));

        if (insn.def() != null) {
            if (insn.def().isVirtual() && regToInt.get(insn.def()) == 0) {
                Register tmpVal = vrToAr.get(insn.def());
                Register tmpAddr = freeSpilledRegs.remove(0);
                Label label = vrMap.get(insn.def());

                section.emit(OpCode.LA, tmpAddr, label);
                section.emit(OpCode.SW, tmpVal, tmpAddr, 0);
            }
        }
    }

    private AssemblyProgram run(AssemblyProgram prog) {

        AssemblyProgram newProg = new AssemblyProgram();

        // we assume that each function has a single corresponding text section
        prog.sections.forEach(section -> {
            if (section.type == AssemblyProgram.Section.Type.DATA)
                newProg.emitSection(section);
            else {
                assert (section.type == AssemblyProgram.Section.Type.TEXT);

                // map from virtual register to corresponding uniquely created label
                final Map<Register.Virtual, Label> vrMap = collectVirtualRegisters(section);

                // allocate one label for each virtual register in a new data section
                AssemblyProgram.Section dataSec = newProg.newSection(AssemblyProgram.Section.Type.DATA);
                dataSec.emit("Allocated labels for spilled registers");
                vrMap.forEach((vr, lbl) -> {
                    if(regToInt.get(vr) == 0) {
                        dataSec.emit(lbl);
                        dataSec.emit(new Directive("space " + 4));
                    }
                });

                // emit new instructions that don't use any virtual registers and transform push/pop registers instructions into real sequence of instructions
                // When dealign with push/pop registers, we assume that if a virtual register is used in the section, then it must be written into.
                final AssemblyProgram.Section newSection = newProg.newSection(AssemblyProgram.Section.Type.TEXT);
                List<Map.Entry<Register.Virtual, Label>> vr = new LinkedList<>(vrMap.entrySet());
                List<Map.Entry<Register.Virtual, Label>> reverseVr = new LinkedList<>(vr);
                Collections.reverse(vr);

                section.items.forEach(item ->
                        item.accept(new AssemblyItemVisitor() {
                            public void visitComment(Comment comment) {
                                newSection.emit(comment);
                            }
                            public void visitLabel(Label label) {
                                newSection.emit(label);
                            }
                            public void visitDirective(Directive directive) {
                                newSection.emit(directive);
                            }
                            public void visitInstruction(Instruction insn) {
                                List<Register> usedRegs = new ArrayList<>();
                                if (insn == Instruction.Nullary.pushRegisters) {
                                    newSection.emit("Original instruction: pushRegisters");
                                    for (Map.Entry e : vr) {
                                        if(regToInt.get(e.getKey()) == 0) {
                                            // load content of memory at label into $t0
                                            newSection.emit(OpCode.LA, Register.Arch.t0, (Label) e.getValue());
                                            newSection.emit(OpCode.LW, Register.Arch.t0, Register.Arch.t0, 0);
                                            // push $t0 onto stack
                                            newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4);
                                            newSection.emit(OpCode.SW, Register.Arch.t0, Register.Arch.sp, 0);
                                        } else {
                                            Register reg = registers[regToInt.get((Register) e.getKey())-1];
                                            if(!usedRegs.contains(reg)) {
                                                // push $t0 onto stack
                                                newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, -4);
                                                newSection.emit(OpCode.SW, reg, Register.Arch.sp, 0);
                                                usedRegs.add(reg);
                                            }
                                        }
                                    }
                                } else if (insn == Instruction.Nullary.popRegisters) {
                                    newSection.emit("Original instruction: popRegisters");
                                    usedRegs.clear();
                                    for (Map.Entry e : reverseVr) {
                                        if(regToInt.get(e.getKey()) == 0) {
                                            // pop from stack into $t0
                                            newSection.emit(OpCode.LW, Register.Arch.t0, Register.Arch.sp, 0);
                                            newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4);

                                            // store content of $t0 in memory at label
                                            newSection.emit(OpCode.LA, Register.Arch.t1, (Label) e.getValue());
                                            newSection.emit(OpCode.SW, Register.Arch.t0, Register.Arch.t1, 0);
                                        } else {
                                            Register reg = registers[regToInt.get((Register) e.getKey())-1];
                                            if(!usedRegs.contains(reg)) {
                                                // pop from stack into $t0
                                                newSection.emit(OpCode.LW, reg, Register.Arch.sp, 0);
                                                newSection.emit(OpCode.ADDI, Register.Arch.sp, Register.Arch.sp, 4);
                                                usedRegs.add(reg);
                                            }
                                        }
                                    }
                                } else
                                    emitInstructionWithoutVirtualRegister(insn, vrMap, newSection);
                            }
                        }));


            }
        });


        return newProg;
    }
}
