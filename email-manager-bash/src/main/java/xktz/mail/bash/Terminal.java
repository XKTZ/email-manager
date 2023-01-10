package xktz.mail.bash;

import xktz.xkamework.annotation.Autowired;
import xktz.xkamework.annotation.Component;
import xktz.xkamework.annotation.Qualifier;
import xktz.xkamework.annotation.Value;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Terminal object
 *
 * @author XKTZ
 * @date 2022-06-18
 */
@Component
public class Terminal {

    private static final String ELLIPSIS = "...";

    private int width;

    @Autowired
    @Qualifier("terminalPrinter")
    private PrintStream printer;

    @Autowired
    @Qualifier("terminalErrorPrinter")
    private PrintStream errorPrinter;

    @Autowired
    @Qualifier("terminalScanner")
    private Scanner scanner;

    private String divider;

    @Autowired
    public Terminal(@Qualifier("terminalWidth") int width) {
        this.width = width;
        divider = new String(new char[width]).replace('\0', '-');
    }

    /**
     * print some objects, separated by space
     *
     * @param objs objs
     */
    public void print(Object... objs) {
        String output = String.join(" ", Arrays.stream(objs).map(obj -> obj == null ? "" : obj.toString()).toArray(String[]::new));
        for (int i = 0, N = output.length(); i < N; i += width) {
            printer.print(output.substring(i, Math.min(i + width, N)));
            if (i + width < N) {
                printer.println();
            }
        }
    }

    /**
     * Print objects with line
     *
     * @param objs objs
     */
    public void println(Object... objs) {
        print(objs);
        print('\n');
    }

    /**
     * Scan a line
     *
     * @return a line
     */
    public String nextLine() {
        return scanner.nextLine();
    }

    /**
     * Scan a line with output
     *
     * @param s output
     * @return a line
     */
    public String nextLine(String s) {
        print(s);
        return scanner.nextLine();
    }

    /**
     * Scan an integer
     *
     * @return an integer
     */
    public Integer nextInt() {
        return scanner.nextInt();
    }

    /**
     * Scan an integer with output
     *
     * @param s output
     * @return integer
     */
    public Integer nextInt(String s) {
        print(s);
        return scanner.nextInt();
    }

    /**
     * Print a divider
     */
    public void printDivider() {
        println(divider);
    }

    /**
     * Get width
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Ellipsis a word with specific length
     *
     * @param s string
     * @param l length
     * @return ellipsised
     */
    public static String ellipsis(String s, int l) {
        if (s.length() >= l - 3) {
            return s.substring(0, l - 3) + ELLIPSIS;
        } else {
            return s;
        }
    }
}
