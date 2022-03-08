package sem;
import java.util.HashMap;
import java.util.Map;

public class Scope {

    Scope outer;
    Map<String, Symbol> symbolTable;

    public Scope(Scope outer) {
        this.outer = outer;
        this.symbolTable = new HashMap<>();
    }

    public Scope() { this(null); }

    public Symbol lookupCurrent(String name) {
        return lookupCurrent(name, false);
    }

    public Symbol lookupCurrent(String name, boolean isStruct) {
        if(isStruct)
            name = "struct." + name;
        return symbolTable.get(name);
    }

    public Symbol lookup(String name) {
        return lookup(name, false);
    }

    public Symbol lookup(String name, boolean isStruct) {
        Symbol symbol = lookupCurrent(name, isStruct);
        if( symbol == null && outer != null)
            symbol = outer.lookup(name, isStruct);
        return symbol;
    }

    public void put(Symbol s) {
        String name = s.name;
        if(s.isStruct)
            name = "struct." + name;
        symbolTable.put(name, s);
    }

}
