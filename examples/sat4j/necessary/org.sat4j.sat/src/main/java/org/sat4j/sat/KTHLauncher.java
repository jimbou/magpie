package org.sat4j.sat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.sat4j.minisat.core.ConflictTimer;
import org.sat4j.minisat.core.LearnedConstraintsDeletionStrategy;
import org.sat4j.minisat.learning.MiniSATLearning;
import org.sat4j.minisat.orders.NaturalStaticOrder;
import org.sat4j.minisat.orders.VarOrderHeap;
import org.sat4j.minisat.restarts.Glucose21Restarts;
import org.sat4j.minisat.restarts.LubyRestarts;
import org.sat4j.minisat.restarts.NoRestarts;
import org.sat4j.pb.IPBSolver;
import org.sat4j.pb.OptToPBSATAdapter;
import org.sat4j.pb.PBSolverHandle;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.SolverFactory;
import org.sat4j.pb.constraints.PBMaxClauseCardConstrDataStructure;
import org.sat4j.pb.constraints.pb.AutoDivisionStrategy;
import org.sat4j.pb.constraints.pb.ConflictMapDivideByPivot;
import org.sat4j.pb.constraints.pb.ConflictMapReduceByGCD;
import org.sat4j.pb.constraints.pb.ConflictMapReduceByPowersOf2;
import org.sat4j.pb.constraints.pb.ConflictMapReduceToCard;
import org.sat4j.pb.constraints.pb.ConflictMapReduceToClause;
import org.sat4j.pb.constraints.pb.ConflictMapRounding;
import org.sat4j.pb.constraints.pb.ConflictMapWeakenReason;
import org.sat4j.pb.constraints.pb.ConflictMapWeakenToClash;
import org.sat4j.pb.constraints.pb.IWeakeningStrategy;
import org.sat4j.pb.constraints.pb.PostProcessToCard;
import org.sat4j.pb.constraints.pb.PostProcessToClause;
import org.sat4j.pb.constraints.pb.PreProcessReduceConflict;
import org.sat4j.pb.constraints.pb.SkipStrategy;
import org.sat4j.pb.core.PBSolverCP;
import org.sat4j.pb.lcds.PBActivityLCDS;
import org.sat4j.pb.lcds.PBGlucoseLCDS;
import org.sat4j.pb.orders.BumpStrategy;
import org.sat4j.pb.orders.Bumper;
import org.sat4j.pb.orders.BumperEffective;
import org.sat4j.pb.orders.BumperEffectiveAndPropagated;
import org.sat4j.pb.orders.DoubleBumpClashingLiteralsDecorator;
import org.sat4j.pb.orders.IBumper;
import org.sat4j.pb.reader.OPBReader2012;
import org.sat4j.pb.restarts.GrowingCoefficientRestarts;
import org.sat4j.pb.tools.InprocCardConstrLearningSolver;
import org.sat4j.pb.tools.PreprocCardConstrLearningSolver;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.DotSearchTracing;

/**
 * A specific launcher for experimenting various settings in the PB solvers with
 * Jakob Nordstrom's group @ KTH.
 * 
 * @author leberre
 * @since 2.3.6
 */
public class KTHLauncher {

    private static final PrintWriter out = new PrintWriter(System.out);
    
    public static Options createCLIOptions() {
        Options options = new Options();
        options.addOption("cl", "coeflim", true,
                "coefficient limit triggering division");
        options.addOption("cls", "coeflim-small", true,
                "coefficient limit for divided constraints");
        options.addOption("fb", "find-best-divisor-when-dividing-for-overflow",
                true, "rounding coefficient. Legal values are true or false.");
        options.addOption("wr", "when-resolve", true,
                "behavior when performing conflict analysis. Legal values are skip, weaken or always.");
        options.addOption("rr", "round-reason", true,
                "Rounding strategy during conflict analysis. Legal values are divide-v1, divide-unless-equal, divide-unless-divisor, round-to-gcd, never, weaken-and-divide, weaken, weaken-to-clash, or partial-weaken-and-divide.");
        options.addOption("rwp", "rounding-weaken-priority", true,
                "Which literals are removed to ensure conflicting constraints. Legal values are any, satisfied, unassigned");
        options.addOption("tlc", "type-of-learned-constraint", true,
                "Type of constraints learned. Legal values are general-linear, cardinality, clause");
        options.addOption("wni", "weaken-nonimplied", true,
                "Remove literals that are not implied/propagated by the assignment at the backjump level. Legal values are true or false.");
        options.addOption("division", "division-strategy", true,
                "Detect if all the coefficients can be divided by a common number. Legal values are two, gcd or none.");
        options.addOption("dot", "dot-output", true,
                "Output the search as a dot file");
        options.addOption("detect", "detect-cards", true,
                "Detect cardinality constraints from original constraints. Legal value are never, preproc, inproc, lazy.");
        options.addOption("natural", "natural-static-order", false,
                "Use a static order for decisions, using the natural order of the variables, from 1 to n.");
        options.addOption("autodiv", "auto-division", false,
                "Apply division automatically when a common factor is identified.");
        options.addOption("pc", "preprocess-conflict", true,
                "Preprocessing strategy to apply on the conflict before resolving. Legal values are none, reduce-not-falsified or reduce");
        options.addOption("r", "restart-strategy", true,
                "Restart strategy to apply, among luby, picosat, lbd, size");
        options.addOption("b", "bump-strategy", true,
                "Bumping strategy to apply, among one, degree, coefficient, ratio");
        options.addOption("lcds", "deletion-strategy", true,
                "Learned constraint deletion strategy, among lbd, assigned, unassigned-same, unassigned-different, effective,, degree");
        options.addOption("br", "bumper", true,
                "Literal bumper, among any, assigned and falsified");
        options.addOption("db", "double-bump-clashing", false,
                "Whether clashing literal should be doubly bumped");
        Option op = options.getOption("coeflim");
        op.setArgName("limit");
        op = options.getOption("coeflim-small");
        op.setArgName("limit");
        op = options.getOption("when-resolve");
        op.setArgName("strategy");
        op = options.getOption("find-best-divisor-when-dividing-for-overflow");
        op.setArgName("boolean");
        op = options.getOption("round-reason");
        op.setArgName("strategy");
        op = options.getOption("rounding-weaken-priority");
        op.setArgName("strategy");
        op = options.getOption("type-of-learned-constraint");
        op.setArgName("type");
        op = options.getOption("weaken-nonimplied");
        op.setArgName("boolean");
        op = options.getOption("dot-output");
        op.setArgName("filename");
        op = options.getOption("detect-cards");
        op.setArgName("strategy");
        op = options.getOption("preprocess-conflict");
        op.setArgName("strategy");
        op = options.getOption("restart-strategy");
        op.setArgName("strategy");
        op = options.getOption("bump-strategy");
        op.setArgName("strategy");
        op = options.getOption("bumper");
        op.setArgName("strategy");
        op = options.getOption("deletion-strategy");
        op.setArgName("strategy");
        return options;
    }

    public static void log(String str) {
        log(str, "c ");
    }

    public static void log(String str, String prefix) {
        System.out.println(prefix + str);
    }

    public static void main(String[] args) {
        log("SAT4J PB solver for KTH experiments");
        URL url = KTHLauncher.class.getResource("/sat4j.version");
        if (url != null) {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()))) {
                log("version " + in.readLine()); //$NON-NLS-1$
            } catch (IOException e) {
                log("c ERROR: " + e.getMessage());
            }
        }
        CommandLineParser parser = new DefaultParser();
        Options options = createCLIOptions();
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("KTHLauncher", options);
            return;
        }
        try {
            CommandLine line = parser.parse(options, args);

            PBSolverCP cpsolver = SolverFactory.newCuttingPlanes();
            cpsolver.setNoRemove(true);
            cpsolver.setSkipAllow(SkipStrategy.NO_SKIP);
            IPBSolver pbsolver = cpsolver;
            if (line.hasOption("detect-cards")) {
                String value = line.getOptionValue("detect-cards");
                if ("never".equals(value)) {
                    // by default
                } else if ("preproc".equals(value)) {
                    pbsolver = new PreprocCardConstrLearningSolver<IPBSolver>(
                            cpsolver);
                } else if ("inproc".equals(value)) {
                    InprocCardConstrLearningSolver solver = new InprocCardConstrLearningSolver(
                            new MiniSATLearning<>(),
                            new PBMaxClauseCardConstrDataStructure(),
                            new VarOrderHeap(), true, SkipStrategy.NO_SKIP);
                    solver.setDetectCardFromAllConstraintsInCflAnalysis(true);
                    cpsolver = solver;
                } else if ("lazy".equals(value)) {
                    cpsolver = new InprocCardConstrLearningSolver(
                            new MiniSATLearning<>(),
                            new PBMaxClauseCardConstrDataStructure(),
                            new VarOrderHeap(), true, SkipStrategy.NO_SKIP);
                } else {
                    log(value
                            + " is not a supported value for option detect-cards");
                    return;
                }

            }
            if (line.hasOption("division-strategy")) {
                String value = line.getOptionValue("division-strategy")
                        .toLowerCase();
                if ("two".equals(value)) {
                    cpsolver.setConflictFactory(
                            ConflictMapReduceByPowersOf2.factory());
                } else if ("gcd".equals(value)) {
                    cpsolver.setConflictFactory(
                            ConflictMapReduceByGCD.factory());
                } else if ("none".equals(value)) {
                    // do nothing
                } else {
                    log(value
                            + " is not a supported value for option division");
                    return;
                }
            }

            if (line.hasOption("type-of-learned-constraint")) {
                String value = line
                        .getOptionValue("type-of-learned-constraint");
                if ("general-linear".equals(value)) {
                    // default case, do nothing
                } else if ("cardinality".equals(value)) {
                    cpsolver.setPostprocess(PostProcessToCard.instance());
                } else if ("clause".equals(value)) {
                    cpsolver.setPostprocess(PostProcessToClause.instance());
                } else {
                    log(value
                            + " is not a supported value for option type-of-learned-constraint");
                    return;
                }
            }

            if (line.hasOption("preprocess-conflict")) {
                String value = line.getOptionValue("preprocess-conflict");
                if ("none".equals(value)) {
                    // default case, do nothing
                } else if ("reduce".equals(value)) {
                    cpsolver.setPreprocess(
                            PreProcessReduceConflict.instanceWithFalsified());
                } else if ("reduce-not-falsified".equals(value)) {
                    cpsolver.setPreprocess(PreProcessReduceConflict
                            .instanceWithoutFalsified());
                } else {
                    log(value
                            + " is not a supported value for option preprocess-conflict");
                    return;
                }
            }
            if (line.hasOption("when-resolve")) {
                String value = line.getOptionValue("when-resolve");
                if ("always".equals(value)) {
                    cpsolver.setSkipAllow(SkipStrategy.NO_SKIP);
                } else if ("skip".equals(value)) {
                    cpsolver.setSkipAllow(SkipStrategy.SKIP);
                } else if ("weaken".equals(value)) {
                    cpsolver.setSkipAllow(SkipStrategy.WEAKEN_AND_SKIP);
                } else {
                    log(value
                            + " is not a supported value for option when-resolve");
                    return;
                }
            }
            if (line.hasOption("round-reason")) {
                String value = line.getOptionValue("round-reason");
                if ("never".equals(value)) {
                    // by default
                } else if ("clausal".equals(value)) {
                    cpsolver.setConflictFactory(
                            ConflictMapReduceToClause.factory());
                } else if ("cardinality".equals(value)) {
                    cpsolver.setConflictFactory(
                            ConflictMapReduceToCard.factory());
                } else if ("divide-v1".equals(value)) {
                    cpsolver.setConflictFactory(ConflictMapRounding.factory());
                } else if ("weaken".equals(value)) {
                    cpsolver.setConflictFactory(
                            ConflictMapWeakenReason.factory());
                } else if ("weaken-to-clash".equals(value)) {
                    cpsolver.setConflictFactory(
                            ConflictMapWeakenToClash.factory());
                } else if ("weaken-and-divide".equals(value)) {
                    cpsolver.setConflictFactory(ConflictMapDivideByPivot
                            .fullWeakeningOnReasonFactory());
                } else if ("partial-weaken-and-divide".equals(value)) {
                    cpsolver.setConflictFactory(ConflictMapDivideByPivot
                            .partialWeakeningOnReasonFactory());
                } else if ("weaken-and-divide-both".equals(value)) {
                    cpsolver.setConflictFactory(ConflictMapDivideByPivot
                            .fullWeakeningOnBothFactory());
                } else if ("partial-weaken-and-divide-both".equals(value)) {
                    cpsolver.setConflictFactory(ConflictMapDivideByPivot
                            .partialWeakeningOnBothFactory());
                } else if ("weaken-and-divide-conflict".equals(value)) {
                    cpsolver.setConflictFactory(ConflictMapDivideByPivot
                            .fullWeakeningOnConflictFactory());
                } else if ("partial-weaken-and-divide-conflict".equals(value)) {
                    cpsolver.setConflictFactory(ConflictMapDivideByPivot
                            .partialWeakeningOnConflictFactory());
                } else {
                    // "divide-unless-equal":
                    // "divide-unless-divisor":
                    // "round-to-gcd":
                    log(value
                            + " is not a supported value for option round-reason");
                    return;
                }
            }
            // validate that block-size has been set
            if (line.hasOption("coeflim")) {
                log("coeflim option is unsupported at the moment");
                return;
            }

            if (line.hasOption("coeflim-small")) {
                log("coeflim-small option is unsupported at the moment");
                return;
            }
            if (line.hasOption(
                    "find-best-divisor-when-dividing-for-overflow")) {
                log("find-best-divisor-when-dividing-for-overflow option is unsupported at the moment");
                return;
            }
            if (line.hasOption("rounding-weaken-priority")) {
                String value = line.getOptionValue("rounding-weaken-priority");

                if ("unassigned".equals(value)) {
                    cpsolver.setWeakeningStrategy(
                            IWeakeningStrategy.UNASSIGNED_FIRST);
                } else if ("satisfied".equals(value)) {
                    cpsolver.setWeakeningStrategy(
                            IWeakeningStrategy.SATISFIED_FIRST);
                } else if ("any".equals(value)) {
                    cpsolver.setWeakeningStrategy(IWeakeningStrategy.ANY);
                } else {
                    log(value
                            + " is not a supported value for option rounding-weaken-priority");
                    return;
                }

            }
            if (line.hasOption("weaken-nonimplied")) {
                String value = line.getOptionValue("weaken-nonimplied");

                if (!"false".equals(value)) {
                    // "true" or "round":
                    log(value
                            + " is not a supported value for option weaken-nonimplied");
                    return;
                }
            }
            if (line.hasOption("natural")) {
                cpsolver.setOrder(new NaturalStaticOrder());
            }
            if (line.hasOption("autodiv")) {
                cpsolver.setAutoDivisionStrategy(AutoDivisionStrategy.ENABLED);
            }

            if (line.hasOption("restart-strategy")) {
                String value = line.getOptionValue("restart-strategy");

                if ("picosat".equals(value)) {
                    // This is the default.

                } else if ("luby".equals(value)) {
                    cpsolver.setRestartStrategy(new LubyRestarts());

                } else if ("lbd".equals(value)) {
                    cpsolver.setRestartStrategy(new Glucose21Restarts());

                } else if ("size".equals(value)) {
                    cpsolver.setRestartStrategy(
                            new GrowingCoefficientRestarts());

                } else if ("none".equals(value)) {
                    cpsolver.setRestartStrategy(new NoRestarts());

                } else {
                    log(value
                            + " is not a supported value for option restart-strategy");
                    return;
                }
            }

            if (line.hasOption("bump-strategy")) {
                String value = line.getOptionValue("bump-strategy");

                if ("one".equals(value)) {
                    cpsolver.setBumpStrategy(BumpStrategy.ALWAYS_ONE);

                } else if ("degree".equals(value)) {
                    cpsolver.setBumpStrategy(BumpStrategy.DEGREE);

                } else if ("coefficient".equals(value)) {
                    cpsolver.setBumpStrategy(BumpStrategy.COEFFICIENT);

                } else if ("ratio-cd".equals(value)) {
                    cpsolver.setBumpStrategy(BumpStrategy.RATIO_CD);

                } else if ("ratio-dc".equals(value)) {
                    cpsolver.setBumpStrategy(BumpStrategy.RATIO_DC);

                } else {
                    log(value
                            + " is not a supported value for option bump-strategy");
                    return;
                }
            }

            if (line.hasOption("bumper")) {
                String value = line.getOptionValue("bumper");
                IBumper bumper = null;

                if ("any".equals(value)) {
                    bumper = Bumper.ANY;

                } else if ("assigned".equals(value)) {
                    bumper = Bumper.ASSIGNED;

                } else if ("falsified".equals(value)) {
                    bumper = Bumper.FALSIFIED;

                } else if ("falsified-and-propagated".equals(value)) {
                    bumper = Bumper.FALSIFIED_AND_PROPAGATED;

                } else if ("effective".equals(value)) {
                    bumper = new BumperEffective();

                } else if ("effective-and-propagated".equals(value)) {
                    bumper = new BumperEffectiveAndPropagated();

                } else {
                    log(value
                            + " is not a supported value for option bump-strategy");
                    return;
                }

                if (line.hasOption("double-bump-clashing")) {
                    bumper = new DoubleBumpClashingLiteralsDecorator(bumper);
                }

                cpsolver.setBumper(bumper);
            }

            if (line.hasOption("deletion-strategy")) {
                String value = line.getOptionValue("deletion-strategy");
                ConflictTimer timer = cpsolver.lbdBased.getTimer();
                if ("activity".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = new PBActivityLCDS(
                            cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("assigned".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newIgnoreUnassigned(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("unassigned-same".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newUnassignedSame(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("unassigned-different".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newUnassignedDifferent(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("falsified".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newFalsifiedOnly(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("effective".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newEffectiveOnly(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("degree".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newDegree(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("degree-size".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newDegreeSize(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("ratio".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newRatio(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else if ("slack".equals(value)) {
                    LearnedConstraintsDeletionStrategy lcds = PBGlucoseLCDS
                            .newSlack(cpsolver, timer);
                    cpsolver.setLearnedConstraintsDeletionStrategy(lcds);

                } else {
                    log(value
                            + " is not a supported value for option deletion-strategy");
                    return;
                }
            }
            System.out.println(pbsolver.toString("c "));
            String[] leftArgs = line.getArgs();
            if (leftArgs.length == 0) {
                log("Missing filename");
                return;
            }
            String filename = leftArgs[leftArgs.length - 1];
            PBSolverHandle handle = new PBSolverHandle(
                    new PseudoOptDecorator(pbsolver));
            OPBReader2012 reader = new OPBReader2012(handle);
            final OptToPBSATAdapter optimizer = new OptToPBSATAdapter(handle);
            final Thread shutdownHook = new Thread() {
                @Override
                public void run() {
                    // stop the solver before displaying solutions

                    optimizer.expireTimeout();
                    optimizer.printStat(out);
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            try {
                reader.parseInstance(filename);
                if (line.hasOption("dot-output")) {
                    String dotfilename = line.getOptionValue("dot-output");
                    if (dotfilename != null) {
                        DotSearchTracing<String> dotTracing = new DotSearchTracing<>(
                                dotfilename);
                        cpsolver.setSearchListener(dotTracing);
                        dotTracing.setMapping(reader.getMapping());
                    }
                }
                if (optimizer.isSatisfiable()) {
                    if (optimizer.isOptimal()) {
                        log("OPTIMUM FOUND", "s ");
                    } else {
                        log("SATISFIABLE", "s ");
                    }
                    out.print("v ");
                    reader.decode(optimizer.model(),out);
                    out.println(" 0");
                } else {
                    log("UNSATISFIABLE", "s ");
                }
            } catch (TimeoutException e) {
                log("UNKNOWN", "s ");
            } catch (ContradictionException e) {
                log("UNSATISFIABLE", "s ");
            } catch (Exception e) {
                log("Unexpected Exception:" + e.getMessage());
            }
        } catch (ParseException exp) {
            log("Unexpected parsing exception:" + exp.getMessage());
        }
    }
}
