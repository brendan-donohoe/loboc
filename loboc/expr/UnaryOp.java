package expr;

import java.util.LinkedList;

import symtable.SymbolTable;
import type.Type;
import error.SemanticError;
import error.UnaryTypeError;

/**
 * UnaryOp.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Common interface shared by all nodes representing some operation on a single
 * expression.
 */
public abstract class UnaryOp extends Expr
{
  protected Expr e;
  
  public UnaryOp(SymbolTable tb, Expr e)
  {
    super(tb);
    this.e = e;
  }
  
  public String toString(boolean anno)
  {
    String eStr = e.toString(anno);
    
    String returnStr = "";
    
    if (anno)
    {
      returnStr += " " + type.getShorthand() + ":";
    }
    
    returnStr += "(" + getOp() + eStr + ")";
    return returnStr;
  }
  
  public void labelType(LinkedList<SemanticError> errors)
  {
    // First, we label our child with a type.
    
    e.labelType(errors);
    
    // Then, based on our child's type, we compute our own type.
    
    type = computeType();
    
    if (errors != null && type.isInvalid() && !e.getType().isInvalid())
    {
      String op = getOp();
      String [] expected = getExpectedTypes();
      errors.add(new UnaryTypeError(op, e, expected));
    }
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
   * Get a string array of different legal types for the operand of this
   * operation.
   * @return The legal types for the left operand of this expression, as
   * strings to be printed to output in the event of an error.
   */
  public abstract String [] getExpectedTypes();
}