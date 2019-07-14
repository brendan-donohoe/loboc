package mips;

/**
 * ExitVal.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Class representing the operation to set a return value prior to returning
 * from main.
 */
public class ExitVal implements Instruction
{
  private int reg;
  
  public ExitVal(int reg)
  {
    this.reg = reg;
  }
  
  public String toString()
  {
    return "move $v0, $t" + reg;
  }
}
