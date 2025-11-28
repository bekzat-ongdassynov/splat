package splat.parser;

import java.util.ArrayList;
import java.util.List;

import splat.lexer.Token;
import splat.parser.elements.*;

public class Parser {

	private List<Token> tokens;
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}
	// main method, builds the entire program AST
	public ProgramAST parse() throws ParseException {
		try {
			Token tok = tokens.get(0);
			checkNext("program");
			
			List<Declaration> decls = parseDecls();
			checkNext("begin");
			List<Statement> stmts = parseStmts();
			checkNext("end");
			checkNext(";");

			return new ProgramAST(decls, stmts, tok);
		} catch (IndexOutOfBoundsException ex) {
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	// parse all declarations before begin
	private List<Declaration> parseDecls() throws ParseException {
		List<Declaration> decls = new ArrayList<>();
		while (!peekNext("begin")) {
			decls.add(parseDecl());
		}
		return decls;
	}
	// parse single declaration (var or func)
	private Declaration parseDecl() throws ParseException {
		if (peekTwoAhead(":")) {
			return parseVarDecl();
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			throw new ParseException("Declaration expected", tokens.get(0));
		}
	}
	// parse variable declaration: label : type ;
	private VariableDecl parseVarDecl() throws ParseException {
		Token tok = tokens.get(0);
		String label = tokens.remove(0).getValue();
		checkNext(":");
		String type = parseType();
		checkNext(";");
		return new VariableDecl(label, type, tok);
	}
	// parse function declaration with params, local vars, and body
	private FunctionDecl parseFuncDecl() throws ParseException {
		Token tok = tokens.get(0);
		String label = tokens.remove(0).getValue();
		checkNext("(");
		List<VariableDecl> params = parseParams();
		checkNext(")");
		checkNext(":");
		String retType = parseType();
		checkNext("is");
		List<VariableDecl> localVars = parseLocVarDecls();
		checkNext("begin");
		List<Statement> stmts = parseStmts();
		checkNext("end");
		checkNext(";");
		return new FunctionDecl(label, params, retType, localVars, stmts, tok);
	}
	// parse function params
	private List<VariableDecl> parseParams() throws ParseException {
		List<VariableDecl> params = new ArrayList<>();
		if (!peekNext(")")) {
			params.add(parseParam());
			while (peekNext(",")) {
				checkNext(",");
				params.add(parseParam());
			}
		}
		return params;
	}
	// parse single parameter: label : type
	private VariableDecl parseParam() throws ParseException {
		Token tok = tokens.get(0);
		String label = tokens.remove(0).getValue();
		checkNext(":");
		String type = parseType();
		return new VariableDecl(label, type, tok);
	}
	// parse local var declarations inside func
	private List<VariableDecl> parseLocVarDecls() throws ParseException {
		List<VariableDecl> localVars = new ArrayList<>();
		while (!peekNext("begin")) {
			localVars.add(parseVarDecl());
		}
		return localVars;
	}
	// parse type
	private String parseType() throws ParseException {
		Token tok = tokens.remove(0);
		String type = tok.getValue();
		if (!type.equals("Integer") && !type.equals("Boolean") && 
		    !type.equals("String") && !type.equals("void")) {
			throw new ParseException("Expected type, got '" + type + "'.", tok);
		}
		return type;
	}
	// parse list of statements
	private List<Statement> parseStmts() throws ParseException {
		List<Statement> stmts = new ArrayList<>();
		while (!peekNext("end")) {
			stmts.add(parseStmt());
		}
		return stmts;
	}
	// parse single statement, dispatch based on first token
	private Statement parseStmt() throws ParseException {
		if (peekNext("if")) {
			return parseIfStmt();
		} else if (peekNext("while")) {
			return parseWhileLoop();
		} else if (peekNext("return")) {
			return parseReturnStmt();
		} else if (peekNext("print_line")) {
			return parsePrintLineStmt();
		} else if (peekNext("print")) {
			return parsePrintStmt();
		} else if (peekTwoAhead(":=")) {
			return parseAssignment();
		} else if (peekTwoAhead("(")) {
			// func call stmt
			Token tok = tokens.get(0);
			String label = tokens.remove(0).getValue();
			checkNext("(");
			List<Expression> args = parseArgs();
			checkNext(")");
			checkNext(";");
			FunctionCall funcCall = new FunctionCall(label, args, tok);
			return new FunctionCallStatement(funcCall, tok);
		} else {
			throw new ParseException("Statement expected", tokens.get(0));
		}
	}
	// parse assignment: label := expr ;
	private Assignment parseAssignment() throws ParseException {
		Token tok = tokens.get(0);
		String label = tokens.remove(0).getValue();
		checkNext(":=");
		Expression expr = parseExpr();
		checkNext(";");
		return new Assignment(label, expr, tok);
	}
	// parse if stmt with optional else
	private IfStatement parseIfStmt() throws ParseException {
		Token tok = tokens.get(0);
		checkNext("if");
		Expression cond = parseExpr();
		checkNext("then");
		List<Statement> thenStmts = new ArrayList<>();
		while (!peekNext("else") && !peekNext("end")) {
			thenStmts.add(parseStmt());
		}
		List<Statement> elseStmts = new ArrayList<>();
		if (peekNext("else")) {
			checkNext("else");
			while (!peekNext("end")) {
				elseStmts.add(parseStmt());
			}
		}
		checkNext("end");
		checkNext("if");
		checkNext(";");
		return new IfStatement(cond, thenStmts, elseStmts, tok);
	}
	// parse while loop
	private WhileLoop parseWhileLoop() throws ParseException {
		Token tok = tokens.get(0);
		checkNext("while");
		Expression cond = parseExpr();
		checkNext("do");
		List<Statement> stmts = new ArrayList<>();
		while (!peekNext("end")) {
			stmts.add(parseStmt());
		}
		checkNext("end");
		checkNext("while");
		checkNext(";");
		return new WhileLoop(cond, stmts, tok);
	}
	// parse return stmt with optional expr
	private ReturnStatement parseReturnStmt() throws ParseException {
		Token tok = tokens.get(0);
		checkNext("return");
		Expression expr = null;
		if (!peekNext(";")) {
			expr = parseExpr();
		}
		checkNext(";");
		return new ReturnStatement(expr, tok);
	}
	// parse print stmt
	private PrintStatement parsePrintStmt() throws ParseException {
		Token tok = tokens.get(0);
		checkNext("print");
		Expression expr = parseExpr();
		checkNext(";");
		return new PrintStatement(expr, tok);
	}
	// parse print_line stmt
	private PrintLineStatement parsePrintLineStmt() throws ParseException {
		Token tok = tokens.get(0);
		checkNext("print_line");
		checkNext(";");
		return new PrintLineStatement(tok);
	}
	// parse expression, full parenthesization ( expr op expr ) | ( unary-op expr ) | label ( args ) | label | literal
	private Expression parseExpr() throws ParseException {
		// fully parenthesized expressions
		if (peekNext("(")) {
			Token tok = tokens.get(0);
			checkNext("(");
			// check for unary op
			if (peekNextType(Token.TokenType.NOT) || peekNext("-")) {
				Token op = tokens.remove(0);
				Expression expr = parseExpr();
				checkNext(")");
				if (op.getValue().equals("not")) {
					return new BinaryExpression(new BooleanLiteral(true, op), "not", expr, op);
				} else {
					return new BinaryExpression(new IntegerLiteral(0, op), "-", expr, op);
				}
			}
			// binary op
			Expression left = parseExpr();
			Token op = tokens.get(0);
			if (!isBinaryOp()) {
				throw new ParseException("Expected binary operator", op);
			}
			tokens.remove(0);
			Expression right = parseExpr();
			checkNext(")");
			return new BinaryExpression(left, op.getValue(), right, op);
		}
		Token tok = tokens.get(0);
		// func call or var
		if (tok.getType() == Token.TokenType.IDENTIFIER) {
			String label = tokens.remove(0).getValue();
			if (peekNext("(")) {
				checkNext("(");
				List<Expression> args = parseArgs();
				checkNext(")");
				return new FunctionCall(label, args, tok);
			} else {
				return new Variable(label, tok);
			}
		}
		// int literal
		if (tok.getType() == Token.TokenType.INTEGER_LITERAL) {
			tokens.remove(0);
			int val = Integer.parseInt(tok.getValue());
			return new IntegerLiteral(val, tok);
		}
		// string literal
		if (tok.getType() == Token.TokenType.STRING_LITERAL) {
			tokens.remove(0);
			return new StringLiteral(tok.getValue(), tok);
		}
		// bool literals
		if (peekNextType(Token.TokenType.TRUE)) {
			tokens.remove(0);
			return new BooleanLiteral(true, tok);
		}
		if (peekNextType(Token.TokenType.FALSE)) {
			tokens.remove(0);
			return new BooleanLiteral(false, tok);
		}
		throw new ParseException("Expression expected", tok);
	}
	// check if next token is binary op
	private boolean isBinaryOp() {
		return peekNext("and") || peekNext("or") || peekNext(">") || peekNext("<") || 
			peekNext("==") || peekNext(">=") || peekNext("<=") || peekNext("!=") ||
		    peekNext("+") || peekNext("-") || peekNext("*") || peekNext("/") || peekNext("%");
	}
	// parse function call args
	private List<Expression> parseArgs() throws ParseException {
		List<Expression> args = new ArrayList<>();
		if (!peekNext(")")) {
			args.add(parseExpr());
			while (peekNext(",")) {
				checkNext(",");
				args.add(parseExpr());
			}
		}
		return args;
	}
	// check next token matches expected, consume it
	private void checkNext(String expected) throws ParseException {
		Token tok = tokens.remove(0);
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" + tok.getValue()+ "'.", tok);
		}
	}
	// look at next token value without consuming
	private boolean peekNext(String expected) {
		return tokens.get(0).getValue().equals(expected);
	}
	// look at next token type without consuming
	private boolean peekNextType(Token.TokenType expected) {
		return tokens.get(0).getType() == expected;
	}
	// look at token after next without consuming
	private boolean peekTwoAhead(String expected) {
		return tokens.get(1).getValue().equals(expected);
	}
}