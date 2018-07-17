/*
 * Jatha - a Common LISP-compatible LISP library in Java.
 * Copyright (C) 1997-2005 Micheal Scott Hewett
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * For further information, please contact Micheal Hewett at
 *   hewett@cs.stanford.edu
 *
 */

package org.jatha.read;

import java.io.*;
import java.math.BigInteger;

import java.util.regex.Pattern;
import org.jatha.dynatype.*;
import org.jatha.Jatha;
import org.jatha.compile.CompilerException;


/**
 * A parser that reads LISP-syntax text from a text
 * stream or string.  It recognizes all standard
 * LISP datatypes, although not structured ones.
 * This function is designed to fulfill the function
 * of the reader in a LISP <tt>read-eval-print</tt> loop.
 *
 * Once the LISP parser is instantiated, the
 * <tt>parse()</tt> function can be used to read from
 * a string or stream.
 *
 * It is best not to instantiate the LispParser yourself.
 * Instead, do the following:
 * <pre>
 *   1.  LispParser parser = Jatha.getParser();
 *   2.  parser.setInputString(myString);
 *   3.  parser.setCaseSensitivity(LispParser.PRESERVE);
 *   4.  LispValue result = parser.parse();
 * </pre>
 * Normal usage is to parse a string.  If you want to use a Reader,
 * do: <code>new PushbackReader(myReader)</code>.
 *
 * @see org.jatha.dynatype.LispValue
 * @author Micheal S. Hewett
 * @version 1.0
 */
public class LispParser
{
  public static final int UPCASE         = 1;
  public static final int DOWNCASE       = 2;
  public static final int PRESERVE       = 3;

  static final char AT_SIGN              = '@';
  static final char BACK_QUOTE           = '`';
  static final char BACKSLASH            = '\\';
  static final char COLON                = ':';
  static final char COMMA                = ',';
  static final char DECIMAL              = '.';
  static final char DOUBLE_QUOTE         = '"';
  static final char EQUAL_SIGN           = '=';
  static final char LEFT_ANGLE_BRACKET   = '<';
  static final char LEFT_PAREN           = '(';
  static final char HYPHEN               = '-';
  static final char OR_BAR               = '|';
  static final char POUND                = '#';
  static final char PERIOD               = '.';
  static final char RIGHT_PAREN          = ')';
  static final char SEMICOLON            = ';';
  static final char RIGHT_ANGLE_BRACKET  = '>';
  static final char SINGLE_QUOTE         = '\'';
  static final char UNDERSCORE           = '_';

  // Parser states
  static final int READING_NOTHING           = 0;
  static final int READING_SYMBOL            = 1;
  static final int READING_MIXED_CASE_SYMBOL = 2;
  static final int READING_CHARACTER         = 3;
  static final int READING_STRING            = 4;
  static final int READING_BACKQUOTED_LIST   = 5;

  private      int BackQuoteLevel            = 0;
  private      PushbackReader  inputReader;

  private int f_caseSensitivity = UPCASE;  // default LISP behavior.

  private static LispParser f_myParser = null;


  private Jatha f_lisp = null;

  public LispParser(Jatha lisp, Reader inStream)
  {
    this(lisp, inStream, UPCASE);
  }

  public LispParser(Jatha lisp, String inString)
  {
    this(lisp, new StringReader(inString), UPCASE);
  }


  /**
   * Allows you to create a parser that handles
   * input case conversion as you like.
   * Default is UPCASE.  Other values are DOWNCASE and PRESERVE.
   * @param inStream
   */
  public LispParser(Jatha lisp, Reader inStream, int caseSensitivity)
  {
    f_lisp = lisp;

    if (inStream instanceof PushbackReader)
      inputReader = (PushbackReader)inStream;
    else
      inputReader   = new PushbackReader(inStream);
    setCaseSensitivity(caseSensitivity);
  }

  /**
   * Allows you to create a parser that handles
   * input case conversion as you like.
   * Default is UPCASE.  Other values are DOWNCASE and PRESERVE.
   */
  public LispParser(Jatha lisp, String inString, int caseSensitivity)
  {
    this(lisp, new StringReader(inString));
    setCaseSensitivity(caseSensitivity);
  }

  /**
   * Retrieves the current case-sensitivity of the parser.
   * It can be eiher LispParser.UPCASE, LispParser.DOWNCASE
   * or LispParser.PRESERVE
   * @return UPCASE, DOWNCASE or PRESERVE
   */
  public int getCaseSensitivity()
  {
    return f_caseSensitivity;
  }

  /**
   * Sets the current case-sensitivity of the parser.
   * It can be eiher LispParser.UPCASE, LispParser.DOWNCASE
   * or LispParser.PRESERVE
   */
  public void setCaseSensitivity(int caseSensitivity)
  {
    f_caseSensitivity = caseSensitivity;
  }


  /**
   * Gets the current reader to be parsed.
   */
  public PushbackReader getInputReader()
  {
    return inputReader;
  }


  /**
   * Sets the input reader for the Parser.
   */
  public void setInputReader(PushbackReader inputReader)
  {
    this.inputReader = inputReader;
  }


  /**
   * Sets the input string for the parser.  This is the
   * String to parse.
   */
  public void setInputString(String s)
  {
    this.inputReader = new PushbackReader(new StringReader(s));
  }


  /**
   * Parse() assumes that there is only one expression in the input
   * string or file.  If you need to read multiple items from a
   * string or file, use the read() function.
   * Parse just calls read right now.
   *
   * @see #read
   */
  public LispValue parse()
          throws EOFException
  {
    return read();
  }

  /**
   * Reads one s-expression from the input stream (a string or file).
   * Throws an EOFxception when EOF is reached.
   * Call this method repeatedly to do read-eval-print on a file.
   */
  public LispValue read()
  throws EOFException
  {
    StringBuffer   token = new StringBuffer(80);    // Should cover most tokens.
    char           ch;
    int            intCh = 0;
    int            parseState  = READING_NOTHING;

    while (true)
    {
      try {
        intCh = inputReader.read();
      } catch (IOException ioe) {
        break;
      }

      if (intCh < 0)
        if (parseState == READING_SYMBOL)  // end of symbol at end of input
          ch = ' ';
        else
          throw new EOFException("Premature end of LISP input at: " + token.toString());
      else
        ch = (char) intCh;

      // Debugging
      // System.err.print("\n Read: read: " + intCh + " ('" + ch + "')");

      // Encounter a comment?: flush the remaining characters on the line.
      if (isSemi(ch)
              && (parseState != READING_STRING)
              && (parseState != READING_CHARACTER))
      {
        do
        {
          try { intCh = inputReader.read(); }
          catch (IOException e)
          { break; }
          if (intCh < 0)
            throw new EOFException("Premature end of LISP input at: " + token.toString());
          else
            ch = (char) intCh;

          // Apparently read() doesn't do translation.
          if (ch == '\r')
            ch = '\n';
        }
        while (ch != '\n');
        // System.err.println("\n Finished comment with: " + (int) ch);
        continue;
      }

      if (parseState != READING_NOTHING) {      /* If reading anything... */
        switch (parseState) {
          case READING_SYMBOL:
            if (isTerminator(ch))       /* Terminate reading token. */
            {
              try { inputReader.unread(ch); }
              catch (IOException e)
              { System.err.println("\n *** I/O error while unreading character '" + ch + "'."); }
              parseState = READING_NOTHING;
              if (f_caseSensitivity == UPCASE)
                return(tokenToLispValue(token.toString().toUpperCase()));
              else if (f_caseSensitivity == DOWNCASE)
                return(tokenToLispValue(token.toString().toLowerCase()));
              else // if (f_caseSensitivity == PRESERVE)
                return(tokenToLispValue(token.toString()));
            }
            else
              token.append(ch);
            break;

          case READING_MIXED_CASE_SYMBOL:
            if (isOrBar(ch))       /* Terminate reading token. */
            {
              String s = token.toString();

              token.append(ch);
              parseState = READING_NOTHING;
              // Strip off the beginning and ending Or Bars.
              return(tokenToLispValue(s.substring(1, s.length())));
            }
            else
              token.append(ch);
            break;

          case READING_STRING:
            if (ch == BACKSLASH)  // Next char is always in the string
            {
              try { intCh = inputReader.read(); }
              catch (IOException e)
              { break; }
              if (intCh < 0)
                throw new EOFException("Premature end of LISP input at: " + token.toString());
              else
                ch = (char) intCh;

              token.append(ch);
              break;
            }

            if (ch == DOUBLE_QUOTE)
            {
              token.append(ch);
              parseState = READING_NOTHING;
              return(tokenToLispValue(token.toString()));
            }
            else
              token.append(ch);
            break;
        } /* END OF SWITCH */
      } /* END OF IF (parseState) */

      // We are not in the middle of reading something recognizable, so
      // we try to start something recognizable.
      else
        if (!isSpace(ch))         /* Start reading a token */
        {
          if (isLparen(ch))
          {
            return(read_list_token(inputReader));
          }
          else if (isRparen(ch))
          {
            System.err.println("WARNING: Too many right parentheses.  NIL assumed.");
            return(f_lisp.NIL);
          }
          else if (isQuote(ch))
          {
            return(read_quoted_token(inputReader));
          }
          else if (isDoubleQuote(ch))
          {
            token.append(ch);
            parseState = READING_STRING;
          }
          else if (isPound(ch))
          {
            return(applyReaderMacro(inputReader));
          }
          else if (isBackQuote(ch))
          {
            return(read_backquoted_token(inputReader));
          }
          else if (isComma(ch))
          {
            return(read_comma_token(inputReader));
          }
          else if (isOrBar(ch))
          {
            token.append(ch);
            parseState = READING_MIXED_CASE_SYMBOL;
          }
          else
          {
            parseState = READING_SYMBOL;
            try { inputReader.unread(ch); }
            catch (IOException e)
            { System.err.println("\n *** I/O error while unreading character '" + ch + "'."); }
          }
        }  /* if (!isSpace(ch)) */

    } /* main WHILE loop */

    /* WE ONLY EXECUTE THIS CODE IF WE HIT end of input string or file. */
    if (token.length() > 0)
      return(tokenToLispValue(token.toString()));
    else
      return(f_lisp.NIL);
  }


  /**
   * Reads one list expression from the input stream and returns it.
   * The input pointer should be on the character following the left parenthesis.
   */
  public LispValue read_list_token(PushbackReader stream) throws EOFException
  {
    boolean firstTime  = true;
    boolean haveDot    = false;
    char    ch;
    int     intCh = 0;
    LispValue      newToken;
    LispValue      newList, newCell;

    newList =  f_lisp.NIL;
    newCell =  f_lisp.NIL;

    LispValue newListLast = f_lisp.NIL;

    while (true)
    {
      try { intCh = inputReader.read(); }
      catch (IOException e)
      { break; }
      if (intCh < 0)  { throw new EOFException("Premature end of LISP input."); }
      else
        ch = (char) intCh;

      if (!isSpace(ch))
      {
        if (isRparen(ch))
          return(newList);

        if (isPeriod(ch))
        {
          if (haveDot)
          {
            System.err.println("WARNING: Illegal dotted syntax.  NIL assumed.");
            return f_lisp.NIL;
          }
          haveDot = true;
          continue;             // Skip to end of while loop.
        }

        // Encounter a comment?: flush the remaining characters on the line.
        if (isSemi(ch))
        {
          do
          {
            try { intCh = inputReader.read(); }
            catch (IOException e)
            { break; }
            if (intCh < 0)  { throw new EOFException("Premature end of LISP input."); }
            else
              ch = (char) intCh;

            // Apparently read() doesn't do translation.
            if (ch == '\r')  ch = '\n';
          }
          while (ch != '\n');
          continue;
        }

        // otherwise process a normal token.

        try { inputReader.unread(ch); }
        catch (IOException e)
        { System.err.println("\n *** I/O error while unreading character '" + ch + "'."); }

        // System.err.print("\nRLT calling parse()");
        newToken = read();
        // System.err.print("...got back: " + newToken.toString());

        if (firstTime)
        {
          newList   = f_lisp.makeCons(f_lisp.NIL, f_lisp.NIL);
          newList.rplaca(newToken);
          newListLast = newList;
          firstTime = false;
        }
        else
        {
          if (haveDot)
          {
            newListLast.rplacd(newToken);
            newListLast = newToken;
          }
          else
          {
            newCell  = f_lisp.makeCons(newToken, f_lisp.NIL);  /* (NIL . NIL) */
            
            newListLast.rplacd(newCell);
            newListLast = newCell;
          }
        }
      }  // if (!isSpace())
    }    // while ()...

    return f_lisp.NIL;     // Shouldn't get here.
  }

  /**
   * This routine is called by parse when it encounters
   * a quote mark.  It calls parse recursively.
   */
  LispValue read_quoted_token(PushbackReader stream) throws EOFException
  {
    LispValue newCell          = f_lisp.NIL;
    LispValue newQuotedList    = f_lisp.NIL;

    /* Construct the quoted list (QUOTE . (NIL . NIL)) then
     * read a token and replace the first NIL by the token read.
     */

    newQuotedList = f_lisp.makeCons(f_lisp.QUOTE,
                                    f_lisp.makeCons(f_lisp.NIL, f_lisp.NIL));
    newCell = read();
    newQuotedList.cdr().rplaca(newCell);
    return(newQuotedList);
  }


  /**
   * This routine is called by parse when it encounters
   * a backquote mark.  It calls parse recursively.
   */
  public LispValue read_backquoted_token(PushbackReader stream) throws EOFException
  {
    LispValue newCell          = f_lisp.NIL;
    LispValue newQuotedList    = f_lisp.NIL;

    /* Construct the quoted list (SYS::BACKQUOTE . (NIL . NIL)) then
    * read a token and replace the first NIL by the token read.
    */

    newQuotedList = f_lisp.makeCons(f_lisp.BACKQUOTE,
                                    f_lisp.makeCons(f_lisp.NIL, f_lisp.NIL));

    ++BackQuoteLevel;
    newCell = read();
    --BackQuoteLevel;

    newQuotedList.cdr().rplaca(newCell);
    return(newQuotedList);
  }


  /**
   * This routine is called by parse when it encounters
   * a comma, which is only legal inside a backquote.
   */
  LispValue read_comma_token(PushbackReader stream) throws EOFException
  {
    LispValue newCell          = f_lisp.NIL;
    LispValue newQuotedList    = f_lisp.NIL;
    LispValue identifier       = f_lisp.NIL;
    int  intCh;
    char ch;

    if (BackQuoteLevel <= 0)
    {
      System.err.println(";; *** ERROR: Comma not inside backquote.");
      return f_lisp.NIL;
    }

    try { intCh = inputReader.read(); }
    catch (IOException e) { return f_lisp.NIL; }

    if (intCh < 0)  { throw new EOFException("Premature end of LISP input."); }
    else
      ch = (char) intCh;

    // Apparently read() doesn't do translation.
    if (ch == '\r')  ch = '\n';

    if (isAtSign(ch))
      identifier = f_lisp.COMMA_ATSIGN_FN;
    else if (isPeriod(ch))
      identifier = f_lisp.COMMA_DOT_FN;
    else
    {
      identifier = f_lisp.COMMA_FN;
      try { inputReader.unread(ch); }
      catch (IOException e)
      { System.err.println("\n *** I/O error while unreading character '" + ch + "'."); }
    }

    newQuotedList = f_lisp.makeCons(identifier,
                                    f_lisp.makeCons(f_lisp.NIL, f_lisp.NIL));

    newCell = read();

    newQuotedList.cdr().rplaca(newCell);
    return(newQuotedList);
  }


  /**
   * This routine is called by parse when it encounters
   * a pound (#) mark.
   */
  public LispValue applyReaderMacro(PushbackReader stream) throws EOFException
  {
    char   ch = '0';
    int    intCh = 0;

    try { intCh = inputReader.read(); }
    catch (IOException e)
    { System.err.println("\n *** I/O error while reading '#' token."); }
    if (intCh < 0)  { throw new EOFException("Premature end of LISP input."); }
    else
      ch = (char) intCh;

    // #:foo is an uninterned symbol.
    if (isColon(ch))
    {
      StringBuffer token = new StringBuffer(80);
      token.append('#');

      while (!isTerminator(ch))       /* Terminate reading token. */
      {
        token.append(ch);
        try { intCh = inputReader.read(); }
        catch (IOException e)
        { System.err.println("\n *** I/O error while reading '#:' token."); }
        if (intCh < 0)  { throw new EOFException("Premature end of LISP input."); }
        else
          ch = (char) intCh;
      }

      try { inputReader.unread(ch); }
      catch (IOException e)
      { System.err.println("\n *** I/O error while unreading character '" + ch + "'."); }

      if (f_caseSensitivity == UPCASE)
        return(tokenToLispValue(token.toString().toUpperCase()));
      else if (f_caseSensitivity == DOWNCASE)
        return(tokenToLispValue(token.toString().toLowerCase()));
      else // if (f_caseSensitivity == PRESERVE)
        return(tokenToLispValue(token.toString()));
    }

    // #'foo means (function foo)
    else if (isQuote(ch))
    {
      LispValue result = f_lisp.makeList(tokenToLispValue("FUNCTION"), read());
      return result;

      //LispValue result = f_lisp.makeList(tokenToLispValue("FUNCTION"));
//
//      try {
//        intCh = inputReader.read();
//      } catch (IOException e) {
//        System.err.println("\n *** I/O error while reading a #' expression." + e);
//        return tokenToLispValue(token.toString());
//      }
//      if (intCh < 0)
//       throw new EOFException("Premature end of LISP input.");
//      else
//        ch = (char) intCh;

      //LispValue fnToken = read();

//      if (intCh == LEFT_PAREN)   // #'(lambda...
//      {
//        fnToken = read_list_token(inputReader);
//      }
//
//      else
//      {
//        while (! isTerminator(ch))
//        {
//          token.append(ch);
//          try {
//            intCh = inputReader.read();
//          } catch (IOException e) {
//            System.err.println("\n *** I/O error while reading a #'symbol expression." + e);
//          }
//          if (intCh < 0)
//            throw new EOFException("Premature end of LISP input.");
//          else
//            ch = (char) intCh;
//        }
//
//        try {
//          inputReader.unread(ch);
//        } catch (IOException e) {
//          System.err.println("\n *** I/O error while reading a #' expression." + e);
//          return tokenToLispValue(token.toString());
//        }
//
//        if (f_caseSensitivity == UPCASE)
//          fnToken = tokenToLispValue(token.toString().toUpperCase());
//        else if (f_caseSensitivity == DOWNCASE)
//          fnToken = tokenToLispValue(token.toString().toLowerCase());
//        else // if (f_caseSensitivity == PRESERVE)
//          fnToken = tokenToLispValue(token.toString());
//      }

      //return result.append(f_lisp.makeList(fnToken));

    }

    // #\ reads a character macro
    else if (isBackSlash(ch))
    {
      StringBuffer token = new StringBuffer(80);
      try { intCh = inputReader.read(); }
      catch (IOException e)
      { System.err.println("\n *** I/O error while reading character token."); }
      if (intCh < 0)  { throw new EOFException("Premature end of LISP input."); }
      else
        ch = (char) intCh;

      while (! isTerminator(ch))
      {
        token.append(ch);
        try {
          intCh = inputReader.read();
        } catch (IOException e) {
          System.err.println("\n *** I/O error while reading a #' expression." + e);
        }
        if (intCh < 0)
          throw new EOFException("Premature end of LISP input.");
        else
          ch = (char) intCh;
      }

      try {
        inputReader.unread(ch);
      } catch (IOException e) {
        System.err.println("\n *** I/O error while reading a #' expression." + e);
        return tokenToLispValue(token.toString());
      }

      if(token.length()>1) {
          final String tok = token.toString();
          if(tok.equalsIgnoreCase("SPACE")) {
              ch = ' ';
          } else if(tok.equalsIgnoreCase("NEWLINE")) {
              ch = '\n';
          } else {
              ch = 0;
          }
      } else {
          ch = token.charAt(0);
      }

      return new StandardLispCharacter(f_lisp, ch);
    }

    // #< usually starts a structure
    else if (isLeftAngleBracket(ch))
    {
      System.err.println("\n *** parser can't read structures yet.");
      while (! isRightAngleBracket(ch))
        try {
          intCh = inputReader.read();
          ch = (char)intCh;
        } catch (IOException e) {
          System.err.println("\n *** I/O error while reading a structure.");
        }

      if (intCh < 0)
      {
        throw new EOFException("Premature end of LISP input.");
      }

      else
        ch = (char) intCh;

      return f_lisp.NIL;
    }

    // -- #| ... |# is a block comment
    else if (isOrBar(ch))
    {
      try {
        boolean done = false;
        boolean terminating = false;
        while (! done)
        {
          intCh = inputReader.read();
          ch = (char)intCh;
          if (isOrBar(ch))
            terminating = true;
          else if (terminating && isPound(ch))
            done = true;
          else
            terminating = false;
        }
      } catch (IOException e) {
        System.err.println("\n *** I/O error while reading a block comment.  Not terminated?");
      }
    return f_lisp.NIL;
    }

    else
    {
      System.err.println("\n *** unknown '#' construct.");
      return f_lisp.NIL;
    }
  }


  /**
   * This library can't read backquotes yet.
   */
  public LispValue read_backquoted_list_token(PushbackReader stream)
  {
    System.err.println("\n *** Parser can't read backquoted lists yet.");
    return f_lisp.NIL;
  }

  /**
   * Converts a string to a LISP value such as
   * NIL, T, an integer, a real number, a string or a symbol.
   */
  public LispValue tokenToLispValue(String token)
  {
    LispValue newCell = null;
    LispValue keywordPackage = f_lisp.findPackage("KEYWORD");

    if (T_token_p(token))
      newCell = f_lisp.T;
    else if (NIL_token_p(token))
      newCell = f_lisp.NIL;
    else if (INTEGER_token_p(token))
    {
      // It may be an Fixnum or a Bignum.
      // Let Java tell us by generating a NumberFormatException
      // when the number is too big (or too negatively big).

      // SK: java cannot parse integer with '+' in front of it?  25 Jul 2009
      if( token.charAt(0) == '+' ){
        token = token.substring(1);
      }

      try {
        newCell = f_lisp.makeInteger(new Long(token)); 
      } catch (NumberFormatException e) {
        newCell = f_lisp.makeBignum(new BigInteger(token)); 
      }
    }
    else if (REAL_token_p(token))
      newCell = f_lisp.makeReal(new Double(token));
    else if (STRING_token_p(token))
    { /* remove the first and last double quotes. */
      try
      { newCell = f_lisp.makeString(token.substring(1, token.length() - 1)); }
      catch (StringIndexOutOfBoundsException e)
      { System.err.println("Hey, got a bad string index in 'tokenToLispValue'!"); }

    }
    else if (SYMBOL_token_p(token))
    {
      // default package.
      LispValue pkg      = f_lisp.PACKAGE_SYMBOL.symbol_value();
      String packageStr  = "";
      boolean   external = false;

      // Added packages, 10 May 1997 (mh)
      if (token.indexOf(':') >= 0)
      {
        packageStr = token.substring(0, token.indexOf(':'));
        if (packageStr.equals("#"))   // Uninterned symbol
          pkg = null;
        else
        {
          pkg = f_lisp.findPackage(packageStr);
          if (pkg == f_lisp.NIL)
          {
            // throw(new LispUndefinedPackageException(packageStr));
            System.err.println("Warning: package '" + packageStr +
                               "' undefined.  Using current package.");
            pkg = f_lisp.PACKAGE_SYMBOL.symbol_value();
          }
        }

        // Strip off the package.
        token = token.substring(packageStr.length(), token.length());

        if (token.startsWith(":::"))
        {
          System.err.println("Warning: ignored extra ':' in '" +
                             packageStr + token + "'.");
          token = token.substring(token.lastIndexOf(':')+1, token.length());
        }
        else if (token.startsWith("::"))
          token = token.substring(2, token.length());
        else if (token.startsWith(":"))
        {
          if (pkg != null)
            external = true;
          token    = token.substring(1, token.length());
        }
      }  // end of package parsing

      // Handle external symbols separately, except for keywords
      if (external && (pkg != null) && (! packageStr.equals("")))
      {
        newCell = ((LispPackage)pkg).getExternalSymbol(f_lisp.makeString(token));
        if (newCell == f_lisp.NIL)
          System.err.println(";; *** ERROR: " + packageStr + ":" + token +
                             " is not an external symbol in " + packageStr +
                             ".\n;; *** Creating new symbol in current package.");
        newCell = f_lisp.EVAL.intern(token, (LispPackage) f_lisp.PACKAGE_SYMBOL.symbol_value());
      }
      // keywords must always be uppercase.
      else if (pkg == keywordPackage)
        newCell = f_lisp.EVAL.intern(token.toUpperCase(), (LispPackage)pkg);
      else if (pkg != null)
        newCell = f_lisp.EVAL.intern(token, (LispPackage) pkg);
      else
        newCell= f_lisp.makeSymbol(token);
    }
    else
    {
      System.err.println("ERROR: Unrecognized input: \"" + token + "\"");
      newCell = f_lisp.NIL;
    }

    if (newCell == null)
    {
      System.err.println("MEMORY_ERROR in  \"tokenToLispValue\" " + "for token \""
                         + token + "\", returning NIL.");
      newCell = f_lisp.NIL;
    }

    return(newCell);
  }

  // ----  Utility functions  ----------------------------------

  static boolean isLparen(char x)             { return (x == LEFT_PAREN);   }
  static boolean isRparen(char x)             { return (x == RIGHT_PAREN);  }
  static boolean isAtSign(char x)             { return (x == AT_SIGN);      }
  static boolean isBackQuote(char x)          { return (x == BACK_QUOTE);   }
  static boolean isBackSlash(char x)          { return (x == BACKSLASH);    }
  static boolean isColon(char x)              { return (x == COLON);        }
  static boolean isComma(char x)              { return (x == COMMA);        }
  static boolean isDoubleQuote(char x)        { return (x == DOUBLE_QUOTE); }
  static boolean isOrBar(char x)              { return (x == OR_BAR);       }
  static boolean isPound(char x)              { return (x == POUND);        }
  static boolean isPeriod(char x)             { return (x == PERIOD);       }
  static boolean isQuote(char x)              { return (x == SINGLE_QUOTE); }
  static boolean isSemi(char x)               { return (x == SEMICOLON);    }
  static boolean isLeftAngleBracket(char x)   { return (x == LEFT_ANGLE_BRACKET); }
  static boolean isRightAngleBracket(char x)  { return (x == RIGHT_ANGLE_BRACKET); }

  static boolean isSpace(char x)
  { return
      ((x == ' ')          // space
       || (x == '\n')      // newline
       || (x == '\r')      // carriage return
       || (x == '\t')      // tab
       || (x == '\f')      // form feed
       || (x == '\b'));    // backspace
      }

   /*
    * Because isTerminator is called in a very tight loop (for every input
    * character) and we want to deal with potentailly massive literals,
    * we use a small lookup table to make it as fast as possible.
    */
   private static final boolean[] terminatorLookupTable = new boolean[256];
   static
   {
       for (int i = 0; i < 256; i++)
       {
           terminatorLookupTable[i] = (isSpace((char)i) // white space
             || isLparen((char)i) || isRparen((char)i)
             || isQuote((char)i)  || isSemi((char)i)
             || isDoubleQuote((char)i)
             || isComma((char)i));
       }
   }
   static boolean isTerminator(char x)
   {
       return terminatorLookupTable[(byte)x];
   }


  /** The equivalent of the C function 'strspn'.
   * Given a string and another string representing a set of characters,
   * this function scans the string and accepts characters that are
   * elements of the given set of characters.  It returns the index
   * of the first element of the string that is not a member of the
   * set of characters.
   * For example:
   *    pos = firstCharNotInSet(0, "hello there, how are you?", "ehlort ");
   * returns 11.
   *
   * If the string does not contain any of the characters in the set,
   * str.length() is returned.
   */
  public static int firstCharNotInSet(int startIndex, String str, String charSet)
  {
    int searchIndex = startIndex - 1;  // we add one at the end.
    int length      = str.length();

    //    System.out.print("\nSearching \"" + str + "\" for \"" + charSet + "\" from index " + startIndex);
    try {
      for (int i = startIndex;
           ((i < length) && (charSet.indexOf(str.charAt(i)) >= 0));
           ++i)
        searchIndex = i;
    }
    catch (StringIndexOutOfBoundsException e) {
      System.err.println("Hey, got a bad string index in 'firstCharNotInSet'!"); 
    }

    //    System.out.println("...returning " + searchIndex);
    return searchIndex + 1;
  }


  private static final Pattern REAL_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
  boolean REAL_token_p(String str)
  {
      return REAL_PATTERN.matcher(str).matches();
  }
  
  
  /**
   * Does NOT recognize an isolated '+' or '-' as a real number.
   */
  boolean REAL_token_p_old(String str)
  {
    String DECIMALchars  = ".";
    String INTchars      = "0123456789";

    int   decimalPos;
    int   length = str.length();
    int   index  = 0;

    if ((index < length) && ((str.charAt(index) == '-') || (str.charAt(index) == '+')))
      index++;

    if (index == length)   // Don't accept a single '-' or '+'
      return false;

    decimalPos = str.indexOf(DECIMALchars);     /* Check for decimal.  If none, not a real number. */
    if (decimalPos < 0)
      return(false);

    if (firstCharNotInSet(index, str, INTchars) != decimalPos)
      return(false);

    if (decimalPos == str.length() - 1)
      return(true);         /* Decimal point followed by no digits is legal in LISP. */

    /* Check decimal digits. */
    index = decimalPos + 1;
    
    return(firstCharNotInSet(index, str, INTchars) == length);
  }


  boolean INTEGER_token_p(String str)
  /*
   * Does NOT recognize an isolated '+' or '-' as an integer.
   */
  {
    String INTchars = "0123456789";

    int   length = str.length();
    int   index  = 0;

    try {
      if ((index < length) && ((str.charAt(index) == '-') || (str.charAt(index) == '+')))
        index++;
    }
    catch (StringIndexOutOfBoundsException e) {
      System.err.println("Hey, got a bad string index in 'INTEGER_token_p'! on string '" + str + "'"); 
    }

    if (index == length)   // Don't accept a single '-' or '+'
      return false;

    return(firstCharNotInSet(index, str, INTchars) == length);
  }

  boolean NIL_token_p(String str) { return(str.equalsIgnoreCase("NIL")); }

  boolean STRING_token_p(String str)
  {
    int       length = str.length();
    boolean   value;

    value = false;

    try {
      value = ((length >= 2)
	       && (str.charAt(0)        == DOUBLE_QUOTE)
	       && (str.charAt(length-1) == DOUBLE_QUOTE));
    }
    catch (StringIndexOutOfBoundsException e) {
      System.err.println("Hey, got a bad string index in 'NIL_token_p'!"); }

    return value;
  }


  boolean SYMBOL_token_p(String str) { return(str.length() >= 1); }


  boolean T_token_p(String str) { return(str.equalsIgnoreCase("T")); }



  // ----  Test functions  ----------------------------------

  public void    test_parser(String s)
  {
    System.out.print("The string \"" + s + "\" ");

    if (T_token_p(s))
      System.out.println("is T.");
    else if (NIL_token_p(s))
      System.out.println("is NIL.");
    else if (INTEGER_token_p(s))
      System.out.println("is an integer.");
    else if (REAL_token_p(s))
      System.out.println("is a double.");
    else if (STRING_token_p(s))
      System.out.println("is a string.");
    else if (SYMBOL_token_p(s))
      System.out.println("is a symbol.");
    else
      System.out.println("is not recognized.");
  }


  public void    test_parser_loop() throws EOFException
  {
    LispValue temp, exit;

    exit = f_lisp.EVAL.intern("EXIT");
    temp = f_lisp.EVAL.intern("*TEMP*");

    System.out.println("Run (EXIT) to stop.");
    try {
      do
      {
        System.out.print("\nJATHA>");  System.out.flush();  // Should print top-level prompt
        //    input = parse(stdin);
        //    setq(temp, symbol_value(STAR));

        //    print(setq(STAR, eval(input)));
        //    setq(STARSTARSTAR, symbol_value(STARSTAR));
        //    setq(STARSTAR, symbol_value(temp));
        temp = read();
        // System.out.println(); temp.prin1();
        temp = f_lisp.COMPILER.compile(f_lisp.MACHINE, temp, f_lisp.NIL);  // No globals for now
        // System.out.println(); temp.prin1();
        temp = f_lisp.MACHINE.Execute(temp, f_lisp.NIL);
        System.out.println(); temp.prin1();
      }
      while (temp != exit);
    } catch (CompilerException ce) {
      System.err.println("Compiler error: " + ce.toString());
    }
    System.out.println();
    System.out.flush();
  }

  /**
   * Returns true if the input expression has balanced parentheses
   * @param input a String
   * @return true if it has balanced parentheses
   */
  public static boolean hasBalancedParentheses(Jatha lisp, LispValue input)
  {
    return hasBalancedParentheses(lisp, input.toString());
  }

  /**
   * Returns true if the input expression has balanced parentheses
   * @param input a String
   * @return true if it has balanced parentheses
   */
  public static boolean hasBalancedParentheses(Jatha lisp, String input)
  {
    LispValue result = lisp.NIL;

    if (f_myParser == null)
      f_myParser = new LispParser(lisp, input);
    else
      f_myParser.setInputString(input);

    try {
      while (true)
        result = f_myParser.read();

    } catch (EOFException eofe) {
      if (eofe.getMessage().toLowerCase().startsWith("premature"))
      {
        System.err.println("Unbalanced parentheses in input.  Last form read was " + result);
        return false;
      }
      else
        return true;
    }
  }

  public void simple_parser_test()
  {
    test_parser("1234.56789");
    test_parser("1234.");
    test_parser(".56789");
    test_parser("-1234.56789");
    test_parser("+1234.56789");
    test_parser("-.56789");
    test_parser("1234");
    test_parser("-1234");
    test_parser("+1234");
    test_parser("T");
    test_parser("NIL");
    test_parser("\"This is a string\"");
    test_parser("\"astring\"");
    test_parser("\"\"");
    test_parser("ABCD");
    test_parser("def1234");
    test_parser("123def");
    test_parser("abc_def_ghi");
  }


}

