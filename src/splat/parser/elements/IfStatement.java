package splat.parser.elements;

import java.util.List;
import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// if statement with optional else
public class IfStatement extends Statement {
	private Expression condition;
	private List<Statement> thenStmts;
	private List<Statement> elseStmts;
	
	public IfStatement(Expression condition, List<Statement> thenStmts, List<Statement> elseStmts, Token tok) {
		super(tok);
		this.condition = condition;
		this.thenStmts = thenStmts;
		this.elseStmts = elseStmts;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public List<Statement> getThenStmts() {
		return thenStmts;
	}
	
	public List<Statement> getElseStmts() {
		return elseStmts;
	}
	
	public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// ? cond is Boolean
		Type condType = condition.analyzeAndGetType(funcMap, varAndParamMap);
		if (!condType.getTypeName().equals("Boolean")) {
			throw new SemanticAnalysisException("If condition must be Boolean, got " + condType.getTypeName(), this);
		}
		// then stmts
		for (Statement stmt : thenStmts) {
			stmt.analyze(funcMap, varAndParamMap);
		}
		// else stmts
		if (elseStmts != null) {
			for (Statement stmt : elseStmts) {
				stmt.analyze(funcMap, varAndParamMap);
			}
		}
	}
	// eval cond and execute then/else branch
	public void execute(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		if (condition.evaluate(funcMap, varAndParamMap).getBoolValue()) {
			for (Statement stmt : thenStmts) {
				stmt.execute(funcMap, varAndParamMap);
			}
		} else if (elseStmts != null) {
			for (Statement stmt : elseStmts) {
				stmt.execute(funcMap, varAndParamMap);
			}
		}
	}
	
	public String toString() {
		String result = "if (" + condition + ") then\n";
		for (Statement stmt : thenStmts) {
			result += "   " + stmt + "\n";
		}
		if (elseStmts != null && !elseStmts.isEmpty()) {
			result += "else\n";
			for (Statement stmt : elseStmts) {
				result += "   " + stmt + "\n";
			}
		}
		result += "end if;";
		return result;
	}
}