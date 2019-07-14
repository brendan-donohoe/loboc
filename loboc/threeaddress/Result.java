package threeaddress;

import java.util.LinkedList;

import mips.Instruction;

/**
 * Result.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing any operand that may appear in a three address code
 * instruction.
 */
public abstract class Result
{
  /**
   * Determine whether or not this result is a variable.
   * @return True if this is a variable - false otherwise.
   */
  public boolean isVar()
  {
    return false;
  }
  
  /**
   * Determine whether or not this result is an immediate.
   * @return True if this is an immediate - false otherwise.
   */
  public boolean isImm()
  {
    return false;
  }
  
  /**
   * Generate the instruction(s) to load this result into a specified register
   * and add them to the list we provide.
   * @param ins - The list of instructions.
   * @param reg - The register to load the result into.
   */
  public abstract void getLoadInst(LinkedList<Instruction> ins, int reg);
  
  /**
   * Get the variable associated with this result.
   * @return The var associated with this result, or null if no such variable
   * is associated with this result.
   */
  public abstract Var getVar();
}