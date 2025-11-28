package splat.parser.elements;

import splat.lexer.Token;
// wrapper for type info during semantic analysis
public class Type extends ASTElement {

	private final String typeName;

	public Type(String typeName, int line, int column) {
		super(new Token(Token.TokenType.IDENTIFIER, typeName, line, column));
		this.typeName = typeName;
	}
	// returns "Integer", "Boolean", "String", or "void"
	public String getTypeName() {
		return typeName;
	}
	
	public String toString() {
		return typeName;
	}
}