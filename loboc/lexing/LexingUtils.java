package lexing;

import java.util.Arrays;

/**
 * LexingUtils.java
 * 
 * @version 1.1
 * 
 * @author Brendan Donohoe
 *
 * Utility class consisting of methods for tasks relating to the lexing
 * process.
 */

class LexingUtils
{
  /**
   * The list of characters in our language that appear in literal tokens of
   * length either one or two (note that every such character is a valid one
   * byte literal in and of itself).  All values in the array must be ordered
   * lexicographically.
   */
  private static final char [] LIT_CHARS = {
    '!',
    '%',
    '&',
    '(',
    ')',
    '*',
    '+',
    ',',
    '-',
    '.',
    '/',
    ':',
    ';',
    '<',
    '=',
    '>',
    '?',
    '[',
    ']',
    '^',
    '{',
    '|',
    '}',
  };
  
  /**
   * The list of keywords in our language.  All values in the array must be
   * ordered lexicographically.
   */
  private static final String [] KEYWORDS = {
    "bool",
    "break",
    "case",
    "continue",
    "default",
    "do",
    "else",
    "false",
    "float",
    "if",
    "return",
    "signed",
    "static",
    "struct",
    "switch",
    "true",
    "unsigned",
    "var",
    "void",
    "while"
  };
  
  /**
   * The list of literal tokens of length two.  All values in the array must be
   * ordered lexicographically.
   */
  private static final String [] TWO_LIT = {
    "!=",
    "&&",
    "++",
    "--",
    "::",
    "<<",
    "<=",
    "==",
    ">=",
    ">>",
    "||"
  };
  
  /**
   * Determine whether the current byte is part of a literal token or not.  If
   * it is, we return its index in the LIT_CHARS array, which corresponds to
   * the position of its enum representation in LitToken.LitType, or -1
   * otherwise.
   * @param c - The byte to be considered.
   * @return The index of the character in the LIT_CHARS array, if found, and
   * -1 otherwise.
   */
  static int literalCharIndex(int c)
  {
    if (c == -1)
    {
      return -1;
    }
    
    int idx = Arrays.binarySearch(LIT_CHARS, (char) c);
    
    return idx >= 0 ? idx : -1;
  }
  
  /**
   * Determine whether the given string is a keyword in our language.  If it
   * is, we return its index in the KEYWORDS array, which corresponds to the
   * position of its enum representation in KeyToken.KeyType, or -1 otherwise.
   * @param s - The string to be considered.
   * @return The index of the string in the KEYWORDS array, if found, and -1
   * otherwise.
   */
  static int keywordIndex(String s)
  {
    int idx = Arrays.binarySearch(KEYWORDS, s);
    
    return idx >= 0 ? idx : -1;
  }
  
  /**
   * Determine whether the given string is a literal token of length two.  If
   * it is, we return its index in the TWO_LIT array plus the length of the
   * LIT_CHARS array, which corresponds to the position of its enum
   * representation in LitToken.LitType, or -1 otherwise.
   * @param s - The string to be considered.
   * @return The index of the string in the TWO_LIT array plus the length of
   * the LIT_CHARS array, if found, and -1 otherwise.
   */
  static int twoLitIndex(String s)
  {
    int idx = Arrays.binarySearch(TWO_LIT, s);
    
    return idx >= 0 ? idx + LIT_CHARS.length : -1;
  }
  
  /**
   * Return whether the current byte is an alphanumeric character (or an
   * underscore).
   * @param c - The byte to be considered.
   * @return True if the character is alphanumeric or an underscore, false
   * otherwise.
   */
  static boolean isAlphanumeric(int c)
  {
    return c != -1 && (Character.isDigit(c) || Character.isAlphabetic(c) ||
      c == '_');
  }
  
  /**
   * Determine whether the given character is a whitespace character (either
   * a horizontal tab, newline, vertical tab, form feed, or carriage return -
   * that is, ASCII values 9 to 13 - or a space).
   * @param c - The char to be tested.
   * @return True if the character is whitespace, false otherwise.
   */
  static boolean isWhitespace(int c)
  {
    return (c <= '\r' && c >= '\t') || c == ' ';
  }
}