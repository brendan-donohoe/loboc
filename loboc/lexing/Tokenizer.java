package lexing;

import java.io.IOException;

import lexing.LexingUtils;
import lexing.KeyToken.KeyType;
import lexing.LitToken.LitType;

/**
 * Tokenizer.java
 * 
 * @version 1.1
 * 
 * @author Brendan Donohoe
 * 
 * Given an input stream, this class offers functionality to cleanly read
 * tokens in our language from the input stream.
 */

public class Tokenizer
{
  private CountReader in;
  
  private Token peeked;
  
  public Tokenizer(CountReader in) throws IOException
  {
    this.in = in;
    
    peeked = null;
    
    skipWhitespace();
  }
  
  /**
   * Peek at a single token from the input stream without "moving the stream
   * ahead" (i.e., fake equivalent behavior through use of a temporary variable
   * to store a single token value).
   * @return The peeked-at token.
   * @throws IOException
   */
  public Token peek() throws IOException
  {
    // If we peeked at a token previously, but haven't read it yet, just return
    // that token - otherwise, read a token from the input stream and save it
    // for future calls to either peek or read.

    if (peeked == null)
    {
      peeked = getTokenFromStream();
    }
    return peeked;
  }
  
  /**
   * Read a single token from the input stream.
   * @return The token from the input stream.
   * @throws IOException
   */
  public Token read() throws IOException
  {
    // If we have a token we've peeked at previously, return that and set
    // peeked to null so that we are set up to peek at another token, if
    // necessary.
    
    if (peeked != null)
    {
      Token t = peeked;
      peeked = null;
      return t;
    }
    else
    {
      return getTokenFromStream();
    }
  }
  
  /**
   * Parse a token from the input stream.
   * @return The parsed token.
   */
  private Token getTokenFromStream() throws IOException
  {
    long tLineNum = in.getLineNum();
    long tByteNum = in.getByteNum();
    
    int c = in.peek();
    
    String data = null;
    Token.TType type = null;
    
    int litIdx = -1;
    int keyIdx = -1;
    
    if (c == -1)
    {
      // We've reached the end of our stream - return an EOF token.
      
      type = Token.TType.EOF;
    }
    else if ((litIdx = LexingUtils.literalCharIndex(c)) >= 0)
    {
      // The first character was one that appears only in a literal, so this is
      // a literal.
      
      type = Token.TType.LITERAL;
      
      // Is it a one byte literal or a two byte literal?  If it's the latter,
      // we want to parse the two byte literal rather than two independent one
      // byte literals.  We'll have to peek an additional character ahead.
      
      int [] cs = in.peek2();
      String lit = Character.toString((char) cs[0]) + (char) cs[1];
      
      int twoLitIdx;
      
      if (cs[1] != -1 && (twoLitIdx = LexingUtils.twoLitIndex(lit)) >= 0)
      {
        // It is a two byte literal.  Record it and move our stream forward by
        // two bytes.
        
        data = lit;
        litIdx = twoLitIdx;
        in.read2();
      }
      else
      {
        // Because all characters in literals are themselves one byte literals,
        // this is a one byte literal.  Record it and move our stream forward
        // by one byte.
        
        data = Character.toString((char) in.read());
      }
    }
    else if (Character.isDigit(c))
    {
      // The first character is a digit, so as per the spec, this is a number
      // token.  Parse it and record it.
      
      type = Token.TType.NUMBER;
      data = parseNum();
      
      if (data == null)
      {
        // If the length of the numeric token is greater than 1024, we signal
        // an error.
        
        type = Token.TType.NUM_LEN_ERR;
      }
    }
    else if (LexingUtils.isAlphanumeric((char) c))
    {
      // The first character is either a letter or underscore (not a number -
      // otherwise it would have been caught in the previous if body), so this
      // next token is either a keyword or an identifier.  Parse first, ask
      // questions later.

      data = parseAlphanumeric();
      
      if (data == null)
      {
        // If the length of the alphanumeric token is greater than 1024, we
        // signal an error.
        
        type = Token.TType.ALN_LEN_ERR;
      }
      else
      {
        // Now, is the alphanumeric token we parsed a keyword, or an identifier?

        if ((keyIdx = LexingUtils.keywordIndex(data)) >= 0)
        {
          type = Token.TType.KEYWORD;
        }
        else
        {
          type = Token.TType.IDENTIFIER;
        }
      }
    }
    else
    {
      // This is not a legal character.  Record it and carry on.
      
      type = Token.TType.ILLCHR;
      data = Character.toString((char) in.read());
    }
    
    skipWhitespace();
    
    if (litIdx != -1)
    {
      return new LitToken(data, tLineNum, tByteNum, type,
        LitType.VALS[litIdx]);
    }
    else if (keyIdx != -1)
    {
      return new KeyToken(data, tLineNum, tByteNum, type,
        KeyType.VALS[keyIdx]);
    }
    else
    {
      return new Token(data, tLineNum, tByteNum, type);
    }
  }
  
  /**
   * Skip as many whitespace characters at the front of the input stream as
   * possible, updating lineNum and byteNum accordingly as we do so.  We
   * consider whitespace to be those characters outlined in the specification
   * as well as C-style inline comments made by the programmer.
   * @throws IOException
   */
  private void skipWhitespace() throws IOException
  {
    int c;
    
    while (LexingUtils.isWhitespace(c = in.peek()))
    {
      in.read();
    }
    
    // We've reached a non-whitespace character.  See if it's a comment.
    // If it is, consume it and consume all characters until end of line
    // (either newline or EOF) and recursively repeat the process.  Otherwise,
    // if we do not have a comment following the whitespace we've skipped,
    // we're done.
    
    if (c == '/')
    {
      int [] cs = in.peek2();
      
      if (cs[0] == '/' && cs[1] == '/')
      {
        while ((c = in.peek()) != '\n' && c != -1)
        {
          in.read();
        }
        
        // Note that we don't actually consume the character terminating the
        // comment - the recursive call will take care of it if it's a newline,
        // and if it's EOF, we want to do all of our handling of that in our
        // next getTokenFromStream() call.
        
        skipWhitespace();
      }
    }
  }
  
  /**
   * Parse the largest contiguous string of digits present in the input stream.
   * It's assumed that this is called when there is at least one numeric byte
   * at the front of the input stream.
   * @return A string representation of the number read, or null if the number
   * we were given to parse is greater than 10 bytes in length.
   */
  private String parseNum() throws IOException
  {
    StringBuilder num = new StringBuilder();
    int c;
    
    int numBytes = 0;
    
    while (Character.isDigit(c = in.peek()))
    {
      if (numBytes++ >= 10)
      {
        return null;
      }
      
      num.append((char) c);
      
      in.read();
    }
    
    // If we parsed the maximum allowed number of digits (10), we'll perform a
    // final quick check to ensure that the number does not exceed the maximum
    // allowed value of an unsigned integer.
    
    if (numBytes == 10 && Long.parseLong(num.toString()) > 4294967295L)
    {
      return null;
    }
    
    return num.toString();
  }
  
  /**
   * Parse the largest contiguous string of alphanumeric characters (here, we
   * stretch our definition of "alphanumeric" to include underscores as well -
   * see the implementation in LexingUtils).  It's assumed that this is called
   * when there is at least one alphanumeric (or underscore) byte at the front
   * of the input stream.
   * @return The alphanumeric string read, or null if the contiguous string is
   * greater than 1024 bytes in length.
   * @throws IOException
   */
  private String parseAlphanumeric() throws IOException
  {
    StringBuilder alp = new StringBuilder();
    
    int numBytes = 0;
    
    while (LexingUtils.isAlphanumeric(in.peek()))
    {
      if (numBytes++ >= 1024)
      {
        return null;
      }
      
      alp.append((char) in.read());
    }
    
    return alp.toString();
  }
}