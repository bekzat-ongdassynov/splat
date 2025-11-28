package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// standalone func call statement (must be void)
public class FunctionCallStatement extends Statement {

	private FunctionCall funcCall;
	
	public FunctionCallStatement(FunctionCall funcCall, Token tok) {
		super(tok);
		this.funcCall = funcCall;
	}
	
	public FunctionCall getFuncCall() {
		return funcCall;
	}
	
	public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// analyze func call and check return type is void
		Type retType = funcCall.analyzeAndGetType(funcMap, varAndParamMap);
		if (!retType.getTypeName().equals("void")) {
			throw new SemanticAnalysisException("Function call statement must call void function", this);
		}
	}
	// just eval the call, ignore return (should be void)
	public void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		funcCall.evaluate(funcMap, varAndParamMap);
	}
	
	public String toString() {
		return funcCall.toString() + ";";
	}
}