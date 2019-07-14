package optimization;

import java.util.Arrays;

import threeaddress.Result;
import threeaddress.Var;

/**
 * VarCounter.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class to keep track of the number of uses of each of the temporaries
 * in our three address code representation of our program.  Used for dead-code
 * elimination to eliminate assignments to unused temporary variables (due to
 * the existence of pointers in our language, this cannot be done for user-
 * defined variables without pointer analysis).
 */
public class VarCounter
{
  private int [] countMap;
  
  public VarCounter(int tempCount)
  {
    countMap = new int [tempCount];
  }
  
  /**
   * Increment the number of uses of the given temporary by one.
   * @param v - The temporary whose uses are to be incremented.
   */
  public void incr(Result r)
  {
    if (r == null || !r.isVar() || !((Var) r).isTemp())
    {
      return;
    }
    
    Var v = (Var) r;
    
    int key = v.getVarNum();
    
    countMap[key]++;
  }
  
  /**
   * Decrement the number of uses of the given temporary by one.
   * @param v - The temporary whose uses are to be decremented.
   */
  public void decr(Result r)
  {
    if (r == null || !r.isVar() || !((Var) r).isTemp())
    {
      return;    
    }
    
    Var v = (Var) r;
    
    int key = v.getVarNum();
    
    countMap[key]--;
  }
  
  /**
   * Determine whether or not the temporary is used at any point in the code.
   * @param v - The temporary to be checked.
   * @return True if this temporary has at least one use in the program - false
   * otherwise.
   */
  public boolean isUsed(Var v)
  {
    if (!v.isTemp())
    {
      return true;
    }
    
    int key = v.getVarNum();
    
    return countMap[key] > 0;
  }
  
  public String toString()
  {
    return Arrays.toString(countMap);
  }
}