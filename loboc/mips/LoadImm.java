package mips;

/**
 * LoadImm.java
 * 
 * @author Brendan Donohoe
 *
 * Load a numerical constant into a register.
 */
public class LoadImm implements Instruction
{
  private int bits;
  private int reg;
  
  public LoadImm(int bits, int reg)
  {
    this.bits = bits;
    this.reg = reg;
  }
  
  public String toString()
  {
    return "li $t" + reg + ", " + bits;
  }
}