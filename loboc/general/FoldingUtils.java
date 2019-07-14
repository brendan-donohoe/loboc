package general;

/**
 * FoldingUtils.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility methods for constant folding for various nontrivial operations.
 * Some of them may... leave something to be desired (as in, to implement
 * unsigned multiplication and division, we stuff both ints into longs
 * beforehand and perform the arithmetic afterward), but oh well.
 */

public class FoldingUtils
{
  public static int multU(int n1, int n2)
  {
    return (int) (GeneralUtils.unsignedIntToLong(n1)
      * GeneralUtils.unsignedIntToLong(n2));
  }
  
  public static int divU(int n1, int n2)
  {
    return (int) (GeneralUtils.unsignedIntToLong(n1)
      / GeneralUtils.unsignedIntToLong(n2));
  }
  
  public static boolean ltU(int n1, int n2)
  {
    return n1 != n2 && !gtU(n1, n2);
  }
  
  public static boolean lteU(int n1, int n2)
  {
    return !gtU(n1, n2);
  }
  
  public static boolean gtU(int n1, int n2)
  {
    if ((n1 >= 0 && n2 >= 0) || (n1 < 0 && n2 < 0))
    {
      return n1 > n2;
    }
    else
    {
      return n1 < n2;
    }
  }
  
  public static boolean gteU(int n1, int n2)
  {
    return n1 == n2 || gtU(n1, n2);
  }
}