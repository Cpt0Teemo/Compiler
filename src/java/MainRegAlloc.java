import gen.CodeGenerator;
import gen.asm.AssemblyParser;
import gen.asm.AssemblyProgram;

import java.io.*;

public final class MainRegAlloc {
    private static final int FILE_NOT_FOUND = 2;
    private static final int IO_EXCEPTION   = 3;

    private static void usage() {
        System.out.println("Usage: java " + MainRegAlloc.class.getSimpleName() + " inputfile outputfile");
        System.exit(-1);
    }

    public static void main(String[] args) {

        if (args.length != 2)
            usage();

        var inputFile = new File(args[0]);
        var outputFile = new File(args[1]);

        AssemblyProgram program;
        try {
            var reader = new FileReader(inputFile);
            program = AssemblyParser.readAssemblyProgram(new BufferedReader(reader));
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + inputFile + " does not exist.");
            System.exit(FILE_NOT_FOUND);
            return;
        } catch (IOException e) {
            System.out.println("An I/O exception occurred when reading " + inputFile + ".");
            System.exit(IO_EXCEPTION);
            return;
        }

        // Create a code generator to acquire the default register allocator. Then apply that register allocator.
        var codegen = new CodeGenerator();
        var programWithoutVRegs = codegen.registerAllocator.apply(program);

        PrintWriter writer;
        try {
            writer = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot write to output file " + outputFile + ".");
            System.exit(FILE_NOT_FOUND);
            return;
        }
        programWithoutVRegs.print(writer);
        writer.close();
    }
}
