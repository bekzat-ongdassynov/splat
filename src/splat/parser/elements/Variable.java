package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// var reference like x or doSt(a, b)
public class Variable extends Expression {

	private String label;
	
	public Variable(String label, Token tok) {
		super(tok);
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	// lookup var type in map
	public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// ? var/param is declared
		if (!varAndParamMap.containsKey(label)) {
			throw new SemanticAnalysisException("Undeclared variable '" + label + "'", this);
		}
		return varAndParamMap.get(label);
	}
	// lookup var val in map
	public splat.executor.Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ExecutionException {
		if (!varAndParamMap.containsKey(label)) {
			throw new splat.executor.ExecutionException("Undeclared variable '" + label + "'", this);
		}
		return varAndParamMap.get(label);
	}
	
	public String toString() {
		return label;
	}
}