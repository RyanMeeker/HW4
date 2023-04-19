///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Main Class File:  ast.java
// File:             P4.java
// Semester:         CS536 Spring 2023
//
// Author:           Arun Balaji - abalaji7@wisc.edu
// CS Login:         abalaji
// Lecturer's Name:  Beck Hasti
// Lab Section:      002
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ////////////////////
//
// Pair Partner:     Ryan Meeker
// Email:            rtmeeker@wisc.edu
// CS Login:         meeker
// Lecturer's Name:  Beck Hasti
// Lab Section:      002
//
//////////////////// STUDENTS WHO GET HELP FROM OTHER THAN THEIR PARTNER //////
//                   fully acknowledge and credit all sources of help,
//                   other than Instructors and TAs.
//
// Persons:          Identify persons by name, relationship to you, and email.
//                   Describe in detail the the ideas and help they provided.
//
// Online sources:   avoid web searches to solve your problems, but if you do
//                   search, be sure to include Web URLs and description of 
//                   of any information you find.
//////////////////////////// 80 columns wide //////////////////////////////////

import java.io.*;
import java_cup.runtime.*;

/****
 * Main program to test the brevis parser.
 *
 * There should be 2 command-line arguments:
 * 1. the file to be parsed
 * 2. the output file into which the AST built by the parser should be unparsed
 ****/

public class P4 {
    public static void main(String[] args)
        throws IOException // may be thrown by the scanner
    {
        // check for command-line args
        if (args.length != 2) {
            System.err.println("please supply name of file to be parsed " +
			                   "and name of file for unparsed version");
            System.exit(-1);
        }

        // open input file
        FileReader inFile = null;
        try {
            inFile = new FileReader(args[0]);
        } catch (FileNotFoundException ex) {
            System.err.println("file " + args[0] + " not found");
            System.exit(-1);
        }

        // open output file
        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(args[1]);
        } catch (FileNotFoundException ex) {
            System.err.println("file " + args[1] +
                               " could not be opened for writing");
            System.exit(-1);
        }

        parser P = new parser(new Yylex(inFile));

        Symbol root = null; // the parser will return a Symbol whose value
                            // field is the translation of the root nonterminal
                            // (i.e., of the nonterminal "program")

        try {
            root = P.parse(); // do the parse
            System.out.println ("program parsed correctly");
        } catch (Exception ex){
            System.err.println("exception occured during parse: " + ex);
            System.exit(-1);
        }
		
		// ****** Add name analysis part here ******
        ((ProgramNode)root.value).nameAnalysis();

	if (!(ErrMsg.isFatal)){	
	        ((ASTnode)root.value).unparse(outFile, 0);
	}
        outFile.close();


        return;
    }
}
