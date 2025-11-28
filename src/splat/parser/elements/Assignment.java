package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// assignment stmnt: var := expr
public class Assignment extends Statement {
	private String label;
	private Expression expr;
	
	public Assignment(String label, Expression expr, Token tok) {
		super(tok);
		this.label = label;
		this.expr = expr;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// ? var is declared
		if (!varAndParamMap.containsKey(label)) {
			throw new SemanticAnalysisException("Undeclared variable '" + label + "'", this);
		}
		// expr type matches var type
		Type exprType = expr.analyzeAndGetType(funcMap, varAndParamMap);
		Type varType = varAndParamMap.get(label);
		
		if (!exprType.getTypeName().equals(varType.getTypeName())) {
			throw new SemanticAnalysisException("Cannot assign " + exprType.getTypeName() + " to variable '" + label + "' of type " + varType.getTypeName(), this);
		}
	}
	
	public void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		// eval expr and update var in map
		splat.executor.Value val = expr.evaluate(funcMap, varAndParamMap);
		varAndParamMap.put(label, val);
	}
	
	public String toString() {
		return label + " := " + expr + ";";
	}
}