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

        public final List<AssemblyItem> items = new ArrayList<AssemblyItem>();

        public void emit(AssemblyItem.Instruction instruction) {
            assert this.type == Type.TEXT;
            items.add(instruction);
        }

        public void emit(AssemblyItem.CoreArithmetic.OpCode opcode, Register dst, Register src1, Register src2) {
            assert this.type == Type.TEXT;
            items.add(new AssemblyItem.CoreArithmetic(opcode, dst, src1, src2));
        }

        public void emit(AssemblyItem.Branch.OpCode opcode, Register src1, Register src2, AssemblyItem.Label label) {
            assert this.type == Type.TEXT;
            items.add(new AssemblyItem.Branch(opcode, src1, src2, label));
        }

        public void emit(AssemblyItem.ArithmeticWithImmediate.OpCode opcode, Register dst, Register src, int imm) {
            assert this.type == Type.TEXT;
            items.add(new AssemblyItem.ArithmeticWithImmediate(opcode, dst, src, imm));
        }

        public void emitLoadAddress(Register dst, AssemblyItem.Label label) {
            assert this.type == Type.TEXT;
            items.add(new AssemblyItem.LoadAddress(dst, label));
        }

        public void emitLoad(AssemblyItem.Load.OpCode opcode, Register val, Register addr, int imm) {
            assert this.type == Type.TEXT;
            items.add(new AssemblyItem.Load(opcode, val, addr, imm));
        }

        public void emitStore(AssemblyItem.Store.OpCode opcode, Register val, Register addr, int imm) {
            assert this.type == Type.TEXT;
            items.add(new AssemblyItem.Instruction.Store(opcode, val, addr, imm));
        }


        public void emit(AssemblyItem.Label label){
            items.add(label);
        }

        public void emit(AssemblyItem.Comment comment) {
            items.add(comment);
        }

        public void emit(String comment) {
            items.add(new AssemblyItem.Comment(comment));
        }

        public void emit(AssemblyItem.Directive directive) {
            items.add(directive);
        }

        public void print(final PrintWriter writer) {
            switch(type) {
                case DATA : writer.println(".data"); break;
                case TEXT : writer.println(".text"); break;
            }
            items.forEach(item ->
                    item.accept(new AssemblyItemVisitor() {

                        public void visitComment(AssemblyItem.Comment comment) {
                            writer.println(comment);
                        }
                        public void visitLabel(AssemblyItem.Label label) {
                            writer.println(label + ":");
                        }

                        public void visitDirective(AssemblyItem.Directive directive) {
                            writer.println(directive);
                        }

                        public void visitInstruction(AssemblyItem.Instruction instruction) {
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

    public final List<Section> sections = new ArrayList<Section>();

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
