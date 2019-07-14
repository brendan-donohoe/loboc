package mips;

/**
 * DivInst.java
 *
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing multiplication (either signed or unsigned) between two
 * registers.  Unlike other binary operations, multiplication does not have a
 * store register - the value must be retrieved out of the lo register,  and so
 * we treat multiplication (and division, which is similar) uniquely.
 */
public class MultInst implements Instruction
{
  private int regOp1;
  private int regOp2;
  private boolean unsigned;
  
  public MultInst(int regOp1, int regOp2, boolean unsigned)
  {
    this.regOp1 = regOp1;
    this.regOp2 = regOp2;
    this.unsigned = unsigned;
  }
  
  public String toString()
  {
    String inst = unsigned ? "multu" : "mult";
    return inst + " $t" + regOp1 + ", $t" + regOp2;
  }
}