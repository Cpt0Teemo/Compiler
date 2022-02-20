package gen.asm;

import java.util.Objects;

/**
 * A comment in an assembly program. Comments do not change the meaning of a program, but may aid humans in their
 * understanding of programs.
 */
public class Comment extends AssemblyItem {
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
        gen.asm.Comment comment1 = (gen.asm.Comment) o;
        return comment.equals(comment1.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment);
    }
}
