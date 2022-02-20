package gen.asm;


import java.util.*;

/**
 * A single item in an {@link AssemblyProgram.Section}. This typically corresponds to a line in the textual
 * representation of an assembly program.
 */
public abstract class AssemblyItem {
    public abstract void accept(AssemblyItemVisitor v);

}
