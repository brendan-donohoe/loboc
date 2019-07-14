package mips;

/**
 * DivInst.java
 *
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing division (either signed or unsigned) between two
 * registers.  Unlike other binary operations, division does not have a store
 * register - the value must be retrieved out of the lo register,  and so we
 * treat division (and multiplication, which is similar) uniquely.
 */
public class DivInst implements Instruction
{
  private int regOp1;
  private int regOp2;
  private boolean unsigned;
  
  public DivInst(int regOp1, int regOp2, boolean unsigned)
  {
    this.regOp1 = regOp1;
    this.regOp2 = regOp2;
    this.unsigned = unsigned;
  }
  
  public String toString()
  {
    String inst = unsigned ? "divu" : "div";
    return inst + " $t" + regOp1 + ", $t" + regOp2;
  }
}