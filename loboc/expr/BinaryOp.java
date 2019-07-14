package expr;

import java.util.LinkedList;

import error.BinaryTypeError;
import error.SemanticError;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.Binary;
import threeaddress.BOpType;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.Type;

/**
 * BinaryOp.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Common interface shared by all nodes representing some operation on two
 * expressions.
 */
public abstract class BinaryOp extends Expr
{
  protected Expr e1;
  protected Expr e2;
  
  public BinaryOp(SymbolTable tb, Expr e1, Expr e2)
  {
    super(tb);
    this.e1 = e1;
    this.e2 = e2;
  }
  
  public String toString(boolean anno)
  {
    String e1Str = e1.toString(anno);
    String e2Str = e2.toString(anno);
    
    String returnStr = "";
    
    if (anno)
    {
      returnStr += " " + type.getShorthand() + ":";
    }
    
    returnStr += "(" + e1Str + getOp() + e2Str + ")";
    return returnStr;
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    // First, we label our children with types.
    
    e1.labelType(errors);
    e2.labelType(errors);
    
    // Next, based on the types of our children, we compute our own type and
    // label ourselves with it.
    
    type = computeType();
    
    // But if we have an error the user probably cares about - that is, one
    // where our type is invalid, yet both of our children have valid types
    // (so that we don't needlessly propagate errors up for all eternity) then
    // we will add it to our list of errors.
    
    if (errors != null && type.isInvalid() && !e1.getType().isInvalid()
      && !e2.getType().isInvalid())
    {
      String op = getOp();
      String [] [] expected = getExpectedTypes();
      errors.add(new BinaryTypeError(op, e1, e2, expected[0], expected[1]));
    }
  }
  
  public Expr fold()
  {
    e1 = e1.fold();
    e2 = e2.fold();
    
    // We want to ensure that our children are numerical constants and that
    // this node is well-typed, so that the fold returns a sensible result.
    
    if (e1.isConstant() && e2.isConstant() && type.isPrimitive())
    {
      Num n1 = (Num) e1;
      Num n2 = (Num) e2;
      
      int bitResult = getFoldResult(n1.getBits(), n2.getBits(), n1.getType(),
        n2.getType());
      
      // Set any expression references to null (no need to worry about tb or
      // type, as these will be reused in the folded node).
      
      e1 = null;
      e2 = null;
      
      return new Num(tb, bitResult, type);
    }
    
    return this;
  }
  
  public Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    // Get the values of each of the left and right sides.  We have three
    // possibilities: both children are non-constants, our left child is a non-
    // constant and our right child is constant, and visa versa.  Each of these
    // cases corresponds to a different three address code.
    
    Var t1 = e1.getValue(tFac, lFac, addresses);
    Var t2 = e2.getValue(tFac, lFac, addresses);
    
    // And add the result to the list of three address expressions, with the
    // appropriate operator.
    
    Var result = tFac.gen(type.getSize(), type.getAlignment());
    
    addresses.add(new Binary(result, t1, t2, getOpType()));
    return result;
  }
  
  /**
   * Get the representation of this binary operator in the input, as a string.
   * @return The representation of the operator in the input.
   */
  public abstract String getOp();
  
  /**
   * Compute the type of this binary expression based on the types of the
   * children (or the invalid primitive type
   * @return The type this expression should have, or INVALID if this
   * expression has no legal typing.
   */
  protected abstract Type computeType();
  
  /**
   * Get a pair of string arrays of different legal types for the left and
   * right operands of the expression, respectively, based on the types they
   * have been labeled with.  Note that, if one of the pairs of string arrays
   * is of length 0, it's assumed that the respective operand was correctly
   * typed.
   * @return The legal types for both operands of this expression, as a pair
   * of string arrays to be printed to output in the event of an error.
   */
  public abstract String [] [] getExpectedTypes();
  
  /**
   * Given two four byte numeric values and the types they represent, compute
   * the four byte representation of the result of this operation.
   * @param n1 - The first four byte value.
   * @param n2 - The second four byte value.
   * @param t1 - The primitive type of the first value.
   * @param t2 - The primitive type of the second value.
   * @return The result of this operation.
   */
  public abstract int getFoldResult(int n1, int n2, Type t1, Type t2);
  
  public abstract BOpType getOpType();
}