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
}

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








