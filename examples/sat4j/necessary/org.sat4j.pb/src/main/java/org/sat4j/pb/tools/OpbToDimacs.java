package org.sat4j.pb.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.reader.PBInstanceReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.tools.DimacsStringSolver;

/**
 * Read an OPB file and output a CNF file.
 * 
 * Uses binomial encoding for PB constraints.
 * 
 * @author leberre
 *
 */
public class OpbToDimacs {

    private static final Logger LOGGER = Logger.getLogger("org.sat4j.pb");

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : opb2dimacs filename.opb");
            return;
        }
        String opbFileName = args[0];
        assert opbFileName.endsWith("opb");
        String cnfFileName = opbFileName.replace("opb", "cnf");
        IPBSolver solver = new PBAdapter(new DimacsStringSolver());
        PBInstanceReader reader = new PBInstanceReader(solver);
        try {
            reader.parseInstance(opbFileName);
            PrintWriter out = new PrintWriter(new FileWriter(cnfFileName));
            out.print(solver.toString());
            out.close();

        } catch (ParseFormatException e) {
            LOGGER.log(Level.INFO, "Input format error", e);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Input error", e);
        } catch (ContradictionException e) {
            LOGGER.log(Level.INFO, "Formula is UNSAT", e);
        }

    }
}
