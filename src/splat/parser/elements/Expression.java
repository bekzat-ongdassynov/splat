package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// base for all exprs
public abstract class Expression extends ASTElement {

	public Expression(Token tok) {
		super(tok);
	}
	// semantic analysis, return type of expr
	public abstract Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException;
	// evaluate expr and return runtime val
	public abstract splat.executor.Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException;
}