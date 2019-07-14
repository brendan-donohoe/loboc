package general;

/**
 * RefInt.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Wrapper class for an int, such that changes to its value may be preserved
 * across function calls (in particular, when generating labels for the various
 * user-defined variables).
 */
public class RefInt
{
  private int val;
  
  public RefInt(int val)
  {
    this.val = val;
  }
  
  public int getVal()
  {
    return val;
  }
  
  public void setVal(int val)
  {
    this.val = val;
  }
  
  /**
   * Shorthand method to increment the variable.
   * @return The value of val before incrementing.
   */
  public int incr()
  {
    int oldVal = val++;
    return oldVal;
  }
}