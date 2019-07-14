package expr;

import java.util.LinkedList;

import error.SemanticError;
import general.LabelFactory;
import general.TempFactory;
import symtable.SymbolTable;
import threeaddress.ThreeAddress;
import threeaddress.Var;
import type.Type;

/**
 * Expr.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Common class shared by all classes representing nodes in the generated
 * expression tree.
 */

public abstract class Expr
{
  /**
   * The symbol table in the scope in which this expression is present.
   */
  protected SymbolTable tb;
  
  /**
   * The type with which this expression is labeled.
   */
  protected Type type;
  
  public Expr()
  {
    // Default no-args constructor - only used for ErrorExpr.
  }
  
  public Expr(SymbolTable tb)
  {
    this.tb = tb;
  }
  
  /**
   * Return whether or not this expression represents a legal postfix
   * expression by the Spike 2 grammar.
   * @return True if this expression represents a postfix expression by the
   * Spike 2 grammar, and false otherwise.
   */
  public boolean isPostfix()
  {
    return false;
  }
  
  /**
   * Return whether or not this expression represents a numerical constant.
   * @return True if this expression is a Num, and false otherwise.
   */
  public boolean isConstant()
  {
    return false;
  }
  
  /**
   * Determine whether or not this expression is assignable (can be acted upon
   * by the unary ampersand operator, and can appear on the left of an
   * assignment).
   * @return True if this expression is addressable, and false otherwise.
   */
  public boolean isAssignable()
  {
    return false;
  }
  
  /**
   * Determine whether or not this expression is semantically valid ("++" and
   * "--" only act on assignable expressions - only assignable expressions
   * appear on the LHS of assignments).
   * @return True if this expression is semantically valid, and false
   * otherwise.
   */
  public boolean isValid()
  {
    return true;
  }
  
  /**
   * Determine whether or not this expression represents a parsing error.
   * @return True if this expression represents an error, and false otherwise.
   */
  public boolean isError()
  {
    return false;
  }
  
  /**
   * Determine whether or not this expression is an identifier.
   * @return True if this expression is an identifier, and false otherwise.
   */
  public boolean isIdentifier()
  {
    return false;
  }
  
  /**
   * Get this expression's type (assuming it has already been labeled).
   * @return The expression's type (if no legal type could be found by the type
   * propagation rules, this will be an invalid primitive type instead).
   */
  public Type getType()
  {
    return type;
  }
  
  /**
   * During the three address code generation process, get the address of this
   * expression and return the variable that will contain this expression's
   * address in the three address code.
   * @param tFac - Factory object to generate fresh temporaries as needed.
   * @param lFac - Factory object to generate fresh (you guessed it!) labels,
   * as needed.
   * @param addresses - The accumulated list of three address code
   * instructions.
   * @return The variable containing this expression's address.
   */
  public Var getAddress(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses)
  {
    return null;
  }
  
  /**
   * Return a string representation of this expression.
   * @param anno - If true, the string representation is annotated with its
   * associated type, as required by Spike 4's output.
   * @return A type-annotated string representation of this expression.
   */
  public abstract String toString(boolean anno);
  
  /**
   * Have this expression compute its type and label itself with it, and check
   * for any type or other miscellaneous semantic errors along the way (here,
   * attempting to address a non-assignable expression, having a non-assignable
   * expression on the LHS of an assignment expression, having increment or
   * decrement operators act on non-assignable expressions).
   * @param errors - The list of type errors encountered during the type
   * labeling process.
   */
  public abstract void labelType(LinkedList<SemanticError> errors);
  
  /**
   * Perform constant folding over this statement.  This expression must have
   * been type-labeled previously.
   * @return The expression the parents should replace this expression with
   * after folding.
   */
  public abstract Expr fold();
  
  /**
   * During the three address code generation process, get the value of this
   * expression and return the variable that will contain this expression's
   * value in the three address code.
   * @param tFac - Factory object to generate fresh temporaries as needed.
   * @param lFac - Factory object to generate fresh (you guessed it!) labels,
   * as needed.
   * @param addresses - The accumulated list of three address code
   * instructions.
   * @return The variable containing this expression's value.
   */
  public abstract Var getValue(TempFactory tFac, LabelFactory lFac,
    LinkedList<ThreeAddress> addresses);
}