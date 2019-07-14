package mips;

import threeaddress.BOpType;

/**
 * BinaryInst.java
 * 
 * @author Brendan Donohoe
 *
 * Class representing a binary operation on two operand registers, in which the
 * result is stored in a separate, third register (see BOpType for several such
 * operations, with the exception of multiplication and division, which each
 * have their own unique instructions).
 */
public class BinaryInst implements Instruction
{
  private int regSt;
  private int regOp1;
  private int regOp2;
  private BOpType op;
  
  public BinaryInst(int regSt, int regOp1, int regOp2, BOpType op)
  {
    this.regSt = regSt;
    this.regOp1 = regOp1;
    this.regOp2 = regOp2;
    this.op = op;
  }
  
  public String toString()
  {
    return op + " $t" + regSt + ", $t" + regOp1 + ", $t" + regOp2;
  }
}