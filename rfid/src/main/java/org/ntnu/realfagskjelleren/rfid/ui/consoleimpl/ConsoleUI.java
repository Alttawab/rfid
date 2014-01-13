package org.ntnu.realfagskjelleren.rfid.ui.consoleimpl;

import org.apache.commons.lang3.StringUtils;
import org.ntnu.realfagskjelleren.rfid.db.model.User;
import org.ntnu.realfagskjelleren.rfid.ui.model.UI;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */
public class ConsoleUI implements UI {

    private Scanner scanner = new Scanner(System.in);

    private final String EXIT_SIGNAL = "***";
                                                         // 0    1    2    3    4    5    6    7    8    9    10   11   12   13   14
    private final char[] boxDrawingCharacters = new char[]{'═', '║', '╔', '╗', '╚', '╝', '╠', '╣', '╦', '╩', '╬', '─', '╟', '╢', '╫'};
    private final int consoleWidth;

    public ConsoleUI(int consoleWidth) {
        this.consoleWidth = consoleWidth;
    }

    @Override
    public void showWelcomeMessage() {
        frameTop();
        printCenterAligned(Arrays.asList(
                "    ____  ______________ ",
                "   / __ \\/ ____/  _/ __ \\",
                "  / /_/ / /_   / // / / /",
                " / _, _/ __/ _/ // /_/ / ",
                "/_/ |_/_/   /___/_____/  ",
                "                         ",
                "  by realfagskjelleren   "
        ));
        frameEmpty();
        frameBottom();
    }

    @Override
    public void showHelp() {
        display("Help...");
    }

    @Override
    public String takeInput(boolean has_user) {
        try {
            if (!has_user) {
                frameTop();
                print("- Input card number or type command. Use /*- for help.");
            }
            else {
                print("- Input amount to withdraw or deposit (+).");
            }
            System.out.print("> ");
            String input = scanner.nextLine();

            return input;
        } catch (NoSuchElementException e) {
            // Occurs when the program is interrupted. Essentially means quit. Returning null will exit.
            return null;
        }
    }

    @Override
    public void rfidRead(User user) {
        List<String> response = Arrays.asList(
                "RFID: " + user.getRfid(),
                "---",
                "Last used: " + user.getLastUsed(),
                "Balance: " + user.getCredit()
        );

        frameEmpty();
        print(table(response));
        frameEmpty();
    }

    @Override
    public void display(String output) {
        display(Arrays.asList(output));
    }

        @Override
    public void display(List<String> output) {
        frameEmpty();
        print(output);
        frameEmpty();
    }

    @Override
    public void endTransaction(String output) {
        endTransaction(Arrays.asList(output));
    }

    @Override
    public void endTransaction(List<String> output) {
        display(output);
        frameBottom();
    }

    @Override
    public void error(String error) {
        print(StringUtils.repeat("!", consoleWidth - 4));
        print(StringUtils.center(error, consoleWidth - 4));
        print(StringUtils.repeat("!", consoleWidth - 4));
    }

    @Override
    public void error(List<String> error) {

    }

    /*
        Some methods to sort out the printing of console specific separation and such
     */

    /* Frame parts - frame meaning the full consoleWidth frame */

    /**
     * Inserts a line into the console that is the top of a square frame.
     */
    private void frameTop() {
        System.out.println(boxDrawingCharacters[2] + StringUtils.repeat(boxDrawingCharacters[0], consoleWidth-2) + boxDrawingCharacters[3]);
    }

    /**
     * Inserts a line into the console that is the bottom of a square frame.
     */
    private void frameBottom() {
        System.out.println(boxDrawingCharacters[4] + StringUtils.repeat(boxDrawingCharacters[0], consoleWidth-2) + boxDrawingCharacters[5]);
    }

    /**
     * Inserts a line into the console with vertical borders and a horizontal double line.
     */
    private void frameMiddle() {
        System.out.println(boxDrawingCharacters[6] + StringUtils.repeat(boxDrawingCharacters[0], consoleWidth-2) + boxDrawingCharacters[7]);
    }

    /**
     * Inserts a line into the console with only vertical borders on either side and no content.
     */
    private void frameEmpty() {
        System.out.println(boxDrawingCharacters[1] + StringUtils.repeat(" ", consoleWidth-2) + boxDrawingCharacters[1]);
    }

    /* General printing methods */

    /**
     * Alias for print with just one string.
     *
     * @param line Line to be printed
     */
    private void print(String line) {
        print(Arrays.asList(line));
    }

    /**
     * Will print a list of strings with normal left alignment.
     *
     * @param lines Lines to be printed
     */
    private void print(List<String> lines) {
        List<String> wrappedLines = wrap(lines);

        for (String line : wrappedLines) {
            System.out.println(leftAlign(line));
        }
    }

    /**
     * Will print a list of strings aligned to the right of the console.
     *
     * @param lines Lines to be printed
     */
    private void printRightAligned(List<String> lines) {
        List<String> wrappedLines = wrap(lines);

        for (String line : wrappedLines) {
            System.out.println(rightAlign(line));
        }
    }

    /**
     * Will print a list of strings to the center of the console.
     *
     * @param lines Lines to be printed
     */
    private void printCenterAligned(List<String> lines) {
        List<String> wrappedLines = wrap(lines);

        for (String line : wrappedLines) {
            System.out.println(center(line));
        }
    }

    /**
     * Checks the length of each string in the list against the set width of the console.
     * Any line will be wrapped to the number of new lines required for it to fit inside the console.
     *
     * Wrapping attempts to use spaces to break lines, but will break words into bits that fit on each
     * line if necessary.
     *
     * @param lines Lines to be wrapped
     * @return Wrapped lines
     */
    protected List<String> wrap(List<String> lines) {
        // There should be room for "║ " on the left and " ║" on the right
        int desiredWidth = consoleWidth - 4;
        List<String> wrappedLines = new ArrayList<>();

        for (String line : lines) {
            if (line.length() > desiredWidth) {

                Pattern regex = Pattern.compile("(.{1,"+desiredWidth+"}(?:\\s|$))|(.{0,"+desiredWidth+"})", Pattern.DOTALL);
                Matcher regexMatcher = regex.matcher(line);
                while (regexMatcher.find()) {
                    String result = regexMatcher.group().trim();
                    if (result.isEmpty()) continue;
                    wrappedLines.add(result);
                }

            }
            else {
                wrappedLines.add(line);
            }
        }

        return wrappedLines;
    }

    /**
     * Aligns a line to the right of the console. Alignment should be done after wrapping.
     *
     * @param line Line to be aligned left
     * @return Left aligned line
     */
    protected String leftAlign(String line) {
        return String.format("%s %s %s", boxDrawingCharacters[1], StringUtils.rightPad(line, consoleWidth - 4, " "), boxDrawingCharacters[1]);
    }

    /**
     * Aligns a line to the right of the console. Alignment should be done after wrapping.
     *
     * @param line Line to be aligned right
     * @return Right aligned line
     */
    protected String rightAlign(String line) {
        return String.format("%s %s %s", boxDrawingCharacters[1], StringUtils.leftPad(line, consoleWidth - 4, " "), boxDrawingCharacters[1]);
    }

    /**
     * Alings a line to the center of the console. Alignment should be done after wrapping.
     *
     * @param line Line to be centered
     * @return Centered line
     */
    protected String center(String line) {
        return String.format("%s %s %s", boxDrawingCharacters[1], StringUtils.center(line, consoleWidth - 4, " "), boxDrawingCharacters[1]);
    }

    /* Making tables */

    /**
     * Generates tables from a list of strings where each string is a row.
     * Columns are separated by pipe ('|') and horizontal lines can be inserted by triple dash ('---').
     *
     * @param lines Rows in the table
     * @return List of strings containing the formatted table
     */
    protected List<String> table(List<String> lines) {
        List<String> table = new ArrayList<>();
        String rowFormat = ""+boxDrawingCharacters[1];

        String[][] data = new String[lines.size()][];
        int[] maxLengths = null;
        int mostCells = 0;

        // Find the widest cell in each row
        for (int i=0; i < lines.size(); i++) {
            String[] row = lines.get(i).split("\\s*\\|\\s*");

            if (row.length > mostCells) {
                mostCells = row.length;
            }
            if (maxLengths == null) {
                maxLengths = new int[row.length];
            }
            else if (row.length > maxLengths.length) {
                maxLengths = Arrays.copyOf(maxLengths, row.length);
            }

            data[i] = row;
            for (int j=0; j < row.length; j++) {
                if (row[j].length() > maxLengths[j]) {
                    maxLengths[j] = row[j].length();
                }
            }
        }

        String tableTop = ""+boxDrawingCharacters[2];
        String tableMiddle = ""+boxDrawingCharacters[12];
        String tableBottom = ""+boxDrawingCharacters[4];

        for (int i=0; i < maxLengths.length; i++) {
            int colWidth = maxLengths[i];
            // |%1$-10s|%2$-10s|%3$-20s|\n
            rowFormat += " %"+(i+1)+"$-"+colWidth+"s "+ boxDrawingCharacters[1];

            tableTop += StringUtils.repeat(boxDrawingCharacters[0], colWidth + 2) + boxDrawingCharacters[8];
            tableMiddle += StringUtils.repeat(boxDrawingCharacters[11], colWidth + 2) + boxDrawingCharacters[14];
            tableBottom += StringUtils.repeat(boxDrawingCharacters[0], colWidth + 2) + boxDrawingCharacters[9];
        }

        tableTop = tableTop.substring(0, tableTop.length()-1) + boxDrawingCharacters[3];
        tableMiddle = tableMiddle.substring(0, tableMiddle.length()-1) + boxDrawingCharacters[13];
        tableBottom = tableBottom.substring(0, tableBottom.length()-1) + boxDrawingCharacters[5];

        table.add(tableTop);
        for (String[] row : data) {
            if (row[0].equals("---")) {
                table.add(tableMiddle);
            }
            else {
                if (row.length < mostCells) {
                    int i = row.length;
                    row = Arrays.copyOf(row, mostCells);
                    for (; i < row.length; i++) {
                        row[i] = "";
                    }
                }
                table.add(String.format(rowFormat, row));
            }
        }
        table.add(tableBottom);

        return table;
    }

    /*
        This method is only supposed to be used for testing!
     */

    protected char[] getBoxDrawingCharacters() {
        return this.boxDrawingCharacters;
    }
}