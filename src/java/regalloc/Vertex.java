package regalloc;

import gen.asm.Register;

public class Vertex {
    public String name;
    public Register.Virtual register;
    public int color = 0;

    public Vertex(Register.Virtual reg) {
        this.name = reg.name;
        this.register = reg;
    }

    @Override
    public boolean equals(Object o) {
        return name == ((Vertex)o).name;
    }
}
