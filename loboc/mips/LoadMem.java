package mips;

/**
 * LoadMem.java
 * 
 * @author Brendan Donohoe
 *
 * Load a value from memory at a specific offset to either the stack pointer or
 * global data pointer and store it inside of the specified register.
 */
public class LoadMem implements Instruction
{
  private int memOffset;
  private LoadType lt;
  private int reg;
  private boolean isTemp;
  
  public LoadMem(int memOffset, LoadType lt, int reg, boolean isTemp)
  {
    this.memOffset = memOffset;
    this.lt = lt;
    this.reg = reg;
    this.isTemp = isTemp;
  }
  
  public String toString()
  {
    String inst = lt == LoadType.BYTE ? "lbu" : "lw";
    
    if (isTemp)
    {
      return inst + " $t" + reg + ", -" + memOffset + "($sp)";
    }
    else
    {
      return inst + " $t" + reg + ", " + memOffset + "($gp)";
    }
  }
  
  public static enum LoadType
  {
    WORD,
    BYTE
  };
}
