package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;

import error.Error;
import mips.Instruction;

/**
 * Main.java
 * 
 * @version 1.0
 * 
 * @author Brendan Donohoe
 *
 * Entry class for Spike 6.  We get the input from the command line and run the
 * compiler, printing any errors that we get back to standard error, and the
 * assembly instructions we generate to standard output.
 */

public class Main
{
  public static void main(String args[])
  {
    try
    {
      // We begin by setting up our input stream.
      
      boolean optFlag = false;
      Reader in = null;
      
      if (args.length == 0)
      {
        in = new BufferedReader(new InputStreamReader(System.in));
      }
      else if (args.length == 1 && args[0].equals("-o"))
      {
        in = new BufferedReader(new InputStreamReader(System.in));
        optFlag = true;
      }
      else if (args.length == 1)
      {
        in = new BufferedReader(new FileReader(new File(args[0])));
      }
      else if (args.length == 2 && args[0].equals("-o"))
      {
        in = new BufferedReader(new FileReader(new File(args[1])));
        optFlag = true;
      }
      else
      {
        System.err.println("Program must take 0 or 1 argument(s).");
        System.exit(-1);
      }
      
      // Now, run our compiler.
      
      Compiler cm = new Compiler(in, optFlag);
      
      LinkedList<Instruction> ins = new LinkedList<Instruction>();
      LinkedList<Error> errors = new LinkedList<Error>();
      
      cm.run(ins, errors);
      
      // If we have any errors, print an error report consisting of all
      // encountered errors.
      
      int numErrors = errors.size();
      
      if (numErrors > 0)
      {
        System.err.println("ERROR REPORT: " + numErrors + " error(s):");
        
        while (!errors.isEmpty())
        {
          System.err.println(errors.remove());
        }
      }
      else
      {
        // Otherwise, we print our MIPS assembly code.  First, we print our
        // header.
        
        System.out.println(".text");
        System.out.println(".align 4");
        System.out.println(".globl main");
        System.out.println("main:");
        
        // Next, we print our instructions.
        
        for (Instruction i : ins)
        {
          System.out.println(i);
        }
      }
      
      System.exit(Math.min(numErrors, 10));
    }
    catch (FileNotFoundException e)
    {
      System.err.println("File not found!");
      System.exit(-1);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
  }
}