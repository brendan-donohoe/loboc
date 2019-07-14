package mips;

/**
 * LoadAddress.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Class representing an instruction to load an address from memory (offset
 * from either the stack or the global data segment, based on whether the
 * variable whose address is to be found is a compiler-generated temporary or
 * user-defined, respectively).
 */
public class LoadAddress implements Instruction
{
  private int regSt;
  private int regOff;
  private boolean isTemp;
  
  public LoadAddress(int regSt, int regOff, boolean isTemp)
  {
    this.regSt = regSt;
    this.regOff = regOff;
    this.isTemp = isTemp;
  }
  
  public String toString()
  {
    if (isTemp)
    {
      return "sub $t" + regSt + ", $sp, $t" + regOff;
    }
    else
    {
      return "add $t" + regSt + ", $gp, $t" + regOff;
    }
  }
}