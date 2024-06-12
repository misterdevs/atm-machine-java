package com.tujuhsembilan.logic;

import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_AbsoluteEven;
import de.vandermeer.asciithemes.TA_GridThemes;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ConsoleUtil {
  public static final String REGEX_WORD = "^[-a-zA-Z ]+$";
  public static final String REGEX_NUMBER = "^[0-9]+$";

  public static final int TABLE_WIDTH = 120;

  public static final Scanner in = new Scanner(System.in);
  static Scanner scanner = new Scanner(System.in);

  public static void printClear() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public static void printDivider(Character character) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 35; i++) {
      sb.append(character);
    }
    System.out.println(sb.toString());
  }

  public static void printDivider() {
    printDivider('=');
  }

  public static void delay(int seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }

  public static void delay() {
    delay(3);
  }

  public static void enterToContinue() {
    System.out.println("Press ENTER to continue");
    scanner.nextLine();
  }

  public static Boolean confirmation(String dialog) {
    return confirmationCustom(dialog + " (y/n)", "y", "n");
  }

  public static Boolean confirmationCustom(String dialog, String yes, String no) {
    String input;
    do {
      System.out.print(dialog + " : ");
      input = in.nextLine();
      if (input.equalsIgnoreCase(yes)) {
        return true;
      } else if (input.equalsIgnoreCase(no)) {
        return false;
      }
    } while (true);
  }

  public static String validateRegex(String inputName, String errorMessage, String regex) {
    return validate(inputName, errorMessage, input -> input.matches(regex));
  }

  public static String validate(String inputName, String errorMessage, Predicate<String> condition) {

    return validateCustom(inputName, s -> {
      if (condition.test(s)) {
        return true;
      } else {
        System.out.println(errorMessage);
        return false;
      }
    });
  }

  public static String validateCustom(String inputName, Predicate<String> function) {

    String input;
    while (true) {
      System.out.print(inputName + " : ");
      input = scanner.nextLine();

      if (function.test(input))
        break;
    }
    return input;
  }

  public static String currencyFormatter(int number) {
    DecimalFormat decimalFormat = new DecimalFormat("#,###");
    return decimalFormat.format(number);
  }

  public static String rupiahFormatter(int number) {
    DecimalFormat decimalFormat = new DecimalFormat("'Rp. '#,###");
    return decimalFormat.format(number);
  }

  public static boolean isNumber(String input) {
    return input.matches(REGEX_NUMBER);
  }

  public static boolean isWord(String input) {
    return input.matches(REGEX_WORD);
  }

  public static boolean isNumberWithRange(String input, int min, int max) {
    if (isNumber(input)) {
      int number = Integer.parseInt(input);
      return number >= min && number <= max;
    }
    return false;
  }

  public static boolean isNumberLength(String input, int min, int max) {
    if (isNumber(input)) {
      return input.length() >= min && input.length() <= max;
    }
    return false;
  }

  public static void printTitle(String title) {
    printTitleCustom(title, 1, 1);
  }

  public static void printTitleCustom(String title, int paddingTop, int paddingBottom) {
    createTable(table -> {
      {
        table.addRow(title);
        table.setTextAlignment(TextAlignment.CENTER);
        table.setPaddingTop(paddingTop);
        table.setPaddingBottom(paddingBottom);
        table.getRenderer().setCWC(new CWC_AbsoluteEven());
        table.getContext().setGridTheme(TA_GridThemes.NONE);
        System.out.println(table.render(TABLE_WIDTH));
      }
    });
  }

  public static AsciiTable createTable(Consumer<AsciiTable> function) {
    AsciiTable table = new AsciiTable();
    function.accept(table);
    return table;
  }

  public static void resetDisplay() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public static void createMenu(Consumer<String> function, String menuName, String menuSubName, String[] menuArray,
      int navigationNumber, String navigationName) {
    String chosenMenu;
    do {
      resetDisplay();
      if (menuSubName != null)
        printTitleCustom(menuSubName, 1, 0);
      printTitleCustom(menuName, 0, 1);
      for (int i = 0; i < menuArray.length; i++) {
        System.out.println((i + 1) + ". " + menuArray[i]);
      }
      if (navigationName != null) {
        System.out.println(navigationNumber + ". " + navigationName);
      }
      System.out.println();
      chosenMenu = validate("Pilih menu", "Menu yang dipilih tidak tersedia",
          s -> isNumberWithRange(s, 1, menuArray.length)
              || (isNumber(s) && Integer.valueOf(s) == navigationNumber));
      function.accept(chosenMenu);
    } while (Integer.valueOf(chosenMenu) != navigationNumber);

  }
}
