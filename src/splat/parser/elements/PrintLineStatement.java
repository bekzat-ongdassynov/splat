package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// print_line stmnt - just outputs \n
public class PrintLineStatement extends Statement {

	public PrintLineStatement(Token tok) {
		super(tok);
	}
	
	public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// nothing to analyze, nil, nAn, nada
	}
	// just print \n
	public void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) {
		System.out.println();
	}
	
	public String toString() {
		return "print_line;";
	}
}