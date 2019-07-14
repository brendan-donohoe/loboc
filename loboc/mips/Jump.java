package mips;

/**
 * Jump.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Class representing a jump to a separate label in the program.
 */
public class Jump implements Instruction
{
  private String label;
  
  public Jump(String label)
  {
    this.label = label;
  }
  
  public String toString()
  {
    return "j " + label;
  }
}