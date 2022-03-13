// Author: Jonathan Van der Cruysse

package regalloc;

/**
 * The default register allocation pass.
 */
public final class RegAlloc {
    private RegAlloc() { }

    /**
     * The default register allocation pass.
     *
     * @implNote To use your custom register allocator, replace {@code NaiveRegAlloc.INSTANCE} with an instance of your
     * register allocator pass.
     */
    public static final AssemblyPass INSTANCE = NaiveRegAlloc.INSTANCE;
}
