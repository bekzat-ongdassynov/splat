package splat.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
	private File programFile;
	private String input;
	private int pos; // curr position in input
	private int line; // curr line in src
	private int column; // curr col in src
	// map str to token type, so we can lookup if word is keyword or identifier
	private static final Map<String, Token.TokenType> KEYWORDS = new HashMap<>();
	// keyword map init
	static {
		KEYWORDS.put("program", Token.TokenType.PROGRAM);
		KEYWORDS.put("begin", Token.TokenType.BEGIN);
		KEYWORDS.put("end", Token.TokenType.END);
		KEYWORDS.put("is", Token.TokenType.IS);
		KEYWORDS.put("if", Token.TokenType.IF);
		KEYWORDS.put("then", Token.TokenType.THEN);
		KEYWORDS.put("else", Token.TokenType.ELSE);
		KEYWORDS.put("while", Token.TokenType.WHILE);
		KEYWORDS.put("do", Token.TokenType.DO);
		KEYWORDS.put("return", Token.TokenType.RETURN);
		KEYWORDS.put("print", Token.TokenType.PRINT);
		KEYWORDS.put("print_line", Token.TokenType.PRINT_LINE);
		KEYWORDS.put("true", Token.TokenType.TRUE);
		KEYWORDS.put("false", Token.TokenType.FALSE);
		KEYWORDS.put("Integer", Token.TokenType.INTEGER_TYPE);
		KEYWORDS.put("Boolean", Token.TokenType.BOOLEAN_TYPE);
		KEYWORDS.put("void", Token.TokenType.VOID_TYPE);
		KEYWORDS.put("and", Token.TokenType.AND);
		KEYWORDS.put("or", Token.TokenType.OR);
		KEYWORDS.put("not", Token.TokenType.NOT);
	}
	// constructor
	public Lexer(File programFile) {
		this.programFile = programFile;
		this.pos = 0;
		this.line = 1;
		this.column = 1;
	}
	// read file, scan char by char, build tokens
	public List<Token> tokenize() throws LexException {
		try {
			input = readFile(programFile);
		} catch (IOException e) {
			throw new LexException("Error reading file: " + e.getMessage(), 0, 0); // zeroes are line and col
		}
		
		List<Token> tokens = new ArrayList<>();
		// main tokenize loop
		while (pos < input.length()) {
			skipWhitespace();
			
			if (pos >= input.length()) {
				break;
			}
			
			Token token = nextToken();
			tokens.add(token);
		}

		tokens.add(new Token(Token.TokenType.EOF, "", line, column));
		return tokens;
	}
	
	private String readFile(File file) throws IOException {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append('\n');
			}
		}
		return content.toString();
	}
	// skip spaces, tabs, newlines
	private void skipWhitespace() {
		while (pos < input.length()) {
			char c = input.charAt(pos);
			if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
				if (c == '\n') {
					line++;
					column = 1;
				} else {
					column++;
				}
				pos++;
			} else {
				break;
			}
		}
	}
	// look at curr char, decide what to parse
	private Token nextToken() throws LexException {
		int tokenLine = line;
		int tokenColumn = column;
		char c = input.charAt(pos);
		// basing in first char, we decide what to parse
		if (c == '"') {
			return readStringLiteral();
		}
		// identifiers or keywords
		else if (Character.isLetter(c) || c == '_') {
			return readIdentifierOrKeyword();
		}
		// int literals
		else if (Character.isDigit(c)) {
			return readIntegerLiteral();
		}
		// multichar operators
		else if (c == ':' && peek() == '=') {
			pos += 2;
			column += 2;
			return new Token(Token.TokenType.ASSIGN, ":=", tokenLine, tokenColumn);
		}
		else if (c == '=' && peek() == '=') {
			pos += 2;
			column += 2;
			return new Token(Token.TokenType.EQ, "==", tokenLine, tokenColumn);
		}
		else if (c == '!' && peek() == '=') {
			pos += 2;
			column += 2;
			return new Token(Token.TokenType.NEQ, "!=", tokenLine, tokenColumn);
		}
		else if (c == '<' && peek() == '=') {
			pos += 2;
			column += 2;
			return new Token(Token.TokenType.LEQ, "<=", tokenLine, tokenColumn);
		}
		else if (c == '>' && peek() == '=') {
			pos += 2;
			column += 2;
			return new Token(Token.TokenType.GEQ, ">=", tokenLine, tokenColumn);
		}
		// singlechar if passed thus far
		pos++;
		column++;
		
		switch (c) {
			case '+': return new Token(Token.TokenType.PLUS, "+", tokenLine, tokenColumn);
			case '-': return new Token(Token.TokenType.MINUS, "-", tokenLine, tokenColumn);
			case '*': return new Token(Token.TokenType.MULT, "*", tokenLine, tokenColumn);
			case '/': return new Token(Token.TokenType.DIV, "/", tokenLine, tokenColumn);
			case '%': return new Token(Token.TokenType.MOD, "%", tokenLine, tokenColumn);
			case '<': return new Token(Token.TokenType.LT, "<", tokenLine, tokenColumn);
			case '>': return new Token(Token.TokenType.GT, ">", tokenLine, tokenColumn);
			case '(': return new Token(Token.TokenType.LPAREN, "(", tokenLine, tokenColumn);
			case ')': return new Token(Token.TokenType.RPAREN, ")", tokenLine, tokenColumn);
			case ':': return new Token(Token.TokenType.COLON, ":", tokenLine, tokenColumn);
			case ';': return new Token(Token.TokenType.SEMICOLON, ";", tokenLine, tokenColumn);
			case ',': return new Token(Token.TokenType.COMMA, ",", tokenLine, tokenColumn);
			default: // invalid char if passed thus far
				throw new LexException("Invalid character: '" + c + "'", tokenLine, tokenColumn);
		}
	}
	// read string between quotes
	private Token readStringLiteral() throws LexException {
		int tokenLine = line;
		int tokenColumn = column;
        // skip "
		pos++;
		column++;
		StringBuilder sb = new StringBuilder();
		
		while (pos < input.length()) {
			char c = input.charAt(pos);
			// line break not allowed
			if (c == '\n') {
				throw new LexException("String literal cannot span multiple lines", tokenLine, tokenColumn);
			}
            // string finished
			if (c == '"') {
				pos++;
				column++;
				return new Token(Token.TokenType.STRING_LITERAL, sb.toString(), tokenLine, tokenColumn);
			}
            // part of the string
			sb.append(c);
			pos++;
			column++;
		}
		// string never finished (no line break took place), expected "
		throw new LexException("Unterminated string literal", tokenLine, tokenColumn);
	}
	// read word, check if keyword
	private Token readIdentifierOrKeyword() {
		int tokenLine = line;
		int tokenColumn = column;
		StringBuilder sb = new StringBuilder();
		
		while (pos < input.length()) {
			char c = input.charAt(pos);
			if (Character.isLetterOrDigit(c) || c == '_') {
				sb.append(c);
				pos++;
				column++;
			} else {
				break;
			}
		}
		
		String text = sb.toString();
		// lookup in map, if found its keyword, else identifier
		Token.TokenType type = KEYWORDS.get(text);
		if (type != null) {
			return new Token(type, text, tokenLine, tokenColumn);
		}
		return new Token(Token.TokenType.IDENTIFIER, text, tokenLine, tokenColumn);
	}
	// read digits
	private Token readIntegerLiteral() {
		int tokenLine = line;
		int tokenColumn = column;
		StringBuilder sb = new StringBuilder();
		
		while (pos < input.length()) {
			char c = input.charAt(pos);
			if (Character.isDigit(c)) {
				sb.append(c);
				pos++;
				column++;
			} else {
				break;
			}
		}
		
		return new Token(Token.TokenType.INTEGER_LITERAL, sb.toString(), tokenLine, tokenColumn);
	}
	// look at next char
	private char peek() {
		if (pos + 1 < input.length()) {
			return input.charAt(pos + 1);
		}
		return '\0';
	}
}