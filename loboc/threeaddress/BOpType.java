package threeaddress;

/**
 * BOpType.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Utility class representing the different types of possible binary
 * operations.
 */
public enum BOpType
{
  // Signed arithmetic operations.
  
  ADD,
  SUB,
  MULT,
  DIV,
  
  // Signed relational operations.
  
  LT,
  LE,
  GT,
  GE,
  
  // Unsigned arithmetic operations.
  
  MULTU,
  DIVU,
  
  // Unsigned relational operations.
  
  LTU,
  LEU,
  GTU,
  GEU,
  
  // Equality comparison operations.
  
  EQ,
  NE;
  
  private String [] insts =
  {
    "addu",
    "subu",
    "mult",
    "div",
    
    "slt",
    "sle",
    "sgt",
    "sge",
    
    "multu",
    "divu",
    
    "sltu",
    "sleu",
    "sgtu",
    "sgeu",
    
    "seq",
    "sne"
  };
  
  public String toString()
  {
    return insts[this.ordinal()];
  }
}