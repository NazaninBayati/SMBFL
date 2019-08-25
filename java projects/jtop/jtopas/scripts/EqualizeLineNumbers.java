import de.fub.bytecode.classfile.*;
import de.fub.bytecode.generic.*;
import de.fub.bytecode.util.*;
import de.fub.bytecode.*;
import de.fub.bytecode.Constants;
import de.fub.bytecode.generic.Instruction;
import java.io.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Utility class which equalizes the line number tables of two classes, one of
 * which is expected to have a fault seed (or seeds), and one which is expected
 * to be the baseline class. This is intended to eliminate false positives in
 * determining fault-revealing test cases resulting from a technically trivial
 * variance in the source line numbers supplied by exception stack traces, while
 * preserving the unique identity of such stack traces to the extent that they
 * were uniquely identifiable in the baseline class.
 *
 * <p>There are two ways to use this class. Both methods accept as input a Java
 * source code file that has all faults seeded using cpp-style preprocessor
 * directives and a comma-delimited numeric list of faults currently considered
 * to be active. Faults are numbered in order of appearance in the source file,
 * starting at 1 (one). The macro specified to conditional directives (e.g.
 * #ifdef FAULT_ONE) is used to link separate directives together, as long as
 * they are sequential (this may be used, for example, where removal of a line
 * requires subsequent removal of a closing brace while not affecting
 * intervening lines).</p>
 *
 * <p>The third argument can be either the name of a Java source file or Java
 * class file. If it is a source file, this class will output to that file
 * the processed source with the appropriate faults activated and the line
 * numbers equalized. If it is a class file, it will load that class and
 * directly patch equalized values into the line number tables of each method.
 * The specified class file should have been processed by cpp with the specified
 * fault(s) active before compilation. Regardless of which output type is
 * requested, both the baseline and actively seeded class should be processed
 * by this tool to guarantee correct results.</p>
 *
 * <p>Direct patching of class files operates within a critical constraint
 * at this time. Due to the declared behavior of cpp, long runs of empty lines
 * resulting from its preprocessing will be discarded. If this happens, the
 * direct patching algorithm will not be able to produce correct results.
 * Equalized source files should be generated and compiled instead.</p>
 *
 * <p>Usage: <code>java EqualizeLineNumbers &lt;seeded_source&gt; &lt;1,2,...,n&gt;
 * &lt;eq.java | eq.class&gt;<br>
 * nbsp;nbsp;nbsp;nbsp;seeded_source: source file containing seeded faults (all)
 * nbsp;nbsp;nbsp;nbsp;1,2,...,n: list of faults that are to be considered active
 * nbsp;nbsp;nbsp;nbsp;eq.java | eq.class: name of file to which equalized source
 * should be written, or classfile which should be directly patched.</code></p>
 *
 * @author Alex Kinneer
 * @version 09/29/2003
 */
public class EqualizeLineNumbers {
    private static LinkedList[] faults = new LinkedList[0];
    private static LinkedList activeFaults = new LinkedList();
    private static LinkedList shiftRules = new LinkedList();
     
    private static String inputFile;
 
    private static final int IFDEF = 0x00000001;
    private static final int IFNDEF = 0x00000002;
    private static final int ELIF = 0x00000024;
    private static final int REG_ELSE = 0x00000028;  // #else
    private static final int ENDIF = 0x00000010;
    private static final int ELSE = 0x00000020;  // Wildcard match for #else and #elif
    private static final int ACTIVE = 0x80000000;
    
    private static final boolean DEBUG = false;
    
    /**
     * Print the usage message and exit.
     */
    private static void printUsage() {
        System.err.println("Usage: <seeded_source> <1,2,...,n> <eq.java | eq.class>");
        System.exit(1);
    }
    
    /**
     * Entry point for the tool and the method responsible for parsing and generating
     * the set of shift rules.
     *
     * <p>The program generates a set of rules which specify that from a given line
     * number (inclusive), the remaining lines should be shifted a certain number
     * of lines in a particular direction. This is maintained cumulatively, so that
     * only the nearest matching rule must be found to determine the proper action
     * to take for a given line. When shifting lines downward, empty lines are
     * inserted.</p>
     */
    public static void main(String[] argv) throws IOException {
        if (argv.length != 3) {
            printUsage();
        }
    
        inputFile = argv[0];
    
        try {
            StringTokenizer strtok = new StringTokenizer(argv[1], ",");
            while (strtok.hasMoreTokens()) {
                activeFaults.add(new Integer(strtok.nextToken()));
            }
        }
        catch (NumberFormatException e) {
            printUsage();
        }
     
        // Read the seeded file (containing preprocessor directives)
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        
        // Set up the tokenizer
        StreamTokenizer stok = new StreamTokenizer(br);
        stok.resetSyntax();
        stok.eolIsSignificant(true);
        stok.wordChars(0x0000, 0xFFFF);
        stok.whitespaceChars(0x00, 0x20);
        
        // Locate fault seeds and build lists identifying affected regions of code
        int tokenType, toknum = 1;
        LinkedList faultSeeds = new LinkedList();
        LinkedList faultData = null;
        String faultName = null;
        while ((tokenType = stok.nextToken()) != StreamTokenizer.TT_EOF) {
            if (tokenType == StreamTokenizer.TT_EOL) {
               toknum = 1;
               continue;
            }
            if ((toknum == 1) && (stok.sval != null)) {
                if (stok.sval.equals("#ifdef")) {
                    if (faultData != null) {
                        System.err.println("Error: #ifdef without #endif on line " +
                                           ((Integer) faultData.getFirst()).intValue() +
                                           " (nesting is not permitted).");
                        System.exit(1);
                    }
                    stok.nextToken();
                    if (stok.sval.equals(faultName)) {
                        faultData = (LinkedList) faultSeeds.removeLast();
                    }
                    else {
                        faultData = new LinkedList();
                    }
                    faultData.add(new CondBlock(IFDEF, IFDEF, stok.lineno() + 1));
                    faultName = stok.sval;
                }
                else if (stok.sval.equals("#ifndef")) {
                    if (faultData != null) {
                        System.err.println("Error: #ifndef without #endif on line " +
                                           ((Integer) faultData.getFirst()).intValue() +
                                           " (nesting is not permitted).");
                        System.exit(1);
                    }
                    // Multiple preprocessor directives may be used for one fault,
                    // we need to chain them together as one logical fault in this case
                    stok.nextToken();
                    if (stok.sval.equals(faultName)) {
                        faultData = (LinkedList) faultSeeds.removeLast();
                    }
                    else {
                        faultData = new LinkedList();
                    }
                    faultData.add(new CondBlock(IFNDEF, IFNDEF, stok.lineno() + 1));
                    faultName = stok.sval;
                }
                else if (stok.sval.equals("#else") || stok.sval.equals("#elif")) {
                    if (faultData == null) {
                        System.err.println("Error: #else or #elif without #if on line " +
                                           stok.lineno());
                        System.exit(1);
                    }
                    // Set the length of the previous block
                    CondBlock lastBlock = ((CondBlock) faultData.getLast());
                    lastBlock.length = stok.lineno() - lastBlock.startLine; 
                    // Now add the current block
                    faultData.add(new CondBlock(lastBlock.condType, ELSE, stok.lineno() + 1));
                }
                else if (stok.sval.equals("#endif")) {
                    if (faultData == null) {
                        System.err.println("Error: #endif without #if on line " +
                                           stok.lineno());
                        System.exit(1);
                    }
                    // Set the length of the previous block
                    CondBlock lastBlock = ((CondBlock) faultData.getLast());
                    lastBlock.length = stok.lineno() - lastBlock.startLine; 
                    // Now add the current block, which actually points to the start
                    // of the block following the #endif
                    faultData.add(new CondBlock(lastBlock.condType, ENDIF, stok.lineno() + 1));
                    faultSeeds.add(faultData);
                    faultData = null;
                }
            }
            toknum++;
        }
        // Done with seeded file
        stok = null;
        br.close();
         
        faults = (LinkedList[]) faultSeeds.toArray(faults);
        
        if (DEBUG) {
            for (int i = 0; i < faults.length; i++) {
                faultData = faults[i];
                for (ListIterator li = faultData.listIterator(); li.hasNext(); ) {
                    CondBlock theBlock = (CondBlock) li.next();
                    if (theBlock.type == IFDEF) {
                        System.out.print("#ifdef: \t");
                    }
                    else if (theBlock.type == IFNDEF) {
                        System.out.print("#ifndef: \t");
                    }
                    else if (theBlock.type == ELSE) {
                        System.out.print("#else:  \t");
                    }
                    else if (theBlock.type == ENDIF) {
                        System.out.print("#endif: \t");
                    }
                    System.out.println("line " + theBlock.startLine + ",\tlength = " + theBlock.length);
                }
            }
        }
        
        int condType = 0, blockProperties = 0x00000000, shiftDistance = 0;
        //boolean hasElseBlock, blockIsEmpty;
        CondBlock curBlock, lastBlock = null;
        for (int i = 0; i < faults.length; i++) {
            faultData = faults[i];
             
            while (faultData.size() > 0) {
                curBlock = (CondBlock) faultData.removeFirst();
                if (curBlock.type == ENDIF) {
                    // These blocks are used merely as sentinels in various parts of
                    // the algorithm
                    continue;
                }
                
                if (activeFaults.contains(new Integer(i + 1))) {
                    blockProperties = ACTIVE;
                }
                else {
                    blockProperties = 0x00000000;
                }
                blockProperties |= curBlock.condType;
                blockProperties |= curBlock.type;
                
                if ((curBlock.type == IFDEF) || (curBlock.type == IFNDEF)) {
                    shiftDistance -= 1;
                    shiftRules.add(new ShiftRule(curBlock.startLine, shiftDistance, false));
                }
                
                switch (blockProperties) {
                    case (IFDEF):
                        //blockIsEmpty = true;
                        if (((CondBlock) faultData.getFirst()).type != ENDIF) { // has else clause
                            shiftDistance -= curBlock.length + 1;
                        }
                        else {
                            shiftDistance -= 1;
                        }
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, true));
                        break;
                    case (IFDEF | ACTIVE):
                        //blockIsEmpty = false;
                        shiftDistance -= 1;
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, false));
                        break;
                    case (IFDEF | ACTIVE | ELSE):
                        //blockIsEmpty = true;
                        if (curBlock.length > lastBlock.length) {
                            shiftDistance -= (curBlock.length + 1)
                                             - (curBlock.length - lastBlock.length);
                            if (DEBUG) System.out.println("Imbalance rule one: shift " + shiftDistance);
                        }
                        else {
                            shiftDistance -= curBlock.length + 1;
                        }
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, true));
                        break;
                    case (IFDEF | ELSE):
                        //blockIsEmpty = false;
                        if (lastBlock.length > curBlock.length) {
                            shiftDistance -= 1 - (lastBlock.length - curBlock.length);
                            if (DEBUG) System.out.println("Imbalance rule three: shift " + shiftDistance);
                        }
                        else {
                            shiftDistance -= 1;
                        }
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, false));
                        break;
                    case (IFNDEF):
                        //blockIsEmpty = false;
                        shiftDistance -= 1;
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, false));
                        break;
                    case (IFNDEF | ACTIVE):
                        //blockIsEmpty = true;
                        if (((CondBlock) faultData.getFirst()).type != ENDIF) {  // has an else block
                            shiftDistance -= curBlock.length + 1;
                        }
                        else {
                            shiftDistance -= 1;
                        }
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, true));
                        break;
                    case (IFNDEF | ACTIVE | ELSE):
                        //blockIsEmpty = false;
                        if (lastBlock.length > curBlock.length) {
                            shiftDistance -= 1 - (lastBlock.length - curBlock.length);
                            if (DEBUG) System.out.println("Imbalance rule four: shift " + shiftDistance);
                        }
                        else {
                            shiftDistance -= 1;
                        }
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, false));
                        break;
                    case (IFNDEF | ELSE):
                        //blockIsEmpty = true;
                        if (curBlock.length > lastBlock.length) {
                            shiftDistance -= (curBlock.length + 1)
                                             - (curBlock.length - lastBlock.length);
                            if (DEBUG) System.out.println("Imbalance rule two: shift " + shiftDistance);
                        }
                        else {
                            shiftDistance -= curBlock.length + 1;
                        }
                        shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance, true));
                        break;
                    default:  // ENDIF
                        //blockIsEmpty = false;
                }
                lastBlock = curBlock;
            }
        }
  
        if (DEBUG) {
            ShiftRule rule;
            int n = 1;
            for (ListIterator li = shiftRules.listIterator(); li.hasNext(); ) {
                rule = (ShiftRule) li.next();
                System.out.println("Rule " + n + ": From line " + rule.lineNum + ", shift " + rule.shiftDistance + " lines");
                n += 1;
            }
            /* Reverse iteration (order in which rules will be searched)
            for (ListIterator li = shiftRules.listIterator(shiftRules.size()); li.hasPrevious(); ) {
                rule = (ShiftRule) li.previous();
                System.out.println("Rule " + n + ": Line " + rule.lineNum + " shift " + rule.shiftDistance + " lines");
                n += 1;
             }
             */
         }
 
         if (argv[2].endsWith(".java")) {
             try {
 	        writeEqualizedSource(argv[2]);
 	    }
 	    catch (IOException e) {
 	        System.err.println("Error creating normalized source file");
 	        e.printStackTrace();
 	        System.exit(1);
 	    }
 	}
 	else if (argv[2].endsWith(".class")) {
 	    try {
 	        writeEqualizedClassFile(argv[2]);
 	    }
 	    catch (IOException e) {
 	        System.err.println("Error creating normalized class file");
 	        e.printStackTrace();
 	        System.exit(1);
 	    }
 	}
 	else {
 	    System.err.println("Invalid output file: must be .java or .class file");
 	    System.exit(1);
 	}
    }
     
    private static int getShiftedLineNumber(int lineNum) {
        ShiftRule rule;
        for (ListIterator li = shiftRules.listIterator(shiftRules.size()); li.hasPrevious(); ) {
            rule = (ShiftRule) li.previous();
            if (rule.lineNum <= lineNum) {
                return lineNum + rule.shiftDistance;
            }
        }
        return lineNum;
    }
    
    /**
     * Writes a version of the input class file which has its line number entries
     * for each method equalized.
     *
     * <p>This method uses the same algorithm which is used to generate equalized
     * source files, it just attempts to apply the changes directly to the class
     * file instead. The source file should be processed with cpp and then compiled,
     * supplying the processed source and compiled file to this tool. Both the
     * baseline and seeded versions should be processed by this tool for correct
     * results.</p>
     *
     * <p><b>WARNING:</b> It is the declared behavior of cpp to discard long runs
     * of empty lines resulting from its own processing. If the number of lines
     * modified by the fault seed is sufficient to invoke this behavior, direct
     * patching of the class file with this method <b>will not</b> work at this
     * time. Instead, one should use this tool to generated equalized source files
     * and compile those instead.</p>
     *
     * @param classFile Name of the file to which the equalized class should be
     * written.
     */
    private static void writeEqualizedClassFile(String classFile) throws IOException {
        JavaClass clazz = null;
        ClassGen classGen;
        ConstantPoolGen cpg;
        Method[] methods;
        MethodGen mg;
        LineNumberGen[] lntable;
        try {
            clazz = new ClassParser(classFile).parse();
        }
        catch (ClassFormatError err) {
            err.printStackTrace();
            System.err.println("Invalid classfile");
            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        classGen = new ClassGen(clazz);
        cpg = classGen.getConstantPool();
        methods = classGen.getMethods();
        
        for (int i = 0; i < methods.length; i++) {
            mg = new MethodGen(methods[i], clazz.getClassName(), cpg);
            lntable = mg.getLineNumbers();
            mg.removeLineNumbers();
            for (int j = 0; j < lntable.length; j++) {
                mg.addLineNumber(lntable[j].getInstruction(),
                                 getShiftedLineNumber(lntable[j].getSourceLine()));
            }
            classGen.setMethodAt(mg.getMethod(), i);
        }
        
        try {
            classGen.getJavaClass().dump(classFile);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Writes a version of the input source file which has its line numbers equalized
     * for the specified active faults.
     *
     * <p>This is effectively a specialized preprocessor that works in a fashion
     * similar to cpp. Using the list of specified active faults, it scans the input
     * file, builds an abstract representation of the fault-seeded areas defined
     * by preprocessor directives, and analyzes them to determine how the lines
     * should be positioned. This process should be applied even to the baseline
     * version (one without any faults seeded) for correct results.</p>
     *
     * @param outputFile Name of the file to which the equalized source should be
     * written.
     */
    private static void writeEqualizedSource(String outputFile) throws IOException {
        LineNumberReader inFile = new LineNumberReader(new FileReader(inputFile));
        PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
        
        try {
            String curLine = null;
            ShiftRule rule = null;
            int targetLine = 0, discardCount = 0, padCount = 0;
            int lastShift = 0, shiftDifference = 0;
            for (ListIterator li = shiftRules.listIterator(); li.hasNext(); ) {
                rule = (ShiftRule) li.next();
                shiftDifference = rule.shiftDistance - lastShift;
                lastShift = rule.shiftDistance;
                if (shiftDifference < 0) {
                   targetLine = rule.lineNum + shiftDifference - 1;
                   discardCount = java.lang.Math.abs(shiftDifference);
                   padCount = 0;
                }
                else {
                   targetLine = rule.lineNum - 1;
                   discardCount = 0;
                   padCount = shiftDifference;
                }
                if (DEBUG) {
                    System.out.println("targetLine=" + targetLine + ", discardCount=" + discardCount + ", padCount=" + padCount);
                }
                while (inFile.getLineNumber() < targetLine) {
                    curLine = inFile.readLine();
 		    if (curLine.trim().startsWith("#") || rule.isEmptyBlock) {
 		        outFile.println();
 		    }
 		    else {
                         outFile.println(curLine);
 		    }
                }
                for (int i = 0; i < discardCount; i++) {
                    curLine = inFile.readLine();
                    if (DEBUG) System.out.println("Discarding: " + curLine);
                }
                for (int i = 0; i < padCount; i++) {
                    if (DEBUG) System.out.println("Padding at: " + inFile.getLineNumber());
                    outFile.println();
                }
            }
            while ((curLine = inFile.readLine()) != null) {
                outFile.println(curLine);
            }
        }
        finally {
            inFile.close();
            outFile.close();
        }
    }
         
    /**
     * Java analogue of a struct, to maintain information about an identified conditional
     * compilation (pre-processed) block. This is a convenience object for use in the
     * data structure. Since it is private to this application, we trust that the use
     * of public fields won't be abused and will save us a large number of trivial
     * method calls.
     */
    private static class CondBlock {
        /** The conditional type (#ifdef or #ifndef). The algorithm sets the lead block
            of identified faults appropriately, and then copies the value to succeeding
            blocks until an #endif is reached. */
        public int condType = 0;
        /** The type of this block (#ifdef, #ifndef, #else, #elif, or #endif). */
        public int type = 0;
        /** The line at which the block starts, which is taken to be the first line
            after the preprocessor directive. */
        public int startLine = 0;
        /** The length of the block. A value of -1 signifies we don't care about the
            actual length of the block (as in the case of an #endif, which represents
            the block following the conditional, and may be of indeterminate length
            based on the location of the next conditional). */
        public int length = -1;
        
        /**
         * Creates a new block data struct.
         * @param condType The type of the conditional with which the block is
         * associated.
         * @param type The type of the block.
         * @param startLine The line number of the start of the block.
         */
        public CondBlock(int condType, int type, int startLine) {
            this.condType = condType;
            this.type = type;
            this.startLine = startLine;
        }
    }
     
    /**
     * Java analogue of a struct, stores information about which line numbers need to
     * be shifted and by how much to equalize the line numbers. Negative values mean
     * all following lines should be shifted upward (delete some number of preceding
     * lines), positive values mean they should be shifted downward (requiring
     * insertion of blank lines).
     */
    private static class ShiftRule {
        /** The line number from which the shift rule is to apply. */
        public int lineNum = 0;
        /** The distance which lines should be shifted. */
        public int shiftDistance = 0;
        /** Records whether the block should be empty given the current fault
            activations. Used by the text processing algorithm. */
        public boolean isEmptyBlock = false;
        
        /**
         * Creates a new rule for shifting lines.
         * @param lineNum Line from which the rule applies.
         * @param shiftDistance Shift distance in lines.
         * @param isEmptyBlock Specifies whether block is considered to be
         * empty given current fault activations.
         */
        public ShiftRule(int lineNum, int shiftDistance, boolean isEmptyBlock) {
            this.lineNum = lineNum;
            this.shiftDistance = shiftDistance;
            this.isEmptyBlock = isEmptyBlock;
        }
    }
}
 
 /****************************************************************************************************
  * Original pseudocode algorithm, included for reference. It is known not to be entirely correct,
  * and certain aspects changed architecturally in the implementation. Still, may be useful for
  * gaining some understanding of the implementation.
  ****************************************************************************************************
    Scan fault seeded file
    ----------------------
    For each fault (preprocessor directive line):
      1. First element in list records type
          * -1 for #ifndef
          * 1 for #ifdef
      2. Second element records line number of opening directive
      3. Additional elements record line numbers of each #elif or the #else
      4. Last element records line number of closing directive (#endif)
 
    Scan processed file
    -------------------
    For each fault, jump to line and do following:
      1. Add shift -1 starting at line+1 to correct for #ifdef
      2. While elements remain in list
           Scan lines until next line number in list is reached.
             If block is empty then
               If type 1 (#ifdef)
                 If hasElse && (elseLength > ifLength) && isElseBlock
                   Add shift -n where n is number of lines in block, plus 1 to
                   correct for #else/#elif/#endif, minus difference in length
                 else
                   Add shift -n where n is number of lines in block, plus 1 to
                   correct for #else/#elif/#endif
                 end
               else
                 If hasElse then
                   if (elseLength > ifLength) && isElseBlock
                     Add shift -n where n is number of lines in block, plus 1 to
                     correct for #else/#elif/#endif, minus difference in length
                   else
                     Add shift -n where n is number of lines in block, plus 1 to
                     correct for #else/#elif/#endif
                   end
                 else
                   Add shift -1 starting at line+1 to correct for
                   #else/#elif/#endif
                 end
               end
             else
               If type 1
                 If hasElse then
                   If (ifLength > elseLength) && isElseBlock
                     Add shift -1 + difference in length starting at line+1 to correct for
                     #else/#elif/#endif
                   else
                     Add shift -1 starting at line+1 to correct for
                     #else/#elif/#endif
                   end
                 else
                   Add shift -n where n is number of lines in block, plus 1 to
                   correct for #else/#elif/#endif
                 endif
               else
                 If hasElse && (ifLength > elseLength) && isElseBlock
                   Add shift -1 + difference in length starting at line+1 to correct for
                   correct for #else/#elif/#endif, minus difference in length
                 else
                   Add shift -1 starting at line+1 to correct for
                   #else/#elif/#endif
                 end
               end
             end
         end
      
    Process class file
    ------------------
    For each line number entry
      1. Search for first shift rule for line number <= that line number
      2. Apply shift; if no rule applies, skip
      
  ****************************************************************************************************
  * The following is included for reference, since it more closely matches the pseudocode shown above.
  * It was converted into a switch statement for performance reasons, and is also known not to be
  * entirely correct.
  ****************************************************************************************************        
     if (blockIsEmpty) {
         if (curBlock.condType == IFDEF) {
             if (((curBlock.type == ELSE) || (curBlock.type == ELIF)) &&
                      (curBlock.length > lastBlock.length)) {
                 shiftDistance -= (curBlock.length + 1)
                                   - (curBlock.length - lastBlock.length);
                 System.out.println("Imbalance rule one: shift " + shiftDistance);
             }
             else {
                 shiftDistance -= curBlock.length + 1;
             }
             shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance));
         }
         else { // IFNDEF
             if ((curBlock.type == ELSE) || (curBlock.type == ELIF) ||
                     (((CondBlock) faultData.getFirst()).type != ENDIF)) {  // has an else block
                 if (((curBlock.type == ELSE) || (curBlock.type == ELIF)) &&
                         (curBlock.length > lastBlock.length)) {
                     shiftDistance -= (curBlock.length + 1)
                                      - (curBlock.length - lastBlock.length);
                     System.out.println("Imbalance rule two: shift " + shiftDistance);
                 }
                 else {
                     shiftDistance -= curBlock.length + 1;
                 }
                 shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance));
             }
             else {
                 shiftDistance -= 1;
                 shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance));
             }
         }
     }
     else {
         if (curBlock.condType == IFDEF) {
             if ((curBlock.type == ELSE) || (curBlock.type == ELIF) ||
                     (((CondBlock) faultData.getFirst()).type != ENDIF)) {  // has an else block
                 if (((curBlock.type == ELSE) || (curBlock.type == ELIF)) &&
                         (lastBlock.length > curBlock.length)) {
                     shiftDistance -= 1 - (lastBlock.length - curBlock.length);
                     System.out.println("Imbalance rule three: shift " + shiftDistance);
                 }
                 else {
                     shiftDistance -= 1;
                 }
                 shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance));
             }
             else {
                 shiftDistance -= curBlock.length + 1;
                 shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance));
             }
         }
         else { // IFNDEF
             if (((curBlock.type == ELSE) || (curBlock.type == ELIF)) &&
                     (lastBlock.length > curBlock.length)) {
                 shiftDistance -= 1 - (lastBlock.length - curBlock.length);
                 System.out.println("Imbalance rule four: shift " + shiftDistance);
             }
             else {
                 shiftDistance -= 1;
             }
             shiftRules.add(new ShiftRule(curBlock.startLine + curBlock.length + 1, shiftDistance));
         }
     }
 */
 
 