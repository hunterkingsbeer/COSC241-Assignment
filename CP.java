package week11;

import java.util.Arrays;
import java.util.Scanner;

/**
 * A class to manipulate and gather information on a deck of cards.
 *
 * COSC241 - Assignment 1.
 *
 * @author Hunter Kingsbeer and Jack Heikell
 */
class CP implements CardPile {

    /** Original 1D cards array. */
    private int[] cards;

    /** Array of spec's algorithms for command-line purposes. */
    private static final String[] TRANSFORMATIONS
            = {"TL", "BL", "TR", "BR", "LT", "LB", "RT", "RB"};

    /**
     * Main method handles user input.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        CP newCards = new CP();
        switch (args.length) {
            case 0: //Read from file/stdin (no commandline args)
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    String[] arg = scanner.nextLine().split(" ");
                    int r = 0;
                    String spec = "";
                    if (arg.length > 1) { //make sure input is correct.
                        if (!(newCards.errorCheck("command", arg[1]))) {
                            r = Integer.parseInt(arg[1]);
                            if (arg.length > 2) {
                                spec = arg[2];
                            }
                        }
                    }
                    newCards.fileCommand(arg, r, spec);
                }
                break;
            case 1: //Handling of a singular command-line argument.
                System.out.println("This program doesn't allow 1 argument");
                break;
            case 2: //Transform cards of length (args[0]) by rows (args[1])
                try {
                    newCards.load(Integer.parseInt(args[0]));
                    for (String trans : TRANSFORMATIONS) {
                        System.out.println(trans + " "
                                + newCards.count(Integer.parseInt(args[1]),
                                trans));
                    }
                } catch (NumberFormatException e) {
                    throw new CardPileException("You entered: " + args[0] + " "
                            + args[1] + ". Please enter two numbers.");
                }
                break;
            default: //Handling for 3+ commandline arguments.
                try {
                    newCards.load(Integer.parseInt(args[0]));
                    newCards.arrayWhitespace(newCards.getPile());
                    for (int i = 2; i < args.length; i++) {
                        newCards.transform(Integer.parseInt(args[1]), args[i]);
                        newCards.arrayWhitespace(newCards.getPile());
                    }
                } catch (NumberFormatException e) {
                    throw new CardPileException("Incorrect input. " +
                            "Please follow correct format: 'int int spec...");
                }
        }
    }

    /**
     * Loads a copy of the given array as the deck of cards.
     *
     * @param cards Array to load.
     */
    public void load(int[] cards) {
        if (errorCheck("load", cards.length + "")) {
            throw new CardPileException("Array is empty. " +
                    "Please load array with items.");
        }
        this.cards = new int[cards.length];
        System.arraycopy(cards, 0, this.cards, 0, cards.length);
    }

    /**
     * Creates an array from 1-n to be loaded as the deck of cards.
     *
     * @param n Highest value of card in the new deck.
     * @throws CardPileException when input int isn't a positive number.
     */
    public void load(int n) {
        if (errorCheck("load", "" + n)) {
            throw new CardPileException("You entered: " + n
                    + ". Please enter a positive integer.");
        }
        cards = new int[n]; //Assign correct length.
        for (int i = 0; i < n; i++) { //Loads 1-n integers into cards.
            cards[i] = i + 1;
        }
    }

    /**
     * Returns a copy of the array of cards.
     *
     * @return Copy of the array of cards.
     * @throws CardPileException when our deck of cards is null or has 0 cards.
     */
    public int[] getPile() {
        if (cards == null || cards.length == 0) {
            throw new CardPileException("No card pile found. " +
                    "Please load cards.");
        }
        int[] cardsArr = new int[cards.length];
        System.arraycopy(cards, 0, cardsArr, 0, cards.length);
        return cardsArr;
    }

    /**
     * Transforms the pile of cards given row length, and method.
     *
     * @param rowLength Row length for the transformation to be enacted on.
     * @param spec      Transformation method to carry out.
     * @throws CardPileException when spec isn't using correct letters.
     * @throws CardPileException when rowLength can't be used with deck size.
     */
    public void transform(int rowLength, String spec) {
        int colLength = cards.length / rowLength;
        int[][] cards2d = convertTo2d(colLength, rowLength, cards);
        int[][] cards2dCopy = new int[colLength][rowLength];

        if (errorCheck("spec", spec)) {
            throw new CardPileException("You entered: " + spec
                    + ". Please enter a 2 letter combo of L &| R &| T &| B.");
        } else if (errorCheck("row", rowLength + "")) {
            throw new CardPileException("Your row length of '" + rowLength
                    + "', is invalid.");
        }

        int colCount; //keeps track of what column we are using.
        int cardCount; //keeps track of what card in the column we are altering.

        //LT,RB,LB,RT alter using rows, whereas BL,TR,BR,TL alter using cols.
        switch (spec) {
            case "LT":
            case "RB": //Left top and right bottom (reverse of LT)
                for (int col = 0; col < colLength; col++) {
                    if (rowLength >= 0) {
                        System.arraycopy(cards2d[col], 0, cards2dCopy[col],
                                0, rowLength);
                    }
                }
                break;
            case "LB":
            case "RT": //Left bottom and right top (RT is reverse of LB)
                for (int col = 0; col < colLength; col++) {
                    if (rowLength >= 0) {
                        System.arraycopy(cards2d[colLength - 1 - col],
                                0, cards2dCopy[col], 0, rowLength);
                    }
                }
                break;
            case "BL":
            case "TR": //Bottom left and top right (TR is reverse of BL)
                cardCount = colLength - 1; //start at bottom (B) of cards
                colCount = 0; //start at left (L) of cards
                for (int col = 0; col < colLength; col++) {
                    for (int row = 0; row < rowLength; row++) {
                        cards2dCopy[col][row] = cards2d[cardCount][colCount];
                        cardCount--;
                        if (cardCount == -1) {
                            cardCount = colLength - 1;
                            colCount++; //move to right
                        }
                    }
                }
                break;
            case "BR":
            case "TL": //Bottom right and top left (TL is reverse of BR)
                cardCount = colLength - 1; //start at bottom (B) of cards
                colCount = rowLength - 1; //start at right (R) of cards
                for (int col = 0; col < colLength; col++) {
                    for (int row = 0; row < rowLength; row++) {
                        cards2dCopy[col][row] = cards2d[cardCount][colCount];
                        cardCount--;
                        if (cardCount == -1) {
                            cardCount = colLength - 1;
                            colCount--; //move to left
                        }
                    }
                }
                break;
        }
        finalizeDeck(cards2dCopy, (spec.equals("RB") || spec.equals("RT")
                || spec.equals("TR") || spec.equals("TL")));
    }

    /**
     * Counts transformations of cards until they're in their original order.
     *
     * @param rowLength Row length for the transformation to be enacted on.
     * @param spec Transformation method to carry out.
     * @return Minimum transformations until cards are in their original order.
     */
    public int count(int rowLength, String spec) {

        int[] copy = new int[cards.length]; //copy cards for comparison.
        System.arraycopy(cards, 0, copy, 0, cards.length);

        int tries = 0;
        while (true) {
            int count = 0;
            transform(rowLength, spec);
            for (int i = 0; i < cards.length - 1; i++) { //check matching cards.
                if (cards[i] == copy[i]) {
                    count++;
                }
            }
            tries++;
            if (count == cards.length - 1) { //once cards all match, break loop.
                break;
            }
        }
        return tries;
    }

    //HELPER METHODS BELOW

    /**
     * Performs the commands for the file input (args length 0).
     *
     * @param arg  our line from the file to read.
     * @param r    row length.
     * @param spec sorting specification.
     */
    private void fileCommand(String[] arg, int r, String spec) {
        switch (arg[0]) {
            case "c":
                if (errorCheck("", "") ||
                        errorCheck("command", arg[1]) ||
                        errorCheck("row", r + "") ||
                        errorCheck("spec", spec)) {
                    break;
                }
                System.out.println(count(r, spec));
                break;
            case "l":
                if (errorCheck("command", arg[1]) ||
                        Integer.parseInt(arg[1]) <= 0) {
                    break;
                }
                load(r);
                break;
            case "L":
                if (errorCheck("command", arg[1])) {
                    break;
                }
                int[] arr = new int[arg.length - 1];
                for (int i = 1; i < arg.length; i++) {
                    if (Character.isDigit(arg[i].charAt(0))) { //Check if int.
                        arr[i - 1] = Integer.parseInt(arg[i]);
                    } else {
                        break;
                    }
                }
                load(arr);
                break;
            case "p":
                if (errorCheck("", "") ||
                        errorCheck("load",
                                getPile().length + "")) {
                    break;
                }
                arrayWhitespace(getPile());
                break;
            case "P":
                if (errorCheck("", "") ||
                        errorCheck("row", r + "")) {
                    break;
                }
                arr = getPile();
                int count = 0;
                for (int col = 0; col < arr.length / r; col++) {
                    for (int row = 0; row < r; row++) {
                        System.out.print(arr[count] + " ");
                        count++;
                    }
                    System.out.println();
                }
                break;
            case "t":
                if (errorCheck("", "") ||
                        errorCheck("commands", arg[1]) ||
                        errorCheck("row", r + "") ||
                        errorCheck("spec", spec)) {
                    break;
                }
                transform(r, spec);
                break;
            default:
                break;
        }
    }

    /**
     * Error checks for most possible errors.
     *
     * @param type  Error to check for.
     *              "spec" Check if valid transformation specification.
     *              "row" Check if row amount can be used with deck size.
     *              "load" Check if loading less than or equal to zero.
     *              "command" Check if command is correct format.
     *              ""  Blank checks is deck is null and 0 in length.
     * @param value Object to check.
     * @return Returns true if an error is detected, false otherwise.
     */
    private boolean errorCheck(String type, String value) {
        int numValue = 0;
        if (type.equals("row") || type.equals("load")) {
            numValue = Integer.parseInt(value);
        }
        switch (type) {
            case "spec":
                if (!((value.contains("L") && (value.contains("T")
                        || value.contains("B"))) || (value.contains("R")
                        && (value.contains("T") || value.contains("B"))))) {
                    return true;
                }
                break;
            case "row":
                if (numValue == 0 ||
                        (cards.length % numValue) == 1 ||
                        numValue > cards.length) {
                    return true;
                }
                break;
            case "load":
                if (numValue <= 0) {
                    return true;
                }
                break;
            case "command":
                for (int i = 0; i < value.length(); i++) {
                    if (!(Character.isDigit(value.charAt(i)))) {
                        return true;
                    }
                }
                break;
            default:
                if (cards == null || cards.length == 0) {
                    return true;
                }
        }
        return false;
    }

    /**
     * Takes an integer array and prints it out using whitespace to separate.
     *
     * @param arr Array to convert.
     */
    private void arrayWhitespace(int[] arr) {
        if (errorCheck("load", arr.length + "")) {
            throw new CardPileException("Array is empty. " +
                    "Please load array with items.");
        }
        System.out.println(Arrays.toString(arr)
                .replaceAll("[\\[|\\]]", "")
                .replaceAll(",", ""));
    }

    /**
     * Converts a 1d array into a 2d array using row and col sizes.
     *
     * @param colLength desired column length for the 2d array.
     * @param rowLength desired row length for the 2d array.
     * @param arr array to convert to 2d array
     * @return the created 2d array.
     */
    private int[][] convertTo2d(int colLength, int rowLength, int[] arr) {
        int count = 0;
        int[][] cards2d = new int[colLength][rowLength];
        for (int col = 0; col < colLength; col++) {
            for (int row = 0; row < rowLength; row++) {
                cards2d[col][row] = arr[count];
                count++;
            }
        }
        return cards2d;
    }

    /**
     * Finalizes deck, loading our 2d array into the main cards array,
     * as well as reversing it, if needed.
     *
     * @param deck our 2D array that's in need of conversion.
     * @param reversible whether our deck needs reversing.
     */
    private void finalizeDeck(int[][] deck, boolean reversible){
        int count = 0;
        for (int[] row : deck) {
            for (int val : row) {
                cards[count] = val;
                count++;
            }
        }
        if(reversible) {
            for (int i = 0; i < cards.length / 2; i++) {
                int temp = cards[i];
                cards[i] = cards[cards.length - i - 1];
                cards[cards.length - i - 1] = temp;
            }
        }
    }
}
