package splat.semanticanalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import splat.lexer.Token;
import splat.parser.elements.Declaration;
import splat.parser.elements.Expression;
import splat.parser.elements.FunctionDecl;
import splat.parser.elements.IfStatement;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.ReturnStatement;
import splat.parser.elements.Statement;
import splat.parser.elements.Type;
import splat.parser.elements.VariableDecl;
import splat.parser.elements.WhileLoop;

public class SemanticAnalyzer {

	private ProgramAST progAST;
	private Map<String, FunctionDecl> funcMap;
	private Map<String, Type> progVarMap;
	
	public SemanticAnalyzer(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void analyze() throws SemanticAnalysisException {
		checkNoDuplicateProgLabels(); // check no duplicate labels in program
		setProgVarAndFuncMaps(); // set up maps for functions and vars
		// analyze each funct
		for (FunctionDecl funcDecl : funcMap.values()) {	
			analyzeFuncDecl(funcDecl);
		}
		checkNoReturnInProgram(progAST.getStmts()); // check program body has no return stmts
		// analyze program bod
		for (Statement stmt : progAST.getStmts()) {
			stmt.analyze(funcMap, progVarMap);
		}
	}

	private void analyzeFuncDecl(FunctionDecl funcDecl) throws SemanticAnalysisException {
		checkNoDuplicateFuncLabels(funcDecl); // check no duplicate labels in function	
		Map<String, Type> varAndParamMap = getVarAndParamMap(funcDecl); // get types of params and local vars
		// analyze function body
		for (Statement stmt : funcDecl.getStmts()) {
			stmt.analyze(funcMap, varAndParamMap);
		}
		checkReturnStatements(funcDecl); // check return statement validity
	}
	
	private Map<String, Type> getVarAndParamMap(FunctionDecl funcDecl) {
		Map<String, Type> varAndParamMap = new HashMap<>();
		// +params
		for (VariableDecl param : funcDecl.getParams()) {
			varAndParamMap.put(param.getLabel(), new Type(param.getType(), param.getLine(), param.getColumn()));
		}
		// +local vars
		for (VariableDecl var : funcDecl.getLocalVars()) {
			varAndParamMap.put(var.getLabel(), new Type(var.getType(), var.getLine(), var.getColumn()));
		}
		return varAndParamMap;
	}

	private void checkNoDuplicateFuncLabels(FunctionDecl funcDecl) 
	                                        throws SemanticAnalysisException {
		Set<String> labels = new HashSet<>();
		// check params
		for (VariableDecl param : funcDecl.getParams()) {
			String label = param.getLabel();
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Duplicate parameter '" + label + "' in function", param);
			}
			if (funcMap.containsKey(label)) {
				throw new SemanticAnalysisException("Parameter '" + label + "' cannot have same name as function", param);
			}
			labels.add(label);
		}
		// check local vars
		for (VariableDecl var : funcDecl.getLocalVars()) {
			String label = var.getLabel();
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Duplicate variable '" + label + "' in function", var);
			}
			if (funcMap.containsKey(label)) {
				throw new SemanticAnalysisException("Variable '" + label + "' cannot have same name as function", var);
			}
			labels.add(label);
		}
	}
	
	private void checkNoDuplicateProgLabels() throws SemanticAnalysisException {
		Set<String> labels = new HashSet<>();
		
 		for (Declaration decl : progAST.getDecls()) {
 			String label = decl.getLabel();
 			
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Duplicate label '" + label + "' in program", decl);
			}
			labels.add(label);
		}
	}
	
	private void setProgVarAndFuncMaps() {
		funcMap = new HashMap<>();
		progVarMap = new HashMap<>();
		
		for (Declaration decl : progAST.getDecls()) {
			String label = decl.getLabel();
			
			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);
			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
				progVarMap.put(label, new Type(varDecl.getType(), varDecl.getLine(), varDecl.getColumn()));
			}
		}
	}
	
	private void checkReturnStatements(FunctionDecl funcDecl) throws SemanticAnalysisException {
		String retType = funcDecl.getRetType();
		boolean isVoid = retType.equals("void");
		// check all return stmts in function body
		for (Statement stmt : funcDecl.getStmts()) {
			checkReturnStmt(stmt, retType, isVoid, funcMap, getVarAndParamMap(funcDecl));
		}
		
		// if non-void, must have at least one return stmt
		if (!isVoid && !hasReturnStatement(funcDecl.getStmts())) {
			throw new SemanticAnalysisException("Non-void function '" + funcDecl.getLabel() + "' must have return statement", funcDecl);
		}
	}
	
	private void checkReturnStmt(Statement stmt, String expectedRetType, boolean isVoid, Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
		if (stmt instanceof ReturnStatement) {
			ReturnStatement retStmt = (ReturnStatement)stmt;
			Expression expr = retStmt.getExpr();
			
			if (isVoid && expr != null) {
				throw new SemanticAnalysisException("Void function cannot return a value", retStmt);
			}
			
			if (!isVoid && expr == null) {
				throw new SemanticAnalysisException("Non-void function must return a value", retStmt);
			}
			
			if (!isVoid && expr != null) {
				Type retExprType = expr.analyzeAndGetType(funcMap, varAndParamMap);
				if (!retExprType.getTypeName().equals(expectedRetType)) {
					throw new SemanticAnalysisException("Return type " + retExprType.getTypeName() + " does not match function return type " + expectedRetType, retStmt);
				}
			}
		} else if (stmt instanceof IfStatement) {
			IfStatement ifStmt = (IfStatement)stmt;
			for (Statement s : ifStmt.getThenStmts()) {
				checkReturnStmt(s, expectedRetType, isVoid, funcMap, varAndParamMap);
			}
			if (ifStmt.getElseStmts() != null) {
				for (Statement s : ifStmt.getElseStmts()) {
					checkReturnStmt(s, expectedRetType, isVoid, funcMap, varAndParamMap);
				}
			}
		} else if (stmt instanceof WhileLoop) {
			WhileLoop whileStmt = (WhileLoop)stmt;
			for (Statement s : whileStmt.getStmts()) {
				checkReturnStmt(s, expectedRetType, isVoid, funcMap, varAndParamMap);
			}
		}
	}
	
	private boolean hasReturnStatement(List<Statement> stmts) {
		for (Statement stmt : stmts) {
			if (stmt instanceof ReturnStatement) {
				return true;
			} else if (stmt instanceof IfStatement) {
				IfStatement ifStmt = (IfStatement)stmt;
				if (hasReturnStatement(ifStmt.getThenStmts()) || 
				    (ifStmt.getElseStmts() != null && hasReturnStatement(ifStmt.getElseStmts()))) {
					return true;
				}
			} else if (stmt instanceof WhileLoop) {
				WhileLoop whileStmt = (WhileLoop)stmt;
				if (hasReturnStatement(whileStmt.getStmts())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void checkNoReturnInProgram(List<Statement> stmts) throws SemanticAnalysisException {
		for (Statement stmt : stmts) {
			if (stmt instanceof ReturnStatement) {
				throw new SemanticAnalysisException("Return statement not allowed in program body", stmt);
			} else if (stmt instanceof IfStatement) {
				IfStatement ifStmt = (IfStatement)stmt;
				checkNoReturnInProgram(ifStmt.getThenStmts());
				if (ifStmt.getElseStmts() != null) {
					checkNoReturnInProgram(ifStmt.getElseStmts());
				}
			} else if (stmt instanceof WhileLoop) {
				WhileLoop whileStmt = (WhileLoop)stmt;
				checkNoReturnInProgram(whileStmt.getStmts());
			}
		}
	}
}