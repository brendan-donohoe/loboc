package general;

import java.util.LinkedList;

/**
 * Utils.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class containing methods for general tasks not restricted to any
 * particular part of the process.
 */

public class GeneralUtils
{
  /**
   * Generate a simple string of padding - that is, a sequence of 2*offset
   * spaces.
   * @param offset - parameter determining the number of spaces to be
   * generated.
   * @return The string of padding.
   */
  public static String getPadding(int offset)
  {
    StringBuilder pad = new StringBuilder();
    
    for (int i = 0; i < offset; i++)
    {
      pad.append("  ");
    }
    
    return pad.toString();
  }
  
  /**
   * Generate a long with the first 32 bits set to zero and the last 32 bits
   * set to those of the supplied int.  This gives us a way to do unsigned int
   * arithmetic for e.g. division.
   * @return The long whose last 32 bits are set to those set in the given int.
   */
  public static long unsignedIntToLong(int ui)
  {
    long result;
    
    // Since Java uses sign extension for bitwise operators, we have to set the
    // result bits in such a manner that bit operations are always being
    // performed on positive values.
    
    // First, we'll set least significant 31 bits of the result to the least
    // significant 31 bits of the integer.
    
    result = ui & 0x7FFFFFFF;
    
    // Then we'll perform a logical shift rightward by 1 to get the sign bit in
    // position 31, convert to long, shift left, and set the bit in the result.
    
    result = (((long) ((ui >>> 1) & 0x40000000)) << 1) | result;
    
    return result;
  }
  
  /**
   * Method to generate a comma separated list of strings.
   * @param arr - The array of strings.
   * @return The comma separated list, as a string.
   */
  public static String getCommaSeparatedList(String [] arr)
  {
    String result = "";
    for (int i = 0; i < arr.length - 1; i++)
    {
      result += arr[i];
      
      // Print out a comma since this is not the last expected item in the
      // list.
      
      result += ", ";
    }
    
    // Finally, print our last item without the comma.
    
    result += arr[arr.length - 1];
    return result;
  }
  
  /**
   * Empty all elements of the second list into the first.
   * @param list1 - The list to be added to.
   * @param list2 - The list to be emptied.
   */
  public static <E> void collectAll(LinkedList<E> list1, LinkedList<E> list2)
  {
    while (!list2.isEmpty())
    {
      list1.add(list2.remove());
    }
  }
}