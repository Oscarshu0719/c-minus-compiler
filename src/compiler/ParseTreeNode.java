package compiler;

import java.util.ArrayList;

/**
 * Parse tree node.
 */
public class ParseTreeNode {
	final static String NON_TERMINAL = "Non-terminal";
	final static String TERMINAL = "Terminal";
	final static String ACTION_SIGN = "Action sign";
	final static String END_SIGN = "End sign";

	// Non-terminal list.
	private static ArrayList<String> nonTerminalList = new ArrayList<>();
	// Action sign list.
	private static ArrayList<String> actionSignList = new ArrayList<>();

	static{
		nonTerminalList.add("S");
		nonTerminalList.add("A");
		nonTerminalList.add("B");
		nonTerminalList.add("C");
		nonTerminalList.add("D");
		nonTerminalList.add("E");
		nonTerminalList.add("F");
		nonTerminalList.add("G");
		nonTerminalList.add("H");
		nonTerminalList.add("L");
		nonTerminalList.add("M");
		nonTerminalList.add("O");
		nonTerminalList.add("P");
		nonTerminalList.add("Q");
		nonTerminalList.add("X");
		nonTerminalList.add("Y");
		nonTerminalList.add("Z");
		nonTerminalList.add("R");
		nonTerminalList.add("U");
		nonTerminalList.add("Z'");
		nonTerminalList.add("U'");
		nonTerminalList.add("E'");
		nonTerminalList.add("H'");
		nonTerminalList.add("L'");
		nonTerminalList.add("T");
		nonTerminalList.add("T'");
		nonTerminalList.add("K");
		nonTerminalList.add("I");
		nonTerminalList.add("J");
		nonTerminalList.add("M");
		actionSignList.add("@ADD_SUB");
		actionSignList.add("@ADD");
		actionSignList.add("@SUB");
		actionSignList.add("@DIV_MUL");
		actionSignList.add("@DIV");
		actionSignList.add("@MUL");
		actionSignList.add("@SINGLE");
		actionSignList.add("@SINGTLE_OP");
		actionSignList.add("@ASS_M");
		actionSignList.add("@ASS_H");
		actionSignList.add("@ASS_U");
		actionSignList.add("@TRAN_HM");
		actionSignList.add("@EQ");
		actionSignList.add("@EQ_U'");
		actionSignList.add("@COMPARE");
		actionSignList.add("@COMPARE_OP");
		actionSignList.add("@IF_FJ");
		actionSignList.add("@IF_BACKPATCH_FJ");
		actionSignList.add("@IF_RJ");
		actionSignList.add("@IF_BACKPATCH_RJ");
		actionSignList.add("@WHILE_FJ");
		actionSignList.add("@WHILE_BACKPATCH_FJ");
		actionSignList.add("@IF_RJ");
		actionSignList.add("@FOR_FJ");
		actionSignList.add("@FOR_RJ");
		actionSignList.add("@FOR_BACKPATCH_FJ");
	}

	// Node type.
	private String type;
	// Node name.
	private String name;
	// Node value.
	private String value;

	/**
	 * Constructor with node type, node name, and node value.
	 * @param type Node type.
	 * @param name Node name.
	 * @param value Node value.
	 */
	ParseTreeNode(String type, String name, String value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}

	/**
	 * Check if the input node is a non-terminal.
	 * @param node Input node.
	 * @return Is or is not a non-terminal.
	 */
	static boolean isNonTerminal(ParseTreeNode node) {
		return nonTerminalList.contains(node.name);
	}

	/**
	 * Check if the input node is a terminal.
	 * @param node Input node.
	 * @return Is or is not a terminal.
	 */
	static boolean isTerminal(ParseTreeNode node) {
		return Word.isKeyword(node.name) || Word.isOperator(node.name) || Word.isBoundarySign(node.name) ||
				node.name.equals("id") || node.name.equals("num") || node.name.equals("ch");
	}

	/**
	 * Check if the input node is an action sign.
	 * @param node Input node.
	 * @return Is or is not an action sign.
	 */
	public static boolean isActionSign(ParseTreeNode node) {
		return actionSignList.contains(node.name);
	}

	/**
	 * Return `type`.
	 * @return `type`.
	 */
	String getNodeType() {
		return this.type;
	}

	/**
	 * Return `name`.
	 * @return `name`.
	 */
	String getNodeName() {
		return this.name;
	}

	/**
	 * Return `value`.
	 * @return `value`.
	 */
	String getNodeValue() {
		return this.value;
	}

	/**
	 * Set `type`.
	 * @param type `type`.
	 */
	void setNodeType(String type) {
		this.type = type;
	}

	/**
	 * Set `name`.
	 * @param name `name`.
	 */
	void setNodeName(String name) {
		this.name = name;
	}

	/**
	 * Set `value`.
	 * @param value `value`.
	 */
	void setNodeValue(String value) {
		this.value = value;
	}
}
