package lexing;

/**
 * KeyToken.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * A class holding extra relevant information about a keyword token - namely,
 * the type of keyword that it is as an enum, so as to avoid the need for
 * string comparison when parsing input expressions.
 */

public class KeyToken extends Token
{
  /** The keyword this token represents, as a KeyType enum. */
  private KeyType ktype;
  
  /**
   * @param data - The literal string representation of the token in the input.
   * @param lineNum - The line number at which this token was found.
   * @param byteNum - The position on the line at which this token was found.
   * @param type - The type of this token, as a TType enum.
   * @param ktype - The keyword this token represents, as a KeyType enum.
   */
  public KeyToken(String data, long lineNum, long byteNum, TType type,
    KeyType ktype)
  {
    super(data, lineNum, byteNum, type);
    this.ktype = ktype;
  }
  
  /**
   * @return The specific keyword this token represents, as a KeyType enum.
   */
  public KeyType getKeyType()
  {
    return ktype;
  }
  
  /**
   * KeyType is an enum containing values for each of the possible keywords
   * this token can represent.
   * 
   * THE ORDERING OF THESE VALUES IS SIGNIFICANT.  In particular, all keywords
   * must be in lexicographic order of their literal representation in the
   * input.
   */
  public static enum KeyType
  {
    BOOL,
    BREAK,
    CASE,
    CONTINUE,
    DEFAULT,
    DO,
    ELSE,
    FALSE,
    FLOAT,
    IF,
    RETURN,
    SIGNED,
    STATIC,
    STRUCT,
    SWITCH,
    TRUE,
    UNSIGNED,
    VAR,
    VOID,
    WHILE;
    
    /** Cached array consisting of all KeyType values. */
    public static final KeyType [] VALS = values();
  };
}