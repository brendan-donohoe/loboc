package mips;

import threeaddress.BranchType;

/**
 * Branch.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a conditional branch in control flow, in which the
 * condition is a function of a single register (in our case, either equal to
 * zero or not equal to zero).
 */
public class Branch implements Instruction
{
  private int regCnd;
  private String label;
  private BranchType bt;
  
  public Branch(int regCnd, String label, BranchType bt)
  {
    this.regCnd = regCnd;
    this.label = label;
    this.bt = bt;
  }
  
  public String toString()
  {
    String inst;
    
    if (bt == BranchType.NEZ)
    {
      inst = "bnez";
    }
    else
    {
      inst = "beqz";
    }
    
    return inst + " $t" + regCnd + ", " + label;
  }
}