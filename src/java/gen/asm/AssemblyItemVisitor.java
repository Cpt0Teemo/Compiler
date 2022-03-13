// Authors: Christophe Dubach

// DO NOT MODIFY THIS FILE. For technical grading reasons, we may roll back this file to the original version we
// provided. This will overwrite any and all local changes you made, likely breaking your compiler if you made
// changes.
//
// Open a question on Ed if you need additional features that the classes in this file do not support, such as an
// instruction/opcode that is essential but not currently exposed.

package gen.asm;


public interface AssemblyItemVisitor {
    public void visitLabel(Label label);
    public void visitDirective(Directive directive);
    public void visitInstruction(Instruction instruction);
    public void visitComment(Comment comment);
}
