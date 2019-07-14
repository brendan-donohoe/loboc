package expr;

import general.LabelFactory;
import general.TempFactory;

import java.util.LinkedList;

import symtable.SymbolTable;
import threeaddress.Assign;
import threeaddress.BranchType;
import threeaddress.CondGoto;
import threeaddress.Goto;
import threeaddress.Imm;
import threeaddress.Label;
import threeaddress.ThreeAddress;
import threeaddress.BOpType;
import threeaddress.Var;
import type.InvalidType;
import type.PrimType;
import type.Type;

/**
 * LogExpr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing binary operations (AND and OR) between two boolean
 * expressions.
 */
public class LogExpr extends BinaryOp
{
  private LogOp op;
  
  public LogExpr(SymbolTable tb, Expr e1, Expr e2, LogOp op)
  {
    super(tb, e1, e2);
    this.op = op;
  }
  
  protected Type computeType()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    if (t1.isBool() && t2.isBool())
    {
      return PrimType.BOOL_TYPE;
    }
    else
    {
      return InvalidType.INVALID_TYPE;
    }
  }
  
  public String getOp()
  {
    return op == LogOp.AND ? "&&" : "||";
  }
  
  public String [] [] getExpectedTypes()
  {
    Type t1 = e1.getType();
    Type t2 = e2.getType();
    
    String [] [] arr = new String [2] [];
    
    // This is relatively simple - both operands need to be of type bool.
    
    if (t1.isBool())
    {
      // t2 was not of type bool.
      
      String [] arr1 = {};
      String [] arr2 = {"bool"};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else if (t2.isBool())
    {
      // t1 was not of type bool.
      
      String [] arr1 = {"bool"};
      String [] arr2 = {};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    else
    {
      // Neither was of type bool.
      
      String [] arr1 = {"bool"};
      String [] arr2 = {"bool"};
      arr[0] = arr1;
      arr[1] = arr2;
    }
    
    return arr;
  }
  
  public int getFoldResult(int n1, int n2, Type t1, Type t2)
  {
    boolean result = false;  
    
    if (op == LogOp.AND)
    {
      result = n1 != 0 && n2 != 0;
    }
    else
    {
      result = n1 != 0 || n2 != 0;
    }
    
    // Since a boolean is true if it's nonzero, and false otherwise, this is
    // simply the following.
    
    return result ? 1 : 0;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // First, get the value of the condition and put it in a temp.
    
    Var e1Val = e1.getValue(tFac, lFac, addresses);
    Var t1 = tFac.gen(e1.getType().getSize(), e1.getType().getAlignment());
    addresses.add(new Assign(t1, e1Val));
    
    // Now, we'll set up two labels to perform short-circuit evaluation.
    
    String circL = lFac.gen();
    String endL = lFac.gen();
    
    // We'll also generate a temporary for the final result of our expression.
    
    Var result = tFac.gen(type.getSize(), type.getAlignment());
    
    if (op == LogOp.AND)
    {
      // Jump ahead if the argument is false.
      
      addresses.add(new CondGoto(t1, circL, BranchType.EQZ));
    }
    else
    {
      // Jump ahead if the argument is true.
      
      addresses.add(new CondGoto(t1, circL, BranchType.NEZ));
    }
    
    // Next, generate the result for evaluation of the second argument and put
    // any postfix operations encountered during this process into a separate
    // list, which we'll later add, along with a conditional jump, to the main
    // list of postfix operations.
    
    Var t2 = e2.getValue(tFac, lFac, addresses);
    
    // Generate the final branch and possible assignments for each operator.
    
    if (op == LogOp.AND)
    {
      addresses.add(new CondGoto(t2, circL, BranchType.EQZ));
      addresses.add(new Assign(result, Imm.IMM_ONE));
    }
    else
    {
      addresses.add(new CondGoto(t2, circL, BranchType.NEZ));
      addresses.add(new Assign(result, Imm.IMM_ZERO));
    }
    
    addresses.add(new Goto(endL));
    addresses.add(new Label(circL));
    
    if (op == LogOp.AND)
    {
      addresses.add(new Assign(result, Imm.IMM_ZERO));
    }
    else
    {
      addresses.add(new Assign(result, Imm.IMM_ONE));
    }
    
    addresses.add(new Label(endL));
    
    return result;
  }
  
  public BOpType getOpType()
  {
    // Unused.

    return null;
  }
  
  public static enum LogOp
  {
    AND,
    OR
  };
}