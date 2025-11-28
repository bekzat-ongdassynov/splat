package splat.parser.elements;

import java.util.Map;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
// binary expr like (a + b) or (x < y)
public class BinaryExpression extends Expression {
	private Expression left;
	private String operator;
	private Expression right;
	
	public BinaryExpression(Expression left, String operator, Expression right, Token tok) {
		super(tok);
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public Expression getRight() {
		return right;
	}
	// type check based on op
	public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		Type leftType = left.analyzeAndGetType(funcMap, varAndParamMap);
		Type rightType = right.analyzeAndGetType(funcMap, varAndParamMap);
		// +, -, *, /, % - need ints, return int
		if (operator.equals("+") || operator.equals("-") || operator.equals("*") || 
		    operator.equals("/") || operator.equals("%")) {
			if (!leftType.getTypeName().equals("Integer") || !rightType.getTypeName().equals("Integer")) {
				throw new SemanticAnalysisException("Arithmetic operator requires Integer operands", this);
			}
			return new Type("Integer", getLine(), getColumn());
		}
		// <, >, <=, >= - need ints, return bool
		if (operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")) {
			if (!leftType.getTypeName().equals("Integer") || !rightType.getTypeName().equals("Integer")) {
				throw new SemanticAnalysisException("Relational operator requires Integer operands", this);
			}
			return new Type("Boolean", getLine(), getColumn());
		}
		// ==, != - need same types, return bool
		if (operator.equals("==") || operator.equals("!=")) {
			if (!leftType.getTypeName().equals(rightType.getTypeName())) {
				throw new SemanticAnalysisException("Equality operator requires matching types", this);
			}
			return new Type("Boolean", getLine(), getColumn());
		}
		// and, or - need bools, return bool
		if (operator.equals("and") || operator.equals("or")) {
			if (!leftType.getTypeName().equals("Boolean") || !rightType.getTypeName().equals("Boolean")) {
				throw new SemanticAnalysisException("Logical operator requires Boolean operands", this);
			}
			return new Type("Boolean", getLine(), getColumn());
		}
		// not - unary, need bool
		if (operator.equals("not")) {
			if (!rightType.getTypeName().equals("Boolean")) {
				throw new SemanticAnalysisException("Not operator requires Boolean operand", this);
			}
			return new Type("Boolean", getLine(), getColumn());
		}
		
		throw new SemanticAnalysisException("Unknown operator '" + operator + "'", this);
	}
	// eval both sides and apply op
	public splat.executor.Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, splat.executor.Value> varAndParamMap) throws splat.executor.ReturnFromCall, splat.executor.ExecutionException {
		splat.executor.Value leftVal = left.evaluate(funcMap, varAndParamMap);
		splat.executor.Value rightVal = right.evaluate(funcMap, varAndParamMap);
		// arithm ops
		if (operator.equals("+")) {
			return new splat.executor.IntegerValue(leftVal.getIntValue() + rightVal.getIntValue());
		} else if (operator.equals("-")) {
			return new splat.executor.IntegerValue(leftVal.getIntValue() - rightVal.getIntValue());
		} else if (operator.equals("*")) {
			return new splat.executor.IntegerValue(leftVal.getIntValue() * rightVal.getIntValue());
		} else if (operator.equals("/")) {
			int divisor = rightVal.getIntValue();
			if (divisor == 0) {
				throw new splat.executor.ExecutionException("Division by zero", this);
			}
			return new splat.executor.IntegerValue(leftVal.getIntValue() / divisor);
		} else if (operator.equals("%")) {
			int divisor = rightVal.getIntValue();
			if (divisor == 0) {
				throw new splat.executor.ExecutionException("Modulo by zero", this);
			}
			return new splat.executor.IntegerValue(leftVal.getIntValue() % divisor);
		} else if (operator.equals("<")) { // comparison ops
			return new splat.executor.BooleanValue(leftVal.getIntValue() < rightVal.getIntValue());
		} else if (operator.equals(">")) {
			return new splat.executor.BooleanValue(leftVal.getIntValue() > rightVal.getIntValue());
		} else if (operator.equals("<=")) {
			return new splat.executor.BooleanValue(leftVal.getIntValue() <= rightVal.getIntValue());
		} else if (operator.equals(">=")) {
			return new splat.executor.BooleanValue(leftVal.getIntValue() >= rightVal.getIntValue());
		} else if (operator.equals("==")) { // equality ops
			Object leftObj = leftVal.getValue();
			Object rightObj = rightVal.getValue();
			return new splat.executor.BooleanValue(leftObj.equals(rightObj));
		} else if (operator.equals("!=")) {
			Object leftObj = leftVal.getValue();
			Object rightObj = rightVal.getValue();
			return new splat.executor.BooleanValue(!leftObj.equals(rightObj));
		} else if (operator.equals("and")) { // logical ops
			return new splat.executor.BooleanValue(leftVal.getBoolValue() && rightVal.getBoolValue());
		} else if (operator.equals("or")) {
			return new splat.executor.BooleanValue(leftVal.getBoolValue() || rightVal.getBoolValue());
		} else if (operator.equals("not")) {
			return new splat.executor.BooleanValue(!rightVal.getBoolValue());
		}
		
		throw new splat.executor.ExecutionException("Unknown operator '" + operator + "'", this);
	}
	
	public String toString() {
		return "(" + left + " " + operator + " " + right + ")";
	}
}