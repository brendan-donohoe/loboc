package mips;

/**
 * LabelInst.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Class representing a label - the destination of a jump from another part of
 * the program.
 */
public class LabelInst implements Instruction
{
  private String label;
  
  public LabelInst(String label)
  {
    this.label = label;
  }
  
  public String toString()
  {
    return label + ":";
  }
}