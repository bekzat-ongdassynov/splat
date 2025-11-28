package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// base for all stmnts
public abstract class Statement extends ASTElement {

	public Statement(Token tok) {
		super(tok);
	}

	// semantic analysis on stmnt
	public abstract void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException;
	// run the stmnt
	public abstract void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException;   
}