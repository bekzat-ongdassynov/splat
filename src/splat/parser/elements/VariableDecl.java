package splat.parser.elements;

import splat.lexer.Token;
// var or param decl
public class VariableDecl extends Declaration {

	private String type;
	
	public VariableDecl(String label, String type, Token tok) {
		super(label, tok);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return getLabel() + " : " + type + ";";
	}
}