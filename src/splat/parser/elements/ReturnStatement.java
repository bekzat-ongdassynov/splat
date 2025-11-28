package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// return statement, exits func with optional val
public class ReturnStatement extends Statement {

	private Expression expr;
	
	public ReturnStatement(Expression expr, Token tok) {
		super(tok);
		this.expr = expr;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// ? expr present
		if (expr != null) {
			expr.analyzeAndGetType(funcMap, varAndParamMap);
		}
		// return type check handled by SemanticAnalyzer
	}
	// eval expr and throw exception to unwind call stack
	public void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		splat.executor.Value retVal = null;
		if (expr != null) {
			retVal = expr.evaluate(funcMap, varAndParamMap);
		}
		throw new splat.executor.ReturnFromCall(retVal); // use exception for control flow
	}
	
	public String toString() {
		if (expr != null) {
			return "return " + expr + ";";
		}
		return "return;";
	}
}