package gen;

import gen.asm.*;
import regalloc.NaiveRegAlloc;

import java.io.*;

public class MainGen {
    public static void main(String[] args) {
        AssemblyProgram prog = new AssemblyProgram();

        AssemblyProgram.Section text = prog.newSection(AssemblyProgram.Section.Type.TEXT);
        Register v1 = Register.Virtual.create();
        Register v2 = Register.Virtual.create();
        Register v3 = Register.Virtual.create();
        text.emit(OpCode.PUSH_REGISTERS);
        text.emit(OpCode.ADDI, v1, Register.Arch.zero, 4);
        text.emit(OpCode.ADDI, v2, Register.Arch.zero, 8);
        text.emit(OpCode.ADD, v3, v1, v2);
        text.emit(OpCode.POP_REGISTERS);

        try {
            PrintWriter writer = new PrintWriter("t1.asm");
            prog.print(writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AssemblyProgram newProg = NaiveRegAlloc.INSTANCE.apply(prog);

        try {
            PrintWriter writer = new PrintWriter("t2.asm");
            newProg.print(writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assertCanRoundTrip(prog);
        assertCanRoundTrip(newProg);
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
