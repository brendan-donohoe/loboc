package mips;

import mips.StoreMem.StoreType;

/**
 * StoreDeref.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Storage of a value inside the address inside of another register.
 */
public class StoreDeref implements Instruction
{
  private int regVal;
  private int regMem;
  private StoreType st;
  
  public StoreDeref(int regVal, int regMem, StoreType st)
  {
    this.regVal = regVal;
    this.regMem = regMem;
    this.st = st;
  }
  
  public String toString()
  {
    String inst = st == StoreType.BYTE ? "sb" : "sw";

    return inst + " $t" + regVal + ", 0($t" + regMem + ")";
  }
}