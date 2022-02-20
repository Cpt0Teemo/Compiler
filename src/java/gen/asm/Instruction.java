package gen.asm;

import java.util.*;

/**
 * An instruction in a MIPS assembly program.
 */
public abstract class Instruction extends AssemblyItem {

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
    public abstract List<Register> uses();

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
     * @param regMap replacement map for register
     * @return a new instruction where the registers have been replaced based on the regMap
     */
    public abstract gen.asm.Instruction rebuild(Map<Register, Register> regMap);

    public void accept(AssemblyItemVisitor v) {
        v.visitInstruction(this);
    }

    /**
     * A core arithmetic instruction that takes three register arguments. This is a type R instruction.
     */
    public static final class CoreArithmetic extends Instruction {
        public final Register dst;
        public final Register src1;
        public final Register src2;

        public CoreArithmetic(OpCode.CoreArithmetic opcode, Register dst, Register src1, Register src2) {
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

        public CoreArithmetic rebuild(Map<Register,Register> regMap) {
            return new CoreArithmetic(
                (OpCode.CoreArithmetic)opcode,
                regMap.getOrDefault(dst, dst),
                regMap.getOrDefault(src1,src1),
                regMap.getOrDefault(src2,src2));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoreArithmetic that = (CoreArithmetic) o;
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
    public static final class Branch extends Instruction {
        public final Label label;
        public final Register src1;
        public final Register src2;

        public Branch(OpCode.Branch opcode, Register src1, Register src2, Label label) {
            super(opcode);
            this.label = label;
            this.src1 = src1;
            this.src2 = src2;
        }

        public String toString() {
            return opcode + " " + src1 + "," + src2 + "," + label;
        }


        public Register def() {
            return null;
        }


        public List<Register> uses() {
            return List.of(src1, src2);
        }

        public Branch rebuild(Map<Register,Register> regMap) {
            return new Branch(
                (OpCode.Branch)opcode,
                regMap.getOrDefault(src1,src1),
                regMap.getOrDefault(src2,src2), label);
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

    /**
     * A core arithmetic instruction with two register operands and one immediate operand. This is a type I instruction.
     */
    public static final class ArithmeticWithImmediate extends Instruction {
        public final int imm;
        public final Register dst;
        public final Register src;

        public ArithmeticWithImmediate(OpCode.ArithmeticWithImmediate opcode, Register dst, Register src, int imm) {
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

        public ArithmeticWithImmediate rebuild(Map<Register,Register> regMap) {
            return new ArithmeticWithImmediate(
                (OpCode.ArithmeticWithImmediate)opcode,
                regMap.getOrDefault(dst, dst),
                regMap.getOrDefault(src, src),
                imm);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArithmeticWithImmediate that = (ArithmeticWithImmediate) o;
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
    public static final class Jump extends Instruction {
        /**
         * The J-type instruction's address operand, encoded as a label.
         */
        public final Label label;

        /**
         * Creates a new J-type instruction from an opcode and a label.
         * @param opcode The opcode that defines the type of the instruction.
         * @param label The label that serves as the address operand of the instruction.
         */
        public Jump(OpCode.Jump opcode, Label label) {
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

    /**
     * An instruction that loads or stores an address in memory. The address is computed based on a register and an
     * immediate operand.
     */
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

    /**
     * An instruction that stores a value in memory. This is a type I instruction.
     */
    public static final class Store extends MemIndirect {
        public Store(OpCode.Store opcode, Register op1, Register op2, int imm) {
            super(opcode, op1, op2, imm);
        }
        public Store rebuild(Map<Register,Register> regMap) {
            return new Store((OpCode.Store)opcode, regMap.getOrDefault(op1, op1),regMap.getOrDefault(op2, op2), imm);
        }
        public Register def() {
            return null;
        }

        public List<Register> uses() {
            Register[] uses = {op1, op2};
            return Arrays.asList(uses);
        }
    }

    /**
     * An instruction that loads a value from memory and stores it in a register. This is a type I instruction.
     */
    public static final class Load extends MemIndirect {
        public Load(OpCode.Load opcode, Register op1, Register op2, int imm) {
            super(opcode, op1, op2, imm);
        }
        public Load rebuild(Map<Register, Register> regMap) {
            return new Load((OpCode.Load)opcode, regMap.getOrDefault(op1, op1),regMap.getOrDefault(op2, op2), imm);
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
    public static final class LoadUpperImmediate extends Instruction {
        public final Register dst;
        public final int imm;

        public LoadUpperImmediate(Register dst, int imm) {
            super(OpCode.LUI);
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
    public static final class LoadAddress extends Instruction {
        public final Label label;
        public final Register dst;

        public LoadAddress(Register dst, Label label) {
            super(OpCode.LA);
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
     * A nullary compiler intrinsic. That is, a pseudo-instruction that takes no arguments and is understood only by
     * the compiler.
     */
    public static final class NullaryIntrinsic extends Instruction {
        private NullaryIntrinsic(OpCode.NullaryIntrinsic opcode) {
            super(opcode);
        }

        /**
         * An intrinsic instruction that pushes onto the stack all registers currently in use by the compiler.
         */
        public static final NullaryIntrinsic pushRegisters = new NullaryIntrinsic(OpCode.PUSH_REGISTERS);

        /**
         * An intrinsic instruction that pops from the stack all registers currently in use by the compiler.
         */
        public static final NullaryIntrinsic popRegisters = new NullaryIntrinsic(OpCode.POP_REGISTERS);

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
        public String toString() {
            return opcode.toString();
        }
    }
}
