package splat.parser.elements;

import java.util.List;
import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// function call expr like doSt(x, y)
public class FunctionCall extends Expression {
	private String label;
	private List<Expression> args;
	
	public FunctionCall(String label, List<Expression> args, Token tok) {
		super(tok);
		this.label = label;
		this.args = args;
	}
	
	public String getLabel() {
		return label;
	}
	
	public List<Expression> getArgs() {
		return args;
	}
	
	public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		// ? function exists
		if (!funcMap.containsKey(label)) {
			throw new SemanticAnalysisException("Undeclared function '" + label + "'", this);
		}
		
		FunctionDecl funcDecl = funcMap.get(label);
		List<VariableDecl> params = funcDecl.getParams();
		// arg count
		if (args.size() != params.size()) {
			throw new SemanticAnalysisException("Function '" + label + "' expects " + params.size() + " arguments, got " + args.size(), this);
		}
		// arg types
		for (int i = 0; i < args.size(); i++) {
			Type argType = args.get(i).analyzeAndGetType(funcMap, varAndParamMap);
			String paramType = params.get(i).getType();
			if (!argType.getTypeName().equals(paramType)) {
				throw new SemanticAnalysisException("Argument " + (i + 1) + " of function '" + label + "' expects type " + paramType + ", got " + argType.getTypeName(), this);
			}
		}
		
		return new Type(funcDecl.getRetType(), getLine(), getColumn());
	}
	// call func - create new scope, bind params, execute body
	public splat.executor.Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		if (!funcMap.containsKey(label)) {
			throw new splat.executor.ExecutionException("Undeclared function '" + label + "'", this);
		}
		
		FunctionDecl funcDecl = funcMap.get(label);
		List<VariableDecl> params = funcDecl.getParams();
		// new scope for this func call
		Map<String, splat.executor.Value> localVarAndParamMap = new java.util.HashMap<>();
		// eval args and bind to params
		for (int i = 0; i < args.size(); i++) {
			splat.executor.Value argVal = args.get(i).evaluate(funcMap, varAndParamMap);
			localVarAndParamMap.put(params.get(i).getLabel(), argVal);
		}
		// init local vars with defaults
		for (VariableDecl localVar : funcDecl.getLocalVars()) {
			String type = localVar.getType();
			if (type.equals("Integer")) {
				localVarAndParamMap.put(localVar.getLabel(), new splat.executor.IntegerValue(0));
			} else if (type.equals("Boolean")) {
				localVarAndParamMap.put(localVar.getLabel(), new splat.executor.BooleanValue(false));
			} else if (type.equals("String")) {
				localVarAndParamMap.put(localVar.getLabel(), new splat.executor.StringValue(""));
			}
		}
		// execute func body, catch return
		try {
			for (Statement stmt : funcDecl.getStmts()) {
				stmt.execute(funcMap, localVarAndParamMap);
			}
		} catch (splat.executor.ReturnFromCall retEx) {
			return retEx.getReturnVal();
		}
		// void funcs fall through
		return null;
	}
	
	public String toString() {
		String result = label + "(";
		for (int i = 0; i < args.size(); i++) {
			result += args.get(i);
			if (i < args.size() - 1) result += ", ";
		}
		result += ")";
		return result;
	}
}