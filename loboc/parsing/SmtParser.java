package parsing;

import java.io.IOException;
import java.util.LinkedList;

import defs.Def;
import defs.ErrorDef;
import defs.IdDef;
import error.DefFoldError;
import error.DupDefError;
import error.ParseError;
import error.UnexpectedError;
import expr.ErrorExpr;
import expr.Expr;
import lexing.KeyToken.KeyType;
import lexing.LitToken.LitType;
import lexing.Token;
import lexing.Tokenizer;
import smt.BlockSmt;
import smt.ErrorSmt;
import smt.ExprSmt;
import smt.IfSmt;
import smt.Smt;
import smt.WhileSmt;
import symtable.SymbolData;
import symtable.SymbolTable;
import type.ArrayType;
import type.PointerType;
import type.Type;

public class SmtParser
{
  /**
   * Parse a block.
   * @param to - The tokenizer to be read from.
   * @param errors - The list of parsing errors encountered.
   * @return The parsed statement.
   * @throws IOException
   */
  public static Smt topBlock(Tokenizer to, LinkedList<ParseError> errors)
    throws IOException
  {
    Token startToken = to.peek();
    
    // Instantiate our topmost symbol table.
    
    SymbolTable tb = new SymbolTable();
    
    // Begin by parsing our definitions.
    
    LinkedList<Def> defs = defs(to, tb, errors); 
    
    // Now that we've parsed our definitions, parse our statements.
    
    boolean error = true;
    LinkedList<Smt> smts = new LinkedList<Smt>();
    
    while (error)
    {
      error = false;
      
      LinkedList<Smt> smtsTemp = statements(to, tb, errors);
      smts.addAll(smtsTemp);
      
      // There are two possibilities once the call to statements returns at the
      // top level.  Either we are at EOF (in which case, all is good) or we
      // are at a closing brace (in which case we're missing a corresponding
      // open brace).
      
      if (ParsingUtils.isLitType(to.peek(), LitType.CLSBRC))
      {
        // We have one too many closing braces.  Report this as an error, read
        // it (no further recovery is necessary since we're already in a
        // closing brace), and continue parsing.
        
        error = true;
        
        errors.add(new UnexpectedError(to.peek(), "Statement", "$EOF"));
        
        to.read();
      }
    }
    
    return new BlockSmt(defs, smts, tb, startToken);
  }
  
  /**
   * Parse a block.
   * @param to - The tokenizer to be read from.
   * @param errors - The list of parsing errors encountered.
   * @return The parsed statement.
   * @throws IOException
   */
  public static Smt block(Tokenizer to, SymbolTable tb,
    LinkedList<ParseError> errors) throws IOException
  {
    Token startToken = to.peek();
    
    // Instantiate a new symbol table with the table we just passed in as its
    // parent.
    
    SymbolTable child = new SymbolTable(tb);
    
    // Begin by parsing our definitions.
    
    LinkedList<Def> defs = defs(to, child, errors);
    
    // Now that we've parsed our definitions, parse our statements.
    
    LinkedList<Smt> smts = statements(to, child, errors);
    
    // Finally, return the result.
    
    BlockSmt blk = new BlockSmt(defs, smts, child, startToken);
    
    return blk;
  }
  
  /**
   * Attempt to parse a sequence of zero or more definitions.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated.
   * @param errors - The list of parsing errors encountered.
   * @return The parsed definitions.
   * @throws IOException
   */
  public static LinkedList<Def> defs(Tokenizer to, SymbolTable tb,
    LinkedList<ParseError> errors) throws IOException
  {
    LinkedList<Def> results = new LinkedList<Def>();
    
    // A definition always begins with a type specifier, so as long as we see a
    // type specifier, we have another type to parse (assuming valid input).
    
    Def errorDef;
    
    do
    {
      errorDef = null;
      
      while (ParsingUtils.getPrimType(to.peek()) != null &&
        errorDef == null)
      {
        Def def = def(to, tb);
        
        if (def.isError())
        {
          errorDef = def;
        }
        else
        {
          results.add(def);
        }
      }
      
      if (errorDef != null)
      {
        // Something went wrong when parsing our definition - print the error
        // and perform error recovery.
        
        errors.add(((ErrorDef) errorDef).getErr());
        
        // Throw out tokens until we reach either a semicolon or EOF.
        
        while (!ParsingUtils.isLitType(to.peek(), LitType.SEMI) &&
          !ParsingUtils.isEOF(to.peek()))
        {
          to.read();
        }
        
        // If we reach an EOF, signal an additional error.
        
        if (ParsingUtils.isEOF(to.peek()))
        {
          errors.add(new UnexpectedError(to.peek(), "\";\""));
        }
        
        to.read();
      }
    }
    while (errorDef != null);
    
    return results;
  }
  
  /**
   * Attempt to parse a definition, and, if successful, update the SymbolTable
   * with the relevant information for these declared variables.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated.
   * @return The result of attempting to parse the given definition.
   */
  public static Def def(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // A definition consists of a single type specifier, followed by one or
    // more sequences of identifier names and commas, and ending with a
    // semicolon.  We'll also update our symbol table with the information we
    // receive.
    
    Token startToken = to.peek();  
    
    Type type = ParsingUtils.getPrimType(to.peek());

    if (type == null)
    {
      return new ErrorDef(new UnexpectedError(to.peek(), "signed", "unsigned",
        "bool"));
    }
    
    // We've read a type name.  Now we should expect to read zero or more
    // sequences of square brackets with optional contents.
    
    to.read();
    while (ParsingUtils.isLitType(to.peek(), LitType.OPNBRK))
    {
      to.read();
      
      // Are the brackets empty?
      
      if (ParsingUtils.isLitType(to.peek(), LitType.CLSBRK))
      {
        // Yes they are.
        
        to.read();
        type = new PointerType(type);
      }
      else
      {
        // No, they are not - parse the contained expression (but don't worry
        // about updating the symbol table if there are identifiers in the
        // square brackets, passing null as the second parameter will cause
        // the expression parser to skip the process of updating the table).
        
        // We'll save the token at which this expression begins in case the
        // folding process fails and we need to signal an error, with the
        // location of the faulty expression.
        
        Token exprToken = to.peek();
        
        Expr len = ExprParser.listExpr(to, null);
        
        if (len.isError())
        {
          return new ErrorDef((ErrorExpr) len);
        }
        
        // Next, attempt to constant fold the contained expression.
        
        len.labelType(null);
        len = len.fold();
        
        // And now we check - did the interior of this array type fold to a
        // numerical constant?  If not, we throw an error.
        
        if (!len.isConstant())
        {
          return new ErrorDef(new DefFoldError(exprToken, len));
        }
        
        type = new ArrayType(type, len);
        
        // And now we should expect a closing bracket.
        
        if (!ParsingUtils.isLitType(to.peek(), LitType.CLSBRK))
        {
          return new ErrorDef(new UnexpectedError(to.peek(), "\"]\""));
        }
        
        to.read();
      }
    }

    // Now that we have our type information, we'll parse our variable names
    // and add entries for those to our table.  As we do so, we will check each
    // variable name we read to see if it has a conflicting name with another
    // variable in the symbol table we've passed in.
    
    boolean idParsed = false;
    
    // We'll also store the names of the different variables we have declared
    // in this single definition, so that we can add this information to the
    // AST representation of our DEF.
    
    LinkedList<String> names = new LinkedList<String>();
    
    do
    {
      if (idParsed)
      {
        // This isn't our first identifier we've parsed, so read the
        // intermediary comma.
        
        to.read();
      }
      
      // We need an identifier.
      
      if (!ParsingUtils.isIdentifier(to.peek()))
      {
        return new ErrorDef(new UnexpectedError(to.peek(), "Identifier"));
      }
      
      String name = to.peek().getData();
      SymbolData data = new SymbolData(type, to.peek().getLineNum(),
        to.peek().getByteNum());
      SymbolData dup;
      
      if ((dup = tb.get(name)) != null)
      {
        // We have already declared this variable somewhere else in this scope.
        // Return an error.
        
        return new ErrorDef(new DupDefError(to.peek(), dup));
      }
      else
      {
        // Otherwise, we'll add the variable along with its type information to
        // our table, and we'll also add the variable's name to our list of
        // names.
        
        tb.put(name, data);
        names.add(name);
      }
      
      to.read();
      idParsed = true;
    }
    while (ParsingUtils.isLitType(to.peek(), LitType.COMMA));
    
    // Lastly, we need a semicolon.
    
    if (!ParsingUtils.isLitType(to.peek(), LitType.SEMI))
    {
      return new ErrorDef(new UnexpectedError(to.peek(), "\";\""));
    }
    
    to.read();
    
    // Finally, return the def in a parse result.
    
    return new IdDef(type, names, startToken);
  }
  
  /**
   * Attempt to parse a sequence of statements of the form either EXPR ";" or
   * "{" STATEMENTS "}".  We will also perform error recovery in this method as
   * necessary, and return a list of all the successfully parsed statements, as
   * well as the number of times error recovery was performed.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated.
   * @param errors - The list of parsing errors encountered.
   * @return The parsed statements.
   */
  public static LinkedList<Smt> statements(Tokenizer to, SymbolTable tb,
    LinkedList<ParseError> errors) throws IOException
  {
    LinkedList<Smt> results = new LinkedList<Smt>();
    
    Smt errorSmt = null;
    
    do
    {
      errorSmt = null;
      
      // The parsing of a legal set of statements always ends with either a
      // closing brace (if this was not at called at the top level) or EOF (if
      // this was called at the top level).
      
      while (!ParsingUtils.isLitType(to.peek(), LitType.CLSBRC) &&
        !ParsingUtils.isEOF(to.peek()) && errorSmt == null)
      {
        // We should have at least one statement at the front of our input.
        // Parse it.
        
        Smt smt = statement(to, tb, errors);
        
        // Otherwise, we look to see if the statement we attempted to parse was
        // successfully parsed.  If so, we add it to our output.  If not, we
        // set ourselves up for error recovery.
        
        if (!smt.isError())
        {
          results.add(smt);
        }
        else
        {
          errorSmt = smt;
        }
      }
      
      if (errorSmt != null)
      {
        // Did we experience an error when parsing the previous statement?  If
        // so, we'll add the error to our list of errors, perform statement
        // error recovery, and keep on truckin'.
        
        errors.add(((ErrorSmt) errorSmt).getErr());
        
        // Now we'll recover to the nearest semicolon, closing brace, or EOF
        // (signaling additional errors in the latter two cases).
        
        while (!ParsingUtils.isLitType(to.peek(), LitType.SEMI)
          && !ParsingUtils.isLitType(to.peek(), LitType.CLSBRC)
          && !ParsingUtils.isEOF(to.peek()))
        {
          to.read();
        }
        
        if (ParsingUtils.isLitType(to.peek(), LitType.CLSBRC)
          || ParsingUtils.isEOF(to.peek()))
        {
          errors.add(new UnexpectedError(to.peek(), "\";\""));
        }
        
        to.read();
      }
    }
    while (errorSmt != null);
    
    return results;
  }
  
  /**
   * Attempt to parse a single statement.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated.
   * @param errors - The list of parsing errors encountered.
   * @return The parsed statement.
   * @throws IOException
   */
  public static Smt statement(Tokenizer to, SymbolTable tb,
    LinkedList<ParseError> errors) throws IOException
  {
    // We have four possibilities - a statement of the form "{" BLOCK "}",
    // a while statement, an if statement, or a statement of the form EXPR ";".
    
    if (ParsingUtils.isLitType(to.peek(), LitType.OPNBRC))
    {
      // We've got a statement of the form "{" BLOCK "}".
      
      to.read();
      
      Smt blk = block(to, tb, errors);
      
      // We need the closing brace corresponding to this open brace (if the
      // kill switch is triggered, our tokenizer is currently at EOF, so we'll
      // immediately return from this method anyway - the error itself that we
      // return with it doesn't actually matter).
      
      if (!ParsingUtils.isLitType(to.peek(), LitType.CLSBRC))
      {
        return new ErrorSmt(new UnexpectedError(to.peek(), "\"}\""));
      }
      
      to.read();
      return blk;
    }
    else if (ParsingUtils.isKeyType(to.peek(), KeyType.WHILE))
    {
      // We have a while statement.
      
      return whileSmt(to, tb, errors);
    }
    else if (ParsingUtils.isKeyType(to.peek(), KeyType.IF))
    {
      // We have an if statement.
      
      return ifSmt(to, tb, errors);
    }
    else
    {
      // Otherwise, we have a statement of the form EXPR ";".
      
      return exprSmt(to, tb);
    }
  }
  
  /**
   * Attempt to parse a statement of the form EXPR ";".
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated, if successful.
   * @return The parsed statement.
   * @throws IOException
   */
  public static Smt exprSmt(Tokenizer to, SymbolTable tb)
    throws IOException
  {
    // An expression statement is an expression followed by a semicolon.
    
    // We'll use a temporary table to store the updates we will get to our
    // symbol table by parsing the expression.  If and only if we parse this
    // expression statement without error, we will perform a mass update to the
    // table we passed in, using the entries of the temporary table.
    
    Token startToken = to.peek();
    
    Expr le = ExprParser.listExpr(to, tb);
    
    if (le.isError())
    {
      return new ErrorSmt((ErrorExpr) le);
    }
    
    // We got the expression, now let's check for the semicolon.
    
    if (!ParsingUtils.isLitType(to.peek(), LitType.SEMI))
    {
      return new ErrorSmt(new UnexpectedError(to.peek(), "\";\""));
    }
    
    to.read();
    
    return new ExprSmt(le, startToken);
  }
  
  /**
   * Attempt to parse a single while statement.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated.
   * @param errors - The list of parsing errors encountered.
   * @return The parsed statement.
   * @throws IOException
   */
  public static Smt whileSmt(Tokenizer to, SymbolTable tb,
    LinkedList<ParseError> errors) throws IOException
  {
    // A while statement is the keyword "while", a left parenthesis, an
    // expression, a right parenthesis, followed by a statement.
    
    Token startToken = to.peek();
    
    if (!ParsingUtils.isKeyType(to.peek(), KeyType.WHILE))
    {
      return new ErrorSmt(new UnexpectedError(to.peek(), "while"));
    }
    
    // Next, we should have a parenthesized condition.
    
    to.read();
    
    if (!ParsingUtils.isLitType(to.peek(), LitType.OPNPAR))
    {
      return new ErrorSmt(new UnexpectedError(to.peek(), "\"(\""));
    }
    
    to.read();
    
    Expr expr = ExprParser.listExpr(to, tb);
    
    if (expr.isError())
    {
      return new ErrorSmt((ErrorExpr) expr);
    }
    
    if (!ParsingUtils.isLitType(to.peek(), LitType.CLSPAR))
    {
      return new ErrorSmt(new UnexpectedError(to.peek(), "\")\""));
    }
    
    to.read();
    
    // Next, we should have a single statement comprising the body of the while
    // loop.
    
    Smt smt = statement(to, tb, errors);
    
    // Did we have a well-formed while body?  If not, just return the recovery
    // state from the statement.
    
    if (smt.isError())
    {
      return smt;
    }
    
    // Otherwise, construct a new parse result, put it in a recovery state, and
    // return it.
    
    return new WhileSmt(expr, smt, startToken);
  }
  
  /**
   * Attempt to parse a single if statement.
   * @param to - The tokenizer to be read from.
   * @param tb - The symbol table to be updated.
   * @param errors - The list of parsing errors encountered.
   * @return The parsed statement.
   * @throws IOException
   */
  public static Smt ifSmt(Tokenizer to, SymbolTable tb,
    LinkedList<ParseError> errors) throws IOException
  {
    // An if statement is the keyword "if", a left parenthesis, an expression,
    // a right parenthesis, followed by a statement.  Optionally, we may also
    // have an "else" keyword followed by another statement.
    
    Token startToken = to.peek();  
    
    if (!ParsingUtils.isKeyType(to.peek(), KeyType.IF))
    {
      return new ErrorSmt(new UnexpectedError(to.peek(), "if"));
    }
    
    // Next, we should have a parenthesized condition.
   
    to.read();
    
    if (!ParsingUtils.isLitType(to.peek(), LitType.OPNPAR))
    {
      return new ErrorSmt(new UnexpectedError(to.peek(), "\"(\""));
    }
    
    to.read();
    
    Expr expr = ExprParser.listExpr(to, tb);
	    
    if (expr.isError())
    {
      return new ErrorSmt((ErrorExpr) expr);
    }
	    
    if (!ParsingUtils.isLitType(to.peek(), LitType.CLSPAR))
    {
      return new ErrorSmt(new UnexpectedError(to.peek(), "\")\""));
    }
    
    to.read();
    
    // Next, we should have a single statement representing the case where
    // the condition is true.
    
    Smt truS = statement(to, tb, errors);
    
    // Did we have a well-formed clause?  If not, just return the recovery
    // state from the statement.
    
    if (truS.isError())
    {
      return truS;
    }
    
    // Now, check to see if we have an "else" keyword.  If we do not, we have
    // a simple if statement with a single clause - return it.
    
    if (!ParsingUtils.isKeyType(to.peek(), KeyType.ELSE))
    {
      return new IfSmt(expr, truS, startToken);
    }
    
    // Otherwise, read the "else" followed by yet another statement.
    
    to.read();
    
    Smt flsS = statement(to, tb, errors);
    
    if (flsS.isError())
    {
      return flsS;
    }
    
    // And return the statement.
    
    return new IfSmt(expr, truS, flsS, startToken);
  }
}