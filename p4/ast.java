import java.io.*;
import java.util.*;
//added code:
// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a brevis program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and identifiers contain line and character 
// number information; for string literals and identifiers, they also 
// contain a string; for integer literals, they also contain an integer 
// value.
//
// Here are all the different kinds of AST nodes and what kinds of 
// children they have.  All of these kinds of AST nodes are subclasses
// of "ASTnode".  Indentation indicates further subclassing:
//
//     Subclass              Children
//     --------              --------
//     ProgramNode           DeclListNode
//     DeclListNode          linked list of DeclNode
//     DeclNode:
//       VarDeclNode         TypeNode, IdNode, int
//       FnDeclNode          TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode      TypeNode, IdNode
//       RecordDeclNode      IdNode, DeclListNode
//
//     StmtListNode          linked list of StmtNode
//     ExpListNode           linked list of ExpNode
//     FormalsListNode       linked list of FormalDeclNode
//     FnBodyNode            DeclListNode, StmtListNode
//
//     TypeNode:
//       BoolNode            --- none ---
//       IntNode             --- none ---
//       VoidNode            --- none ---
//       RecordNode          IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignExpNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       TrueNode            --- none ---
//       FalseNode           --- none ---
//       IdNode              --- none ---
//       IntLitNode          --- none ---
//       StrLitNode          --- none ---
//       DotAccessNode       ExpNode, IdNode
//       AssignExpNode       ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         LessEqNode
//         GreaterNode
//         GreaterEqNode
//         AndNode
//         OrNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of children, 
// or internal nodes with a fixed number of children:
//
// (1) Leaf nodes:
//        BoolNode,  IntNode,     VoidNode,   TrueNode,  FalseNode,
//        IdNode,    IntLitNode,  StrLitNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, StmtListNode, ExpListNode, FormalsListNode
//
// (3) Internal nodes with fixed numbers of children:
//        ProgramNode,     VarDeclNode,     FnDeclNode,    FormalDeclNode,
//        RecordDeclNode,  FnBodyNode,      RecordNode,    AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, IfStmtNode,    IfElseStmtNode,
//        WhileStmtNode,   ReadStmtNode,    WriteStmtNode, CallStmtNode,
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode, CallExpNode,
//        UnaryExpNode,    UnaryMinusNode,  NotNode,       BinaryExpNode,   
//        PlusNode,        MinusNode,       TimesNode,     DivideNode,
//        EqualsNode,      NotEqualsNode,   LessNode,      LessEqNode,
//        GreaterNode,     GreaterEqNode,   AndNode,       OrNode
//
// **********************************************************************

// **********************************************************************
//   ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode { 
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void doIndent(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
}

// **********************************************************************
//   ProgramNode,  DeclListNode, StmtListNode, ExpListNode, 
//   FormalsListNode, FnBodyNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // one child
    private DeclListNode myDeclList;

    public void nameAnalysis(){
        SymTab symtab = new SymTab();
        myDeclList.nameAnalysis(symtab);
	System.out.println("entering programNode nameanalysis");
    }
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of children (DeclNodes)
    private List<DeclNode> myDecls;

    public void nameAnalysis(SymTab symtab){     
        System.out.println("Entering declList nameAnalysis");    
        for(DeclNode decl : myDecls){
            if(decl instanceof VarDeclNode) {
                ((VarDeclNode)decl).nameAnalysis(symtab);
            } 
	    else if((decl instanceof FnDeclNode)){
                ((FnDeclNode)decl).nameAnalysis(symtab);
            } /*
	    else if((decl instanceof FormalDeclNode)){
                ((FormalDeclNode)decl).nameAnalysis(symtab);
            }/* 
	    else if((decl instanceof RecordDeclNode)){
                ((RecordDeclNode)decl).nameAnalysis(symtab);
            }
	    else {
		System.out.println("Not any child of delnode");
                System.exit(-1);
            }*/
        }
    }
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }      
    }

    public void nameAnalysis(SymTab symtab){
	System.out.println("entering stmtList analysis");
        for(StmtNode stmt : myStmts){
            stmt.nameAnalysis(symtab);
        }
    }

    // list of children (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void nameAnalysis(SymTab symtab){
	System.out.println("Entering expListNode nameAnalysis");
        for(ExpNode exp : myExps){
            exp.nameAnalysis(symtab);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of children (ExpNodes)
    private List<ExpNode> myExps;
}
class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }



	public void nameAnalysis(SymTab symtab, FnSym sym) {
		System.out.println("entering formalsListNode nameAnalysis");
		for (FormalDeclNode fdn : myFormals) {
			fdn.nameAnalysis(symtab, sym);
		}
	}



    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of children (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }
    public void nameAnalysis(SymTab symtab){
	System.out.println("entering fnBody nameAnalysis");
        myDeclList.nameAnalysis(symtab);
	myStmtList.nameAnalysis(symtab);
    }


    // two children
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}


// **********************************************************************
// ****  DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    //include abstract method to avoid casting 
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    // three children
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NON_RECORD if this is not a record type

    public static int NON_RECORD = -1;

    public void nameAnalysis(SymTab symtab){
	Sym sym = null;
	IdNode recordId = null;
	boolean multDecl = false;
	
	//check for multiple declarations 
	try{
	//System.out.println("myId.getStrVal(): " + myId.getStrVal());
	sym = symtab.lookupLocal(myId.getStrVal());
//	System.out.println("###############################ENTERING VARDECL###################################");
	if(sym != null &&  myType instanceof VoidNode){
		ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
		ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Identifier multiply declared");
		multDecl = true;
		return;
	}
	if(sym != null){
		ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Identifier multiply declared");
		multDecl = true;
 		return;  
	}  
} catch(SymTabEmptyException e){
	ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Unexpected SymTabEmptyException thrown");
}       
	
	//check for void
	if(myType instanceof VoidNode){
		ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
		return;
	} else if(myType instanceof RecordNode){
		recordId = ((RecordNode)myType).getIdNode();
		//global lookup to make sure that record type has alread been declared
		try{
		sym = symtab.lookupGlobal(recordId.getStrVal());
		//if the record type has not already been declared then error out
		if(sym == null || !(sym instanceof RecordDefSym)){
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Invalid name of struct type");
			return;
		}
	
		} catch(SymTabEmptyException e){
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Unexpected SymTabEmptyException thrown");
		} 
	}

	if(multDecl == false){ //good decl
		try {
			if(myType instanceof RecordNode){
				sym = new RecordDeclSym((RecordDefSym)(symtab.lookupGlobal(recordId.toString())), recordId.toString());
				sym.setName(myId.getStrVal());
			} else {
				sym = new Sym(myType.toString());
	//			System.out.println("myType.toString(): " +  myType.toString());
				sym.setName(myId.getStrVal());
			}
			//add type current symbolTable
			symtab.addDecl(myId.getStrVal(), sym);
	//		System.out.println("myId.toString(): " + myId.toString());
	//		System.out.println("Sym: " + sym.toString());
	//		System.out.print("Symtab: "); 
	//		symtab.print();
	//		System.out.println("LookupLocal After AddDecl: " + symtab.lookupLocal(myId.getStrVal()));

		} catch (SymDuplicationException e){
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Unexpected SymDuplicationException thrown in VDN");
		} catch (SymTabEmptyException e){
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Unexpected SymTabEmptyException thrown in VDN");
		}
	}

//	System.out.println("#####################################EXCITING VARDECL AFTER ADDDEC#########################");

		
}
}
class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }
    public void nameAnalysis(SymTab symtab) {
	
	System.out.println("entering fnDeclNode nameAnalysis");
	Sym sym = new FnSym(myType.toString());

	try { 
	    symtab.addDecl(myId.getStrVal(), sym);
	} catch (SymDuplicationException e) {
	    ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Identifier multiply-declared");
	} catch (SymTabEmptyException e){
	     ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "EmptyException in FnNode");
	}
	
	symtab.addScope();

	myFormalsList.nameAnalysis(symtab, (FnSym)sym);

	myBody.nameAnalysis(symtab);

	try {
	    symtab.removeScope();
	} catch (SymTabEmptyException e) { 
	    ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "EmptyException in FnNode when removing scope");
	}
    }
    // 4 children
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }
    public void nameAnalysis(SymTab symtab, FnSym fnsym){
	    try{
		Sym sym = symtab.lookupLocal(myId.getStrVal()); // Check for duplicates

		if(myType instanceof VoidNode){
			ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declare void");
		}
		
		sym = new Sym(myType.toString()); // create new sym and add to Decl

		symtab.addDecl(myId.getStrVal(), sym);
		
		fnsym.addFormals(myType.toString());

	    } catch (SymDuplicationException e){
		ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Identifier multiply-declared");
	    } catch (SymTabEmptyException e) {
		ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "FormalDeclNode Exception");
	    } 

}		
    // two children
    private TypeNode myType;
    private IdNode myId;
}

class RecordDeclNode extends DeclNode {
    public RecordDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("record ");
        myId.unparse(p, 0);
        p.println("(");
        myDeclList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println(");\n");
    }
   // public void nameAnalysis(SymTab symtab, 
    // two children
    private IdNode myId;
    private DeclListNode myDeclList;
}

// **********************************************************************
// ****  TypeNode and its subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    abstract public String toString();
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("boolean");
    }

    public String toString() {
        return "boolean";
    }
}

class IntNode extends TypeNode {

    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("integer");
    }
    public String toString() {
        return "integer";
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }

    public String toString(){
	return "void";
    }
}

class RecordNode extends TypeNode {
    public RecordNode(IdNode id) {
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("record ");
        myId.unparse(p, 0);
    }

    public String toString(){
	return "record";
    }

    public IdNode getIdNode(){
        return myId;
    }
    
    // one child
    private IdNode myId;
}

// **********************************************************************
// ****  StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
	abstract public void nameAnalysis(SymTab symtab);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignExpNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }
    
    public void nameAnalysis(SymTab symtab){
	System.out.println("entering assignStmt name analysis");
	myAssign.nameAnalysis(symtab);
    }
    // one child
    private AssignExpNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    public void nameAnalysis(SymTab symtab){
    	myExp.nameAnalysis(symtab);
    }
    // one child
    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTab symtab){
	    myExp.nameAnalysis(symtab);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    // one child
    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void nameAnalysis(SymTab symtab){
    	//analyze the first expression
	myExp.nameAnalysis(symtab);

	//add new scope from curly braces
	symtab.addScope();

	//perform name analysis for decls then statements
	myDeclList.nameAnalysis(symtab);
	myStmtList.nameAnalysis(symtab);

	//remove new scope
	try{
		symtab.removeScope();
	} catch(SymTabEmptyException e){
		System.out.println("Unexpected SymTabEmptyException thrown in IfStmtNode nameanalysis");
		System.exit(-1);
	}
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");        
    }

    // three children
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void nameAnalysis(SymTab symtab){
    	//like ifStmtNode analyze the first expression
	myExp.nameAnalysis(symtab);

	//add new scope
	symtab.addScope();

	//perform name anlysis on the then declList and the then stmtList
	myThenDeclList.nameAnalysis(symtab);
	myThenStmtList.nameAnalysis(symtab);

	//close the then scope
	try{
		symtab.removeScope();
	} catch(SymTabEmptyException e){
		System.out.println("Unexpected SymTabEmptyException thrown in ifElseStmtNode analysis (then)");
		System.exit(-1);
	}

	//add new scope for else
	symtab.addScope();

	//perform name analysis for else declList and else stmtList
	myElseDeclList.nameAnalysis(symtab);
	myElseStmtList.nameAnalysis(symtab);

	//close the else scope
	try{
		symtab.removeScope();
	} catch(SymTabEmptyException e){
		System.out.println("Unexpected SymTabEmptyException thrown in ifElseStmtNode analysis (else)");
		System.exit(-1);
	}
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
        doIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}"); 
    }

    // five children
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
 
    public void nameAnalysis(SymTab symtab){
    	//analyze expression
	myExp.nameAnalysis(symtab);

	//add new scope
	symtab.addScope();

	//perform name analysis on declList then StmtList
	myDeclList.nameAnalysis(symtab);
	myStmtList.nameAnalysis(symtab);

	//close scope
	try{
		symtab.removeScope();
	} catch(SymTabEmptyException e){
		System.out.println("Unexpected symtabemptyexception thrown in while nameAnalysis");
		System.exit(-1);
	}
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }

    // three children
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

//TODO: do this
class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void nameAnalysis(SymTab symtab){}
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("scan -> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // one child (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

//TODO:do this
class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTab symtab){}
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("print <- ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // one child
    private ExpNode myExp;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void nameAnalysis(SymTab symtab){
    	//perform nameAnalysis for myCall
	myCall.nameAnalysis(symtab);
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    // one child
    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void nameAnalysis(SymTab symtab){
    	//perform null check - void typed function
	if(myExp != null){
		myExp.nameAnalysis(symtab);
	}
	
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    // one child
    private ExpNode myExp; // possibly null
}

// **********************************************************************
// ****  ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
   abstract public void nameAnalysis(SymTab symtab);

}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    public void nameAnalysis(SymTab symtab){}

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    public void nameAnalysis(SymTab symtab){}
    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    public int getLineNum(){
        return myLineNum;
    }

    public int getCharNum(){
        return myCharNum;
    }

    public String getStrVal(){
        return myStrVal;
    }

    public void setSymLink(Sym sym){
        this.symLink = sym;
    }

    public void nameAnalysis(SymTab symtab) {

	try{
		symLink = symtab.lookupGlobal(myStrVal);
	} catch (SymTabEmptyException e) {
		ErrMsg.fatal(this.myLineNum, this.myCharNum, "Unexpected SymTabEmptyException");
		System.exit(-1); 
	}
 
	if(symLink == null){
	    ErrMsg.fatal(this.myLineNum, this.myCharNum, "Identifier undeclared");
	}	
	else {
	    setSymLink(symLink);
	}

}

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private Sym symLink;
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    public void nameAnalysis(SymTab symtab){
	}
    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    public void nameAnalysis(SymTab symtab){}

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;    
        myId = id;
    }

    // **** unparse ****
    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print(").");
        myId.unparse(p, 0);
    }

    public void nameAnalysis(SymTab symtab){}
    // two children
    private ExpNode myLoc;    
    private IdNode myId;
}

class AssignExpNode extends ExpNode {
    public AssignExpNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");       
    }
    public void nameAnalysis(SymTab symtab){
	myLhs.nameAnalysis(symtab);
	myExp.nameAnalysis(symtab);
}
    // two children
    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");       
    }

    public void nameAnalysis(SymTab symtab){
    	//analyze idNode - could be null
	myId.nameAnalysis(symtab);

	if(myExpList != null){
		myExpList.nameAnalysis(symtab);
	}

    }

    // two children
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }
    public void nameAnalysis(SymTab symtab){
    	myExp.nameAnalysis(symtab);
    }
    // one child
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
	}

    public void nameAnalysis(SymTab symtab){
    	//analyze both exprs
	myExp1.nameAnalysis(symtab);
	myExp2.nameAnalysis(symtab);
    }

    // two children
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// *****  Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(\\");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

// **********************************************************************
// ****  Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void unparse(PrintWriter p, int indent){
        myExp1.unparse(p, 0);
        p.print(" \\= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
 }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
 }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

