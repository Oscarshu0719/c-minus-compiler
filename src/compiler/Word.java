package compiler;

import java.util.ArrayList;

/**
 * Word info.
 */
public class Word {
	final static String KEY = "Keyword";
	final static String OPERATOR = "Operator";
	final static String INT_CONST = "Integer constant";
	final static String CHAR_CONST = "Character constant";
	final static String BOOL_CONST = "Boolean constant";
	final static String IDENTIFIER = "Identifier";
	final static String BOUNDARY_SIGN = "Boundary sign";
	final static String END_SIGN = "End sign";
	final static String UNDEFINED = "Undefined";

	// Keyword list.
	private static ArrayList<String> keywordList = new ArrayList<>();
	// Operator list.
	private static ArrayList<String> operatorList = new ArrayList<>();
	// Boundary signList
	private static ArrayList<String> boundarySignList = new ArrayList<>();

	// Add to lists.
	static {
		Word.operatorList.add("+");
		Word.operatorList.add("-");
		Word.operatorList.add("++");
		Word.operatorList.add("--");
		Word.operatorList.add("*");
		Word.operatorList.add("/");
		Word.operatorList.add(">");
		Word.operatorList.add("<");
		Word.operatorList.add(">=");
		Word.operatorList.add("<=");
		Word.operatorList.add("==");
		Word.operatorList.add("!=");
		Word.operatorList.add("=");
		Word.operatorList.add("&&");
		Word.operatorList.add("||");
		Word.operatorList.add("!");
		Word.operatorList.add(".");
		Word.operatorList.add("?");
		Word.operatorList.add("|");
		Word.operatorList.add("&");
		Word.boundarySignList.add("(");
		Word.boundarySignList.add(")");
		Word.boundarySignList.add("{");
		Word.boundarySignList.add("}");
		Word.boundarySignList.add(";");
		Word.boundarySignList.add(",");
		Word.keywordList.add("void");
		Word.keywordList.add("main");
		Word.keywordList.add("int");
		Word.keywordList.add("char");
		Word.keywordList.add("if");
		Word.keywordList.add("else");
		Word.keywordList.add("while");
		Word.keywordList.add("for");
		Word.keywordList.add("printf");
		Word.keywordList.add("scanf");
	}

	// Word No.
	private int id;
	// Word value.
    private String value;
    // Word type.
    private String type;
    // Word line No..
    private int line;
    // Check if the word is legal.
    private boolean legalFlag = true;

	/**
	 * Constructor with word No., word line No., and word value.
	 * @param id Word No..
	 * @param line Word line No..
	 * @param value Word value.
	 */
	public Word(int id, int line, String value) {
        this.id = id;
        this.line = line;
        this.value = value;
	}

	/**
	 * Constructor with word No., word line No., word value, and word type.
	 * @param id Word No..
	 * @param line Word line No..
	 * @param value Word value.
	 * @param type Word type.
	 */
	public Word(int id, int line, String value, String type) {
		this.id = id;
        this.line = line;
        this.value = value;
        this.type = type;
	}

	/**
	 * Check if the input word is a keyword.
	 * @param word Input word.
	 * @return Is or is not a keyword.
	 */
	static boolean isKeyword(String word) {
		return keywordList.contains(word);
	}

	/**
	 * Check if the input word is an operator.
	 * @param word Input word.
	 * @return Is or is not an operator.
	 */
	static boolean isOperator(String word) {
		return operatorList.contains(word);
	}

	/**
	 * Check if the input word is a boundary sign.
	 * @param word Input word.
	 * @return Is or is not a boundary sign.
	 */
	static boolean isBoundarySign(String word) {
		return boundarySignList.contains(word);
	}

	/**
	 * Check if the input word is an arithmetic operator.
	 * @param word Input word.
	 * @return Is or is not an arithmetic operator.
	 */
	public static boolean isArithmeticOp(String word) {
		return word.equals("+") || word.equals("-") || word.equals("*") || word.equals("/");
	}

	/**
	 * Check if the input word is a boolean operator.
	 * @param word Input word.
	 * @return Is or is not a boolean operator.
	 */
	public static boolean isBooleanOp(String word) {
		return word.equals(">") || word.equals("<") || word.equals("==") || word.equals("!=") || word.equals("!") ||
				word.equals("&&") || word.equals("||");
	}

	/**
	 * Return `value`.
	 * @return `value`.
	 */
	String getWordValue() {
	    return this.value;
    }

	/**
	 * Return `type`.
	 * @return `type`.
	 */
	String getWordType() {
	    return this.type;
    }

	/**
	 * Return `id`.
	 * @return `id`.
	 */
	int getWordId() {
	    return this.id;
    }

	/**
	 * Return `line`.
	 * @return `line`.
	 */
	int getWordLine() {
	    return this.line;
    }

	/**
	 * Return `legalFlag`.
	 * @return `legalFlag`.
	 */
	boolean getWordLegalFlag() {
	    return this.legalFlag;
    }


	/**
	 * Set `value`.
	 * @param value `value`.
	 */
	void setWordValue(String value) {
		this.value = value;
	}

	/**
	 * Set `type`.
	 * @param type `type`.
	 */
	void setWordType(String type) {
	    this.type = type;
    }

	/**
	 * Set `id`.
	 * @param id `id`.
	 */
	void setWordId(int id) {
		this.id = id;
	}

	/**
	 * Set `line`.
	 * @param line `line`.
	 */
	void setWordLine(int line) {
		this.line = line;
	}

	/**
	 * Set `legalFlag`.
	 * @param flag `legalFlag`.
	 */
    void setWordLegalFlag(boolean flag) {
	    this.legalFlag = flag;
    }
}
