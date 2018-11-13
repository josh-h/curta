package im.moonshot.curta;

import im.moonshot.curta.parser.CalculatingInterpreter;
import im.moonshot.curta.parser.GraphvizInterpreter;
import im.moonshot.curta.parser.Parser;
import im.moonshot.curta.parser.SemanticException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command line parser runner.
 */
public class Runner {
    private static final Logger logger = Logger.getLogger(Runner.class.getName());
    private boolean isRender;

    public static void main(String[] args) {
        try {
            var runner = new Runner();
            String expression = runner.processArguments(args);
            if (expression != null) {
                System.out.println(runner.run(expression));
            }

        } catch (ParseException | SemanticException | IOException e) {
            if (e instanceof ParseException)
                System.err.println("Error in character position " + ((ParseException)e).getErrorOffset());

            System.err.println(e.getMessage());

            // Log at FINER rather than SEVERE because users don't need to see our stack
            // Also we have to walk the stack trace by hand, because logger.log(Level, String, Throwable) doesn't
            // log the stack trace.
            logger.finer(e.getClass().getName() + ":");
            for (StackTraceElement ex : e.getStackTrace()) { logger.finer("\t at " + ex); }

            System.exit(1);
        }
    }

    private String program;

    /**
     * Parses the specified expression.
     *
     * @param  expression the expression to parse
     * @return the result of execution
     * @throws ParseException if an error occurred parsing the expression
     * @throws IOException if an error occurred generating the graphviz diagram
     */
    public String run(String expression) throws ParseException, IOException {
        if (expression == null) return "";

        Parser parser = new Parser(expression);

        //
        // TODO: Consider extracting an interface if we build more concrete interpreters
        //
        String result;
        if (isRender) {
            result = new GraphvizInterpreter(parser).interpret();
            if (isMacOs() && hasGraphviz()) {
                generateDiagram(result);
                // Confirm to the user that a diagram was also generated,
                // in case they copy/paste the forward slash denotes a comment and won't break a manually
                // graphviz execution.
                System.out.println("//Diagram generated and open in browser");
            }
        } else {
            result = new CalculatingInterpreter(parser).interpret().toString();
        }

        return result;
    }

    private boolean hasGraphviz() {
        logger.fine("Checking for Graphviz (dot)");
        try {
            new ProcessBuilder("dot", "-V").start();
            logger.fine ("Graphviz found");
            return true;
        } catch (IOException e) {
            logger.fine("Graphviz not found on path");
            return false;
        }
    }

    private boolean isMacOs() {
        logger.fine("Checking for supported operating system");
        String os = System.getProperty("os.name") ;
        if("Mac OS X".equals(os)) {
            logger.fine("Mac OS detected");
            return true;
        } else {
            logger.fine("Unsupported operating system: " + os);
            return false;
        }
    }

    private void generateDiagram(String result) throws IOException {
        logger.fine("Generating diagram");

        try {
            String input = File.createTempFile("curta-", ".txt").getCanonicalPath();
            String svg = File.createTempFile("curta-", ".svg").getCanonicalPath();

            logger.finer("Saving graphviz to: " + input);
            logger.finer("Saving svg to: " + svg);

            PrintWriter out = new PrintWriter(input);
            out.println(result);
            out.close();

            new ProcessBuilder("dot", input, "-o" + svg, "-Tsvg")
                .start()
                .waitFor();
            logger.fine("Generated diagram");

            logger.fine("Launching diagram in OS browser");
            new ProcessBuilder("/usr/bin/open", "-a", "Safari", svg)
                .start();

        } catch (InterruptedException e) {
            logger.severe("Aborting, due to: " + e.getMessage());
        }
    }

    String processArguments(String[] args) {
        if (args.length == 0) args = new String[]{"-h"};

        for(var argument : args) {
            switch (argument) {
                case "--debug":
                case "-d":
                    debug();
                    break;
                case "--verbose":
                case "-v":
                    verbose();
                    break;
                case "-h":
                case "--help":
                    usage();
                    break;
                case "-g":
                case "--graph":
                    logger.fine("Generating graphviz output");
                    isRender = true;
                    break;
                default:
                    // We found the program, stop processing arguments
                    return argument;
            }
        }
        return null;
    }

    private void usage() {
        System.out.println("usage: [-d | --debug] [-v | --verbose] [-g | --graph] program");
    }

    private void debug() {
        if (System.getProperty("java.util.logging.SimpleFormatter.format") == null)
            System.setProperty("java.util.logging.SimpleFormatter.format", "%2$s %5$s%n");

        enableLogging(Level.ALL);
        logger.fine("Debug enabled");
    }

    private void verbose() {
        if (System.getProperty("java.util.logging.SimpleFormatter.format") == null)
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");

        enableLogging(Level.FINE);
        logger.fine("Verbose logging enabled");
    }

    private void enableLogging(Level level) {
        Logger logger = Logger.getLogger("im.moonshot");
        logger.setUseParentHandlers(false);

        ConsoleHandler console = new ConsoleHandler();
        console.setLevel(level);
        logger.setLevel(level);
        logger.addHandler(console);
    }
}
