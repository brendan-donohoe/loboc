package threeaddress;

/**
 * UserVar.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Variable class representing a user-defined variable in the program.
 */
public class UserVar extends Var
{
  public UserVar(int varNum, int size, int align, int offset)
  {
    this.varNum = varNum;
    this.size = size;
    this.align = align;
    this.offset = offset;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public String toString()
  {
    return "v" + varNum /*+ " (offset: " + offset + ")"*/;
  }
}