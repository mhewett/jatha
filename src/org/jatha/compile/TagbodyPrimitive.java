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
/**
 * $Id: TagbodyPrimitive.java,v 1.4 2009/07/26 05:23:12 mhewett Exp $
 */
package org.jatha.compile;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;

/**
 * <p>The TAGBODY special form. Read CLTL2 for more information</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.4 $
 */
public class TagbodyPrimitive extends LispPrimitive 
{
  public static final boolean DEBUG = false;


  public TagbodyPrimitive(final Jatha lisp) {
    super(lisp,"TAGBODY",1,Long.MAX_VALUE);
  }

  public void Execute(final SECDMachine machine) {
    machine.S.pop();
    machine.C.pop();
    machine.X.pop();
  }

  /**
   * Compiles the arguments for Tagbody.
   * An argument is either a list, in which case it is a statement to be executed
   * or a symbol, in which case it is a tag that can be a destination of a (GO tag) statement.
   */
  public LispValue CompileArgs(final LispCompiler compiler, final SECDMachine machine, final LispValue args, final LispValue valueList, final LispValue code) throws CompilerException 
  {
    if (DEBUG)
      System.out.println("\n\nTAGBODY compile ----------------------------------");

    final Map<LispValue, Integer> tags = new HashMap<LispValue, Integer>();
    final List<LispValue> progns = new ArrayList<LispValue>();

    // Put all the tags in a tag map, and put all the statements in the progns list.
    for(final Iterator<LispValue> iter = args.iterator();iter.hasNext();) {
      final LispValue val = iter.next();
      if(val.basic_symbolp()) {
        tags.put(val,new Integer(progns.size()));
      } else if(val.basic_listp()) {
        progns.add(val);
      }
    }

    // DEBUG
    if (DEBUG)
    {
      System.out.println("\n\n  Found " + tags.size() + " tags  ------------------------------");
      if (tags.size() > 0)
        for (Map.Entry<LispValue, Integer> entry : tags.entrySet())
          System.out.format("    %d: %s\n", entry.getValue(), entry.getKey().toString());
    }


    // Map each tag to all statements that will be executed after a (GO ...) for that tag.
    final Map<LispValue, List<LispValue>> tags2 = new HashMap<LispValue, List<LispValue>>();
    for(final Iterator<LispValue> iter = tags.keySet().iterator();iter.hasNext();) {
      final LispValue tag = iter.next();
      final int index = tags.get(tag).intValue();
      tags2.put(tag,progns.subList(index,progns.size()));
    }
    if (DEBUG)
    {
      System.out.println("\n\n  Found " + tags.size() + " tag bodies   -------------------------------");
      if (tags2.size() > 0)
        for (Map.Entry<LispValue, List<LispValue>> entry : tags2.entrySet())
          System.out.format("    %s: %d\n", entry.getKey().toString(), entry.getValue().size());
    }


    // Tell the compiler what tags are available.
    compiler.getLegalTags().push(tags.keySet());

    // Put a progn around each tag destination computed above
    // Compile the progn and store the compiled code with the tag.
    final Map<LispValue, LispValue> tags3 = new HashMap<LispValue, LispValue>();
    for(final Iterator<LispValue> iter = tags2.keySet().iterator();iter.hasNext();) {
      final LispValue tag = iter.next();
      final LispValue allProgns = f_lisp.makeList(f_lisp.makeCons(f_lisp.getEval().intern("PROGN"),f_lisp.makeList(tags2.get(tag))));
      tags3.put(tag,compiler.compileArgsLeftToRight(allProgns,valueList,code));
    }
    if (DEBUG)
    {
      System.out.println("\n\n  Created " + tags3.size() + " tagbody progns  ---------------------------------------");
      if (tags3.size() > 0)
        for (Map.Entry<LispValue, LispValue> entry : tags3.entrySet())
          System.out.format("    %s: %s\n", entry.getKey().toString(), entry.getValue().toString());
    }

    // Compile the quoted code  ?? why ??
    final Map<LispValue, LispValue> metaCode = new HashMap<LispValue, LispValue>();
    for(final Iterator<LispValue> iter = tags3.keySet().iterator();iter.hasNext();) {
      final LispValue tag = iter.next();
      final LispValue codeX = f_lisp.makeList(f_lisp.QUOTE,tags3.get(tag));
      final LispValue code2 = compiler.compile(codeX,valueList,f_lisp.NIL);
      metaCode.put(tag,code2);
    }
    if (DEBUG)
    {
      System.out.println("\n\n  Created " + metaCode.size() + " tagbody metacodes  -----------------------------------");
      if (metaCode.size() > 0)
        for (Map.Entry<LispValue, LispValue> entry : metaCode.entrySet())
          System.out.format("    %s: %s\n", entry.getKey().toString(), entry.getValue().toString());
    }

    // Put a progn around all of the progns and compile it.
    final LispValue all = f_lisp.makeList(f_lisp.makeCons(f_lisp.getEval().intern("PROGN"),f_lisp.makeList(progns)));
    final LispValue theCode = compiler.compileArgsLeftToRight(all,valueList,f_lisp.makeList(code.car()));

    // Remove the list of legal tags
    compiler.getLegalTags().pop();

    if (DEBUG)
    {
      Map<Long, LispValue> gos = compiler.getRegisteredGos();
      System.out.println("\n\n  Found " + gos.size() + " tagbody gos  --------------------------------");
      if (gos.size() > 0)
        for (Map.Entry<Long, LispValue> entry : gos.entrySet())
          System.out.format("    %d: %s\n", entry.getKey(), entry.getValue().toString());
    }

    // Generate the code.
    LispValue loadBindings = f_lisp.NIL;
    LispValue unloadBindings = f_lisp.NIL;

    HashSet<LispValue> allTags = new HashSet<LispValue>(compiler.getRegisteredGos().values());
    
    for(final Iterator<Long> iter = compiler.getRegisteredGos().keySet().iterator();iter.hasNext();) {
      final Long key = iter.next();
      final LispValue tag = compiler.getRegisteredGos().get(key);
      if(tags.containsKey(tag)) {
        final LispValue tagSym = f_lisp.getEval().intern("#:T"+key);
        loadBindings = f_lisp.makeCons(machine.LD_GLOBAL,f_lisp.makeCons(tag,f_lisp.makeCons(machine.SP_BIND,f_lisp.makeCons(tagSym,loadBindings))));        
        unloadBindings = f_lisp.makeCons(machine.SP_UNBIND,f_lisp.makeCons(tagSym,unloadBindings));
        iter.remove();
      }            
    }

    for(final Iterator<LispValue> iter = allTags.iterator();iter.hasNext();) {
      final LispValue tag = iter.next();
      if(tags.containsKey(tag)) {
        loadBindings = f_lisp.makeCons(machine.LDC,f_lisp.makeCons(tags3.get(tag),
            f_lisp.makeCons(machine.SP_BIND,f_lisp.makeCons(tag,loadBindings))));
        unloadBindings = f_lisp.makeCons(machine.SP_UNBIND,f_lisp.makeCons(tag,unloadBindings));
      }
    }

    if (DEBUG)
      System.out.println("\nEND TAGBODY -----------------------------------------");

    return loadBindings.append(f_lisp.makeList(machine.TAG_B)).append(theCode).append(f_lisp.makeList(machine.TAG_E)).append(unloadBindings).append(code.cdr());
  }
}// TagbodyPrimitive
