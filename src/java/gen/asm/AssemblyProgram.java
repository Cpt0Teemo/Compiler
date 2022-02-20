package gen.asm;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AssemblyProgram {

    public static class Section {

        public enum Type {TEXT, DATA}
        public final Type type;

        public Section(Type type) {
            this.type = type;
        }

        public final List<AssemblyItem> items = new ArrayList<>();

        public void emit(Instruction instruction) {
            assert this.type == Type.TEXT;
            items.add(instruction);
        }

        public void emit(OpCode.CoreArithmetic opcode, Register dst, Register src1, Register src2) {
            assert this.type == Type.TEXT;
            items.add(new Instruction.CoreArithmetic(opcode, dst, src1, src2));
        }

        public void emit(OpCode.Branch opcode, Register src1, Register src2, Label label) {
            assert this.type == Type.TEXT;
            items.add(new Instruction.Branch(opcode, src1, src2, label));
        }

        public void emit(OpCode.ArithmeticWithImmediate opcode, Register dst, Register src, int imm) {
            assert this.type == Type.TEXT;
            items.add(new Instruction.ArithmeticWithImmediate(opcode, dst, src, imm));
        }

        public void emit(OpCode.LoadAddress ignoredOpcode, Register dst, Label label) {
            assert this.type == Type.TEXT;
            items.add(new Instruction.LoadAddress(dst, label));
        }

        public void emit(OpCode.LoadUpperImmediate ignoredOpcode, Register dst, int immediate) {
            assert this.type == Type.TEXT;
            items.add(new Instruction.LoadUpperImmediate(dst, immediate));
        }

        public void emit(OpCode.Load opcode, Register val, Register addr, int imm) {
            assert this.type == Type.TEXT;
            items.add(new Instruction.Load(opcode, val, addr, imm));
        }

        public void emit(OpCode.Store opcode, Register val, Register addr, int imm) {
            assert this.type == Type.TEXT;
            items.add(new Instruction.Store(opcode, val, addr, imm));
        }

        public void emit(OpCode.NullaryIntrinsic opcode) {
            assert this.type == Type.TEXT;
            if (opcode == OpCode.PUSH_REGISTERS) {
                emit(Instruction.NullaryIntrinsic.pushRegisters);
            } else if (opcode == OpCode.POP_REGISTERS) {
                emit(Instruction.NullaryIntrinsic.popRegisters);
            } else {
                throw new Error("Cannot emit instruction for ill-understood intrinsic opcode " + opcode);
            }
        }


        public void emit(Label label){
            items.add(label);
        }

        public void emit(Comment comment) {
            items.add(comment);
        }

        public void emit(String comment) {
            items.add(new Comment(comment));
        }

        public void emit(Directive directive) {
            items.add(directive);
        }

        public void print(final PrintWriter writer) {
            switch(type) {
                case DATA : writer.println(".data"); break;
                case TEXT : writer.println(".text"); break;
            }
            items.forEach(item ->
                    item.accept(new AssemblyItemVisitor() {

                        public void visitComment(Comment comment) {
                            writer.println(comment);
                        }
                        public void visitLabel(Label label) {
                            writer.println(label + ":");
                        }

                        public void visitDirective(Directive directive) {
                            writer.println(directive);
                        }

                        public void visitInstruction(Instruction instruction) {
                            writer.println(instruction);
                        }
                    })
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Section section = (Section) o;
            return type == section.type && items.equals(section.items);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, items);
        }
    }


    private Section currSection;

    public final List<Section> sections = new ArrayList<>();

    public void emitSection(Section section) {
        currSection = section;
        sections.add(currSection);
    }

    public Section newSection(Section.Type type) {
        currSection = new Section(type);
        sections.add(currSection);
        return currSection;
    }

    public void print(final PrintWriter writer) {
        sections.forEach(section -> {
                section.print(writer);
                writer.println();
        });

        writer.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssemblyProgram that = (AssemblyProgram) o;
        return sections.equals(that.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
