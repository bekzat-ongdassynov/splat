package splat.parser.elements;

import java.util.List;
import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// while loop stmnt
public class WhileLoop extends Statement {
	private Expression condition;
	private List<Statement> stmts;
	
	public WhileLoop(Expression condition, List<Statement> stmts, Token tok) {
		super(tok);
		this.condition = condition;
		this.stmts = stmts;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public List<Statement> getStmts() {
		return stmts;
	}
	
	public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// ? cond is Boolean
		Type condType = condition.analyzeAndGetType(funcMap, varAndParamMap);
		if (!condType.getTypeName().equals("Boolean")) {
			throw new SemanticAnalysisException("While condition must be Boolean, got " + condType.getTypeName(), this);
		}
		// body stmts
		for (Statement stmt : stmts) {
			stmt.analyze(funcMap, varAndParamMap);
		}
	}
	
	public void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		while (condition.evaluate(funcMap, varAndParamMap).getBoolValue()) {
			for (Statement stmt : stmts) {
				stmt.execute(funcMap, varAndParamMap);
			}
		}
	}
	
	public String toString() {
		String result = "while (" + condition + ") do\n";
		for (Statement stmt : stmts) {
			result += "   " + stmt + "\n";
		}
		result += "end while;";
		return result;
	}
}