package threeaddress;

import general.FoldingUtils;

import java.util.LinkedList;

import optimization.DefMap;
import optimization.VarCounter;
import mips.BinaryInst;
import mips.DivInst;
import mips.Instruction;
import mips.LoadLowBits;
import mips.MultInst;

/**
 * Binary.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing assignment of one variable to the result of a binary
 * operation of two variables.
 */
public class Binary extends ThreeAddress
{
  /**
   * Var in which the result will be stored.
   */
  private Var st;
  
  /**
   * Result containing the first operand.
   */
  private Result r1;
  
  /**
   * Result containing the second operand.
   */
  private Result r2;
  
  /**
   * The operator.
   */
  private BOpType op;
  
  public Binary(Var st, Result r1, Result r2, BOpType op)
  {
    this.st = st;
    this.r1 = r1;
    this.r2 = r2;
    this.op = op;
  }
  
  public BOpType getOp()
  {
    return op;
  }
  
  public String getOpStr()
  {
    switch (op)
    {
      case ADD : return "+";
      case SUB : return "-";
      case MULT : return "*";
      case DIV : return "/";
      case MULTU : return "* (u)";
      case DIVU : return "/ (u)";
      case LT : return "<";
      case LE : return "<=";
      case GT : return ">";
      case GE : return ">=";
      case LTU : return "< (u)";
      case LEU : return "<= (u)";
      case GTU : return "> (u)";
      case GEU : return ">= (u)";
      case EQ : return "==";
      default : return "!=";
    }
  }
  
  public String toString()
  {
    return idx + ": " + st + " = " + r1 + " " + getOpStr() + " " + r2 + "; DEFS: " + inMask;
  }
  
  public void getInst(LinkedList<Instruction> ins)
  {
    // Load our variables into separate registers...

    int reg1 = 0;
    int reg2 = 1;
    
    r1.getLoadInst(ins, reg1);
    r2.getLoadInst(ins, reg2);
    
    // Compute the necessary operation, returning the result in our first
    // register...
    
    // Multiplication and division require a more specialized procedure where
    // we compute the result and need to grab the bits of the result out of
    // the $LO register.  So, tackle those cases independently.
    
    if (op == BOpType.MULT || op == BOpType.MULTU || op == BOpType.DIV
      || op == BOpType.DIVU)
    {
      Instruction in;
      
      switch (op)
      {
        case MULT : in = new MultInst(reg1, reg2, false); break;
        case MULTU : in = new MultInst(reg1, reg2, true); break;
        case DIV : in = new DivInst(reg1, reg2, false); break;
        default : in = new DivInst(reg1, reg2, true);
      }
      
      ins.add(in);
      ins.add(new LoadLowBits(reg1));
    }
    else
    {
      ins.add(new BinaryInst(reg1, reg1, reg2, op));
    }
    
    // And finally, write that register into our store variable's memory.
    
    st.getStoreInst(ins, reg1);
  }
  
  public Var getDef()
  {
    return st;
  }
  
  public void addUseCount(VarCounter ct)
  {
    ct.incr(r1.getVar());
    ct.incr(r2.getVar());
  }
  
  public void subUseCount(VarCounter ct)
  {
    ct.decr(r1.getVar());
    ct.decr(r2.getVar());
  }
  
  public boolean isDeadCode(VarCounter ct)
  {
    return !ct.isUsed(st);
  }
  
  public boolean propagate(VarCounter ct, DefMap dm, ThreeAddress [] instArr)
  {
    Result p1 = propagateVar(r1, dm, instArr);
    Result p2 = propagateVar(r2, dm, instArr);
    boolean prop = false;
    
    if (p1 != null)
    {
      ct.decr(r1.getVar());
      ct.incr(p1.getVar());
    
      r1 = p1;
    
      prop = true;
    }
    
    if (p2 != null)
    {
      ct.decr(r2.getVar());
      ct.incr(p2.getVar());
      
      r2 = p2;
      
      prop = true;
    }
    
    return prop;
  }
  
  public ThreeAddress fold()
  {
    // Before we can fold, we need both operands to be immediates.
    
    if (!r1.isImm() || !r2.isImm())
    {
      return this;
    }
    
    // Otherwise, we get the values of the r1 and r2 immediates and perform the
    // corresponding operation.
    
    int v1 = ((Imm) r1).getBits();
    int v2 = ((Imm) r2).getBits();
    
    int newV;
    
    // Also, check that we aren't dividing by zero.  If we are, refuse to fold.
    
    if (v2 == 0 && (op == BOpType.DIV || op == BOpType.DIVU))
    {
      return this;
    }
    
    // Now, perform the folding based on the chosen operation.
    
    switch (op)
    {
      case ADD : newV = v1 + v2; break;
      case SUB : newV = v1 - v2; break;
      case MULT : newV = v1 * v2; break;
      case DIV : newV = v1 / v2; break;
      case LT : newV = v1 < v2 ? 1 : 0; break;
      case LE : newV = v1 <= v2 ? 1 : 0; break;
      case GT : newV = v1 > v2 ? 1 : 0; break;
      case GE : newV = v1 >= v2 ? 1 : 0; break;
      case MULTU : newV = FoldingUtils.multU(v1, v2); break;
      case DIVU : newV = FoldingUtils.divU(v1, v2); break;
      case LTU : newV = FoldingUtils.ltU(v1, v2) ? 1 : 0; break;
      case LEU : newV = FoldingUtils.lteU(v1, v2) ? 1 : 0; break;
      case GTU : newV = FoldingUtils.gtU(v1, v2) ? 1 : 0; break;
      case GEU : newV = FoldingUtils.gteU(v1, v2) ? 1 : 0; break;
      case EQ : newV = v1 == v2 ? 1 : 0; break;
      default : newV = v1 != v2 ? 1 : 0;
    }
    
    // Finally, return an assignment statement with the LHS assigned to the
    // resulting constant.
    
    return new Assign(st, new Imm(newV));
  }
  
  public ThreeAddress algebraicIdentity(VarCounter ct)
  {
    if (r1.isVar() && r2.isVar())
    {
      Var v1 = (Var) r1;
      Var v2 = (Var) r2;
      
      if (v1.equals(v2))
      {
        if (op == BOpType.SUB)
        {
          // Identity 1: x - x = 0.
          
          subUseCount(ct);
          return new Assign(st, Imm.IMM_ZERO);
        }
        else if (op == BOpType.DIV || op == BOpType.DIVU)
        {
          // Identity 2: x / x = 1.
          
          subUseCount(ct);
          return new Assign(st, Imm.IMM_ONE);
        }
        else if (op == BOpType.EQ || op == BOpType.GE || op == BOpType.GEU
          || op == BOpType.LE || op == BOpType.LEU)
        {
          // Identity 3: x == x = 1.
          
          subUseCount(ct);
          return new Assign(st, Imm.IMM_ONE);
        }
        else if (op == BOpType.NE || op == BOpType.GT || op == BOpType.GTU
          || op == BOpType.LT || op == BOpType.LTU)
        {
          // Identity 4: x != x = 0.
          
          subUseCount(ct);
          return new Assign(st, Imm.IMM_ZERO);
        }
      }
      
      return this;
    }
    
    Var var;
    int v;
    
    if (r1.isVar() && r2.isImm())
    {
      var = (Var) r1;
      v = ((Imm) r2).getBits();
    }
    else if (r2.isVar() && r1.isImm())
    {
      var = (Var) r2;
      v = ((Imm) r1).getBits();
    }
    else
    {
      return this;
    }
    
    if (v == 0)
    {
      if (op == BOpType.ADD)
      {
        // Identity 5: x + 0 = x.
        
        return new Assign(st, var);
      }
      else if (op == BOpType.MULT || op == BOpType.MULTU)
      {
        // Identity 6: x * 0 = 0.
        
        subUseCount(ct);
        return new Assign(st, Imm.IMM_ZERO);
      }
    }
    else if (v == 1)
    {
      if (op == BOpType.MULT || op == BOpType.MULTU)
      {
        // Identity 7: x * 1 = x.
        
        return new Assign(st, var);
      }
      else if (op == BOpType.DIV || op == BOpType.DIVU)
      {
        // Identity 8: x / 1 = x.
        
        return new Assign(st, var);
      }
    }
    
    return this;
  }
}