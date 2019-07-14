package defs;

import java.util.LinkedList;

import lexing.Token;

import type.Type;

/**
 * IdDef.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Class representing a variable definition, where a definition has a single
 * name and one or more types.  Admittedly not actually a statement, but
 * similar enough that it's grouped in the smt package as well.
 */
public class IdDef extends Def
{
  private Type type;
  private LinkedList<String> names;
  private Token startToken;
  
  public IdDef(Type type, LinkedList<String> names, Token startToken)
  {
    this.type = type;
    this.names = names;
    this.startToken = startToken;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public Token getStartToken()
  {
    return startToken;
  }
  
  public LinkedList<String> getNames()
  {
    return names;
  }
  
  public String toString()
  {
    return type.toString() + " " + names;
  }
  
  public boolean isError()
  {
    return false;
  }
}