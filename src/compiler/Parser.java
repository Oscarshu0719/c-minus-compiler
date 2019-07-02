package compiler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

/**
 * LL1 parser.
 */
public class Parser {
    // Lexer.
    private Lexer lexAnalyze;
    // Word list.
    private ArrayList<Word> wordList;
    // Parse stack.
    private Stack<ParseTreeNode> AnalyzeStack;
    // Semantic stack.
    private Stack<String> semanticStack;
    // Quadruple list.
    private ArrayList<Quadruple> quadrupleList;
    // Error list.
    private ArrayList<Error> errorList;
    // Buffer of parse stack.
    private StringBuffer stringBuffer;
    // Error index.
    private int errorIndex = 0;
    // Check if some error occurs during parsing.
    private boolean parseErrorFlag = false;
    // Temporary variable count.
    private int tmpCount = 0;
    // Quadruple count.
    private int quadrupleCount = 0;
    // Top of the stack.
    private ParseTreeNode stackTop;
    // The first word that hasn't parsed.
    private Word firstWord;
    private Word secondWord;
    // Operator, first argument, second argument, and result.
    private String OP = null, ARG1, ARG2, RES;
    private Error error;
    // Stack of address to jump.
    private Stack<Integer> if_fj, if_rj, while_fj, while_rj, for_fj, for_rj;
    private Stack<String> for_op;

    private ParseTreeNode S, B, A, C, X, Y, R, Z, Z1, U, U1, E, E1, H, H1, G, M, D, L, L1, T, T1, F, O, P, Q, I, J, K;
    private ParseTreeNode ADD_SUB, DIV_MUL, ADD, SUB, DIV, MUL, ASS_H, ASS_M, ASS_U, TRAN_HM;
    private ParseTreeNode SINGLE, SINGLE_OP, EQ, EQ_U1, COMPARE, COMPARE_OP, IF_FJ, IF_RJ, IF_BACKPATCH_FJ, IF_BACKPATCH_RJ;
    private ParseTreeNode WHILE_FJ, WHILE_RJ, WHILE_BACKPATCH_FJ, FOR_FJ, FOR_RJ, FOR_BACKPATCH_FJ;

    /**
     * Constructor.
     * @param lexAnalyze Lexer.
     */
    public Parser(Lexer lexAnalyze) {
        this.lexAnalyze = lexAnalyze;
        this.wordList = lexAnalyze.getWordList();
        initialize();
    }

    /**
     * Initialize.
     */
    private void initialize() {
        S = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "S", null);//program
        A = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "A", null);//local-declarations
        B = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "B", null);//statment-list
        C = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "C", null);//var-declaration
        D = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "D", null);//statement
        E = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "E", null);//else-part
        F = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "F", null);//add-exp-follower
        G = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "G", null);//statement-block
        H = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "H", null);//expression
        I = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "I", null);//simple-expression
        J = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "J", null);//additive-expression
        K = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "K", null);//add-exp
        L = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "L", null);//term
        L1 = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "L'", null);//term'
        M = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "M", null);//facotr
        R = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "R", null);
        X = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "X", null);
        Y = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "Y", null);//type-specifier
        Z = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "Z", null);//identifier list
        Z1 = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "Z'", null);//, id
        U = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "U", null);//initialization in var declaration
        U1 = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "U'", null);//= xxx
        E1 = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "E'", null);
        H1 = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "H'", null);
        T = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "T", null);
        T1 = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "T'", null);
        O = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "O", null);
        P = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "P", null);
        Q = new ParseTreeNode(ParseTreeNode.NON_TERMINAL, "Q", null);
        ADD_SUB = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@ADD_SUB", null);
        ADD = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@ADD", null);
        SUB = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@SUB", null);
        DIV_MUL = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@DIV_MUL", null);
        DIV = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@DIV", null);
        MUL = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@MUL", null);
        ASS_M = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@ASS_M", null);
        ASS_H = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@ASS_H", null);
        ASS_U = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@ASS_U", null);
        TRAN_HM = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@TRAN_HM", null);
        SINGLE = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@SINGLE", null);
        SINGLE_OP = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@SINGLE_OP", null);
        EQ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@EQ", null);
        EQ_U1 = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@EQ_U'", null);
        COMPARE = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@COMPARE", null);
        COMPARE_OP = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@COMPARE_OP", null);
        IF_FJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@IF_FJ", null);
        IF_RJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@IF_RJ", null);
        IF_BACKPATCH_FJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@IF_BACKPATCH_FJ", null);
        IF_BACKPATCH_RJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@IF_BACKPATCH_RJ", null);
        WHILE_FJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@WHILE_FJ", null);
        WHILE_RJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@WHILE_RJ", null);
        WHILE_BACKPATCH_FJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@WHILE_BACKPATCH_FJ", null);
        FOR_FJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@FOR_FJ", null);
        FOR_RJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@FOR_RJ", null);
        FOR_BACKPATCH_FJ = new ParseTreeNode(ParseTreeNode.ACTION_SIGN, "@FOR_BACKPATCH_FJ", null);

        if_fj = new Stack<>();
        if_rj = new Stack<>();
        while_fj = new Stack<>();
        while_rj = new Stack<>();
        for_fj = new Stack<>();
        for_rj = new Stack<>();
        for_op = new Stack<>();
        AnalyzeStack = new Stack<>();
        semanticStack = new Stack<>();
        quadrupleList = new ArrayList<>();
        errorList = new ArrayList<>();
        stringBuffer=new StringBuffer();
    }

    /**
     * Return if error occurs during parsing.
     * @return Error occurs or not.
     */
    public boolean isParseError() {
        return parseErrorFlag;
    }

    /**
     * New temporary variable.
     * @return New register.
     */
    private String newTemporary() {
        tmpCount++;
        return "T" + tmpCount;
    }

    /**
     * LL1 parser.
     */
    public void grammarAnalyze() {
        int stepCount = 0;
        error = null;
        AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.END_SIGN, "#", null));
        AnalyzeStack.push(S);
        semanticStack.add("#");

        while (!AnalyzeStack.empty() && !wordList.isEmpty()) {
            stringBuffer.append("Step " + stepCount + ": \t\t");
            
            //the maximum step count
            if (stepCount++ > 10000) {
                parseErrorFlag = true;
                break;
            }

            // Top of the stack. 
            stackTop = AnalyzeStack.peek();
            // Look ahead
            // we look ahead two characters for expression and simple-expression
            firstWord = wordList.get(0);

            // The end.
            if (firstWord.getWordValue().equals("#") && stackTop.getNodeName().equals("#")) {
                stringBuffer.append("\n");
                AnalyzeStack.pop();
                wordList.remove(0);
                //The teminating symbol on the top of the stack
            } else if (stackTop.getNodeName().equals("#")) {
                AnalyzeStack.pop();
                parseErrorFlag = true;
                break;
            // Terminal.
            } else if (ParseTreeNode.isTerminal(stackTop)) {
                terminalOp(stackTop.getNodeName());
            // Non-terminal.
            } else if (ParseTreeNode.isNonTerminal(stackTop)) {
                nonTerminalOp(stackTop.getNodeName());
            // Action sign.
            } else if(stackTop.getNodeType().equals(ParseTreeNode.ACTION_SIGN)) {
                actionSignOp();
            }

            //print the state of analyzing stack and the later inputs
            stringBuffer.append("Parsing: ");
            for (int i = 0; i < AnalyzeStack.size(); i++) {
                stringBuffer.append(AnalyzeStack.get(i).getNodeName());
                stringBuffer.append(" ");
            }
            stringBuffer.append("\t").append("Input: ");
            for (int j = 0; j < wordList.size(); j++) {
                stringBuffer.append(wordList.get(j).getWordValue());
                stringBuffer.append(" ");
            }
            stringBuffer.append("\t").append("Semantics: ");
            for (int k = semanticStack.size() - 1; k >= 0; k--) {
                stringBuffer.append(semanticStack.get(k));
            }

            //beginning a new line
            stringBuffer.append('\n');
        }
    }

    /**
     * Terminal operation.
     * @param word Terminal.
     */
    private void terminalOp(String word) {
        //terminal in the stack top equals the coming one
        if (firstWord.getWordType().equals(Word.INT_CONST) || firstWord.getWordType().equals(Word.CHAR_CONST) ||
                word.equals(firstWord.getWordValue()) || (word.equals("id") && firstWord.getWordType().equals(Word.IDENTIFIER))) {
            //match
            AnalyzeStack.pop();
            wordList.remove(0);
        } else {//error
            errorIndex++;
            AnalyzeStack.pop();
            wordList.remove(0);
            error = new Error(errorIndex,"Grammar error.", firstWord.getWordLine(), firstWord);
            errorList.add(error);
            parseErrorFlag = true;
        }
    }

    /**
     * Non-terminal operation.
     * @param word Non-terminal.
     */
    private void nonTerminalOp(String word) {
    	//deal with Alphabet'
    	if (word.equals("Z'")) word = "1";
    	if (word.equals("U'")) word = "2";
    	if (word.equals("L'")) word = "3";
        switch(word.charAt(0)) {
            case 'S'://program -> void main () {local-declarations statment-list}
                if (firstWord.getWordValue().equals("void")) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "}", null));
                    AnalyzeStack.push(B);
                    AnalyzeStack.push(A);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "{", null));
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ")", null));
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "(", null));
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "main", null));
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "void", null));
                } else {
                    errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "No returned value in main function.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
                }
                break;
            case 'A'://local-declarations -> var-declaration local-declarations
                if (firstWord.getWordValue().equals("int") || firstWord.getWordValue().equals("char") ||
                        firstWord.getWordValue().equals("bool")) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(A);
                    AnalyzeStack.push(C);
                }//local-declarations -> empty
                else if (firstWord.getWordValue().equals("printf") || firstWord.getWordValue().equals("scanf")
                    || firstWord.getWordValue().equals("if") || firstWord.getWordValue().equals("while")
                    || firstWord.getWordValue().equals("for") || firstWord.getWordType().equals(Word.IDENTIFIER)
                    || firstWord.getWordValue().equals("}")) {
                    AnalyzeStack.pop();
                } 
                break;
            case 'B'://statement-list -> empty
                if (firstWord.getWordValue().equals("}")) {
                    AnalyzeStack.pop();
                }
                else if (firstWord.getWordValue().equals("if") || firstWord.getWordValue().equals("while")
                    || firstWord.getWordValue().equals("for") || firstWord.getWordType().equals(Word.IDENTIFIER)) 
                {//statement-list -> statement statement-list
                    AnalyzeStack.pop();
                    AnalyzeStack.push(B);
                    AnalyzeStack.push(D);
                }else {
                    errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined Indentifier in statement.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
                }
                break;
            case 'C'://var-declaration -> type-specifier ID;
                if (firstWord.getWordValue().equals("int") || firstWord.getWordValue().equals("char") ||
                        firstWord.getWordValue().equals("bool"))
                {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ";", null));
                    AnalyzeStack.push(Z);
                    AnalyzeStack.push(Y);
                }
                break;
            case 'D'://statement -> expression-stmt|selection-stmt|while-stmt|for-stmt
                if(firstWord.getWordValue().equals("if")){//selection-stmt -> if (expression) statement-block else-part
                    AnalyzeStack.pop();
                    AnalyzeStack.push(IF_BACKPATCH_RJ);
                    AnalyzeStack.push(E);
                    AnalyzeStack.push(IF_RJ);
                    AnalyzeStack.push(IF_BACKPATCH_FJ);
                    AnalyzeStack.push(G);
                    AnalyzeStack.push(IF_FJ);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ")", null));
                    AnalyzeStack.push(H);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "(", null));
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "if", null));
                }
                else if(firstWord.getWordValue().equals("while")){//while-stmt -> while (expression) statement-block
                    AnalyzeStack.pop();
                    AnalyzeStack.push(WHILE_BACKPATCH_FJ);
                    AnalyzeStack.push(WHILE_RJ);
                    AnalyzeStack.push(G);
                    AnalyzeStack.push(WHILE_FJ);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ")", null));
                    AnalyzeStack.push(H);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "(", null));
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "while", null));

                }
                else if(firstWord.getWordValue().equals("for")){
                    //for-stmt -> for (expression; simple-expression; expression) statement-block
                    AnalyzeStack.pop();
                    AnalyzeStack.push(FOR_BACKPATCH_FJ);
                    AnalyzeStack.push(FOR_RJ);
                    AnalyzeStack.push(G);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ")", null));
                    AnalyzeStack.push(H);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ";", null));
                    AnalyzeStack.push(FOR_FJ);
                    AnalyzeStack.push(I);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ";", null));
                    AnalyzeStack.push(H);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "(", null));
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "for", null));
                }
                else if (firstWord.getWordType().equals(Word.IDENTIFIER)){
                    //expression-stmt -> expression;
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ";", null));
                    AnalyzeStack.push(H);
                }
                else if(firstWord.getWordValue().equals(";")){
                    //expression-stmt -> ;
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ";", null));
                }
                else {
                	errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined identifier.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
                }
                break;
            case 'E'://else-part -> else statement-block 
            	if (firstWord.getWordValue().equals("else")) {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(G);
            		AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "else", null));
            	}
            	else{//else-part -> empty
            		AnalyzeStack.pop();
            	}
            	break;
            case 'F'://add-exp-follower -> relop additive-expression | empty
            	if (firstWord.getWordValue().equals("<=")||firstWord.getWordValue().equals("<")
            		||firstWord.getWordValue().equals(">")||firstWord.getWordValue().equals(">=")
            		||firstWord.getWordValue().equals("==")||firstWord.getWordValue().equals("!=")) 
            	{
            		AnalyzeStack.pop();
            		AnalyzeStack.push(COMPARE);
            		AnalyzeStack.push(J);
            		AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, firstWord.getWordValue(), null));
            		AnalyzeStack.push(COMPARE_OP);
            	}
            	else if (firstWord.getWordValue().equals(";")||firstWord.getWordValue().equals(")")) {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(TRAN_JF);
            	}
            	else{
            		errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined operator.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
            	}
            	break;
            case 'G'://statement-block -> {statement-list} | statement
            	if (firstWord.getWordValue().equals("{")) {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "}", null));
            		AnalyzeStack.push(B);
            		AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "{", null));
            	}
            	else {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(D);
            	}
            	break;
            case 'H'://expression -> ID = simple-expression|simple-expression
            	secondWord = wordList.get(1);
            	if (firstWord.getWordType().equals(Word.IDENTIFIER) && secondWord.getWordValue().equals("=")) {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(EQ);
            		AnalyzeStack.push(I);
            		AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "=", null));
            		AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "id", null));
            		AnalyzeStack.push(ASS_H);
            	}
            	else {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(I);
            	}
            	break;
            case 'I'://simple-expression -> additive-expression add-exp-follower
            	//System.out.println("Hello world");
            	if (firstWord.getWordValue().equals("(")||firstWord.getWordType().equals(Word.IDENTIFIER)
            		||firstWord.getWordType().equals(Word.INT_CONST)||firstWord.getWordType().equals(Word.CHAR_CONST))
            	{
            		AnalyzeStack.pop();
            		AnalyzeStack.push(TRAN_FI);
            		AnalyzeStack.push(F);
            		AnalyzeStack.push(J);
            	}
            	else
            	{
            		errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined identifier.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
            	}
            	break;
            case 'J'://additive-expression -> term add-exp
            	if (firstWord.getWordValue().equals("(")||firstWord.getWordType().equals(Word.IDENTIFIER)
            		||firstWord.getWordType().equals(Word.INT_CONST)||firstWord.getWordType().equals(Word.CHAR_CONST))
            	{
            		AnalyzeStack.pop();
            		AnalyzeStack.push(ADD_SUB);
            		AnalyzeStack.push(K);//add-exp
            		AnalyzeStack.push(L);//term
            	}
            	else {
            		errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined identifier.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
            	}
            	break;
            case 'K'://add-exp -> addop term add-exp | empty
            	if (firstWord.getWordValue().equals("+")) {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(K);//add-exp
            		AnalyzeStack.push(ADD);
            		AnalyzeStack.push(L);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "+", null));
            	}
            	else if (firstWord.getWordValue().equals("-")) {
            		AnalyzeStack.pop();
            		AnalyzeStack.push(K);//add-exp
            		AnalyzeStack.push(SUB);
            		AnalyzeStack.push(L);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "-", null));
            	}
            	else {
            		AnalyzeStack.pop();
            	}
            	break;
            case 'L'://term -> factor term'
            	if (firstWord.getWordValue().equals("(")||firstWord.getWordType().equals(Word.IDENTIFIER)
            		||firstWord.getWordType().equals(Word.INT_CONST))
            	{
            		AnalyzeStack.pop();
            		AnalyzeStack.push(DIV_MUL);
            		AnalyzeStack.push(L1);
            		AnalyzeStack.push(M);
            	}
            	else
            	{
            		errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined identifier.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
            	}
            	break;
            case '3'://term' -> mulop factor term | empty
            	if (firstWord.getWordValue().equals("*")) 
            	{
            		AnalyzeStack.pop();
            		AnalyzeStack.push(L1);//term'
            		AnalyzeStack.push(MUL);
            		AnalyzeStack.push(M);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "*", null));
            	}
            	else if (firstWord.getWordValue().equals("/")) 
            	{
            		AnalyzeStack.pop();
            		AnalyzeStack.push(L1);//term'
            		AnalyzeStack.push(DIV);
            		AnalyzeStack.push(M);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "/", null));
            	}
            	else{
            		AnalyzeStack.pop();
            	}
            	break;
            case 'M'://factor -> (expression)|ID|NUM
            	if(firstWord.getWordType().equals(Word.IDENTIFIER)){
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "id", null));
                    AnalyzeStack.push(ASS_M);
                }else if(firstWord.getWordType().equals(Word.INT_CONST)){
                	AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "num", null));
                    AnalyzeStack.push(ASS_M);
                }else if(firstWord.getWordValue().equals("(")){
                	AnalyzeStack.pop();
                	AnalyzeStack.push(TRAN_HM);
                	AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ")", null));
                	AnalyzeStack.push(H);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "(", null));
                }
                else{
                    AnalyzeStack.remove(0);
                }
                break;
            case 'Y'://type-specifier -> int | char | bool
                if (firstWord.getWordValue().equals("int")) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "int", null));
                } else if (firstWord.getWordValue().equals("char")) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "char", null));
                } else if (firstWord.getWordValue().equals("bool")) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "bool", null));
                } else {
                    errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined data type.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
                }
                break;
            case 'Z'://identifier-list
                if (firstWord.getWordType().equals(Word.IDENTIFIER)) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(Z1);
                    AnalyzeStack.push(U);
                } else {
                    errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined identifier.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
                }
                break;
            case '1'://Z1, id-list's follower
                if (firstWord.getWordValue().equals(",")) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(Z);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, ",", null));
                } else {
                    AnalyzeStack.pop();
                }
                break;
            case 'U'://an id
                if (firstWord.getWordType().equals(Word.IDENTIFIER)) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(U1);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "id", null));
                    AnalyzeStack.push(ASS_U);//The assignment here is initialization.
                } else {
                    errorIndex++;
                    AnalyzeStack.pop();
                    wordList.remove(0);
                    error = new Error(errorIndex, "Undefined identifier.", firstWord.getWordLine(), firstWord);
                    errorList.add(error);
                    parseErrorFlag = true;
                }
                break;
            case '2'://the "= xxx" behind a declaration
                if (firstWord.getWordValue().equals("=")) {
                    AnalyzeStack.pop();
                    AnalyzeStack.push(EQ_U1);
                    AnalyzeStack.push(L);
                    AnalyzeStack.push(new ParseTreeNode(ParseTreeNode.TERMINAL, "=", null));
                } else {
                    AnalyzeStack.pop();
                }
                break;
        }
    }

    /**
     * Action sign operation.
     */
    private void actionSignOp() {
        if (stackTop.getNodeName().equals("@ADD_SUB")) {
            if (OP != null && (OP.equals("+") || OP.equals("-"))) {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemporary();

                Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, ARG2, RES);
                quadrupleList.add(quadruple);

                J.setNodeValue(RES);
                semanticStack.push(J.getNodeValue());

                OP = null;
            }

            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@ADD")) {
            OP = "+";
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@SUB")) {
            OP = "-";
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@DIV_MUL")) {
            if (OP != null && (OP.equals("*") || OP.equals("/"))) {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemporary();

                Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, ARG2, RES);
                quadrupleList.add(quadruple);

                L.setNodeValue(RES);
                semanticStack.push(L.getNodeValue());

                OP = null;
            }

            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@DIV")) {
            OP = "/";
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@MUL")) {
            OP = "*";
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@TRAN_HM")) {
            M.setNodeValue(H.getNodeValue());
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@ASS_H")) {
            H.setNodeValue(firstWord.getWordValue());
            semanticStack.push(H.getNodeValue());
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@ASS_M")) {
            M.setNodeValue(firstWord.getWordValue());
            semanticStack.push(M.getNodeValue());
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@ASS_U")) {
            U.setNodeValue(firstWord.getWordValue());
            semanticStack.push(U.getNodeValue());
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@SINGLE")) {
            if (for_op.peek() != null) {
                ARG1 = semanticStack.pop();
                RES = ARG1;

                Quadruple quadruple = new Quadruple(++quadrupleCount,for_op.pop(), ARG1, "/", RES);
                quadrupleList.add(quadruple);
            }

            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@SINGLE_OP")) {
            for_op.push(firstWord.getWordValue());
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@EQ")) {
            OP = "=";
            ARG1 = semanticStack.pop();
            RES = semanticStack.pop();

            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, "/", RES);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@EQ_U'")) {
            OP = "=";
            ARG1 = semanticStack.pop();
            RES = semanticStack.pop();;

            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, "/", RES);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@COMPARE")) {
            ARG2 = semanticStack.pop();
            OP = semanticStack.pop();
            ARG1 = semanticStack.pop();
            RES = newTemporary();

            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, ARG2, RES);
            quadrupleList.add(quadruple);

            F.setNodeValue(RES);
            semanticStack.push(F.getNodeValue());

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@COMPARE_OP")) {
            D.setNodeValue(firstWord.getWordValue());
            semanticStack.push(D.getNodeValue());
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@IF_FJ")) {
            OP = "FJ";
            ARG1 = semanticStack.pop();
            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, "/", RES);
            if_fj.push(quadrupleCount);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@IF_BACKPATCH_FJ")) {
            backpatch(if_fj.pop(), quadrupleCount+2);
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@IF_RJ")) {
            OP = "RJ";
            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, "/", "/", "/");
            if_rj.push(quadrupleCount);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@IF_BACKPATCH_RJ")) {
            backpatch(if_rj.pop(), quadrupleCount+1);
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@WHILE_FJ")) {
            OP = "FJ";
            ARG1 = semanticStack.pop();

            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, "/", "/");
            while_fj.push(quadrupleCount);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@WHILE_RJ")) {
            OP = "RJ";
            RES = (while_fj.peek() - 1) + "";

            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, "/", "/", RES);
            for_rj.push(quadrupleCount);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@WHILE_BACKPATCH_FJ")) {
            backpatch(while_fj.pop(),  quadrupleCount+1);
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@FOR_FJ")) {
            OP = "FJ";
            ARG1 = semanticStack.pop();

            Quadruple quadruple = new Quadruple(++quadrupleCount, OP, ARG1, "/", "/");
            for_fj.push(quadrupleCount);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@FOR_RJ")) {
            OP = "RJ";
            RES = (for_fj.peek() - 1) + "";

            Quadruple quadruple=new Quadruple(++quadrupleCount, OP, "/", "/", RES);
            for_rj.push(quadrupleCount);
            quadrupleList.add(quadruple);

            OP = null;
            AnalyzeStack.pop();
        } else if (stackTop.getNodeName().equals("@FOR_BACKPATCH_FJ")) {
            backpatch(for_fj.pop(), quadrupleCount + 1);
            AnalyzeStack.pop();
        }
    }

    /**
     * Back patch.
     * @param i Index.
     * @param res Result.
     */
    private void backpatch(int i, int res) {
        Quadruple tmp = quadrupleList.get(i - 1);
        tmp.result = res + "";
        quadrupleList.set(i - 1, tmp);
    }

    /**
     * Output parser result to destination file.
     * @param outputFolder The output file folder.
     * @throws IOException Fail to create output folder.
     */
    public void outputYaccResult(String outputFolder) throws IOException {
        boolean createFolderFlag = true;
        File file = new File(outputFolder);
        if (!file.exists()) {
            createFolderFlag = file.mkdirs();
        }

        // If the folder is created unsuccessfully, exit the program.
        if (!createFolderFlag) {
            System.err.printf("Error: Failed to create folder \"%s\".\n", outputFolder);
            System.exit(1);
        }

        String outputFilePath = outputFolder + "\\yacc_result.txt";
        FileOutputStream fileOutput = new FileOutputStream(outputFilePath);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
        OutputStreamWriter OutputWriter = new OutputStreamWriter(bufferedOutput, "utf-8");

        PrintWriter printWriter = new PrintWriter(OutputWriter);
        printWriter.println(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());

        printWriter.println("");
        printWriter.println("Error: ");
        printWriter.println("----------------------------------------------------------------------------------------------------");
        if (parseErrorFlag) {
            Error error;
            printWriter.printf("%-10s%-10s%-10s%-20s", "No.", "Word", "Line No.", "Info");
            printWriter.println("");

            for (int i = 0; i < errorList.size(); i++) {
                error = errorList.get(i);
                printWriter.printf("%-10d%-10s%-10d%-20s", error.getErrorId(), error.getErrorWord().getWordValue(),
                        error.getErrorLine(), error.getErrorInfo());
                printWriter.println("");
                System.err.printf("Error (in parser): Line %d, %s, %s\n", error.getErrorLine(), error.getErrorWord().getWordValue(),
                        error.getErrorInfo());
            }

            System.err.println("Error: Failed to parse input text.\n");
            printWriter.println("----------------------------------------------------------------------------------------------------");
            printWriter.println("");
            printWriter.println("----------------------------------------------------------------------------------------------------");
            printWriter.println("Error: Some error occurs during parsing.");
            printWriter.println("----------------------------------------------------------------------------------------------------");
        } else {
            printWriter.println("                                                None");
            printWriter.println("----------------------------------------------------------------------------------------------------");
            printWriter.println("");
            printWriter.println("----------------------------------------------------------------------------------------------------");
            printWriter.println("Info: Successfully parsed input text.");
            printWriter.println("----------------------------------------------------------------------------------------------------");
            System.out.println("Info: Successfully parsed.\n");
        }

        printWriter.close();
    }

    /**
     * Output three-address code as quadruple to destination file.
     * @param outputFolder The output file folder.
     * @throws IOException Fail to create output folder.
     */
    public void outputQuadruple(String outputFolder) throws IOException {
        boolean createFolderFlag = true;
        File file = new File(outputFolder);
        if (!file.exists()) {
            createFolderFlag = file.mkdirs();
        }

        // If the folder is created unsuccessfully, exit the program.
        if (!createFolderFlag) {
            System.err.printf("Error: Failed to create folder \"%s\".\n", outputFolder);
            System.exit(1);
        }

        String outputFilePath = outputFolder + "\\quadruple.txt";
        FileOutputStream fileOutput = new FileOutputStream(outputFilePath);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
        OutputStreamWriter OutputWriter = new OutputStreamWriter(bufferedOutput, "utf-8");

        PrintWriter printWriter = new PrintWriter(OutputWriter);
        printWriter.println("Quadruples: ");
        printWriter.println("--------------------------------------------------");
        printWriter.printf("%-10s%-10s%-10s%-10s%-10s", "No.", "OP", "ARG1", "ARG2", "RES");
        printWriter.println("");

        Quadruple tmp;
        for (int i = 0; i < quadrupleList.size(); i++) {
            tmp = quadrupleList.get(i);
            printWriter.printf("%-10d%-10s%-10s%-10s%-10s", tmp.id, tmp.op, tmp.arg1, tmp.arg2, tmp.result);
            printWriter.println("");
        }

        printWriter.close();
    }

    /**
     * Output target code to destination file.
     * @param outputFolder The output file folder.
     * @throws IOException Fail to create output folder.
     */
    public void outputTargetCode(String outputFolder) throws IOException {
        boolean createFolderFlag = true;
        File file = new File(outputFolder);
        if (!file.exists()) {
            createFolderFlag = file.mkdirs();
        }

        // If the folder is created unsuccessfully, exit the program.
        if (!createFolderFlag) {
            System.err.printf("Error: Failed to create folder \"%s\".\n", outputFolder);
            System.exit(1);
        }

        String outputFilePath = outputFolder + "\\target_code.txt";
        FileOutputStream fileOutput = new FileOutputStream(outputFilePath);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
        OutputStreamWriter OutputWriter = new OutputStreamWriter(bufferedOutput, "utf-8");

        PrintWriter printWriter = new PrintWriter(OutputWriter);
        for (int i = 0; i < quadrupleList.size(); i++) {
            String outputString = "";
            Quadruple quadruple = quadrupleList.get(i);

            // '='
            if (quadruple.op.equals("=")) {
                outputString = "MOV " + quadruple.result + ", " + quadruple.arg1;
            // '++'
            } else if (quadruple.op.equals("++")) {
                outputString = "INC " + quadruple.arg1;
            // '+'
            } else if (quadruple.op.equals("+")) {
                outputString = "ADD " + quadruple.result + ", " + quadruple.arg1;
            // '-'
            } else if (quadruple.op.equals("-")) {
                outputString = "SUB " + quadruple.result + ", " + quadruple.arg1;
            // '*'
            } else if (quadruple.op.equals("*")) {
                outputString = "MUL " + quadruple.result + ", " + quadruple.arg1;
            // '/'
            } else if (quadruple.op.equals("/")) {
                outputString = "DIV " + quadruple.result + ", " + quadruple.arg1;
            // 'JR'
            } else if (quadruple.op.equals("RJ")) {
                outputString = "JMP " + quadruple.result;
            // 'JF'
            } else if (quadruple.op.equals("FJ")) {
                outputString = "JZ " + quadruple.result;
            // '>'
            } else if (quadruple.op.equals(">")) {
                outputString = "JG " + quadruple.result;
            // '<'
            } else if (quadruple.op.equals("<")) {
                outputString = "JL " + quadruple.result;
            }

            printWriter.println(outputString);
        }

        printWriter.close();
    }
}
