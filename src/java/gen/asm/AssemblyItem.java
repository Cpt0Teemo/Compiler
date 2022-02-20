package gen.asm;


import java.util.*;
import java.util.stream.Stream;

public abstract class AssemblyItem {
    public abstract void accept(AssemblyItemVisitor v);

    public static class Comment extends AssemblyItem {
        public final String comment;

        public Comment(String comment) {
            this.comment = comment;
        }

        public String toString() {
            return "# " + comment;
        }

        public void accept(AssemblyItemVisitor v) {
            v.visitComment(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Comment comment1 = (Comment) o;
            return comment.equals(comment1.comment);
        }

        @Override
        public int hashCode() {
            return Objects.hash(comment);
        }
    }

    public static class Directive extends AssemblyItem {

        private final String name;
        public Directive(String name) {
            this.name = name;
        }
        public String toString() {
            return "." + name;
        }

        public void accept(AssemblyItemVisitor v) {
            v.visitDirective(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Directive directive = (Directive) o;
            return Objects.equals(name, directive.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    public abstract static class Instruction extends AssemblyItem {
        /**
         * Identifies a MIPS integer opcode.
         */
        public static abstract class OpCode {
            public enum Kind {
                /**
                 * A type R opcode.
                 */
                TypeR,

                /**
                 * A type J opcode.
                 */
                TypeJ,

                /**
                 * A type I branch opcode.
                 */
                TypeIBranch,

                /**
                 * A type I arithmetic opcode.
                 */
                TypeIArithmetic,

                /**
                 * A type I load opcode.
                 */
                TypeILoad,

                /**
                 * A type I store opcode.
                 */
                TypeIStore,

                /**
                 * The special load upper immediate opcode. This is a type I opcode that discards its source register.
                 */
                LoadUpperImmediate,

                /**
                 * The load address pseudo-op.
                 */
                LoadAddress,

                /**
                 * A pseudo-opcode whose meaning is known only to the compiler.
                 */
                Intrinsic
            }

            /**
             * The opcode's mnemonic, i.e., the textual representation of the opcode in an assembly file.
             */
            public final String mnemonic;

            /**
             * Creates an {@link OpCode} instance from a mnemonic.
             * @param mnemonic The mnemonic that identifies the opcode.
             */
            public OpCode(final String mnemonic) {
                this.mnemonic = mnemonic;
            }

            /**
             * Gets the family of opcodes this opcode belongs to.
             * @return An opcode kind.
             */
            public abstract Kind kind();

            @Override
            public String toString() { return mnemonic; }

            /**
             * Gets a list of all opcodes known to the compiler.
             */
            public static List<OpCode> allOps()
            {
                return Stream.of(
                    RInstruction.OpCode.typeROps().stream().map(x -> (OpCode)x),
                    Jump.OpCode.typeJOps().stream().map(x -> (OpCode)x),
                    Branch.OpCode.typeIBranchOps().stream().map(x -> (OpCode)x),
                    IInstruction.OpCode.typeIArithmeticOps().stream().map(x -> (OpCode)x),
                    Load.OpCode.typeILoadOps().stream().map(x -> (OpCode)x),
                    Store.OpCode.typeIStoreOps().stream().map(x -> (OpCode)x),
                    Intrinsic.OpCode.intrinsicOps().stream().map(x -> (OpCode)x),
                    Stream.of(LoadUpperImmediate.opcode, LoadAddress.opcode)
                ).flatMap(s -> s).toList();
            }

            /**
             * Tries to interpret a mnemonic as an opcode.
             * @param mnemonic The mnemonic to interpret.
             * @return An opcode corresponding to {@code mnemonic} if the latter is well-understood; otherwise,
             * {@link Optional#empty()}.
             */
            public static Optional<OpCode> tryParse(String mnemonic)
            {
                return allOps().stream().filter(x -> x.mnemonic.equals(mnemonic)).findAny();
            }
        }

        public final OpCode opcode;

        public Instruction(OpCode opcode) {
            this.opcode = opcode;
        }

        /**
         * @return register that this instructions modifies (if none, returns null)
         */
        public abstract Register def();

        /**
         * @return list of registers that this instruction uses
         */
        public abstract  List<Register> uses();

        /**
         * @return list of registers that are used as operands for this instruction
         */
        public List<Register> registers() {
            List<Register> regs = new ArrayList<>(uses());
            if (def() != null)
                regs.add(def());
            return regs;
        }

        /**
         *
         * @param regMap replacement map for register
         * @return a new instruction where the registers have been replaced based on the regMap
         */
        public abstract Instruction rebuild(Map<Register,Register> regMap);

        public void accept(AssemblyItemVisitor v) {
            v.visitInstruction(this);
        }
    }

    public static class RInstruction extends Instruction {
        /**
         * An opcode for type R instructions.
         */
        public static final class OpCode extends Instruction.OpCode {
            private OpCode(String mnemonic) {
                super(mnemonic);
            }

            @Override
            public Kind kind() {
                return Kind.TypeR;
            }

            public static final OpCode ADD = new OpCode("add");
            public static final OpCode ADDU = new OpCode("addu");
            public static final OpCode AND = new OpCode("and");
            public static final OpCode JR = new OpCode("jr");
            public static final OpCode NOR = new OpCode("nor");
            public static final OpCode SLT = new OpCode("slt");
            public static final OpCode SLTU = new OpCode("sltu");
            public static final OpCode SLL = new OpCode("sll");
            public static final OpCode SRL = new OpCode("srl");
            public static final OpCode SUB = new OpCode("sub");
            public static final OpCode SUBU = new OpCode("subu");

            /**
             * Produces a list of all known type R opcodes.
             * @return A list of all known type R opcodes.
             */
            public static List<OpCode> typeROps() {
                return List.of(ADD, ADDU, AND, JR, NOR, SLT, SLTU, SLL, SRL, SUB, SUBU);
            }
        }

        public final Register dst;
        public final Register src1;
        public final Register src2;

        public RInstruction(OpCode opcode, Register dst, Register src1, Register src2) {
            super(opcode);
            this.dst = dst;
            this.src1 = src1;
            this.src2 = src2;
        }

        public String toString() {
            return opcode + " " + dst + "," + src1 + "," + src2;
        }


        public Register def() {
            return dst;
        }


        public List<Register> uses() {
            return List.of(src1, src2);
        }

        public RInstruction rebuild(Map<Register,Register> regMap) {
            return new RInstruction(
                (OpCode)opcode,
                regMap.getOrDefault(dst, dst),
                regMap.getOrDefault(src1,src1),
                regMap.getOrDefault(src2,src2));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RInstruction that = (RInstruction) o;
            return dst.equals(that.dst) && src1.equals(that.src1) && src2.equals(that.src2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dst, src1, src2);
        }
    }

    /**
     * A branch instruction, that is, a type I instruction that takes a {@link Label} as immediate operand.
     */
    public static class Branch extends Instruction {
        /**
         * An opcode for branch instructions.
         */
        public static final class OpCode extends Instruction.OpCode {
            private OpCode(String mnemonic) {
                super(mnemonic);
            }

            @Override
            public Kind kind() {
                return Kind.TypeIBranch;
            }

            public static final OpCode BEQ = new OpCode("beq");
            public static final OpCode BNE = new OpCode("bne");

            /**
             * Produces a list of all known type I branch opcodes.
             * @return A list of all known type I branch opcodes.
             */
            public static List<OpCode> typeIBranchOps() {
                return List.of(BEQ, BNE);
            }
        }

        public final Label label;
        public final Register src1;
        public final Register src2;

        public Branch(OpCode opcode, Register src1, Register src2, Label label) {
            super(opcode);
            this.label = label;
            this.src1 = src1;
            this.src2 = src2;
        }

        public String toString() {
            return opcode+" "+ src1 + "," + src2 + "," + label;
        }


        public Register def() {
            return null;
        }


        public List<Register> uses() {
            return List.of(src1, src2);
        }

        public Branch rebuild(Map<Register,Register> regMap) {
            return new Branch((OpCode)opcode, regMap.getOrDefault(src1,src1),regMap.getOrDefault(src2,src2), label);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Branch branch = (Branch) o;
            return label.equals(branch.label) && src1.equals(branch.src1) && src2.equals(branch.src2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, src1, src2);
        }
    }


    public static class IInstruction extends Instruction {
        /**
         * An opcode for type I arithmetic instructions.
         */
        public static final class OpCode extends Instruction.OpCode {
            private OpCode(String mnemonic) {
                super(mnemonic);
            }

            @Override
            public Kind kind() {
                return Kind.TypeIArithmetic;
            }

            public static final OpCode ADDI = new OpCode("addi");
            public static final OpCode ADDIU = new OpCode("addiu");
            public static final OpCode ANDI = new OpCode("andi");
            public static final OpCode ORI = new OpCode("ori");
            public static final OpCode SLTI = new OpCode("slti");
            public static final OpCode SLTIU = new OpCode("sltiu");

            /**
             * Produces a list of all known type I arithmetic opcodes.
             * @return A list of all known type I arithmetic opcodes.
             */
            public static List<OpCode> typeIArithmeticOps() {
                return List.of(ADDI, ADDIU, ANDI, ORI, SLTI, SLTIU);
            }
        }

        public final int imm;
        public final Register dst;
        public final Register src;

        public IInstruction(OpCode opcode, Register dst, Register src, int imm) {
            super(opcode);
            this.imm = imm;
            this.src = src;
            this.dst = dst;
        }

        public String toString() {
            return opcode+" "+ dst + "," + src + "," + imm;
        }


        public Register def() {
            return dst;
        }


        public List<Register> uses() {
            Register[] uses = {src};
            return Arrays.asList(uses);
        }

        public IInstruction rebuild(Map<Register,Register> regMap) {
            return new IInstruction((OpCode)opcode, regMap.getOrDefault(dst, dst),regMap.getOrDefault(src, src), imm);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IInstruction that = (IInstruction) o;
            return imm == that.imm && dst.equals(that.dst) && src.equals(that.src);
        }

        @Override
        public int hashCode() {
            return Objects.hash(imm, dst, src);
        }
    }

    /**
     * A J-type instruction, which consists of an opcode and an address operand, encoded as a {@link Label}.
     */
    public static class Jump extends Instruction {
        /**
         * An opcode for type J instructions.
         */
        public static final class OpCode extends Instruction.OpCode {
            private OpCode(String mnemonic) {
                super(mnemonic);
            }

            @Override
            public Kind kind() {
                return Kind.TypeJ;
            }

            public static final OpCode J = new OpCode("j");
            public static final OpCode JAL = new OpCode("jal");

            /**
             * Produces a list of all known type J opcodes.
             * @return A list of all known type J opcodes.
             */
            public static List<OpCode> typeJOps() {
                return List.of(J, JAL);
            }
        }

        /**
         * The J-type instruction's address operand, encoded as a label.
         */
        public final Label label;

        /**
         * Creates a new J-type instruction from an opcode and a label.
         * @param opcode The opcode that defines the type of the instruction.
         * @param label The label that serves as the address operand of the instruction.
         */
        public Jump(OpCode opcode, Label label) {
            super(opcode);
            this.label = label;
        }

        @Override
        public Register def() {
            return null;
        }

        @Override
        public List<Register> uses() {
            return List.of();
        }

        @Override
        public Instruction rebuild(Map<Register, Register> regMap) {
            return this;
        }

        @Override
        public String toString() { return opcode + " " + label; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Jump jump = (Jump) o;
            return label.equals(jump.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }
    }

    public abstract static class MemIndirect extends Instruction {
        public final Register op1;
        public final Register op2;
        public final int imm;
        public MemIndirect(OpCode opcode, Register op1, Register op2, int imm) {
            super(opcode);
            this.op1 = op1;
            this.op2 = op2;
            this.imm = imm;
        }

        public String toString() {
            return opcode + " " + op1 + "," + imm + "(" + op2 + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MemIndirect that = (MemIndirect) o;
            return imm == that.imm && op1.equals(that.op1) && op2.equals(that.op2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(op1, op2, imm);
        }
    }

    public static class Store extends MemIndirect {
        /**
         * An opcode for store instructions.
         */
        public static final class OpCode extends Instruction.OpCode {
            private OpCode(String mnemonic) {
                super(mnemonic);
            }

            @Override
            public Kind kind() {
                return Kind.TypeIStore;
            }

            public static final OpCode SB = new OpCode("sb");
            public static final OpCode SH = new OpCode("sh");
            public static final OpCode SW = new OpCode("sw");
            public static final OpCode SC = new OpCode("sc");

            /**
             * Produces a list of all known type I store opcodes.
             * @return A list of all known type I store opcodes.
             */
            public static List<OpCode> typeIStoreOps() {
                return List.of(SB, SH, SW, SC);
            }
        }

        public Store(OpCode opcode, Register op1, Register op2, int imm) {
            super(opcode, op1, op2, imm);
        }
        public Store rebuild(Map<Register,Register> regMap) {
            return new Store((OpCode)opcode, regMap.getOrDefault(op1, op1),regMap.getOrDefault(op2, op2), imm);
        }
        public Register def() {
            return null;
        }

        public List<Register> uses() {
            Register[] uses = {op1, op2};
            return Arrays.asList(uses);
        }
    }

    public static class Load extends MemIndirect {
        /**
         * An opcode for store instructions.
         */
        public static final class OpCode extends Instruction.OpCode {
            private OpCode(String mnemonic) {
                super(mnemonic);
            }

            @Override
            public Kind kind() {
                return Kind.TypeILoad;
            }

            public static final OpCode LBU = new OpCode("lbu");
            public static final OpCode LHU = new OpCode("lhu");
            public static final OpCode LW = new OpCode("lw");
            public static final OpCode LL = new OpCode("ll");

            /**
             * Produces a list of all known type I load opcodes.
             * @return A list of all known type I load opcodes.
             */
            public static List<OpCode> typeILoadOps() {
                return List.of(LBU, LHU, LW, LL);
            }
        }

        public Load(OpCode opcode, Register op1, Register op2, int imm) {
            super(opcode, op1, op2, imm);
        }
        public Load rebuild(Map<Register,Register> regMap) {
            return new Load((OpCode)opcode, regMap.getOrDefault(op1, op1),regMap.getOrDefault(op2, op2), imm);
        }
        public Register def() {
            return op1;
        }

        public List<Register> uses() {
            Register[] uses = {op2};
            return Arrays.asList(uses);
        }
    }

    /**
     * An instruction that loads its immediate operand into the upper half of its destination register.
     */
    public static class LoadUpperImmediate extends Instruction {
        public static final OpCode opcode = new OpCode("lui") {
            @Override
            public Kind kind() {
                return Kind.LoadUpperImmediate;
            }
        };

        public final Register dst;
        public final int imm;

        public LoadUpperImmediate(Register dst, int imm) {
            super(opcode);
            this.dst = dst;
            this.imm = imm;
        }

        @Override
        public Register def() {
            return dst;
        }

        @Override
        public List<Register> uses() {
            return List.of();
        }

        @Override
        public Instruction rebuild(Map<Register, Register> regMap) {
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoadUpperImmediate that = (LoadUpperImmediate) o;
            return imm == that.imm && dst.equals(that.dst);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dst, imm);
        }
    }

    /**
     * A pseudo-instruction that loads the address of its label operand into its destination register.
     */
    public static class LoadAddress extends Instruction {
        public static final OpCode opcode = new OpCode("la") {
            @Override
            public Kind kind() {
                return Kind.LoadAddress;
            }
        };

        public final Label label;
        public final Register dst;

        public LoadAddress(Register dst, Label label) {
            super(opcode);
            this.label = label;
            this.dst = dst;
        }

        public String toString() {
            return "la " + dst + "," + label;
        }


        public Register def() {
            return dst;
        }


        public List<Register> uses() {
            return List.of();
        }

        public LoadAddress rebuild(Map<Register,Register> regMap) {
            return new LoadAddress(regMap.getOrDefault(dst,dst),label);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoadAddress that = (LoadAddress) o;
            return label.equals(that.label) && dst.equals(that.dst);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, dst);
        }
    }

    /**
     * A compiler intrinsic pseudo-instruction. That is, a pseudo-instruction that is understood only by the compiler.
     */
    public static class Intrinsic extends Instruction {
        /**
         * A pseudo-opcode for intrinsics.
         */
        public static final class OpCode extends Instruction.OpCode {
            private OpCode(String mnemonic) {
                super(mnemonic);
            }

            @Override
            public Kind kind() {
                return Kind.Intrinsic;
            }

            public static final OpCode pushRegisters = new OpCode("pushRegisters");
            public static final OpCode popRegisters = new OpCode("popRegisters");

            /**
             * Produces a list of all known intrinsic pseudo-opcodes.
             * @return A list of all known intrinsic pseudo-opcodes.
             */
            public static List<OpCode> intrinsicOps() {
                return List.of(pushRegisters, popRegisters);
            }
        }

        private Intrinsic(OpCode opcode) {
            super(opcode);
        }

        /**
         * An intrinsic instruction that pushes onto the stack all registers currently in use by the compiler.
         */
        public static final Intrinsic pushRegisters = new Intrinsic(OpCode.pushRegisters);

        /**
         * An intrinsic instruction that pops from the stack all registers currently in use by the compiler.
         */
        public static final Intrinsic popRegisters = new Intrinsic(OpCode.popRegisters);

        @Override
        public Register def() {
            return null;
        }

        @Override
        public List<Register> uses() {
            return List.of();
        }

        @Override
        public Instruction rebuild(Map<Register, Register> regMap) {
            return this;
        }
    }

    /**
     * A label in a MIPS assembly program.
     *
     * {@link Label} instances are flyweights. That is, the class' design makes it so that (barring multithreading
     * or reflection shenanigans) there can be at most one {@link Label} instance per name. Use {@link #create(String)}
     * and {@link #create()} to generate fresh {@link Label} instances.
     */
    public static class Label extends AssemblyItem {
        /**
         * The label's unique name.
         */
        public final String name;

        private Label(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

        public void accept(AssemblyItemVisitor v) {
            v.visitLabel(this);
        }

        // This hash map interns flyweight instances to ensure that no two Virtual instances have the same name.
        private static final HashMap<String, Label> instances = new HashMap<>();

        /**
         * Gets the unique label for a given name.
         * @param name The label's name.
         * @return The unique {@link Label} instance with name {@code name}.
         */
        public static Label get(String name)
        {
            return instances.computeIfAbsent(name, Label::new);
        }

        /**
         * Creates a fresh label with a unique name.
         * @param nameSuffix A suffix to append to the label's name.
         * @return A unique {@link Label} instance.
         */
        public static Label create(String nameSuffix)
        {
            int counter = instances.size();
            String draftName;
            do {
                draftName = "label_" + counter + "_" + nameSuffix;
                counter++;
            } while (instances.containsKey(draftName));
            return get(draftName);
        }

        /**
         * Creates a fresh label with a unique name.
         * @return A unique {@link Label} instance.
         */
        public static Label create()
        {
            return create("");
        }
    }
}
