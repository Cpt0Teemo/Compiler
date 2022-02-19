package gen;

import gen.asm.AssemblyItem;
import gen.asm.Register;
import gen.asm.AssemblyProgram;
import regalloc.NaiveRegAlloc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Test {
    public static void main(String[] args) {
        AssemblyProgram prog = new AssemblyProgram();

        AssemblyProgram.Section text = prog.newSection(AssemblyProgram.Section.Type.TEXT);
        Register v1 = new Register.Virtual();
        Register v2 = new Register.Virtual();
        Register v3 = new Register.Virtual();
        text.emit(AssemblyItem.Intrinsic.pushRegisters);
        text.emit(AssemblyItem.IInstruction.OpCode.ADDI, v1, Register.Arch.zero, 4);
        text.emit(AssemblyItem.IInstruction.OpCode.ADDI, v2, Register.Arch.zero, 8);
        text.emit(AssemblyItem.RInstruction.OpCode.ADD, v3, v1, v2);
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
    }
}
