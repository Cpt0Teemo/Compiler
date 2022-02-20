package gen.asm;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * Parses {@link AssemblyProgram} instances from textual assembly files. Supports the same subset of MIPS assembly that
 * {@link AssemblyProgram#print(PrintWriter)} outputs.
 */
public final class AssemblyParser {
    /**
     * Reads an {@link AssemblyItem} from an input buffer.
     * @param reader A buffered reader that reads lines of MIPS assembly code.
     * @throws java.io.IOException If an I/O error occurs.
     * @return An assembly item if the line read from the input buffer is nonempty; otherwise, {@code null}.
     */
    public static AssemblyItem readAssemblyItem(final BufferedReader reader) throws java.io.IOException {
        return parseAssemblyItem(reader.readLine());
    }

    /**
     * Parses a string as an {@link AssemblyItem}.
     * @param line A single line of MIPS assembly.
     * @return An assembly item if the line is nonempty; otherwise, {@code null}.
     */
    public static AssemblyItem parseAssemblyItem(String line) {
        // Check if the line looks promising.
        if (line == null || line.isBlank()) {
            return null;
        }

        // Trim the line if it looks like an assembly item.
        line = line.trim();
        if (line.startsWith("#")) {
            // Comments start with a hashtag.
            return new AssemblyItem.Comment(line.substring(1).trim());
        } else if (line.startsWith(".")) {
            // Directives start with a dot.
            return new AssemblyItem.Directive(line.substring(1));
        } else if (line.endsWith(":")) {
            // Label definitions end with a colon and must be identifiers.
            String labelIdentifier = line.substring(0, line.length() - 1);
            if (!isLabel(labelIdentifier)) {
                throw new Error("Expected a label identifier; found " + labelIdentifier);
            }
            return AssemblyItem.Label.get(labelIdentifier);
        } else {
            // Anything else must be an instruction. The general format for instructions is `opcode arg1, arg2, ...`.
            String[] opcodeAndArgs = line.split(" ", 2);
            if (opcodeAndArgs.length > 2) {
                throw new Error("Expected an instruction; found " + line);
            } else if (opcodeAndArgs.length == 1) {
                opcodeAndArgs = new String[] { opcodeAndArgs[0], "" };
            }

            // Split and parse the arguments.
            var args = Arrays.stream(opcodeAndArgs[1].split(",")).map(String::trim).toList();

            // We now branch based on the opcode.
            var op = AssemblyItem.Instruction.OpCode.tryParse(opcodeAndArgs[0].trim());
            if (op.isEmpty())
            {
                throw new Error("Ill-understood opcode " + op);
            }

            var opcode = op.get();
            switch (opcode.kind())
            {
                case LoadAddress:
                    checkArity(args, 2, line);
                    return new AssemblyItem.LoadAddress(parseRegister(args.get(0)), parseLabel(args.get(1)));

                case TypeR:
                    checkArity(args, 3, line);
                    return new AssemblyItem.RInstruction(
                            (AssemblyItem.RInstruction.OpCode)opcode,
                            parseRegister(args.get(0)),
                            parseRegister(args.get(1)),
                            parseRegister(args.get(2)));

                case TypeJ:
                    checkArity(args, 1, line);
                    return new AssemblyItem.Jump(
                            (AssemblyItem.Jump.OpCode)opcode,
                            parseLabel(args.get(0)));

                case TypeIBranch:
                    checkArity(args, 3, line);
                    return new AssemblyItem.Branch(
                            (AssemblyItem.Branch.OpCode)opcode,
                            parseRegister(args.get(0)),
                            parseRegister(args.get(1)),
                            parseLabel(args.get(2)));

                case TypeIArithmetic:
                    checkArity(args, 3, line);
                    return new AssemblyItem.IInstruction(
                            (AssemblyItem.IInstruction.OpCode)opcode,
                            parseRegister(args.get(0)),
                            parseRegister(args.get(1)),
                            parseImmediate(args.get(2)));

                case TypeILoad: {
                    checkArity(args, 2, line);
                    var memOperand = parseMemoryOperand(args.get(1));
                    return new AssemblyItem.Load(
                            (AssemblyItem.Load.OpCode) opcode,
                            parseRegister(args.get(0)),
                            memOperand.getKey(),
                            memOperand.getValue());
                }

                case TypeIStore:
                    checkArity(args, 2, line);
                    var memOperand = parseMemoryOperand(args.get(1));
                    return new AssemblyItem.Store(
                            (AssemblyItem.Store.OpCode) opcode,
                            parseRegister(args.get(0)),
                            memOperand.getKey(),
                            memOperand.getValue());

                case LoadUpperImmediate:
                    checkArity(args, 2, line);
                    return new AssemblyItem.LoadUpperImmediate(
                            parseRegister(args.get(0)),
                            parseImmediate(args.get(1)));

                case Intrinsic:
                    if (opcode == AssemblyItem.Intrinsic.OpCode.pushRegisters) {
                        return AssemblyItem.Intrinsic.pushRegisters;
                    } else {
                        return AssemblyItem.Intrinsic.popRegisters;
                    }

                default:
                    throw new Error();
            }
        }
    }

    private static void checkArity(List<String> args, int expectedArity, String line) {
        if (args.size() != expectedArity) {
            throw new Error("Expected " + expectedArity + " arguments; got " + args.size() + ": " + line);
        }
    }

    private static Register parseRegister(String name) {
        if (!name.codePoints().allMatch(Character::isJavaIdentifierPart)) {
            throw new Error("Expected a register name, got " + name);
        }

        if (name.startsWith("$")) {
            // We found an architectural register.
            var candidate = Register.Arch.allRegisters
                .stream()
                .filter(r -> r.toString().equals(name))
                .findFirst();
            if (candidate.isEmpty()) {
                throw new Error("Expected an architectural register name, got " + name);
            }
            return candidate.get();
        } else {
            // Looks we just ran into a virtual register.
            return Register.Virtual.get(name);
        }
    }

    private static AssemblyItem.Label parseLabel(String name) {
        if (!isLabel(name)) {
            throw new Error("Expected a label, got " + name);
        }
        return AssemblyItem.Label.get(name);
    }

    private static boolean isLabel(String text) {
        return text.length() > 0
            && Character.isJavaIdentifierStart(text.codePointAt(0))
            && text.codePoints().allMatch(Character::isJavaIdentifierPart);
    }

    private static int parseImmediate(String text) {
        return Integer.parseInt(text);
    }

    private static Map.Entry<Register, Integer> parseMemoryOperand(String text) {
        var pieces = text.split("\\(", 2);
        if (pieces.length != 2 || pieces[1].charAt(pieces[1].length() - 1) != ')') {
            throw new Error("Expected a memory operand, got " + text);
        }

        int immediate = parseImmediate(pieces[0]);
        var register = parseRegister(pieces[1].substring(0, pieces[1].length() - 1));
        return new AbstractMap.SimpleEntry<>(register, immediate);
    }
}
