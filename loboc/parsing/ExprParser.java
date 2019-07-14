package parsing;

import java.io.IOException;

import symtable.SymbolData;
import symtable.SymbolTable;
import error.UnexpectedError;
import expr.AddressExpr;
import expr.ArithExpr;
import expr.ArithExpr.ArithOp;
import expr.ArrayExpr;
import expr.AssignExpr;
import expr.CondExpr;
import expr.EqExpr;
import expr.EqExpr.EqOp;
import expr.ErrorExpr;
import expr.Expr;
import expr.Identifier;
import expr.ListExpr;
import expr.LogExpr;
import expr.LogExpr.LogOp;
import expr.NegExpr;
import expr.Num;
import expr.PointerExpr;
import expr.PostExpr;
import expr.PreExpr;
import expr.PreExpr.PreOp;
import expr.RelExpr;
import expr.RelExpr.RelOp;
import expr.PostExpr.PostOp;
import lexing.LitToken.LitType;
import lexing.Tokenizer;

/**
 * ExprParser.java
 * 
 * @version 1.1
 * 
 * @author Brendan Donohoe
 *
 * This class provides methods to parse expressions defined by the spike2
 * grammar, slightly modified as follows:
 * 
 * MODIFIED SPIKE2 GRAMMAR:
 * 
 * SPIKE2       <- (LIST_EXPR ";")* EOF
 * LIST_EXPR    <- ASGN_EXPR ("," ASGN_EXPR)*
 * ASGN_EXPR    <- (POSTFIX_EXPR "=")* COND_EXPR // right-associative
 * COND_EXPR    <- LOGOR_EXPR ("?" LIST_EXPR ":" LOGOR_EXPR)*
 * LOGOR_EXPR   <- LOGAND_EXPR ("||" LOGAND_EXPR)*
 * LOGAND_EXPR  <- EQ_EXPR ("&&" EQ_EXPR)*
 * EQ_EXPR      <- REL_EXPR (EQ_OP REL_EXPR)*
 * REL_EXPR     <- SIMPLE_EXPR (REL_OP SIMPLE_EXPR)*
 * SIMPLE_EXPR  <- TERM (TERM_OP TERM)*
 * TERM         <- FACTOR (FACTOR_OP FACTOR)*
 * FACTOR       <- PREUN_OP* POSTFIX_EXPR
 * POSTFIX_EXPR <- PRIMARY_EXPR (POSTUN_OP | "[" LIST_EXPR? "]")*
 * PRIMARY_EXPR <- IDENTIFIER | NUMBER | "(" LIST_EXPR ")"
 *
 * TERM_OP   <- "+" | "-"
 * FACTOR_OP <- "*" | "/"
 * EQ_OP     <- "==" | "!="
 * REL_OP    <- "<" | "<=" | ">" | ">="
 * PREUN_OP  <- "-" | "--" | "++" | "&"
 * POSTUN_OP <- "--" | "++"
 *
 * IDENTIFER <- ... // part of lexing
 * NUMBER    <- ... // part of lexing
 */

public class ExprParser
{
  /**
   * Parse a single list expression (renamed from EXPR in the original spike2
   * grammar).
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The expression tree, if successful, or null otherwise.
   * @throws IOException
   */
  public static Expr listExpr(Tokenizer to, SymbolTable tb) throws IOException
  {
    // A list expression is an assign expression followed by zero or more
    // sequences of commas and assign expressions.
    
    Expr le = assignExpr(to, tb);
    
    if (le.isError())
    {
      return le;
    }
    
    while (ParsingUtils.isLitType(to.peek(), LitType.COMMA))
    {
      // We've parsed a comma, now we should have an assign expression.
      
      to.read();
      Expr assn = assignExpr(to, tb);
      
      if (assn.isError())
      {
        return assn;
      }
      
      le = new ListExpr(tb, le, assn);
    }
    
    return le;
  }
  
  /**
   * Parse a single assignment expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr assignExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    Expr maybePost = condExpr(to, tb);
    
    if (maybePost.isError())
    {
      return maybePost;
    }
    
    // Do we have a postfix expression on the LHS?
    
    if (maybePost.isPostfix() && ParsingUtils.isLitType(to.peek(),
      LitType.EQUAL))
    {
      // If so, we append an assign expression with this as the LHS to the
      // result.
      
      to.read();
      
      Expr assn = assignExpr(to, tb);
      
      if (assn.isError())
      {
        return assn;
      }
      
      return new AssignExpr(tb, maybePost, assn);
    }
    
    // And if not, we simply return the cond expression we parsed.
    
    return maybePost;
  }
  
  /**
   * Parse a single conditional expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr condExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A conditional expression is a single log-or expression, followed by zero
    // or more sequences of a "?", a list expression, a ":", and a log-or
    // expression.
    
    Expr cond = logOrExpr(to, tb);
    
    if (cond.isError())
    {
      return cond;
    }
    
    while (ParsingUtils.isLitType(to.peek(), LitType.QUEST))
    {
      // We have a "?".  We should now be able to parse a list expression,
      // colon, and log-or expression.
      
      to.read();
      Expr truT = listExpr(to, tb);
      
      if (truT.isError())
      {
        return truT;
      }
      
      if (!ParsingUtils.isLitType(to.peek(), LitType.COLON))
      {
        // We're missing a colon for this ternary expression.  Inform the user
        // of this before returning a failure.
        
        return new ErrorExpr(new UnexpectedError(to.peek(), "\":\""));
      }
      
      to.read();
      Expr flsT = logOrExpr(to, tb);
      
      if (flsT.isError())
      {
        return flsT;
      }
      
      cond = new CondExpr(tb, cond, truT, flsT);
    }
    
    return cond;
  }
  
  /**
   * Parse a single log-or expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr logOrExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A log-or expression is a single log-and expression, followed by zero or
    // more sequences of "||" and log-and expressions.
    
    Expr lgOr = logAndExpr(to, tb);
    
    if (lgOr.isError())
    {
      return lgOr;
    }
    
    while (ParsingUtils.isLitType(to.peek(), LitType.OROR))
    {
      // We've read an "||", now we should be able to read a log-and
      // expression.
      
      to.read();
      Expr lgAnd = logAndExpr(to, tb);
      
      if (lgAnd.isError())
      {
        return lgAnd;
      }
      
      lgOr = new LogExpr(tb, lgOr, lgAnd, LogOp.OR);
    }
    
    return lgOr;
  }
  
  /**
   * Parse a single logAnd expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr logAndExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A log-and expression is a single equality expression, followed by zero
    // or more sequences of "&&"s and equality expressions.
    
    Expr lgAnd = eqExpr(to, tb);
    
    if (lgAnd.isError())
    {
      return lgAnd;
    }
    
    while (ParsingUtils.isLitType(to.peek(), LitType.ANDAND))
    {
      // We've read an "&&", now we should be able to read an equality
      // expression.
      
      to.read();
      Expr eq = eqExpr(to, tb);
      
      if (eq.isError())
      {
        return eq;
      }
      
      lgAnd = new LogExpr(tb, lgAnd, eq, LogOp.AND);
    }
    
    return lgAnd;
  }
  
  /**
   * Parse a single equality expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr eqExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // An equality expression is a single relational expression, followed by
    // zero or more sequences of equality operators and relational expressions.
    
    Expr eq = relExpr(to, tb);
    
    if (eq.isError())
    {
      return eq;
    }
    
    EqOp op = ParsingUtils.getEqOp(to.peek());
    
    while (op != null)
    {
      // We've read an op.  Now, we should be able to read a relational
      // expression.
      
      to.read();
      Expr rel = relExpr(to, tb);
      
      if (rel.isError())
      {
        return null;
      }
      
      eq = new EqExpr(tb, eq, rel, op);
      op = ParsingUtils.getEqOp(to.peek());
    }
    
    return eq;
  }
  
  /**
   * Parse a single relational expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr relExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A relational expression is a single simple expression, followed by zero
    // or more sequences of relational operators and simple expressions.
    
    Expr rel = simpleExpr(to, tb);
    
    if (rel.isError())
    {
      return rel;
    }
    
    RelOp op = ParsingUtils.getRelOp(to.peek());
    
    while (op != null)
    {
      // We've read an op.  Now, we should be able to read a simple expression.
      
      to.read();
      Expr simp = simpleExpr(to, tb);
      
      if (simp.isError())
      {
        return simp;
      }
      
      rel = new RelExpr(tb, rel, simp, op);
      op = ParsingUtils.getRelOp(to.peek());
    }
    
    return rel;
  }
  
  /**
   * Parse a single simple expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr simpleExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A simple expression is a single term expression, followed by zero or
    // more sequences of term operators and term expressions.
    
    Expr simp = termExpr(to, tb);
    
    if (simp.isError())
    {
      return simp;
    }
    
    ArithOp op = ParsingUtils.getTermOp(to.peek());
    
    while (op != null)
    {
      // We've read an op.  Now, we should be able to read a term expression.
      
      to.read();
      Expr term = termExpr(to, tb);
      
      if (term.isError())
      {
        return term;
      }
      
      simp = new ArithExpr(tb, simp, term, op);
      op = ParsingUtils.getTermOp(to.peek());
    }
    
    return simp;
  }
  
  /**
   * Parse a single term expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr termExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A term expression is a single factor expression, followed by zero or
    // more sequences of factor operators and factor expressions.
    
    Expr term = factorExpr(to, tb);
    
    if (term.isError())
    {
      return term;
    }
    
    ArithOp op = ParsingUtils.getFactorOp(to.peek());
    
    while (op != null)
    {
      // We've read an op.  Now, we should be able to read a factor expression.
      
      to.read();
      Expr fact = factorExpr(to, tb);
      
      if (fact.isError())
      {
        return fact;
      }
      
      term = new ArithExpr(tb, term, fact, op);
      op = ParsingUtils.getFactorOp(to.peek());
    }
    
    return term;
  }
  
  /**
   * Parse a single factor expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr factorExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A factor expression is zero or more pre-unary operators, followed by a
    // single postfix expression.  We'll accomplish the task of parsing such an
    // expression recursively.
    
    PreOp op = ParsingUtils.getPreOp(to.peek());
    boolean addressOp = false;
    
    if (op != null)
    {
      // We've read an op.  Just read the next token.
      
      to.read();
    }
    else if (ParsingUtils.isLitType(to.peek(), LitType.AND))
    {
      // We've read an address operator.  Set the addressOp boolean to true and
      // read the next token.
      
      addressOp = true;
      to.read();
    }
    else if (ParsingUtils.isLitType(to.peek(), LitType.MINUS))
    {
      // We've read a negative sign.  Just read the next token.
      
      to.read();
    }
    else
    {
      // We don't see any more pre-unary operators, so we must have reached the
      // postfix expression.  Parse it and return whatever we get from our
      // attempt.
      
      return postfixExpr(to, tb);
    }
    
    Expr fact = factorExpr(to, tb);
    
    if (fact.isError())
    {
      return fact;
    }
    
    if (op != null)
    {
      return new PreExpr(tb, fact, op);
    }
    else if (addressOp)
    {
      return new AddressExpr(tb, fact);
    }
    else
    {
      return new NegExpr(tb, fact);
    }
  }
  
  /**
   * Parse a single postfix expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr postfixExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A postfix expression is one primary expression, followed by zero or more
    // either post-unary operators or array brackets (which may or may not
    // be non-empty).

    Expr post = primaryExpr(to, tb);
    
    if (post.isError())
    {
      return post;
    }
    
    // We've got a primary expression.  Now, as long as we have either post-
    // unary operators or array brackets, we'll concatenate their tree
    // representations to our tree before returning them.
    
    PostOp op = ParsingUtils.getPostUnOp(to.peek());
    
    while (op != null || ParsingUtils.isLitType(to.peek(), LitType.OPNBRK))
    {
      to.read();
      
      if (op != null)
      {
        // The token we read was a post-unary op.  Add this to our tree.
        
        post = new PostExpr(tb, post, op);
      }
      else
      {
        // We read a left square bracket.  Is it empty?  If so, we should see
        // a right square bracket next.
        
        if (ParsingUtils.isLitType(to.peek(), LitType.CLSBRK))
        {
          to.read();
          post = new PointerExpr(tb, post);
        }
        else
        {
          // The array is not empty, so we should read a listExpr followed by a
          // right square bracket.
        
          Expr le = listExpr(to, tb);
        
          if (le.isError())
          {
            return le;
          }
        
          if (!ParsingUtils.isLitType(to.peek(), LitType.CLSBRK))
          {
            // We're missing a right bracket in this expression.  Inform our
            // user of this and exit.

            return new ErrorExpr(new UnexpectedError(to.peek(), "]"));
          }
          
          to.read();
          post = new ArrayExpr(tb, post, le);
        }
      }
      
      op = ParsingUtils.getPostUnOp(to.peek());
    }
    
    return post;
  }
  
  /**
   * Parse a single primary expression from the current tokenizer position.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated (if null, this step will be
   * skipped).
   * @return The result of the attempt.
   * @throws IOException
   */
  public static Expr primaryExpr(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A primary expression in our grammar can be either a number, an
    // identifier, or a parenthesized list expression.
    
    if (ParsingUtils.isNumber(to.peek()))
    {
      return new Num(tb, to.read().getData());
    }
    else if (ParsingUtils.isIdentifier(to.peek()))
    {
      // We have an identifier.  Update the symbol table to denote that this
      // symbol has been used in the program, if it's not null.
      
      String name = to.peek().getData();

      if (tb != null)
      {
        // First, look the SymbolData corresponding to this symbol up in our
        // table and check if it's been declared previously.
        
        SymbolData data = tb.deepGet(name);
        
        if (data != null && data.isDeclared())
        {
          // This symbol has been declared in our table.  Just set its used
          // flag.
          
          data.setUsed();
        }
        else if (data == null)
        {
          // This symbol is not present in our table period.  Construct a new
          // SymbolData object and set its used flag, then stick it in our
          // table - passing in null for our type to mark it undeclared.
          
          data = new SymbolData(null, to.peek().getLineNum(),
            to.peek().getByteNum());
          data.setUsed();
          tb.put(name, data);
        }
      }
      
      to.read();
      
      return new Identifier(tb, name);
    }
    else if (ParsingUtils.isLitType(to.peek(), LitType.OPNPAR))
    {
      to.read();
      Expr le = listExpr(to, tb);
      
      if (le.isError())
      {
        return le;
      }
      
      if (!ParsingUtils.isLitType(to.peek(), LitType.CLSPAR))
      {
        return new ErrorExpr(new UnexpectedError(to.peek(), "\")\""));
      }
      
      to.read();
      return le;
    }
    else
    {
      // We don't have a number, identifier, or left paren needed for a primary
      // expression.  Inform the user of this before returning with a failure.
    
      return new ErrorExpr(new UnexpectedError(to.peek(), "Number",
        "Identifier", "\"(\""));
    }
  }
}