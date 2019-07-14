package mips;

/**
 * LoadLowBits.java
 * 
 * @author Brendan Donohoe
 *
 * Load the value stored in the LO register into a separate register.
 */
public class LoadLowBits implements Instruction
{
  private int reg;
  
  public LoadLowBits(int reg)
  {
    this.reg = reg;
  }
  
  public String toString()
  {
    return "mflo $t" + reg;
  }
}