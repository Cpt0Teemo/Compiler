package gen.asm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Identifies a MIPS integer opcode.
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class OpCode {
    /**
     * The family of opcodes to which an opcode belongs. Each family of opcodes corresponds to a family of
     * {@link Instruction} subclasses.
     */
    public enum Kind {
        /**
         * A type R opcode that takes three register operands.
         */
        TERNARY_ARITHMETIC,

        /**
         * A type R opcode that takes two register operands.
         */
        BINARY_ARITHMETIC,

        /**
         * A type R opcode that takes one register operand.
         */
        UNARY_ARITHMETIC,

        /**
         * A type J opcode.
         */
        JUMP,

        /**
         * A type I branch opcode.
         */
        BRANCH,

        /**
         * A type I arithmetic opcode.
         */
        ARITHMETIC_WITH_IMMEDIATE,

        /**
         * A type I load opcode.
         */
        LOAD,

        /**
         * A type I store opcode.
         */
        STORE,

        /**
         * The special load upper immediate opcode. This is a type I opcode that discards its source register.
         */
        LOAD_IMMEDIATE,

        /**
         * The load address pseudo-op.
         */
        LOAD_ADDRESS,

        /**
         * A pseudo-opcode whose meaning is known only to the compiler.
         */
        NULLARY_INTRINSIC
    }

    /**
     * The opcode's mnemonic, i.e., the textual representation of the opcode in an assembly file.
     */
    public final String mnemonic;

    /**
     * Creates an {@link OpCode} instance from a mnemonic.
     *
     * @param mnemonic The mnemonic that identifies the opcode.
     */
    public OpCode(final String mnemonic) {
        this.mnemonic = mnemonic;
    }

    /**
     * Gets the family of opcodes this opcode belongs to.
     *
     * @return An opcode kind.
     */
    public abstract Kind kind();

    @Override
    public String toString() {
        return mnemonic;
    }

    /**
     * Tries to interpret a mnemonic as an opcode.
     *
     * @param mnemonic The mnemonic to interpret.
     * @return An opcode corresponding to {@code mnemonic} if the latter is well-understood; otherwise,
     * {@link Optional#empty()}.
     */
    public static Optional<OpCode> tryParse(String mnemonic) {
        return allOps().stream().filter(x -> x.mnemonic.equals(mnemonic)).findAny();
    }

    public static final TernaryArithmetic ADD = new TernaryArithmetic("add");
    public static final TernaryArithmetic ADDU = new TernaryArithmetic("addu");
    public static final TernaryArithmetic AND = new TernaryArithmetic("and");
    public static final TernaryArithmetic JR = new TernaryArithmetic("jr");
    public static final TernaryArithmetic NOR = new TernaryArithmetic("nor");
    public static final TernaryArithmetic SLT = new TernaryArithmetic("slt");
    public static final TernaryArithmetic SLTU = new TernaryArithmetic("sltu");
    public static final TernaryArithmetic SLL = new TernaryArithmetic("sll");
    public static final TernaryArithmetic SRL = new TernaryArithmetic("srl");
    public static final TernaryArithmetic SUB = new TernaryArithmetic("sub");
    public static final TernaryArithmetic SUBU = new TernaryArithmetic("subu");

    /**
     * A list of all known ternary type R arithmetic opcodes.
     */
    public static final List<TernaryArithmetic> ternaryArithmeticOps =
        List.of(ADD, ADDU, AND, JR, NOR, SLT, SLTU, SLL, SRL, SUB, SUBU);

    public static final BinaryArithmetic DIV = new BinaryArithmetic("div");
    public static final BinaryArithmetic DIVU = new BinaryArithmetic("divu");
    public static final BinaryArithmetic MULT = new BinaryArithmetic("mult");
    public static final BinaryArithmetic MULTU = new BinaryArithmetic("multu");

    /**
     * A list of all known binary type R arithmetic opcodes.
     */
    public static final List<BinaryArithmetic> binaryArithmeticOps =
        List.of(DIV, DIVU, MULT, MULTU);

    public static final UnaryArithmetic MFHI = new UnaryArithmetic("mfhi");
    public static final UnaryArithmetic MFLO = new UnaryArithmetic("mflo");

    /**
     * A list of all known unary type R arithmetic opcodes.
     */
    public static final List<UnaryArithmetic> unaryArithmeticOps =
        List.of(MFHI, MFLO);

    public static final ArithmeticWithImmediate ADDI = new ArithmeticWithImmediate("addi");
    public static final ArithmeticWithImmediate ADDIU = new ArithmeticWithImmediate("addiu");
    public static final ArithmeticWithImmediate ANDI = new ArithmeticWithImmediate("andi");
    public static final ArithmeticWithImmediate ORI = new ArithmeticWithImmediate("ori");
    public static final ArithmeticWithImmediate SLTI = new ArithmeticWithImmediate("slti");
    public static final ArithmeticWithImmediate SLTIU = new ArithmeticWithImmediate("sltiu");

    /**
     * A list of all known type I arithmetic opcodes.
     */
    public static final List<ArithmeticWithImmediate> arithmeticWithImmediateOps =
        List.of(ADDI, ADDIU, ANDI, ORI, SLTI, SLTIU);

    public static final Branch BEQ = new Branch("beq");
    public static final Branch BNE = new Branch("bne");

    /**
     * A list of all known type I branch opcodes.
     */
    public static final List<Branch> branchOps = List.of(BEQ, BNE);

    public static final Jump J = new Jump("j");
    public static final Jump JAL = new Jump("jal");

    /**
     * A list of all known type J opcodes.
     */
    public static final List<Jump> jumpOps = List.of(J, JAL);

    public static final NullaryIntrinsic PUSH_REGISTERS = new NullaryIntrinsic("pushRegisters");
    public static final NullaryIntrinsic POP_REGISTERS = new NullaryIntrinsic("popRegisters");

    public static final Load LBU = new Load("lbu");
    public static final Load LHU = new Load("lhu");
    public static final Load LW = new Load("lw");
    public static final Load LL = new Load("ll");

    /**
     * A list of all known type I load opcodes.
     */
    public static final List<Load> loadOps = List.of(LBU, LHU, LW, LL);

    public static final Store SB = new Store("sb");
    public static final Store SH = new Store("sh");
    public static final Store SW = new Store("sw");
    public static final Store SC = new Store("sc");

    /**
     * A list of all known type I store opcodes.
     */
    public static final List<Store> storeOps = List.of(SB, SH, SW, SC);

    /**
     * A list of all known intrinsic pseudo-opcodes.
     */
    public static final List<NullaryIntrinsic> nullaryIntrinsicOps = List.of(PUSH_REGISTERS, POP_REGISTERS);

    public static final LoadImmediate LUI = new LoadImmediate("lui");
    public static final LoadImmediate LI = new LoadImmediate("li");
    public static final LoadAddress LA = new LoadAddress("la");

    /**
     * Gets a list of all opcodes known to the compiler.
     */
    public static List<OpCode> allOps() {
        return Stream.of(
                ternaryArithmeticOps.stream().map(x -> (OpCode) x),
                binaryArithmeticOps.stream().map(x -> (OpCode) x),
                unaryArithmeticOps.stream().map(x -> (OpCode) x),
                arithmeticWithImmediateOps.stream().map(x -> (OpCode) x),
                branchOps.stream().map(x -> (OpCode) x),
                jumpOps.stream().map(x -> (OpCode) x),
                loadOps.stream().map(x -> (OpCode) x),
                storeOps.stream().map(x -> (OpCode) x),
                nullaryIntrinsicOps.stream().map(x -> (OpCode) x),
                Stream.of(LUI, LI, LA)
        ).flatMap(s -> s).toList();
    }

    /**
     * An opcode for ternary type R arithmetic instructions.
     */
    public static final class TernaryArithmetic extends OpCode {
        private TernaryArithmetic(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.TERNARY_ARITHMETIC;
        }
    }

    /**
     * An opcode for binary type R arithmetic instructions.
     */
    public static final class BinaryArithmetic extends OpCode {
        private BinaryArithmetic(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.BINARY_ARITHMETIC;
        }
    }

    /**
     * An opcode for unary type R arithmetic instructions.
     */
    public static final class UnaryArithmetic extends OpCode {
        private UnaryArithmetic(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.UNARY_ARITHMETIC;
        }
    }

    /**
     * An opcode for type I arithmetic instructions.
     */
    public static final class ArithmeticWithImmediate extends OpCode {
        private ArithmeticWithImmediate(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.ARITHMETIC_WITH_IMMEDIATE;
        }
    }

    /**
     * A pseudo-opcode for intrinsics.
     */
    public static final class NullaryIntrinsic extends OpCode {
        private NullaryIntrinsic(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.NULLARY_INTRINSIC;
        }
    }

    /**
     * An opcode for type J instructions.
     */
    public static final class Jump extends OpCode {
        private Jump(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.JUMP;
        }
    }

    /**
     * An opcode for branch instructions.
     */
    public static final class Branch extends OpCode {
        private Branch(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.BRANCH;
        }
    }

    /**
     * An opcode for load instructions.
     */
    public static final class Load extends OpCode {
        private Load(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.LOAD;
        }
    }

    /**
     * An opcode for store instructions.
     */
    public static final class Store extends OpCode {
        private Store(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.STORE;
        }
    }

    /**
     * An opcode for the load upper immediate (lui) instruction.
     */
    public static final class LoadImmediate extends OpCode {
        private LoadImmediate(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.LOAD_IMMEDIATE;
        }
    }

    /**
     * An opcode for the load address (la) pseudo-instruction.
     */
    public static final class LoadAddress extends OpCode {
        private LoadAddress(String mnemonic) {
            super(mnemonic);
        }

        @Override
        public Kind kind() {
            return Kind.LOAD_ADDRESS;
        }
    }
}
