package threeaddress;

import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Instruction;
import mips.UnaryInst;

/**
 * Unary.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing assignment of one variable to the result of a unary
 * operation on another variable.
 */
public class Unary extends ThreeAddress
{
  /**
   * Var in which the result will be stored.
   */
  private Var st;
  
  /**
   * Result containing the operand.
   */
  private Result o;
  
  /**
   * The operator.
   */
  private UOpType op;
  
  public Unary(Var st, Result o, UOpType op)
  {
    this.st = st;
    this.o = o;
    this.op = op;
  }
  
  public String getOpStr()
  {
    return op == UOpType.NEG ? "-" : "(BYTE)";
  }
  
  public String toString()
  {
    return idx + ": " + st + " = " + getOpStr() + " " + o + "; DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    // Load our variable into a register...

    int reg = 0;
    
    o.getLoadInst(ins, reg);
    
    // Compute the unary instruction...
    
    ins.add(new UnaryInst(reg, reg, op));
    
    // And finally, write that register into our store variable's memory.
    
    st.getStoreInst(ins, reg);
  }
  
  public Var getDef()
  {
    return st;
  }
  
  public void addUseCount(VarCounter ct)
  {
    ct.incr(o.getVar());
  }
  
  public void subUseCount(VarCounter ct)
  {
    ct.decr(o.getVar());
  }
  
  public boolean isDeadCode(VarCounter ct)
  {
    return !ct.isUsed(st);
  }
  
  public boolean propagate(VarCounter ct, DefMap dm, ThreeAddress [] instArr)
  {
    Result p = propagateVar(o, dm, instArr);
    
    if (p == null)
    {
      return false;
    }
    
    ct.decr(o.getVar());
    ct.incr(p.getVar());
    
    o = p;
    
    return true;
  }
  
  public ThreeAddress fold()
  {
    // Before we can fold, as usual, we need the operand to be an immediate.
    
    if (!o.isImm())
    {
      return this;
    }
    
    // Next, get the value of the operand and perform the relevant operation.
    
    int v = ((Imm) o).getBits();
    
    int newV;
    
    switch (op)
    {
      case NEG : newV = -v; break;
      default : newV = v & 0xFF;
    }
    
    return new Assign(st, new Imm(newV));
  }
  
  public ThreeAddress algebraicIdentity(VarCounter ct)
  {
    return this;
  }
}