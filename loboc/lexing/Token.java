package lexing;

/**
 * Token.java
 * 
 * @version 1.1
 * 
 * @author Brendan Donohoe
 *
 * A class holding all relevant information about a token - including the data
 * it holds (that is, its literal representation in the input), the line and
 * byte position at which it is located in the source file, and the type of
 * token it is.
 */

public class Token
{
  /** The literal string representation of the token in the input. */
  private String data;
  
  /** The line number at which this token was found. */
  private long lineNum;
  
  /** The position on the line at which this token was found. */
  private long byteNum;
  
  /** The type of this token, as a TType enum. */
  private TType type;
  
  /**
   * Construct a token with the given parameters.
   * @param data - The literal string representation of the token in the input.
   * @param lineNum - The line number at which this token was found.
   * @param byteNum - The position on the line at which this token was found.
   * @param type - The type of this token, as a TType enum.
   */
  public Token(String data, long lineNum, long byteNum, TType type)
  {
    this.data = data;
    this.lineNum = lineNum;
    this.byteNum = byteNum;
    this.type = type;
  }
  
  /**
   * @return The literal string representation of the token in the input.
   */
  public String getData()
  {
    return data;
  }
  
  /**
   * @return The line number at which this token was found.
   */
  public long getLineNum()
  {
    return lineNum;
  }
  
  /**
   * @return The position on the line at which this token was found.
   */
  public long getByteNum()
  {
    return byteNum;
  }
  
  /** @return The type of this token, as a TType enum. */
  public TType getType()
  {
    return type;
  }
  
  public String toString()
  {
    if (type == TType.KEYWORD || type == TType.NUMBER
      || type == TType.IDENTIFIER)
    {
      return data;
    }
    else if (type == TType.LITERAL || type == TType.ILLCHR)
    {
      return "\"" + data + "\"";
    }
    else if (type == TType.EOF)
    {
      return "$EOF";
    }
    else if (type == TType.ALN_LEN_ERR)
    {
      return "$ALN_LEN_ERR";
    }
    else
    {
      return "$NUM_LEN_ERR";
    }
  }
  
  /**
   * TType is an enum containing the different token types present in the
   * specification (apart from internal tokens, which are broken up into
   * separate EOF and ILLCHR tokens - additional ERR tokens are also added to
   * signal an error state).
   */
  public static enum TType
  {
    LITERAL,
    KEYWORD,
    IDENTIFIER,
    NUMBER,
    EOF,
    ILLCHR,
    NUM_LEN_ERR,
    ALN_LEN_ERR
  };
}