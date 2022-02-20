package gen;

import gen.asm.AssemblyItem;
import gen.asm.AssemblyParser;
import gen.asm.Register;
import gen.asm.AssemblyProgram;
import regalloc.NaiveRegAlloc;

import java.io.*;

public class Test {
    public static void main(String[] args) {
        AssemblyProgram prog = new AssemblyProgram();

        AssemblyProgram.Section text = prog.newSection(AssemblyProgram.Section.Type.TEXT);
        Register v1 = Register.Virtual.create();
        Register v2 = Register.Virtual.create();
        Register v3 = Register.Virtual.create();
        text.emit(AssemblyItem.Intrinsic.pushRegisters);
        text.emit(AssemblyItem.ArithmeticWithImmediate.OpCode.ADDI, v1, Register.Arch.zero, 4);
        text.emit(AssemblyItem.ArithmeticWithImmediate.OpCode.ADDI, v2, Register.Arch.zero, 8);
        text.emit(AssemblyItem.CoreArithmetic.OpCode.ADD, v3, v1, v2);
        text.emit(AssemblyItem.Intrinsic.popRegisters);

        try {
            PrintWriter writer = new PrintWriter("t1.asm");
            prog.print(writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        AssemblyProgram newProg = NaiveRegAlloc.run(prog);

        try {
            PrintWriter writer = new PrintWriter("t2.asm");
            newProg.print(writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        canParseAssemblyItems();
        assertCanRoundTrip(prog);
        assertCanRoundTrip(newProg);
    }

    private static void canParseAssemblyItems() {
        assertEqual(AssemblyParser.parseAssemblyItem("# hello"), new AssemblyItem.Comment("hello"));
        assertEqual(AssemblyParser.parseAssemblyItem("label:"), AssemblyItem.Label.get("label"));
        assertEqual(AssemblyParser.parseAssemblyItem(".space 4"), new AssemblyItem.Directive("space 4"));
        assertEqual(
            AssemblyParser.parseAssemblyItem("add $t0, v0, $t2"),
            new AssemblyItem.CoreArithmetic(
                AssemblyItem.CoreArithmetic.OpCode.ADD,
                Register.Arch.t0,
                Register.Virtual.get("v0"),
                Register.Arch.t2));
        assertEqual(
            AssemblyParser.parseAssemblyItem("addi $t0, v0, 15"),
            new AssemblyItem.ArithmeticWithImmediate(
                AssemblyItem.ArithmeticWithImmediate.OpCode.ADDI,
                Register.Arch.t0,
                Register.Virtual.get("v0"),
                15));
        assertEqual(
            AssemblyParser.parseAssemblyItem("beq $t0, v0, branch_target"),
            new AssemblyItem.Branch(
                AssemblyItem.Branch.OpCode.BEQ,
                Register.Arch.t0,
                Register.Virtual.get("v0"),
                AssemblyItem.Label.get("branch_target")));
        assertEqual(
            AssemblyParser.parseAssemblyItem("jal jump_target"),
            new AssemblyItem.Jump(
                AssemblyItem.Jump.OpCode.JAL,
                AssemblyItem.Label.get("jump_target")));
        assertEqual(
            AssemblyParser.parseAssemblyItem("la $s0, address"),
            new AssemblyItem.LoadAddress(
                Register.Arch.s0,
                AssemblyItem.Label.get("address")));
        assertEqual(
            AssemblyParser.parseAssemblyItem("lui $s0, 42"),
            new AssemblyItem.LoadUpperImmediate(
                Register.Arch.s0,
                42));
        assertEqual(
            AssemblyParser.parseAssemblyItem("lbu $t0, 16($t1)"),
            new AssemblyItem.Load(
                AssemblyItem.Load.OpCode.LBU,
                Register.Arch.t0,
                Register.Arch.t1,
                16));
        assertEqual(
            AssemblyParser.parseAssemblyItem("sw $t0, 16($t1)"),
            new AssemblyItem.Store(
                AssemblyItem.Store.OpCode.SW,
                Register.Arch.t0,
                Register.Arch.t1,
                16));
        assertEqual(
            AssemblyParser.parseAssemblyItem("pushRegisters"),
            AssemblyItem.Intrinsic.pushRegisters);
        assertEqual(
            AssemblyParser.parseAssemblyItem("popRegisters"),
            AssemblyItem.Intrinsic.popRegisters);
    }

    private static void assertEqual(AssemblyItem first, AssemblyItem second) {
        if (!first.equals(second)) {
            throw new Error(first + " does not equal " + second);
        }
    }

    private static void assertCanRoundTrip(AssemblyProgram program) {
        // First, we convert our AssemblyProgram to a textual MIPS assembly file. We will store the file in memory in
        // the form of a string.
        var out = new StringWriter();
        var writer = new PrintWriter(out);
        program.print(writer);

        // Next, we read the file we just created. This effectively creates a copy of `program`.
        var programCopy = AssemblyParser.readAssemblyProgram(
            new BufferedReader(new StringReader(out.toString())));

        // Now all we need to do is simply check that the programs are equivalent.
        if (!program.equals(programCopy)) {
            throw new Error("Cannot round-trip program:\n" + out);
        }
    }
}
