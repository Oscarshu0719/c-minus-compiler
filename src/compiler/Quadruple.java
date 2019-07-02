package compiler;

/**
 * Quadruple to describe Three-address code.
 */
public class Quadruple {
	// Index.
	int id;
	// Operator.
	String op;
	// First argument.
	String arg1;
	// Second argument.
	String arg2;
	// Result.
	Object result;

    /**
     * Constructor with index, operator, first argument, second argument, and result.
     * @param id Index.
     * @param op Operator.
     * @param arg1 First argument.
     * @param arg2 Second Argument.
     * @param result Result.
     */
	public Quadruple(int id, String op, String arg1, String arg2, String result) {
		this.id = id;
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.result = result;
	}
}
