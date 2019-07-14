package mips;

/**
 * Exit.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Class representing the instruction to return from main.
 */
public class Exit implements Instruction
{
  public String toString()
  {
    return "jr $ra";
  }
}