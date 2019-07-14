package mips;

import mips.LoadMem.LoadType;

/**
 * LoadDeref.java
 * 
 * @author Brendan Donohoe
 *
 * @version 1.0
 * 
 * Load the value whose address has been stored into a register into another
 * register.
 */
public class LoadDeref implements Instruction
{
  private int regSt;
  private int regPt;
  private LoadType lt;
  
  public LoadDeref(int regSt, int regPt, LoadType lt)
  {
    this.regSt = regSt;
    this.regPt = regPt;
    this.lt = lt;
  }
  
  public String toString()
  {
    String inst = lt == LoadType.BYTE ? "lbu" : "lw";
    return inst + " $t" + regSt + ", 0($t" + regPt + ")";
  }
}
