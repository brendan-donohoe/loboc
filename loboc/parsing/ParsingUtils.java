package parsing;

import java.io.IOException;

import type.PrimType;
import type.Type;
import expr.ArithExpr.ArithOp;
import expr.EqExpr.EqOp;
import expr.PostExpr.PostOp;
import expr.PreExpr.PreOp;
import expr.RelExpr.RelOp;
import lexing.LitToken;
import lexing.LitToken.LitType;
import lexing.KeyToken;
import lexing.KeyToken.KeyType;
import lexing.Token;
import lexing.Token.TType;

/**
 * ParsingUtils.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * This class provides several utility methods relevant to the parsing process.
 * These methods are not terribly complicated, and are mainly used to provide a
 * layer of separation between the parser and the underlying structure of the
 * tokens.
 */

class ParsingUtils
{
  /**
   * Given a number, return true if the token represents a number, and false
   * otherwise.
   * @param t - The token to be considered.
   * @return True if the token represents a number, and false otherwise.
   */
  static boolean isNumber(Token t)
  {
    return t.getType() == TType.NUMBER;
  }
  
  /**
   * Given a token, return true if the token represents an identifier, and
   * false otherwise.
   * @param t - The token to be considered.
   * @return True if the token represents an identifier, and false otherwise.
   */
  static boolean isIdentifier(Token t)
  {
    return t.getType() == TType.IDENTIFIER;
  }
  
  /**
   * Given a token, return true if the token represents EOF, and false
   * otherwise.
   * @param t - The token to be considered.
   * @return True if the token represents EOF, and false otherwise.
   */
  static boolean isEOF(Token t)
  {
    return t.getType() == TType.EOF;
  }
  
  /**
   * Given a token and a literal type, return true if the token is a literal
   * token representing a given literal, and false otherwise.
   * @param t - The token to be considered.
   * @param ltype - The specific literal the token must be, as a LitType enum.
   * @return True if the token represents the given literal, and false
   * otherwise.
   */
  static boolean isLitType(Token t, LitType ltype)
  {
    return t.getType() == TType.LITERAL &&
      ((LitToken) t).getLitType() == ltype;
  }
  
  /**
   * Given a token and a literal type, return true if the token is a keyword
   * token representing a given keyword, and false otherwise.
   * @param t - The token to be considered.
   * @param ktype - The specific keyword the token must be, as a KeyType enum.
   * @return True if the token represents the given keyword, and false
   * otherwise.
   */
  static boolean isKeyType(Token t, KeyType ktype)
  {
    return t.getType() == TType.KEYWORD &&
      ((KeyToken) t).getKeyType() == ktype;
  }
  
  /**
   * Given a token, return the PostUnOp enum (as defined in PostfixExpr)
   * corresponding to this token, if applicable, and null otherwise.
   * @param t - The token to be considered.
   * @return The PostUnOp corresponding to the token, if the token does in fact
   * represent a PostUnOp, and null otherwise.
   * @throws IOException
   */
  static PostOp getPostUnOp(Token t) throws IOException
  {
    if (t.getType() != TType.LITERAL)
    {
      return null;
    }
    
    LitToken lt = (LitToken) t;
    
    switch (lt.getLitType())
    {
      case PLUSPLUS : return PostOp.INCR;
      case MINUSMINUS : return PostOp.DECR;
      default : return null;
    }
  }
  
  /**
   * Given a token, return the PreOp enum (as defined in PreExpr) corresponding
   * to this token, if applicable, and null otherwise.
   * @param t - The token to be considered.
   * @return The PreUnOp corresponding to the token, if the token does in fact
   * represent a PreUnOp, and null otherwise.
   * @throws IOException
   */
  static PreOp getPreOp(Token t) throws IOException
  {
    if (t.getType() != TType.LITERAL)
    {
      return null;
    }
    
    LitToken lt = (LitToken) t;
    
    switch (lt.getLitType())
    {
      case MINUSMINUS : return PreOp.DECR;
      case PLUSPLUS : return PreOp.INCR;
      default : return null;
    }
  }
  
  /**
   * Given a token, return the ArithOp enum (as defined in ArithExpr)
   * corresponding to this token, if applicable, and null otherwise.
   * @param t - The token to be considered.
   * @return The FactorOp corresponding to the token, if the token does in fact
   * represent a FactorOp, and null otherwise.
   * @throws IOException
   */
  static ArithOp getFactorOp(Token t) throws IOException
  {
    if (t.getType() != TType.LITERAL)
    {
      return null;
    }
    
    LitToken lt = (LitToken) t;
    
    switch (lt.getLitType())
    {
      case STAR : return ArithOp.MUL;
      case SLASH : return ArithOp.DIV;
      default : return null;
    }
  }
  
  /**
   * Given a token, return the TermOp enum (as defined in SimpleExpr)
   * corresponding to this token, if applicable, and null otherwise.
   * @param t - The token to be considered.
   * @return The TermOp corresponding to the token, if the token does in fact
   * represent a TermOp, and null otherwise.
   * @throws IOException
   */
  static ArithOp getTermOp(Token t) throws IOException
  {
    if (t.getType() != TType.LITERAL)
    {
      return null;
    }
    
    LitToken lt = (LitToken) t;
    
    switch (lt.getLitType())
    {
      case PLUS : return ArithOp.ADD;
      case MINUS : return ArithOp.SUB;
      default : return null;
    }
  }
  
  /**
   * Given a token, return the RelOp enum (as defined in RelExpr)
   * corresponding to this token, if applicable, and null otherwise.
   * @param t - The token to be considered.
   * @return The RelOp corresponding to the token, if the token does in fact
   * represent a RelOp, and null otherwise.
   * @throws IOException
   */
  static RelOp getRelOp(Token t) throws IOException
  {
    if (t.getType() != TType.LITERAL)
    {
      return null;
    }
    
    LitToken lt = (LitToken) t;
    
    switch (lt.getLitType())
    {
      case LSS : return RelOp.LT;
      case LSSEQUAL : return RelOp.LTE;
      case GTR : return RelOp.GT;
      case GTREQUAL : return RelOp.GTE;
      default : return null;
    }
  }
  
  /**
   * Given a token, return the EqOp enum (as defined in EqExpr)
   * corresponding to this token, if applicable, and null otherwise.
   * @param t - The token to be considered.
   * @return The EqOp corresponding to the token, if the token does in fact
   * represent a EqOp, and null otherwise.
   * @throws IOException
   */
  static EqOp getEqOp(Token t) throws IOException
  {
    if (t.getType() != TType.LITERAL)
    {
      return null;
    }
    
    LitToken lt = (LitToken) t;
    
    switch (lt.getLitType())
    {
      case EQUALEQUAL : return EqOp.EQ;
      case NOTEQUAL : return EqOp.NOTEQ;
      default : return null;
    }
  }
  
  /**
   * Given a token, return the primitive type corresponding to this token, if
   * applicable, and null otherwise.
   * @param t - The token to be considered.
   * @return The BasicT corresponding to the token, if the token does in fact
   * represent a BasicT, and null otherwise.
   * @throws IOException
   */
  static Type getPrimType(Token t) throws IOException
  {
    if (t.getType() != TType.KEYWORD)
    {
      return null;
    }
    
    KeyToken kt = (KeyToken) t;
    
    switch (kt.getKeyType())
    {
      case BOOL : return PrimType.BOOL_TYPE;
      case SIGNED : return PrimType.SIGNED_TYPE;
      case UNSIGNED : return PrimType.UNSIGNED_TYPE;
      default : return null;
    }
  }
}