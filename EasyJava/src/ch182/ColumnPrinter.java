package ch182;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

/**
 * A printer for dividing a stream of text into columns
 * <p>
 * Deliberately obtuse and archaic. C Java.
 *
 * @author Evan Dooner
 * @version 30 Sep 2014
 */
public final class ColumnPrinter {

    private static final char ASCII_ZERO = '0';
    private static final char ASCII_NINE = '9';
    private static final char ASCII_SPACE = ' ';
    private static final char ASCII_TILDE = '~';

    public static void main(String[] args) {

        try (FileReader fr = new FileReader("EasyJava" + File.separator +
                "src" +
                File.separator + "ch182" + File.separator + "c182e-input" +
                ".txt")) {

            int colCount = asciiToInteger(fr);
            int colWidth = asciiToInteger(fr);
            int spaceWidth = asciiToInteger(fr);

            char[][] lines = new char[1][colWidth];
            int row = 0;
            int col = 0;

            int i = fr.read();
            while (i != -1) {
                char c = (char) i;

                /* Only print ASCII characters between ' ' and '~',
                that is spaces, punctuation marks, numbers, and letters */
                if (c >= ASCII_SPACE && c <=
                        ASCII_TILDE) {

                    lines[row][col] = c;
                    col++;

                    // Move to next row when end of column reached
                    if (col == lines[row].length) {
                        row++;
                        col = 0;
                    }
                    // Expand array when full
                    if (row == lines.length) {
                        lines = expandArray(lines);
                    }
                }

                i = fr.read();
            }

            printLines(colCount, spaceWidth, lines, row);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Appends an array of characters to a stringBuilder
     *
     * @param sb
     *         the stringBuilder to append the char array to
     * @param currentLine
     *         the characters to append
     */
    private static void appendCharArray(StringBuilder sb, char[] currentLine) {
        for (char c : currentLine) {
            sb.append(c);
        }
    }

    /**
     * Appends the specifed number of spaces to the stringBuilder
     *
     * @param sb
     *         the stringBuilder to append spaces to
     * @param spaceWidth
     *         the number of spaces to append
     */
    private static void appendSpaces(StringBuilder sb, int spaceWidth) {
        for (int m = 0; m < spaceWidth; m++) {
            sb.append(ASCII_SPACE);
        }
    }

    /**
     * Converts an ASCII representation of a positive integer to an integer
     * value
     *
     * @param reader
     *         the reader containing the ASCII integer representation
     * @return the integer value represented by the ASCII characters
     */
    private static int asciiToInteger(Reader reader) {
        int result;
        try {
            int i = getFirstNumericCharacter(reader);

            int[] digits = new int[1];
            int index = 0;
            while (i >= ASCII_ZERO && i <= ASCII_NINE) {
                digits[index] = i;
                index++;
                if (index == digits.length) {
                    digits = Arrays.copyOf(digits, digits.length * 2);
                }
                i = reader.read();
            }

            result = convertDigitsToInteger(digits, index);

        } catch (IOException e) {
            e.printStackTrace();
            result = -1;
        }

        return result;
    }

    /**
     * Converts an array of single digits to the integer they represent
     * <p>
     * For example, {1, 2, 3, 4} would map to 1,234
     *
     * @param digits
     *         the digits to combine
     * @param digitCount
     *         the number of digits to combine
     * @return the integer represented by the specified digits
     */
    private static int convertDigitsToInteger(int[] digits, int digitCount) {
        int result = 0;
        int digitMultiplier = 1;
        for (int i1 = digitCount - 1; i1 >= 0; i1--) {
            result += (digits[i1] - ASCII_ZERO) * digitMultiplier;
            digitMultiplier *= 10;
        }
        return result;
    }

    /**
     * Doubles the input 2D array and fills the new half with empty 1D arrays
     * <p>
     * Assumes that all subarrays are of equal length
     *
     * @param arrayToExpand
     *         the array to expand
     * @return the expanded array
     */
    private static char[][] expandArray(char[][] arrayToExpand) {
        char[][] result = Arrays.copyOf(arrayToExpand, arrayToExpand.length *
                2);
        // Populate new indices with new char arrays
        int subarrayLength = result[0].length;
        for (int i1 = result.length / 2; i1 < result.length; i1++) {
            result[i1] = new char[subarrayLength];
        }
        return result;
    }

    /**
     * Returns the first positive numeric character in a file stream
     *
     * @param fr
     *         a reader of the stream to read
     * @return the first positive numeric character or -1 if no numeric
     * characters were found before the end of the input stream
     * @throws IOException
     *         if there was an error while attempting to read from the stream
     */
    private static int getFirstNumericCharacter(Reader fr) throws IOException {
        int i = fr.read();
        while (!(i >= ASCII_ZERO && i <= ASCII_NINE) && i != -1) {
            i = fr.read();
        }
        return i;
    }

    /**
     * Prints a single line of text composed of a number of equal-width columns
     *
     * @param colCount
     *         the number of columns to print
     * @param spaceWidth
     *         the number of spaces between each column
     * @param lines
     *         the lines of text to print
     * @param offset
     *         the number of lines per column
     * @param lineNumber
     *         the current line number
     */
    private static void printLine(int colCount, int spaceWidth,
                                  char[][] lines, int offset, int lineNumber) {
        int capacity = colCount * lines[0].length + spaceWidth * (colCount - 1);
        StringBuilder sb = new StringBuilder(capacity);
        for (int k = 0; k < colCount; k++) {
            char[] currentLine = lines[lineNumber + offset * k];
            appendCharArray(sb, currentLine);
            if (k < colCount - 1) {
                appendSpaces(sb, spaceWidth);
            }
        }
        System.out.println(sb);
    }

    /**
     * Prints the rows of text in equal width columns
     *
     * @param colCount
     *         the number of columns in which to divide the text
     * @param spaceWidth
     *         the number of spaces between each column
     * @param lines
     *         the lines of text to print
     * @param rowCount
     *         the number of lines of text to print
     */
    private static void printLines(int colCount, int spaceWidth,
                                   char[][] lines, int rowCount) {
        int offset = rowCount / colCount;
        for (int j = 0; j < offset; j++) {
            printLine(colCount, spaceWidth, lines, offset, j);
        }
    }

}
