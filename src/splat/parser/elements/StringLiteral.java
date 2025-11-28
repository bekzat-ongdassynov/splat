package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// str literal like "AmIfailingthecourseforreal?"
public class StringLiteral extends Expression {

	private String value;
	
	public StringLiteral(String value, Token tok) {
		super(tok);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	// always str type
	public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		return new Type("String", getLine(), getColumn());
	}
	// just wrap the value
	public splat.executor.Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) {
		return new splat.executor.StringValue(value);
	}
	
	public String toString() {
		return "\"" + value + "\"";
	}
}