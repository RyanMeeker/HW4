///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Main Class File:  ast.java
// File:             Sym.java
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

import javax.naming.Name;
import java.util.*;

public class Sym {
	private String type;
	private String name;
	private String kind;
	
	public Sym(String type) {
		this.type = type;	
	}

	public String getType() {
		return type;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public void setKind(String kind){
		this.kind = kind;
	}

	public String getKind(){
		return kind;
	} 
	
	public String toString() {
		return "type:" + type + " name:" + name;
	}
}

/**
 * (Class representing a Function Sym which consists of a list of formals and a return type. You should avoid
 * wordiness and redundancy. If necessary, additional paragraphs should
 * be preceded by <p>, the html tag for a new paragraph.)
 *
 * <p>Bugs: (a list of bugs and other problems)
 *
 * @author Arun Balaji + Ryan Meeker
 */
class FnSym extends Sym {
	private List<String> formals;
	private String returnType;

	public FnSym(String returnType){
		super(returnType);
		this.returnType = returnType;
		this.formals = new ArrayList<String>();
	}

	public int getFormalsNum(){
		return formals.size();
	}

	public void addFormals(String type){
		this.formals.add(type);
	}

	public String getRetType(){
		return returnType;
	}

	public List<String> getFormals(){
		return formals;
	}

	public String getFormalsString(){
		String formalsList = "";
		for(int i = 0; i < formals.size(); i++){
			if(i == 0){
				formalsList = formals.get(i);
			} else {
				formalsList = formalsList + "," + formals.get(i);
			}
		}
		return formalsList;
	}		
	
}

/**
 * (Class representing a RecordDecl Sym which consists of a record body and a String type. You should avoid
 * wordiness and redundancy. If necessary, additional paragraphs should
 * be preceded by <p>, the html tag for a new paragraph.)
 *
 * <p>Bugs: (a list of bugs and other problems)
 *
 * @author Arun Balaji + Ryan Meeker
 */
class RecordDeclSym extends Sym {
	private RecordDefSym recBody;

	public RecordDeclSym(RecordDefSym recBody, String type){
		super(type);
		this.recBody = recBody;
	}
	
	public RecordDefSym getBody(){
		return this.recBody;
	}
}

/**
 * (Class representing a RecordDef Sym which consists of a symtab. You should avoid
 * wordiness and redundancy. If necessary, additional paragraphs should
 * be preceded by <p>, the html tag for a new paragraph.)
 *
 * <p>Bugs: (a list of bugs and other problems)
 *
 * @author Arun Balaji + Ryan Meeker
 */

class RecordDefSym extends Sym {
	private SymTab symtab;

	public RecordDefSym(SymTab symtab, String type){
		super(type);
		this.symtab = symtab;
	}
	
	public SymTab getSymTab(){
		return this.symtab;
	}
}








