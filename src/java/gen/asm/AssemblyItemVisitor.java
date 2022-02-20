package gen.asm;


public interface AssemblyItemVisitor {
    public void visitLabel(Label label);
    public void visitDirective(Directive directive);
    public void visitInstruction(Instruction instruction);
    public void visitComment(Comment comment);
}
