package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// true or false literal
public class BooleanLiteral extends Expression {
	private boolean value;
	
	public BooleanLiteral(boolean value, Token tok) {
		super(tok);
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	// always bool type
	public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		return new Type("Boolean", getLine(), getColumn());
	}
	// just wrap the value
	public splat.executor.Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) {
		return new splat.executor.BooleanValue(value);
	}
	
	public String toString() {
		return String.valueOf(value);
	}
}