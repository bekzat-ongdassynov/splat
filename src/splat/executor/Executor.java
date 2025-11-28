package splat.executor;

import java.util.Map;

import splat.parser.elements.FunctionDecl;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.Statement;

public class Executor {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap;
	private Map<String, Value> progVarMap;
	
	public Executor(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void runProgram() throws ExecutionException {

		// This sets the maps that will be needed for executing function 
		// calls and storing the values of the program variables
		setMaps();
		
		try {
			
			// Go through and execute each of the statements
			for (Statement stmt : progAST.getStmts()) {
				stmt.execute(funcMap, progVarMap);
			}
			
		// We should never have to catch this exception here, since the
		// main program body cannot have returns
		} catch (ReturnFromCall ex) {
			System.out.println("Internal error!!! The main program body "
					+ "cannot have a return statement -- this should have "
					+ "been caught during semantic analysis!");
			
			throw new ExecutionException("Internal error -- fix your "
					+ "semantic analyzer!", -1, -1);
		}
	}
	
	private void setMaps() {
		funcMap = new java.util.HashMap<>();
		progVarMap = new java.util.HashMap<>();
		
		for (splat.parser.elements.Declaration decl : progAST.getDecls()) {
			String label = decl.getLabel();
			// add func to map
			if (decl instanceof FunctionDecl) {
				funcMap.put(label, (FunctionDecl)decl);
			} else if (decl instanceof splat.parser.elements.VariableDecl) {
				splat.parser.elements.VariableDecl varDecl = (splat.parser.elements.VariableDecl)decl;
				String type = varDecl.getType();
				// init vars with default values based on type
				if (type.equals("Integer")) {
					progVarMap.put(label, new IntegerValue(0));
				} else if (type.equals("Boolean")) {
					progVarMap.put(label, new BooleanValue(false));
				} else if (type.equals("String")) {
					progVarMap.put(label, new StringValue(""));
				}
			}
		}
	}

}