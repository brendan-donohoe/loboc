package threeaddress;

import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Instruction;

/**
 * Assign.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing the assignment of a variable to a result.
 */
public class Assign extends ThreeAddress
{
  private Var t;
  private Result r;
  
  public Assign(Var t, Result r)
  {
    this.t = t;
    this.r = r;
  }
  
  public String toString()
  {
    return idx + ": " + t + " = " + r + "; DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    int reg = 0;
    
    r.getLoadInst(ins, reg);
    t.getStoreInst(ins, reg);
  }
  
  public Var getDef()
  {
    return t;
  }
  
  public Result getPropResult()
  {
    if (r.isImm() || (r.isVar() && !t.equals((Var) r)))
    {
      // Only return the propagation result if the RHS is either a number or a
      // variable (that is not equal to the LHS).
      
      return r;
    }
    
    return null;
  }
  
  public void addUseCount(VarCounter ct)
  {
    ct.incr(r.getVar());
  }
  
  public void subUseCount(VarCounter ct)
  {
    ct.decr(r.getVar());
  }
  
  public boolean isDeadCode(VarCounter ct)
  {
    return !ct.isUsed(t);
  }
  
  public boolean propagate(VarCounter ct, DefMap dm, ThreeAddress [] instArr)
  {
    Result p = propagateVar(r, dm, instArr);
    
    if (p == null)
    {
      return false;
    }
    
    ct.decr(r.getVar());
    ct.incr(p.getVar());
    
    r = p;
    
    return true;
  }
  
  public ThreeAddress fold()
  {
    return this;
  }
  
  public ThreeAddress algebraicIdentity(VarCounter ct)
  {
    return this;
  }
}