package lexing;

/**
 * LitToken.java
 *
 * @version 1.0
 *
 * @author Brendan Donohoe
 *
 * A class holding extra relevant information about a literal token - namely,
 * the type of literal that it is as an enum, so as to avoid the need for
 * string comparison when parsing input expressions.
 */

public class LitToken extends Token
{
  /** The literal this token represents, as a LitType enum. */
  private LitType ltype;
  
  /**
   * @param data - The literal string representation of the token in the input.
   * @param lineNum - The line number at which this token was found.
   * @param byteNum - The position on the line at which this token was found.
   * @param type - The type of this token, as a TType enum.
   * @param ltype - The literal this token represents, as a LitType enum.
   */
  public LitToken(String data, long lineNum, long byteNum, TType type,
    LitType ltype)
  {
    super(data, lineNum, byteNum, type);
    this.ltype = ltype;
  }
  
  /**
   * @return The specific literal this token represents, as a LitType enum.
   */
  public LitType getLitType()
  {
    return ltype;
  }
  
  /**
   * LitType is an enum containing values for each of the possible literals
   * this token can represent.  The names of each literal match those suggested
   * in the spike1 specification.
   * 
   * THE ORDERING OF THESE VALUES IS SIGNIFICANT.  In particular, the first set
   * of values must consist of one-byte literals in lexicographic order of
   * their literal appearance in the input.  Likewise, the second set of values
   * must consist of two-byte literals in lexicographic order of their literal
   * appearance in the input.
   */
  public static enum LitType
  {
    // The set of one-byte literals.
    
    BANG,
    MOD,
    AND,
    OPNPAR,
    CLSPAR,
    STAR,
    PLUS,
    COMMA,
    MINUS,
    DOT,
    SLASH,
    COLON,
    SEMI,
    LSS,
    EQUAL,
    GTR,
    QUEST,
    OPNBRK,
    CLSBRK,
    XOR,
    OPNBRC,
    OR,
    CLSBRC,
    
    // The set of two-byte literals.
    
    NOTEQUAL,
    ANDAND,
    PLUSPLUS,
    MINUSMINUS,
    COLONCOLON,
    LEFTSHIFT,
    LSSEQUAL,
    EQUALEQUAL,
    GTREQUAL,
    RIGHTSHIFT,
    OROR;
    
    /** Cached array consisting of all LitType values. */
    public static final LitType [] VALS = values();
  };
}