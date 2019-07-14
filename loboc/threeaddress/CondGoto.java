package threeaddress;

import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.Branch;
import mips.Instruction;

/**
 * CondGoto.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a conditional jump to a particular label.
 */
public class CondGoto extends ThreeAddress
{
  private Result cond;
  private String label;
  BranchType bt;
  
  public CondGoto(Result cond, String label, BranchType bt)
  {
    this.cond = cond;
    this.label = label;
    this.bt = bt;
  }
  
  public boolean isJump()
  {
    return true;
  }
  
  public String toString()
  {
    String condStr;
    
    if (bt == BranchType.NEZ)
    {
      condStr = cond.toString();
    }
    else
    {
      condStr = "!" + cond.toString();
    }
    
    return idx + ": if " + condStr + " goto " + label + "; DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    int reg = 0;
    
    cond.getLoadInst(ins, 0);
    ins.add(new Branch(reg, label, bt));
  }
  
  public Var getDef()
  {
    return null;
  }
  
  public void addUseCount(VarCounter ct)
  {
    ct.incr(cond.getVar());
  }
  
  public void subUseCount(VarCounter ct)
  {
    ct.decr(cond.getVar());
  }
  
  public boolean isDeadCode(VarCounter ct)
  {
    return false;
  }
  
  public boolean propagate(VarCounter ct, DefMap dm, ThreeAddress [] instArr)
  {
    Result p = propagateVar(cond, dm, instArr);
    
    if (p == null)
    {
      return false;
    }
    
    ct.decr(cond.getVar());
    ct.incr(p.getVar());
    
    cond = p;
    
    return true;
  }
  
  public ThreeAddress fold()
  {
    // We need our condition to be a constant before we can do any folding.
    
    if (!cond.isImm())
    {
      return this;
    }
    
    // Next, get the value of the cond immediate and, depending on its value
    // and the type of branch this is, we'll perform a different operation.
    
    int v = ((Imm) cond).getBits();
    
    if ((v == 0 && bt == BranchType.EQZ) || (v != 0 && bt == BranchType.NEZ))
    {
      // We replace this statement with a goto.
      
      return new Goto(label);
    }
    else
    {
      // Discard statement entirely and just fall through.
      
      return null;
    }
  }
  
  public ThreeAddress algebraicIdentity(VarCounter ct)
  {
    return this;
  }
}