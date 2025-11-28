package splat.lexer;

public class Token {
	// token types for convenience
	public enum TokenType {
		PROGRAM, BEGIN, END, IS, IF, THEN, ELSE, WHILE, DO, RETURN, PRINT, PRINT_LINE, TRUE, FALSE, INTEGER_TYPE, BOOLEAN_TYPE, VOID_TYPE,
		ASSIGN, // :=
		PLUS, MINUS, MULT, DIV, MOD, // + - * / %
		EQ, NEQ, LT, GT, LEQ, GEQ, // == != < > <= >=
		AND, OR, NOT, // and or not
		LPAREN, RPAREN, // ( )
		COLON, SEMICOLON, COMMA, // : ; ,
		INTEGER_LITERAL, STRING_LITERAL, IDENTIFIER,
		EOF
	}
	
	private TokenType type;
	private String value;
	private int line; // line number in src
	private int column; // column number in src
	// constructor
	public Token(TokenType type, String value, int line, int column) {
		this.type = type;
		this.value = value;
		this.line = line;
		this.column = column;
	}
	// getters
	public TokenType getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
	// overriding default toString method
	@Override
	public String toString() {
		if (value != null && !value.isEmpty()) {
			return type + "(" + value + ") at line " + line + ", column " + column;
		}
		return type + " at line " + line + ", column " + column;
	}
}