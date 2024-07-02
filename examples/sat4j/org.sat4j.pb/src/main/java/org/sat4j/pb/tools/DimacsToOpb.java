package org.sat4j.pb.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.OPBStringSolver;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;

/**
 * Read an OPB file and output a CNF file.
 * 
 * Uses binomial encoding for PB constraints.
 * 
 * @author leberre
 *
 */
public class DimacsToOpb {

    private static final Logger LOGGER = Logger.getLogger("org.sat4j.pb");

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : dimacs2opb filename.cnf");
            return;
        }
        String cnfFileName = args[0];
        assert cnfFileName.endsWith("cnf");
        String opbFileName = cnfFileName.replace("cnf", "opb");
        IPBSolver solver = new OPBStringSolver();
        DimacsReader reader = new DimacsReader(solver);
        try {
            reader.parseInstance(cnfFileName);
            PrintWriter out = new PrintWriter(new FileWriter(opbFileName));
            out.println(solver.toString());
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
