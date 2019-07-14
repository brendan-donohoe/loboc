package mips;

/**
 * StoreMem.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 * 
 * Class representing storage of a value in memory (either the stack or the
 * global data segment, depending on if we are dealing with compiler generated
 * temporaries or user-defined variables, respectively).
 */
public class StoreMem implements Instruction
{
  private int memOffset;
  private StoreType st;
  private int reg;
  private boolean isTemp;
  
  public StoreMem(int memOffset, StoreType st, int reg, boolean isTemp)
  {
    this.memOffset = memOffset;
    this.st = st;
    this.reg = reg;
    this.isTemp = isTemp;
  }
  
  public String toString()
  {
    String inst = st == StoreType.BYTE ? "sb" : "sw";
    
    if (isTemp)
    {
      return inst + " $t" + reg + ", -" + memOffset + "($sp)";
    }
    else
    {
      return inst + " $t" + reg + ", " + memOffset + "($gp)";
    }
  }
  
  public static enum StoreType
  {
    WORD,
    BYTE
  };
}