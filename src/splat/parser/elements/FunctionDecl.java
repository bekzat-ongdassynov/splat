package splat.parser.elements;

import java.util.List;
import splat.lexer.Token;
// function decl with params, return type, local vars, body
public class FunctionDecl extends Declaration {

	private List<VariableDecl> params;
	private String retType;
	private List<VariableDecl> localVars;
	private List<Statement> stmts;
	
	public FunctionDecl(String label, List<VariableDecl> params, String retType, List<VariableDecl> localVars, List<Statement> stmts, Token tok) {
		super(label, tok);
		this.params = params;
		this.retType = retType;
		this.localVars = localVars;
		this.stmts = stmts;
	}
	
	public List<VariableDecl> getParams() {
		return params;
	}
	
	public String getRetType() {
		return retType;
	}
	
	public List<VariableDecl> getLocalVars() {
		return localVars;
	}
	
	public List<Statement> getStmts() {
		return stmts;
	}
	
	public String toString() {
		String result = getLabel() + "(";
		for (int i = 0; i < params.size(); i++) {
			result += params.get(i).getLabel() + ":" + params.get(i).getType();
			if (i < params.size() - 1) result += ", ";
		}
		result += ") : " + retType + " is\n";
		for (VariableDecl var : localVars) {
			result += "   " + var + "\n";
		}
		result += "begin\n";
		for (Statement stmt : stmts) {
			result += "   " + stmt + "\n";
		}
		result += "end;";
		return result;
	}
}