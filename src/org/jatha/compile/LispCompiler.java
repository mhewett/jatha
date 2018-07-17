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

package org.jatha.compile;

import java.util.Set;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;

/**
 * LispCompiler has a <tt>compile()</tt> method that will
 * compile a LISP expression and return the SECD code
 * for that expression.
 *
 *
 * Example LISP read/eval/print loop:
 * <pre>
 *    expr   = parser.read(stream);
 *    code   = compiler.compile(expr);
 *    result = machine.eval(code);
 *    result.print();
 * </pre>
 * <p>
 * Macro compilation contributed by Jean-Pierre Gaillardon, April 2005
 * </p>
 * @see org.jatha.machine.SECDMachine
 * @see org.jatha.machine.SECDop
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
public class LispCompiler
{
  // Set this to true to produce debugging output during compilation.
  static boolean DEBUG = false;

  // These are special forms that get expanded in the compiler
  LispValue AND;
  LispValue DEFMACRO;
  LispValue DEFUN;
  LispValue IF;
  LispValue LAMBDA;
  LispValue LET;
  LispValue LETREC;
  LispValue OR;
  LispValue PRIMITIVE;
  LispValue PROGN;
  LispValue QUOTE;
  LispValue SETQ;
    //  LispValue BLOCK;
    //  LispValue WHEN;

  LispValue AMP_REST;   // keyword &rest used in parameters list
  LispValue MACRO;      // keyword used at begenning of macro code to detect macro
  LispValue DUMMY_FUNCTION; // used for recursive definions
  LispValue DUMMY_MACRO;    // used for recursive definions

  boolean WarnAboutSpecialsP = false;    // todo: Need some way to turn this on.
  private Jatha f_lisp = null;
  private final Stack<LispValue> legalBlocks = new Stack<LispValue>();
  private final Stack<Set<LispValue>> legalTags   = new Stack<Set<LispValue>>();
  private final Map<Long, LispValue> registeredGos = new HashMap<Long, LispValue>();

  // static initializer.
  private void initializeConstants()
  {
      final LispPackage keyPkg = (LispPackage)(f_lisp.findPackage("KEYWORD"));
      final LispPackage sysPkg = (LispPackage)(f_lisp.findPackage("SYSTEM"));

    AMP_REST   = f_lisp.EVAL.intern("&REST",sysPkg);
    sysPkg.export(AMP_REST);
    AND        = f_lisp.EVAL.intern("AND",sysPkg);
    sysPkg.export(AND);
    DEFMACRO   = f_lisp.EVAL.intern("DEFMACRO",sysPkg);
    sysPkg.export(DEFMACRO);
    DEFUN      = f_lisp.EVAL.intern("DEFUN",sysPkg);
    sysPkg.export(DEFUN);
    IF         = f_lisp.EVAL.intern("IF",sysPkg);
    sysPkg.export(IF);
    LAMBDA     = f_lisp.EVAL.intern("LAMBDA",sysPkg);
    sysPkg.export(LAMBDA);
    LET        = f_lisp.EVAL.intern("LET",sysPkg);
    sysPkg.export(LET);
    LETREC     = f_lisp.EVAL.intern("LETREC",sysPkg);
    sysPkg.export(LETREC);
    MACRO      = f_lisp.EVAL.intern("MACRO",keyPkg);
    keyPkg.export(MACRO);
    OR         = f_lisp.EVAL.intern("OR",sysPkg);
    sysPkg.export(OR);
    PROGN      = f_lisp.EVAL.intern("PROGN",sysPkg);
    sysPkg.export(PROGN);
    PRIMITIVE  = f_lisp.EVAL.intern("PRIMITIVE",keyPkg);
    keyPkg.export(PRIMITIVE);
    QUOTE      = f_lisp.EVAL.intern("QUOTE",sysPkg);
    sysPkg.export(QUOTE);
    SETQ       = f_lisp.EVAL.intern("SETQ",sysPkg);
    sysPkg.export(SETQ);
    //    BLOCK       = f_lisp.EVAL.intern("BLOCK",sysPkg);
    //    sysPkg.export(BLOCK);
    //    WHEN       = f_lisp.EVAL.intern("WHEN");

    //##JPG added
    // should be used only to test type. basic_macrop() retutns true for DUMMY_MACRO and false for DUMMY_FUNCTION
    DUMMY_FUNCTION = new StandardLispFunction(f_lisp, null, f_lisp.makeCons(f_lisp.T, f_lisp.NIL));
    DUMMY_MACRO    = new StandardLispMacro(f_lisp, null, f_lisp.makeCons(f_lisp.T, f_lisp.NIL));


  }

  public LispCompiler(Jatha lisp)
  {
    super();

    f_lisp = lisp;

    initializeConstants();
  }


  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  // @date    Wed Feb  5 09:33:27 1997
  /**
   * Initializes the compiler by registering
   * the LISP primitive functions so that the
   * compiler can recognize them.
   *
   * @see org.jatha.compile.LispCompiler
   */
  public void init()
  {
      // Here put a call to in-package, then to export. for these things. I guess register should call export.
      final LispPackage SYSTEM_PKG = (LispPackage)f_lisp.findPackage("SYSTEM");
    Register(new AbsoluteValuePrimitive(f_lisp),SYSTEM_PKG);
    Register(new AppendPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ApplyPrimitive(f_lisp),SYSTEM_PKG);
    Register(new AproposPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ArcSinePrimitive(f_lisp),SYSTEM_PKG);
    Register(new ArcCosinePrimitive(f_lisp),SYSTEM_PKG);
    Register(new ArcTangentPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ArcTangent2Primitive(f_lisp),SYSTEM_PKG);
    Register(new ArrayDimensionsPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ArraypPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ArefPrimitive(f_lisp),SYSTEM_PKG);
    Register(new AssocPrimitive(f_lisp),SYSTEM_PKG);
    Register(new AtomPrimitive(f_lisp),SYSTEM_PKG);
    // Register(new BackquotePrimitive(f_lisp), (LispPackage)f_lisp.findPackage("SYSTEM"));
    Register(new BoundpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new BlockPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ButlastPrimitive(f_lisp),SYSTEM_PKG);
    Register(new CarPrimitive(f_lisp),SYSTEM_PKG);
    Register(new CdrPrimitive(f_lisp),SYSTEM_PKG);
    Register(new CeilingPrimitive(f_lisp),SYSTEM_PKG);
    Register(new CharacterpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ClrhashPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ConcatenatePrimitive(f_lisp),SYSTEM_PKG);
    Register(new ConsPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ConspPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ConstantpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new CopyListPrimitive(f_lisp),SYSTEM_PKG);
    Register(new CosecantPrimitive(f_lisp),SYSTEM_PKG);
    Register(new CosinePrimitive(f_lisp),SYSTEM_PKG);
    Register(new CotangentPrimitive(f_lisp),SYSTEM_PKG);
    Register(new DefconstantPrimitive(f_lisp),SYSTEM_PKG);
    Register(new DefparameterPrimitive(f_lisp),SYSTEM_PKG);
    Register(new DefvarPrimitive(f_lisp),SYSTEM_PKG);
    Register(new DegreesToRadiansPrimitive(f_lisp),SYSTEM_PKG);
    Register(new DocumentationPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfDocumentationPrimitive(f_lisp),SYSTEM_PKG);
    Register(new EighthPrimitive(f_lisp),SYSTEM_PKG);
    Register(new EltPrimitive(f_lisp),SYSTEM_PKG);
    Register(new EqPrimitive(f_lisp),SYSTEM_PKG);
    Register(new EqlPrimitive(f_lisp),SYSTEM_PKG);
    Register(new EqualNumericPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ExitPrimitive(f_lisp),SYSTEM_PKG);
    Register(new EvalPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ExptPrimitive(f_lisp),SYSTEM_PKG);    
    Register(new FactorialPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FboundpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FindPackagePrimitive(f_lisp),SYSTEM_PKG);
    Register(new MakepackagePrimitive(f_lisp),SYSTEM_PKG);
    Register(new UsePackagePrimitive(f_lisp),SYSTEM_PKG);
    Register(new PackageUseListPrimitive(f_lisp),SYSTEM_PKG);
    Register(new PackageNamePrimitive(f_lisp),SYSTEM_PKG);
    Register(new PackageNicknamesPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ImportPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ExportPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ShadowPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ShadowingImportPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FifthPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FirstPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FloatpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FloorPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FuncallPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FunctionPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FourthPrimitive(f_lisp),SYSTEM_PKG);
    Register(new GrindefPrimitive(f_lisp),SYSTEM_PKG);
    Register(new GethashPrimitive(f_lisp),SYSTEM_PKG);
    Register(new GoPrimitive(f_lisp),SYSTEM_PKG);
    Register(new GreaterThanPrimitive(f_lisp),SYSTEM_PKG);
    Register(new GreaterThanOrEqualPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfArefPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfGethashPrimitive(f_lisp),SYSTEM_PKG);
    Register(new HashtablepPrimitive(f_lisp),SYSTEM_PKG);
    Register(new HashtableCountPrimitive(f_lisp),SYSTEM_PKG);
    Register(new HashtableRehashSizePrimitive(f_lisp),SYSTEM_PKG);
    Register(new HashtableRehashThresholdPrimitive(f_lisp),SYSTEM_PKG);
    Register(new HashtableSizePrimitive(f_lisp),SYSTEM_PKG);
    Register(new HashtableTestPrimitive(f_lisp),SYSTEM_PKG);
    Register(new IntegerpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new InternPrimitive(f_lisp),SYSTEM_PKG);
    Register(new KeywordpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new LastPrimitive(f_lisp),SYSTEM_PKG);
    Register(new LengthPrimitive(f_lisp),SYSTEM_PKG);
    Register(new LessThanPrimitive(f_lisp),SYSTEM_PKG);
    Register(new LessThanOrEqualPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ListPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ListStarPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ListAllPackagesPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ListpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new LoadPrimitive(f_lisp),SYSTEM_PKG);
    Register(new LoadFromJarPrimitive(f_lisp),SYSTEM_PKG);
    Register(new Macroexpand1Primitive(f_lisp),SYSTEM_PKG);
    Register(new MacroexpandPrimitive(f_lisp),SYSTEM_PKG);
    Register(new MakeArrayPrimitive(f_lisp),SYSTEM_PKG);
    Register(new MakeHashTablePrimitive(f_lisp),SYSTEM_PKG);
    Register(new MaxPrimitive(f_lisp),SYSTEM_PKG);
    Register(new MemberPrimitive(f_lisp),SYSTEM_PKG);
    Register(new MinPrimitive(f_lisp),SYSTEM_PKG);
    Register(new NconcPrimitive(f_lisp),SYSTEM_PKG);
    Register(new NinthPrimitive(f_lisp),SYSTEM_PKG);
    Register(new NotPrimitive(f_lisp),SYSTEM_PKG);
    Register(new NreversePrimitive(f_lisp),SYSTEM_PKG);
    Register(new NStringCapitalizePrimitive(f_lisp),SYSTEM_PKG);
    Register(new NStringDowncasePrimitive(f_lisp),SYSTEM_PKG);
    Register(new NStringUpcasePrimitive(f_lisp),SYSTEM_PKG);
    Register(new NullPrimitive(f_lisp),SYSTEM_PKG);
    Register(new NumberpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new PopPrimitive(f_lisp),SYSTEM_PKG);
    Register(new PositionPrimitive(f_lisp),SYSTEM_PKG);
    Register(new Prin1Primitive(f_lisp),SYSTEM_PKG);
    Register(new PrincPrimitive(f_lisp),SYSTEM_PKG);
    Register(new PrintPrimitive(f_lisp),SYSTEM_PKG);
    Register(new PushPrimitive(f_lisp),SYSTEM_PKG);
    Register(new QuotePrimitive(f_lisp),SYSTEM_PKG);
    Register(new RadiansToDegreesPrimitive(f_lisp),SYSTEM_PKG);
    Register(new RassocPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ReadFromStringPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ReciprocalPrimitive(f_lisp),SYSTEM_PKG);
    Register(new RemhashPrimitive(f_lisp),SYSTEM_PKG);
    Register(new RemovePrimitive(f_lisp),SYSTEM_PKG);
    Register(new RestPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ReturnFromPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ReversePrimitive(f_lisp),SYSTEM_PKG);
    Register(new RplacaPrimitive(f_lisp),SYSTEM_PKG);
    Register(new RplacdPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SecantPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SecondPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfCarPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfCdrPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfSymbolFunctionPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfSymbolPlistPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetfSymbolValuePrimitive(f_lisp),SYSTEM_PKG);
    Register(new SetqPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SeventhPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SinePrimitive(f_lisp),SYSTEM_PKG);
    Register(new SixthPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SquareRootPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringUpcasePrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringDowncasePrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringCapitalizePrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringEndsWithPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringEqualPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringEqPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringNeqPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringLessThanPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringLesspPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringGreaterThanPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringGreaterpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringLessThanOrEqualPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringGreaterThanOrEqualPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringNotLesspPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringNotGreaterpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringStartsWithPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringTrimPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringLeftTrimPrimitive(f_lisp),SYSTEM_PKG);
    Register(new StringRightTrimPrimitive(f_lisp),SYSTEM_PKG);

    Register(new SubstPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SymbolpPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SymbolFunctionPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SymbolNamePrimitive(f_lisp),SYSTEM_PKG);
    Register(new SymbolPackagePrimitive(f_lisp),SYSTEM_PKG);
    Register(new SymbolPlistPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SymbolValuePrimitive(f_lisp),SYSTEM_PKG);
    Register(new TagbodyPrimitive(f_lisp),SYSTEM_PKG);
    Register(new TangentPrimitive(f_lisp),SYSTEM_PKG);
    Register(new TenthPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ThirdPrimitive(f_lisp),SYSTEM_PKG);
    Register(new TimePrimitive(f_lisp),SYSTEM_PKG);
    Register(new TypeOfPrimitive(f_lisp),SYSTEM_PKG);
    Register(new ZeropPrimitive(f_lisp),SYSTEM_PKG);

    Register(new AddPrimitive(f_lisp),SYSTEM_PKG);
    Register(new DividePrimitive(f_lisp),SYSTEM_PKG);
    Register(new MultiplyPrimitive(f_lisp),SYSTEM_PKG);
    Register(new SubtractPrimitive(f_lisp),SYSTEM_PKG);
    Register(new AddOnePrimitive(f_lisp),SYSTEM_PKG);       /* 1+  */
    Register(new SubtractOnePrimitive(f_lisp),SYSTEM_PKG);  /* 1-  */

    Register(new TracePrimitive(f_lisp),SYSTEM_PKG);
    Register(new GcPrimitive(f_lisp),SYSTEM_PKG);
    Register(new GcFullPrimitive(f_lisp),SYSTEM_PKG);
    Register(new FreePrimitive(f_lisp),SYSTEM_PKG);
  }

  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  // @date    Wed Feb  5 09:45:51 1997
  /**
   * Use this function to register any new LISP primitives
   * that you create from Java code.  The compiler will
   * then recognize them and compile them appropriately.
   *
   * Example:
   * <pre>
   *   compiler.Register(new RevAppendPrimitive());
   * </pre>
   * @see LispPrimitive
   * @param primitive
   */
  public void Register(LispPrimitive primitive)
  {
      Register(primitive,f_lisp.PACKAGE);
  }


 /**
  * Use this function to register any new LISP primitives
  * that you create from Java code.  The compiler will
  * then recognize them and compile them appropriately.
  * This version of the constructor accepts a package in which
  * to intern the symbol.
  *
  * Example:
  * <pre>
  *   compiler.Register(new RevAppendPrimitive());
  * </pre>
  * @see LispPrimitive
  * @param primitive
  */
  public void Register(LispPrimitive primitive, LispPackage pkg)
  {
      final LispValue symbol = f_lisp.getEval().intern(primitive.LispFunctionNameString(), pkg);
      symbol.setf_symbol_function(f_lisp.makeList(PRIMITIVE, primitive));
      pkg.export(symbol);

  }


  /**
   * Use this function to register any new LISP primitives
   * that you create from Java code.  The compiler will
   * then recognize them and compile them appropriately.
   * This version of the constructor accepts a package in which
   * to intern the symbol.
   *
   * Example:
   * <pre>
   *   compiler.Register(new RevAppendPrimitive());
   * </pre>
   * @see LispPrimitive
   * @param primitive
   */
   public void Register(LispPrimitive primitive, String pkgName)
   {
       Register(primitive,(LispPackage)(f_lisp.findPackage(pkgName)));
   }


    public Stack<LispValue> getLegalBlocks() {
        return legalBlocks;
    }

    public Stack<Set<LispValue>> getLegalTags() {
        return legalTags;
    }

    public Map<Long, LispValue> getRegisteredGos() {
      return registeredGos;
    }

    public boolean isLegalTag(final LispValue tag) {
        for(final java.util.Iterator<Set<LispValue>> iter = legalTags.iterator();iter.hasNext();) {
            if(iter.next().contains(tag)) {
                return true;
            }
        }
        return false;
    }

  /* --- Compiler flags   --- */

  public void WarnAboutSpecials(boolean value)
  {  WarnAboutSpecialsP = value; }


  /* --- Utility routines --- */

/*  LispValue	loc(long y, LispValue z)
  {
    if (y == 1)
      return(z.car());
    else
      return loc(y-1, z.cdr());
  }


  LispValue getComponentAt(LispValue ij_indexes, LispValue valueList)
  {
    long i, j;

    i = ((LispInteger)(ij_indexes.car())).getLongValue();
    j = ((LispInteger)(ij_indexes.cdr())).getLongValue();

    return loc(j, loc(i, valueList));
  }


  LispValue index2(LispValue e, LispValue n, long j)
  {
    if (n == f_lisp.NIL)
      return n;
    else if (n.car() == e)
      return f_lisp.makeInteger(j);
    else
      return index2(e, n.cdr(), j+1);
  }


  LispValue index_aux(LispValue e, LispValue n, long i)
  {
    if (n == f_lisp.NIL)
      return n;
    else
    {
      LispValue j;

      j = index2(e, n.car(), 1);

      if (j == f_lisp.NIL)
        return index_aux(e, n.cdr(), i+1);
      else
        return f_lisp.makeCons(f_lisp.makeInteger(i), j);
    }
  }

  /**
   * Looks up the symbol in a list of lists.
   * Returns the index of the list in which it is found and
   * the index in that list.
   * Both indexes start from 1.
   * Returns NIL if not found.
   * Examples:
   * <pre>
   *     index(b, ((a b c) (d e f)) = (1 . 2)
   *     index(f, ((a b c) (d e f)) = (2 . 3)
   *     index(z, ((a b c) (d e f)) = NIL
   * </pre>
   * @param e a Symbol
   * @param n a list of lists
   * @return either NIL, if not found, or a Cons of 2 LispIntegers (a . b) indicating list number (a) and index into that list (b)
   */
/*  LispValue index(LispValue e, LispValue n)
  {
    return index_aux(e, n, 1);
  }
*/

  // New IndexInList and IndexAndAttributes contributed by
  // Jean-Pierre Gaillardon, April 2005
  /**
   * Looks up the symbol in a list
   * @param e a Symbol
   * @param l a list
   * @param attribute The attribute of the found symbol is assigned to attribute[0]. It can be NIL or &rest
   * @return the index in list of found symbol (it start from 1) or 0 if symbol has not been found in list
   */
  public int indexInList(LispValue e, LispValue l, LispValue[] attribute)
  {
    int indexInList = 1;
    LispValue list = l ;
    LispValue previousAttribute = f_lisp.NIL;
    for(;;list = list.cdr())
    {
      if(list == f_lisp.NIL)
      {
        return 0;  // not found
      }
      LispValue elt = list.car();
      if(elt == AMP_REST)
      {
        previousAttribute = AMP_REST;
        continue;
      }
      if (elt == e)
      {
        attribute[0] = previousAttribute;
        return indexInList;
      }
      previousAttribute = f_lisp.NIL;
      indexInList++;
    }
  }


  // New IndexInList and IndexAndAttributes contributed by
  // Jean-Pierre Gaillardon, April 2005
  /**
   * Looks up the symbol in a list of lists.
   * Returns a dotted pair.
   *   - first element is the attribute of the found symbol
   *            it can be "&rest"  or NIL for no attribute (or if symbol has not been found)
   *   - second element is the index of the list in which it is found and
   *        the index in that list. Both indexes start from 1. The &rest keyword eventually
   *        present in the list is not taken into account for index count.
   *        index is NIL if not found.
   * Examples:
   * <pre>
   *     indexAndAttribute(b, ((a b c) (d e f))     = (NIL. (1 . 2))
   *     indexAndAttribute(f, ((a b c) (d e f))     = (NIL.(2 . 3))
   *     indexAndAttribute(z, ((a b c) (d e f))     = (NIL. NIL)
   *     indexAndAttribute(l, ((a &rest l) (d e f)) = (&rest .(1 . 2))
   * </pre>
   * @param e a Symbol
   * @param l a list of lists
   * @return either (NIL.NIL), if not found, or a dotted pair; first is the attribute for symbol, second is a Cons of 2 LispIntegers\
   (a . b) indicating list number (a) and index into that list (b)
   */
  public LispValue indexAndAttribute(LispValue e, LispValue l)
  {
    int indexSubList = 1;
    LispValue subList = l ;
    LispValue[] attribute = new LispValue[]{f_lisp.NIL};

    for (;; indexSubList++, subList = subList.cdr())
    {
      if (subList == f_lisp.NIL)
      {
        return f_lisp.makeCons(f_lisp.NIL, f_lisp.NIL); // not found
      }
      int indexInSubList = indexInList(e, subList.car(), attribute);
      if (indexInSubList != 0) // found
      {
        LispValue position = f_lisp.makeCons(f_lisp.makeCons(f_lisp.makeInteger(indexSubList),
                                                             f_lisp.makeInteger(indexInSubList)),
        f_lisp.NIL);
        return f_lisp.makeCons(attribute[0], position);
      }
    }
  }


  // This places the args on the stack so that they will be evaluated L->R,
  // as is required in Common LISP.
  //
  public LispValue compileArgsLeftToRight(LispValue args,
                                          LispValue valueList,
                                          LispValue code)
    throws CompilerException
  {
    LispValue rest = code;
    if (args != f_lisp.NIL)
    {
      List<LispValue> list = args.toRandomAccess();
      for (int i = list.size() - 1; i >= 0; i--)
      {
        rest = compile(list.get(i), valueList, rest);
      }
    }
    return rest;
  }

  // This places the args on the stack L->R, but unevaluated.
  //
  public LispValue compileConstantArgsLeftToRight(SECDMachine machine,
                                                  LispValue args,
                                                  LispValue valueList,
                                                  LispValue code)
  {
    if (args == f_lisp.NIL)
      return code;
    else
      return f_lisp.makeCons(machine.LDC,
                                       f_lisp.makeCons(args.car(),
                                                                 compileConstantArgsLeftToRight(machine, args.cdr(), valueList, code)));
  }

  /**
   * Returns the input list with quotes added before every
   * top-level expression.
   */
  public LispValue quoteList(LispValue l)
  {
    LispValue rest = f_lisp.NIL;
    if (l != f_lisp.NIL)
    {
      List<LispValue> list = l.toRandomAccess();
      for (int i = list.size() - 1; i >= 0; i--)
      {
        rest = f_lisp.makeCons(f_lisp.makeList(QUOTE, list.get(i)), rest);
      }
    }
    return rest;
  }



  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  // @date    Sun Feb  2 19:46:35 1997
  /**
   * <tt>compile</tt> takes a LISP expression, a list of
   * global variables, and optionally an already-generated
   * list of code.  It returns compiled code in a list.
   *
   * @see LispCompiler
   * @param expr expression to compile
   * @param varValues  global or local variable list.
   * @return LispValue - generated code
   */
  public LispValue compile(SECDMachine machine, LispValue expr, LispValue varValues)
    throws CompilerException
  {
    if (DEBUG)
    {
      System.out.println("expr = " + expr);
      System.out.println("varValues = " + varValues);
      System.out.println("STOP = " + machine.STOP);
      System.out.println("NIL = " + f_lisp.NIL);
      System.out.println("initial code = " + f_lisp.makeCons(machine.STOP, f_lisp.NIL));
    }

    return compile(expr, varValues, f_lisp.makeCons(machine.STOP, f_lisp.NIL));
  }

  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  // @date    Sun Feb  2 19:46:35 1997
  /**
   * <tt>compile</tt> takes a LISP expression, a list of
   * global variables, and optionally an already-generated
   * list of code.  It returns compiled code in a list.
   *
   * @see LispCompiler
   * @param expr expression to compile
   * @param valueList global variable list.
   * @param  code [optional]
   * @return LispValue - generated code
   */
  public LispValue compile(LispValue expr, LispValue valueList,
                           LispValue code) throws CompilerException
  {
    if (DEBUG)
    {
      System.out.print("\nCompile: " + expr);
      System.out.print("\n   code: " + code);
    }

    if (! expr.basic_atom())
      return compileList(f_lisp.MACHINE, expr, valueList, code);
    else
      return compileAtom(f_lisp.MACHINE, expr, valueList, code);
  }


  LispValue compileAtom(SECDMachine machine, LispValue expr, LispValue valueList, LispValue code)
  {
    if (DEBUG)
    {
      System.out.print("\nCompile Atom: " + expr);
      System.out.print(" of type " + expr.getClass().getName());
      System.out.flush();
    }

    if (expr == f_lisp.NIL)
      return(f_lisp.makeCons(machine.NIL, code));
    else if (expr == f_lisp.T)
      return(f_lisp.makeCons(machine.T, code));

    else if ((expr.symbolp() == f_lisp.NIL)      // Self-evaluating atom
            || (expr.keywordp() == f_lisp.T))
      return(f_lisp.makeCons(machine.LDC, f_lisp.makeCons(expr, code)));

    else   /* A symbol.  Get its value */
    {
      //LispValue varIndex = index(expr, valueList);
      //##JPG use indexAndAttributes() instead of index
      LispValue varIdxAndAttributes = indexAndAttribute(expr, valueList);
      LispValue paramAttribute = varIdxAndAttributes.car();
      LispValue varIndex = varIdxAndAttributes.cdr().car();

      if (varIndex == f_lisp.NIL)
      {
        /* Not a local variable, maybe it's global */
        if (!expr.specialP() && WarnAboutSpecialsP)
          System.err.print("\n;; ** Warning - " + expr.toString() + " assumed special.\n");

        return (f_lisp.makeCons(machine.LD_GLOBAL,
                                f_lisp.makeCons(expr, code)));
//	 else
//	   throw new UndefinedVariableException(((LispString)(expr.symbol_name())).getValue());
      }
      else  /* Found the symbol.  Is it bound? */
      {
        //##JPG opcode LDR instead of LD for variable arguments
        // note : paramAttribute can only be nil or &rest
        LispValue loadOpCode = (paramAttribute == AMP_REST) ? machine.LDR : machine.LD;
        return(f_lisp.makeCons(loadOpCode, f_lisp.makeCons(varIndex, code)));
      }
      /*
      return (f_lisp.makeCons(machine.LD,
      f_lisp.makeCons(varIndex, code)));
      */
    }
  }

  LispValue compileList(SECDMachine machine, LispValue expr, LispValue valueList, LispValue code)
    throws CompilerException
  {
    LispValue function = expr.car();
    LispValue args     = expr.cdr();

    if (DEBUG)
      System.out.print("\nCompile List: " + expr);

    // User-defined function
    if (function.basic_functionp())
    {
      LispFunction lFunc = (LispFunction) function;
      if (lFunc.isBuiltin())
        return compileBuiltin(machine, function, args, valueList, code);
      else
        return compileUserDefinedFunction(machine, function, args, valueList, code);
    }

    // Function on a symbol
    else if (function.basic_atom())
    {
      if (isBuiltinFunction(function))
      {
        return compileBuiltin(machine, function, args, valueList, code);
      }

      else if (specialFormP(function))   // LAMBDA, DEFUN, LET, ...
        return compileSpecialForm(machine, function, args, valueList, code);


      else // FUNCTION must represent a non-builtin function or macro.
      {
        // ##JPG compileSpecialForm() has been modified to support DEFMACRO
        // LispValue defn = index(function, valueList);
        // ##JPG if function has a variable number of parameters (&rest is present in paraameters list)
        //   the opcode LDR (LoaD with Rest) is used in place of LD
        LispValue fnIdxAndAttributes = indexAndAttribute(function, valueList);
        LispValue defn = fnIdxAndAttributes.cdr().car();
        LispValue loadOpCode = (fnIdxAndAttributes.car() == AMP_REST) ? machine.LDR : machine.LD;

        if (defn == f_lisp.NIL)
        {
          try
          {
            defn = ((LispFunction)function.symbol_function()).getCode();
          }
          catch (LispException e)
          {
            defn = null;
          }


          if ((defn == f_lisp.NIL) || (defn == null))
          {
            if (function instanceof LispSymbol)
              throw new UndefinedFunctionException(((LispString)(function.symbol_name())).toString());
            else
              throw new UndefinedFunctionException(function.toString());
          }
        }

        // ##JPG add this if block to compile macro
        if (function.symbol_function().basic_macrop())
        //------------------------ compile macro --------------------------------
        {
          if (defn.car().numberp() == f_lisp.T) /* macro present in closure */
          {
            //##JPG idem compileApp but don't evaluate arguments
            return compileAppConstant(machine, args, valueList,
                                      f_lisp.makeCons(loadOpCode,
                                                      f_lisp.makeCons(defn,
                                                                      (code.car() == machine.RTN) ? f_lisp.makeCons(machine.DAP, code.cdr())
                                                                      : f_lisp.makeCons(machine.AP, code))));

          }
          else /* Compiled macro */
          {
            LispValue expandCode =
                f_lisp.makeCons(machine.DUM,
                                f_lisp.makeCons(machine.LDFC,
                                                f_lisp.makeCons(function,
                                                                f_lisp.makeCons(machine.NIL,
                                                                                f_lisp.makeCons(new ConsPrimitive(f_lisp),
                                                                                                compileLambda(machine, expr,  f_lisp.makeCons(
                                                                                                    f_lisp.makeCons(function, f_lisp.NIL),
                                                                                                    valueList),
                                                                                                              f_lisp.makeCons(machine.RAP,
                                                                                                                              f_lisp.NIL)))))));

            LispValue expandValue = machine.Execute(expandCode, f_lisp.NIL);
            if (DEBUG)
              System.out.print("\nMacro " + expr + " expanded to " + expandValue);
            return compile(expandValue, valueList,code );
          }
        }

        else   // compile a function  --------------------------------
        {
          if (defn.car().numberp() == f_lisp.T)
            return compileApp(machine, args, valueList,
                              f_lisp.makeCons(loadOpCode,
                                              f_lisp.makeCons(defn,
                                                              (code.car() == machine.RTN) ? f_lisp.makeCons(machine.DAP, code.cdr())
                                                              : f_lisp.makeCons(machine.AP, code))));

          else if (defn.car() == LAMBDA)    /* Interpreted fn */
            return compileApp(machine, args, valueList,
                              compileLambda(machine, defn.cdr().cdr(),
                                            f_lisp.makeCons(defn.second(), valueList),
                                            code));

          else /* Compiled fn */
            return f_lisp.makeCons(machine.DUM,
                                   f_lisp.makeCons(machine.LDFC,
                                                   f_lisp.makeCons(function,
                                                                   f_lisp.makeCons(machine.NIL,
                                                                                 f_lisp.makeCons(new ConsPrimitive(f_lisp),
                                                                                                 compileLambda(machine, expr,
                                                                                                               f_lisp.makeCons(f_lisp.makeCons(function, f_lisp.NIL),
                                                                                                                               valueList),
                                                                                                               f_lisp.makeCons(machine.RAP, code)))))));
        }
      }
    }


    else
    {
      /* an application from within a nested function */
      return compileApp(machine, args, valueList,
                        compile(function, valueList,
                                (code.car() == machine.RTN) ? f_lisp.makeCons(machine.DAP, code.cdr())
                                : f_lisp.makeCons(machine.AP, code)));
    }
  }

  LispValue compileSpecialForm(SECDMachine machine, LispValue function,
                               LispValue args,
                               LispValue valueList,
                               LispValue code)
    throws CompilerException
  {
    if (DEBUG)
      System.out.print("\nCompile Special Form: " + function);

    if (function == PROGN)
      return compileProgn(args, valueList, code);
    else if (function == LAMBDA)
      return compileLambda(machine, f_lisp.makeCons(PROGN, args.cdr()),
                           f_lisp.makeCons(args.car(), valueList), code);

    else if (function == DEFUN)
      return compileDefun(machine, args.car(), args.cdr(), valueList, code);

    else if (function == DEFMACRO)   // Jatha 2.5.0  April 2005   (JPG)
        return compileDefmacro(machine, args.car(), args.cdr(), valueList, code);

    else if (function == AND)
      return compileAnd(machine, args, valueList, code);

    else if (function == OR)
      return compileOr(machine, args, valueList, code);

    else if (function == IF)
      return compileIf(machine, args.first(), args.second(), args.third(),
                       valueList, code);

    // Since we now have macro, this is better defined as one.
    /*    else if (function == WHEN)
      return compileIf(machine, args.first(),
                       f_lisp.makeList(PROGN, args.second()), f_lisp.NIL,
                       valueList, code);*/

    else if ((function == LET)
            || (function == LETREC))
    {
      LispValue vars      = varsFromLetBindings(args.first());
      LispValue values    = valuesFromLetBindings(args.first());
      LispValue newValues = f_lisp.makeCons(vars, valueList);

      LispValue body      = f_lisp.makeCons(PROGN, args.cdr());

      // Notes:  27 Mar 1997
      // For every var that is a special variable, we
      // need to have SP_BIND at the beginning and
      // SP_UNBIND at the end of the following code.
      // (still need to implement SP_BIND and SP_UNBIND as ops).
      //
      // Note: SETQ must change the latest special binding
      //       if one exists.
      //
      // See "./spectest.lisp" for some test routines.

      if (function == LET)
        return
                compileLet(machine, vars, values, valueList, body, code);
      else /* a LETREC */
        return f_lisp.makeCons(machine.DUM,
                                         compileApp(machine, values, newValues,
                                                    compileLambda(machine, body, newValues,
                                                                  f_lisp.makeCons(machine.RAP,
                                                                                            code))));
    }

    // We have a return for every known branch, but I guess this
    // programmer *could* make a mistake sometime, so we'll put an
    // error message here.

    System.out.println("\n;; *** Compiler error in CompileAtom");
    return f_lisp.NIL;
  }


  public LispValue compileLet(SECDMachine machine,
                              LispValue vars, LispValue values,
                              LispValue valueList,
                              LispValue body, LispValue code)
    throws CompilerException
  {
    // Divide the variables into special and non-special var sets.
    // Special variables get extra binding instructions.

    LispValue specialVars = f_lisp.NIL;
    LispValue specialVals = f_lisp.NIL;
    LispValue localVars   = f_lisp.NIL;
    LispValue localVals   = f_lisp.NIL;
    LispValue varPtr      = vars;
    LispValue valPtr      = values;

    while (varPtr != f_lisp.NIL)
    {
      if (varPtr.car().specialP())
      {
        specialVars = f_lisp.makeCons(varPtr.car(), specialVars);
        specialVals = f_lisp.makeCons(valPtr.car(), specialVals);
      }
      else
      {
        localVars   = f_lisp.makeCons(varPtr.car(), localVars);
        localVals   = f_lisp.makeCons(valPtr.car(), localVals);
      }

      varPtr = varPtr.cdr();
      valPtr = valPtr.cdr();
    }

    // The local vars get compiled by the compileApp,
    // the special vars get compiled after that and just
    // before the Lambda is compiled.
    LispValue ret =
            compileApp(machine, localVals, valueList,
                       compileSpecialBind(machine, specialVars, specialVals, valueList,
                                          compileLambda(machine, body, f_lisp.makeCons(localVars, valueList),
                                                        f_lisp.makeCons(machine.AP,
                                                                                  compileSpecialUnbind(machine, specialVars, code)))));
    return ret;
    // (code.car() == machine.RTN) ?
    // f_lisp.makeCons(machine.DAP, code.cdr())
    //			        : f_lisp.makeCons(machine.AP, code));
  }


  // UTILITY functions for LET

  // Inserts special-bind opcode for each var.
  LispValue compileSpecialBind(SECDMachine machine, LispValue vars, LispValue values,
                               LispValue valueList, LispValue code)
    throws CompilerException
  {
    if (vars == f_lisp.NIL)
      return code;
    else
      return compile(values.car(), valueList,
                     f_lisp.makeCons(machine.SP_BIND,
                                               f_lisp.makeCons(vars.car(),
                                                                         compileSpecialBind(machine, vars.cdr(), values.cdr(), valueList, code))));
  }

  // Inserts special-bind opcode for each var.
  LispValue compileSpecialUnbind(SECDMachine machine, LispValue vars, LispValue code)
  {
    if (vars == f_lisp.NIL)
      return code;
    else
      return f_lisp.makeCons(machine.SP_UNBIND,
                                       f_lisp.makeCons(vars.car(),
                                                                 compileSpecialUnbind(machine, vars.cdr(), code)));
  }


  // each entry is (VAR VAL) or VAR.  Latter has implied value of NIL.
  public LispValue varsFromLetBindings(LispValue varValueList)
  {
    if (varValueList == f_lisp.NIL)
      return f_lisp.NIL;
    else if (varValueList.car().basic_consp())
      return f_lisp.makeCons(varValueList.car().car(),
                                       varsFromLetBindings(varValueList.cdr()));
    else
      return f_lisp.makeCons(varValueList.car(),
                                       varsFromLetBindings(varValueList.cdr()));
  }

  // each entry is (VAR VAL) or VAR.  Latter has implied value of NIL.
  public LispValue valuesFromLetBindings(LispValue varValueList)
  {
    if (varValueList == f_lisp.NIL)
      return f_lisp.NIL;
    else if (varValueList.car().basic_consp())
      return f_lisp.makeCons(varValueList.car().second(),
                                       valuesFromLetBindings(varValueList.cdr()));
    else
      return f_lisp.makeCons(f_lisp.NIL,
                                       valuesFromLetBindings(varValueList.cdr()));
  }

  /* obsolete 1 Sep 2004 (mh)
  boolean builtinFunctionP(LispValue fn)
  {
    if ((! fn.basic_symbolp()) || (fn.fboundp() != f_lisp.T))
      return false;

    LispValue defn = fn.symbol_function();

    if (defn == null)
      return false;

    if ((defn.listp() == f_lisp.T) && (defn.first() == PRIMITIVE))
      return true;
    else
      return false;
  }
  */

  public boolean specialFormP(LispValue fn)
  {
    if ((fn.symbolp() == f_lisp.T)
            &&    ((fn == AND)
            || (fn == DEFMACRO)
            || (fn == DEFUN)
            || (fn == IF)
            || (fn == LET)
            || (fn == LAMBDA)
            || (fn == LETREC)
            || (fn == OR)
            || (fn == PROGN)
                   //            || (fn == BLOCK)
                   //            || (fn == WHEN)
            ))
      return true;
    else
      return false;
  }

  // This version of 'compileApp' is modified from the version in Kogge's
  // book.  It puts args on stack in the correct L->R order and does
  // not require the caller to prepend a NIL instruction on the
  // resulting code.
  LispValue compileApp(SECDMachine machine, LispValue args, LispValue valueList, LispValue code)
    throws CompilerException
  {
    if (DEBUG)
      System.out.print("\nCompile App: " + args + ", valueList = " + valueList);

    if (args == f_lisp.NIL)
      return f_lisp.makeCons(machine.NIL, code);
    else
      return
              compile(args.car(), valueList,
                      compileApp(machine, args.cdr(), valueList,
                                 f_lisp.makeCons(new ConsPrimitive(f_lisp), code)));
  }


  // ##JPG added
  // similar to compileApp() but doesn't evaluate parameters
  LispValue compileAppConstant(SECDMachine machine, LispValue args, LispValue valueList, LispValue code)
  {
    if (DEBUG)
      System.out.print("\nCompile AppConstant: " + args + ", valueList = " + valueList + ", code = " + code);
    
    List<LispValue> list = args.toRandomAccess();
    if (args != f_lisp.NIL)
    {
      for (int i = list.size() - 1; i >= 0; i--)
      {
        code = f_lisp.makeCons(new ConsPrimitive(f_lisp), code);
      }
    }
    code = f_lisp.makeCons(machine.NIL, code);
    LispValue rest = code;
    if (args != f_lisp.NIL)
    {
      for (int i = list.size() - 1; i >= 0; i--)
      {
        rest = f_lisp.makeCons(machine.LDC, f_lisp.makeCons(list.get(i), rest));
      }
    }
    return rest;
  }

  LispValue compileBuiltin(SECDMachine machine, LispValue fn, LispValue args,
                           LispValue valueList, LispValue code) throws CompilerException
  {
    if (DEBUG)
      System.out.print("\nCompile Builtin: " + fn + "  " + args);


    // Builtin LISP primitives have a symbol-function of the
    // form (:PRIMITIVE <ic>).  We call the CompileArgs functions
    // of the primitive instruction.
    LispValue executableCode = f_lisp.NIL;

    if (fn.basic_atom())
      executableCode = ((LispFunction)fn.symbol_function()).getCode().second();
    else if (fn.basic_functionp())
      executableCode = (((LispFunction)fn).getCode()).second();

    if (!((LispPrimitive)executableCode).validArgumentList(args))
      throw new ArgumentCountMismatchException(((LispString)fn.symbol_name()).getValue(),
                                               ((LispPrimitive)executableCode).parameterCountString(),
                                               ((LispInteger)(args.length())).getLongValue());

    return ((LispPrimitive)executableCode).CompileArgs(this, machine, fn, args, valueList, code);
  }

  /**
   * FN is an instance of StandardLispFunction
   * @param machine
   * @param fn an instance of StandardLispFunction
   * @param args
   * @param valueList
   * @param code
   * @throws CompilerException
   */
  LispValue compileUserDefinedFunction(SECDMachine machine, LispValue fn, LispValue args,
                                       LispValue valueList, LispValue code) throws CompilerException
  {
    if (DEBUG)
      System.out.print("\nCompile user-defined: (" + fn + "  " + args + "), vl = " + valueList);

    LispValue executableCode = ((LispFunction)fn).getCode();

    // Assume that the arguments are correct ?
    // TODO: how do we check arguments?  Besides, we've lost the name of the function.
    /*
    if (!((LispPrimitive)(executableCode.second())).validArgumentList(args))
      throw new ArgumentCountMismatchException(((LispString)fn.symbol_name()).getValue(),
                                               ((LispPrimitive)(executableCode.second())).parameterCountString(),
                                               ((LispInteger)(args.length())).getLongValue());
    */

    return compileArgsLeftToRight(args, valueList, executableCode.append(code));
  }


  LispValue compileAnd(SECDMachine machine, LispValue args, LispValue valueList, LispValue code)
    throws CompilerException
  {
    // No args: return default value of T
    if (args == f_lisp.NIL)
      return f_lisp.makeCons(machine.T, code);

    // 1 arg: just compile the argument.
    if (args.cdr() == f_lisp.NIL)
      return compile(args.first(), valueList, code);

    // Multiple arguments: construct an IF statement
    // (let ((*dummy* args.first())) (if ...))

    LispValue dummyVar = f_lisp.EVAL.intern("*AND-DUMMY-VAR*");
    dummyVar.set_special(true);

    return compile(f_lisp.makeList(LET,
      f_lisp.makeCons(f_lisp.makeList(dummyVar, args.first()), f_lisp.NIL),
      f_lisp.makeList(IF, dummyVar, compileAndAux(dummyVar, args.cdr()), f_lisp.NIL)),
      valueList,
      code);
  }


  LispValue compileAndAux(LispValue dummyVar, LispValue args)
  {
    if (args.cdr() == f_lisp.NIL)
      return (args.car());

    return
            f_lisp.makeList(PROGN,
              f_lisp.makeList(SETQ, dummyVar, args.car()),
              f_lisp.makeList(IF, dummyVar,
                compileAndAux(dummyVar, args.cdr()),
                f_lisp.NIL));
  }



  LispValue compileOr(SECDMachine machine, LispValue args, LispValue valueList, LispValue code)
    throws CompilerException
  {
    // No args: return default value of NIL
    if (args == f_lisp.NIL)
      return f_lisp.makeCons(machine.NIL, code);

    // 1 arg: just compile the argument.
    if (args.cdr() == f_lisp.NIL)
      return compile(args.first(), valueList, code);

    // Multiple arguments: construct an IF statement
    // (let ((*dummy* args.first())) (if ...))

    LispValue dummyVar = f_lisp.EVAL.intern("*OR-DUMMY-VAR*");
    dummyVar.set_special(true);

    return compile(f_lisp.makeList(LET,
      f_lisp.makeCons(f_lisp.makeList(dummyVar, args.first()),
        f_lisp.NIL),
      f_lisp.makeList(IF, dummyVar,
        dummyVar,
        compileOrAux(dummyVar, args.cdr()))),
      valueList,
      code);
  }


  LispValue compileOrAux(LispValue dummyVar, LispValue args)
  {
    if (args.cdr() == f_lisp.NIL)
      return (args.car());

    return
      f_lisp.makeList(PROGN,
        f_lisp.makeList(SETQ, dummyVar, args.car()),
        f_lisp.makeList(IF, dummyVar,
          dummyVar,
          compileOrAux(dummyVar, args.cdr())));
  }



  LispValue compileDefun(SECDMachine machine, LispValue name, LispValue argsAndBody,
                         LispValue valueList, LispValue code)
    throws CompilerException
  {
    // Change the DEFUN into a LAMBDA and compile it.
    //##JPG
    // for compilation of recursive functions, we need to know if the symbol under compilation is a
    // function or a macro. It's the aim of DUMMY_FUNCTION
    name.setf_symbol_function(DUMMY_FUNCTION);

    // OB:
    // Added support for documentation strings on defuns.
    // Todo, fix support, so that DECLARE statements are ignored.
    // If the body of the function only consists of a string, this is not taken as a
    // documentation string, as there would be no content of the function otherwise.
    final LispValue possibleDocumentation = argsAndBody.second();
    LispValue endArgsAndBody = argsAndBody;
    if(possibleDocumentation instanceof LispString && argsAndBody.basic_length() > 2) {
        name.setf_documentation(f_lisp.EVAL.intern("FUNCTION"),possibleDocumentation);
        endArgsAndBody = f_lisp.makeCons(argsAndBody.car(),argsAndBody.cdr().cdr());
    }
    // Adds an implicit BLOCK with the same name as the defun around the definition.
    endArgsAndBody = f_lisp.makeList(endArgsAndBody.car(),f_lisp.makeCons(f_lisp.getEval().intern("BLOCK"),f_lisp.makeCons(name, endArgsAndBody.cdr())));
    name.setf_symbol_function(
            compileList(f_lisp.MACHINE, f_lisp.makeCons(LAMBDA, endArgsAndBody),
                        f_lisp.makeCons(f_lisp.makeCons(name, f_lisp.NIL),
                                                  valueList),
                        f_lisp.makeCons(machine.STOP, f_lisp.NIL)).second());

    return
            compileList(f_lisp.MACHINE, f_lisp.makeCons(QUOTE, f_lisp.makeCons(name, f_lisp.NIL)),
                        f_lisp.makeCons(f_lisp.makeCons(name, f_lisp.NIL),
                                                  valueList),
                        code);
  }

  //##JPG  method added, compile DEFMACRO, April 2005
  //
   LispValue compileDefmacro(SECDMachine machine, LispValue name, LispValue argsAndBody,
         LispValue valueList, LispValue code)
      throws CompilerException
  {
      LispValue tempNew = f_lisp.EVAL.intern("%%%" + name.symbol_name().toStringSimple(),(LispPackage)f_lisp.findPackage("SYSTEM"));
      compileDefun(machine,tempNew,argsAndBody,valueList,code); // TODO, fix an ew method for doing this.
    //  ##JPG
    // for compilation of recursive macros, we need to know if the symbol under compilation is a
    // function or a macro. It's the aim of DUMMY_FUNCTION
    name.setf_symbol_function(DUMMY_MACRO);

    // todo: Figure out how to imbed BACKQUOTE in the compiler.
    //##JPG  MACRO keyword if added in front of code to signal this code is a macro
    //       setf_symbol_function() has been modified to detect this keyword
    //LispValue args = argsAndBody.first();
    //LispValue body = argsAndBody.second();
    //LispValue expandedBody = f_lisp.NIL;
    //if (body.first().eq(f_lisp.BACKQUOTE) != f_lisp.NIL)
//      expandedBody = f_lisp.backquote(body.second());
//    else
//      expandedBody = body;

    // OB:
    // Added support for documentation strings on defmacros.
    // Todo, fix support, so that DECLARE statements are ignored.
    // If the body of the macro only consists of a string, this is not taken as a
    // documentation string, as there would be no content of the macro otherwise.
    final LispValue possibleDocumentation = argsAndBody.second();
    LispValue endArgsAndBody = argsAndBody;
    if(possibleDocumentation instanceof LispString && argsAndBody.basic_length() > 2) {
        name.setf_documentation(f_lisp.EVAL.intern("FUNCTION"),possibleDocumentation);
        endArgsAndBody = f_lisp.makeCons(argsAndBody.car(),argsAndBody.cdr().cdr());
    }

    name.setf_symbol_function(
        f_lisp.makeCons(  MACRO ,
                          compileList(f_lisp.MACHINE,
                                      f_lisp.makeCons(LAMBDA, endArgsAndBody), // f_lisp.makeList(args, expandedBody)),
                                      f_lisp.makeCons(f_lisp.makeCons(name, f_lisp.NIL),
                                                      valueList),
                                      f_lisp.makeCons(machine.STOP, f_lisp.NIL)).second()
        ));

    return
        compileList(f_lisp.MACHINE, f_lisp.makeCons(QUOTE, f_lisp.makeCons(name, f_lisp.NIL)),
                    f_lisp.makeCons(f_lisp.makeCons(name, f_lisp.NIL),
                                    valueList),
                    code);
  }

  // Optimization, 15 April 97, p. 174 of Kogge.
  // SEL followed by RTN can be optimized to do a RTN
  // at the end of each branch and eliminate the final RTN.

  LispValue compileIf(SECDMachine machine, LispValue test, LispValue thenExpr, LispValue elseExpr,
                      LispValue valueList, LispValue code)
    throws CompilerException
{
    if ((code.car() == machine.RTN)
      || (code.car() == machine.STOP))
      return compileOptimizedIf(machine, test, thenExpr, elseExpr, valueList, code);
    else
      return
              compile(test, valueList,
                      f_lisp.makeCons(machine.SEL,
                                                f_lisp.makeCons(compile(thenExpr, valueList,
                                                                                  f_lisp.makeCons(machine.JOIN, f_lisp.NIL)),
                                                                          f_lisp.makeCons(compile(elseExpr, valueList,
                                                                                                            f_lisp.makeCons(machine.JOIN, f_lisp.NIL)),
                                                                                                    code))));
  }


  LispValue compileOptimizedIf(SECDMachine machine, LispValue test, LispValue thenExpr,
                               LispValue elseExpr, LispValue valueList,
                               LispValue code)
          // Remove final RTN and optimize by putting RTN in branches.
    throws CompilerException

  {
    return
            compile(test, valueList,
                    f_lisp.makeCons(machine.TEST,
                                              f_lisp.makeCons(compile(thenExpr, valueList,
                                                                                f_lisp.makeCons(code.car(), f_lisp.NIL)),
                                                                        compile(elseExpr, valueList, code))));
  }


  LispValue compileProgn(LispValue body, LispValue valueList, LispValue code)
    throws CompilerException
  {
    if (body == f_lisp.NIL)
      return code;
    else
      return compile(body.car(), valueList,
                     compileProgn(body.cdr(), valueList, code));
  }
    /*
    private java.util.Map blocks = new java.util.HashMap();

  LispValue compileBlock(final SECDMachine machine, final LispValue body, final LispValue valueList, final LispValue code) throws CompilerException {
      System.err.println("We have a block");
      final LispValue tag = body.car();
      System.err.println("Tag " + tag);
      java.util.Stack stBlock = (java.util.Stack)blocks.get(tag);
      if(null == stBlock) {
          System.err.println("Creating new stack for this tag");
          stBlock = new java.util.Stack();
          blocks.put(tag,stBlock);
      }
      final LispValue uhm = f_lisp.makeList(machine.S.value(),machine.E.value(),machine.C.value(),machine.D.value());
      stBlock.push(uhm);
      final int size = stBlock.size();
      final LispValue rest = f_lisp.makeCons(PROGN,body.cdr());
      final LispValue afterC = compile(machine,rest,valueList);
      System.err.println("After compilation");
      if(stBlock.size() == size) {
          System.err.println("Popping block");
          System.err.println(stBlock.pop());
      }
      return afterC;
  }
    */


  LispValue compileLambda(SECDMachine machine, LispValue body, LispValue valueList, LispValue code)
    throws CompilerException
  {
    // System.out.print("\nCompile Lambda: "); body.prin1();
    // System.out.print("\n code = "); code.prin1();
    return f_lisp.makeCons(machine.LDF,
                           f_lisp.makeCons(compile(body, valueList,
                                                   f_lisp.makeCons(machine.RTN, f_lisp.NIL)),
                                           code));
  }

  /**
   * Send in either code or a symbol with a function value.
   * Returns true only if the first element of the code list
   * is :PRIMITIVE.
   * @param code a LISP list.
   * @return true if the code indicates a built-in function
   */
  public static boolean isBuiltinFunction(LispValue code)
  {
    if ((code.basic_symbolp()) && (code.fboundp() == code.getLisp().T))
      code = code.symbol_function();

    if ((code == null) || (code == code.getLisp().NIL))
      return false;

    if (code.basic_functionp())
      code = ((LispFunction)code).getCode();

    return (code.basic_listp() && (code.first() == code.getLisp().EVAL.intern("PRIMITIVE",
                                   (LispPackage)(code.getLisp().findPackage("KEYWORD")))));
  }


  // Contributed by Jean-Pierre Gaillardon, April 2005
  /**
   * @param code a Lisp list
   * @return true if code is code for a macro (the first element is :MACRO)
   */
  public boolean isMacroCode(LispValue code)
  {
    return code.basic_listp() && (code.car() == MACRO);
  }

}
