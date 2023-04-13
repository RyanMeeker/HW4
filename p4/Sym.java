import javax.naming.Name;

public class Sym {
	private String type;
	private String name;
	private String kind;
	
	public Sym(String type, String kind) {
		this.type = type;
		this.kind = kind;	
	}

	public String getType() {
		return type;
	}

	public String getName(){
		return name;
	}

	public String getKind(){
		return kind;
	}
	
	public String toString() {
		return "type:" + type + " kind:" + kind + "name:" + name;
	}
}

