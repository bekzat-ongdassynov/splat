package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// print statement, outputs val without \n
public class PrintStatement extends Statement {
	private Expression expr;
	
	public PrintStatement(Expression expr, Token tok) {
		super(tok);
		this.expr = expr;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// just analyze the expr
		expr.analyzeAndGetType(funcMap, varAndParamMap);
	}
	// eval expr and print to stdout
	public void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		splat.executor.Value val = expr.evaluate(funcMap, varAndParamMap);
		System.out.print(val.getValue());
	}
	
	public String toString() {
		return "print " + expr + ";";
	}
}