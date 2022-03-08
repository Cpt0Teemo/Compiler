// Author: Jonathan Van der Cruysse

package regalloc;

/**
 * The default register allocation pass.
 */
public final class RegAlloc {
    /**
     * The default register allocation pass.
     */
    // NOTE: To use your custom register allocator, replace `NaiveRegAlloc.INSTANCE` with an instance of your register
    // allocator pass.
    public static final AssemblyPass INSTANCE = NaiveRegAlloc.INSTANCE;
}
