package lexing;
import java.io.IOException;
import java.io.Reader;

/**
 * CountReader.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class that, given a reader to an input stream, handles byte position
 * and line position data and, in the event that an EOF is read at any point in
 * the stream, ensures that all subsequent requests from the stream return EOF,
 * even if there are no EOF characters after that point.
 */

public class CountReader
{
  private Reader in;
  
  private long byteNum;
  private long lineNum;
  
  private boolean eof;
  
  private int p1;
  private int p2;
  
  public CountReader(Reader in)
  {
    this.in = in;
    
    byteNum = 1;
    lineNum = 1;
    
    eof = false;
    
    p1 = -2;
    p2 = -2;
  }
  
  /**
   * Return the line position of our current location in the input stream.
   * @return The byte we are positioned at on the current line.
   */
  public long getByteNum()
  {
    return byteNum;
  }
  
  /**
   * Return the current line number we are at in the input stream.
   * @return The line we are currently positioned at in the input stream.
   */
  public long getLineNum()
  {
    return lineNum;
  }
  
  /**
   * Return the next byte at the front of the input stream and advance the
   * stream position.
   * @return The byte at the front of the input stream.
   * @throws IOException
   */
  public int read() throws IOException
  {
    int c;
    if (p1 != -2)
    {
      c = p1;
      p1 = p2;
      p2 = -2;
    }
    else
    {
      c = getCh();
    }
    
    if (c == '\n')
    {
      lineNum++;
      byteNum = 1;    
    }
    else if (c != -1)
    {
      byteNum++;
    }
    
    return c;
  }
  
  /**
   * Return the next two bytes at the front of the input stream and advance the
   * stream position appropriately.
   * @return The two bytes at the front of the input stream.
   * @throws IOException
   */
  public int [] read2() throws IOException
  {
    int [] result = {read(), read()};
    return result;
  }
  
  /**
   * Return the byte at the front of the input stream without advancing the
   * stream position.
   * @return The byte at the front of the input stream.
   * @throws IOException
   */
  public int peek() throws IOException
  {
    if (p1 == -2)
    {
      p1 = getCh();
    }
    
    return p1;
  }
  
  /**
   * Return the two bytes at the front of the input stream without advancing
   * the stream position.
   * @return The two bytes at the front of the input stream.
   * @throws IOException
   */
  public int [] peek2() throws IOException
  {
    if (p1 == -2)
    {
      p1 = getCh();
    }
    if (p2 == -2)
    {
      p2 = getCh();
    }
    
    int [] result = {p1, p2};
    return result;
  }
  
  /**
   * Return the next byte at the front of the input stream.
   * @return The byte at the front of the input stream, or -1 if EOF has been
   * read from the input stream at any point in the past.
   * @throws IOException
   */
  private int getCh() throws IOException
  {
    if (eof)
    {
      return -1;
    }
    else
    {
      int c = in.read();
      if (c == -1)
      {
        eof = true;
      }
      return c;
    }
  }
}