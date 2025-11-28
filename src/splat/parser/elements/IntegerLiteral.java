package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// number literal like 42
public class IntegerLiteral extends Expression {
	private int value;
	
	public IntegerLiteral(int value, Token tok) {
		super(tok);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	// always int type
	public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		return new Type("Integer", getLine(), getColumn());
	}
	// just wrap the value
	public splat.executor.Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) {
		return new splat.executor.IntegerValue(value);
	}
	
	public String toString() {
		return String.valueOf(value);
	}
}