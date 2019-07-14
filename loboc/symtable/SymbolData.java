package symtable;

import type.Type;

/**
 * SymbolData.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * SymbolData objects are the values mapped to by the keys of the symbol table.
 * Right now, this object just holds two key pieces of information: the
 * identifier's type (which is null if the symbol has not been declared) and a
 * flag determining whether or not the given identifier has actually appeared
 * in a statement.
 */
public class SymbolData
{
  /**
   * The type of the symbol (or null if undeclared).
   */
  private Type type;
  
  /**
   * Flag denoting whether or not the symbol has been used in the code.
   */
  private boolean used;
  
  /**
   * The line number at which this symbol was first encountered.
   */
  private long lineNum;
  
  /**
   * The byte number at which this symbol was first encountered.
   */
  private long byteNum;
  
  /**
   * This variable's memory offset from the stack base pointer.
   */
  private int offset;
  
  /**
   * A unique id assigned to this variable, so as to differentiate between
   * variables with the same names in different scopes - particularly for three
   * address code generation.
   */
  private int id;
  
  public SymbolData(Type type, long lineNum, long byteNum)
  {
    this.type = type;
    this.used = false;
    this.lineNum = lineNum;
    this.byteNum = byteNum;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public boolean isUsed()
  {
    return used;
  }
  
  public void setUsed()
  {
    this.used = true;
  }
  
  public long getLineNum()
  {
    return lineNum;
  }
  
  public long getByteNum()
  {
    return byteNum;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public void setOffset(int offset)
  {
    this.offset = offset;
  }
  
  public int getId()
  {
    return id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public boolean isDeclared()
  {
    return type != null;
  }
  
  public String toString()
  {
    if (!isDeclared())
    {
      return "undeclared unknown";
    }
    else if (!isUsed())
    {
      return "unused " + type.toString() + " " + offset;
    }
    else
    {
      return "okay " + type.toString() + " " + offset;
    }
  }
}