package mips;

import threeaddress.UOpType;

/**
 * UnaryInst.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a unary operation on a value stored in a register (of
 * which, thanks to the simplicity of our language, there are only two such
 * operations - negation and set-not-equal).
 */
public class UnaryInst implements Instruction
{
  private int regSt;
  private int reg;
  private UOpType op;
  
  public UnaryInst(int regSt, int reg, UOpType op)
  {
    this.regSt = regSt;
    this.reg = reg;
    this.op = op;
  }
  
  public String toString()
  {
    if (op == UOpType.NEG)
    {
      return "neg $t" + regSt + ", $t" + reg;
    }
    else
    {
      return "andi $t" + regSt + ", $t" + reg + ", 255";
    }
  }
}
