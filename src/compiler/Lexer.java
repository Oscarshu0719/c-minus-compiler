package compiler;

import java.io.PrintWriter;
import java.lang.Character;
import java.util.ArrayList;

public class Lexer
{
    private ArrayList<Word> word_list;
	private ArrayList<Error> error_list;
	private int word_index = 0;
	private int error_index = 0;
	private boolean is_multiple_line_comment = false;
	private boolean is_lex_error = false;

    public Lexer(String str)
    {
        word_list = new ArrayList<>();
        error_list = new ArrayList<>();

        String[] lines = str.split("\n");

        for (int i = 0; i < lines.length; i++)
        {
            Analyze(lines[i].trim(), i+1);
        }

        if(!word_list.get(word_list.size()-1).getWordType().equals(Word.END_SIGN))
        {
            Word word = new Word(++word_index, lines.length+1, "#", Word.END_SIGN);
            word_list.add(word);
        }
    }

    ArrayList ge word_list()
    {
        return this.word_list;
    }

    private static boolean isIdentifier(String word)
    {
        if (!(Character.isLetter(word.charAt(0)) || word.charAt(0) == '_')) return false;
        else
        {
            for(int i = 1; i < word.length; i++)
            {
                if (!(Character.isLetter(word.charAt(i)) || word.charAt(i) == '_' || Character.isDigit(word.charAt(i)))) return false;
            }
        }

        return true;
    }

    private static boolean isInteger(String word)
    {
        int i;
       
        for (i = 0; i < word.length(); i++)
        {
            if (!(Character.isDigit(word.charAt(i)))) return false;
        }

        return true;
    }

    private static boolean isLexError()
    {
        return is_lex_error;
    }

    private static int findEnd(String str, int begin, int length)
    {
        int index = begin;
    
        while ((index < length) && (!Word.isBoundarySign(str.substring(index, index + 1))) &&
                            (!Word.isOperator(str.substring(index, index + 1))) && (str.charAt(index) != ' ') &&
                            (str.charAt(index) != '\t') && (str.charAt(index) != '\r') && (str.charAt(index) != '\n')) {
			index++;}
					
        return index;
    }

    private void Analyze(String str, int num)
    {
        int begin;
        int end;
        int index = 0;
        int length = str.length();
        Word word = null;
        Error error;

        char cur;
        while(index < length)
        {
            cur = str.charAt(index);
            if(!is_multiple_line_comment)
            {
                //Identifier
                if(Character.isLetter(cur) || cur == '_')
                {
                    begin = ++index;	
					end = findEnd(str, begin, length);

                    word = new Word(++word_index, num, str.substring(begin, end));

                    if (Word.isKeyword(word.getWordValue())) 
						word.setWordType(Word.KEY);
					else if (isIdentifier(word.getWordValue())) 
						word.setWordType(Word.IDENTIFIER);
					else 
                    {
						word.setWordType(Word.UNDEFINED);
						word.setWordLegalFlag(false);
						error_index++;
						error = new Error(error_index, "Undefined identifier.", num, word);
						error_list.add(error);
						is_lex_error = true;
					}

					index--;
                }//Integer
                else if(Character.isDigit(cur))
                {
                    begin = index++;
                    end = findEnd(str, begin, length);

                    word = new Word(++word_index, num, str.substring(begin, end));

                    if(isInteger(word.getWordValue()))
                        word.setWordType(Word.INT_CONST);
                    else
                    {
                        word.setWordType(Word.UNDEFINED);
						word.setWordLegalFlag(false);
						error_index++;
						error = new Error(error_index, "Undefined identifier.", num, word);
						error_list.add(error);
						is_lex_error = true;
                    }

                    index--;
                }//Character constant
                else if(String.valueOf(str.charAt(index)).equals("'"))
                {
                    begin = ++index;
                    pos = str.charAt(index);
                    for(;index<length && pos <= 255;index++)
                        if(String.valueOf(str.charAt(index)).equals("'")) break;
                    
                    if(index < length) 
                    {
                        end = index;
                        word = new Word(++word_index, num, str.substring(begin, end),Word.CHAR_CONST);
                        index--;
                    }
                    else
                    {
                        end = index;
                        word = new Word(++word_index, num, str.substring(begin, end),WORD.UNDEFINED);
                        word.setWordLegalFlag(false);

                        error = new Error(error_index, "Undefined identifier.", num, word);
						error_list.add(error);
						is_lex_error = true;

                        index--;
                    }
                }// '='
                else if (pos == '=')
                {
                    begin = index;
                    index++;
                    if(index < length && str.charAt(index) == '=')
                    {
                        end = index + 1;
                        word = new Word(++word_index, num, str.substring(begin, end), WORD.OPERTAOR);
                    }       
                    else
                    {
                        word = new Word(++word_index, num, str.substring(index-1, index), WORD.OPERTAOR);
                        index--;
                    }
                }// '!'
                else if (pos == '!')
                {
                    begin = index;
                    index++;
                    if(index < length && str.charAt(index) == '!')
                    {
                        end = index + 1;
                        word = new Word(++word_index, num, str.substring(begin, end), WORD.OPERTAOR);
                        index++;
                    }       
                    else
                    {
                        word = new Word(++word_index, num, str.substring(index-1, index), WORD.OPERTAOR);
                        index--;
                    }
                }// '&'
                else if (pos == '&')
                {
                    begin = index;
                    index++;
                    if(index < length && str.charAt(index) == '&')
                    {
                        end = index + 1;
                        word = new Word(++word_index, num, str.substring(begin, end), WORD.OPERTAOR);
                    }       
                    else
                    {
                        word = new Word(++word_index, num, str.substring(index-1, index), WORD.OPERTAOR);
                        index--;
                    }
                }// '|'
                else if (pos == '|')
                {
                    begin = index;
                    index++;
                    if(index < length && str.charAt(index) == '|')
                    {
                        end = index + 1;
                        word = new Word(++word_index, num, str.substring(begin, end), WORD.OPERTAOR);
                    }       
                    else
                    {
                        word = new Word(++word_index, num, str.substring(index-1, index), WORD.OPERTAOR);
                        index--;
                    }
                }// '+'
                else if (pos == '+')
                {
                    begin = index;
                    index++;
                    if(index < length && str.charAt(index) == '+')
                    {
                        end = index + 1;
                        word = new Word(++word_index, num, str.substring(begin, end), WORD.OPERTAOR);
                    }       
                    else
                    {
                        word = new Word(++word_index, num, str.substring(index-1, index), WORD.OPERTAOR);
                        index--;
                    }
                }// '-'
                else if (pos == '-')
                {
                    begin = index;
                    index++;
                    if(index < length && str.charAt(index) == '-')
                    {
                        end = index + 1;
                        word = new Word(++word_index, num, str.substring(begin, end), WORD.OPERTAOR);
                    }       
                    else
                    {
                        word = new Word(++word_index, num, str.substring(index-1, index), WORD.OPERTAOR);
                        index--;
                    }
                }// comment
                else if (pos == '/')
                {
                    index++;
                    if (index < length && str.charAt(index) == '/') break;
					else if(index < length && str.charAt(index) == '*') is_multiple_line_comment = true;
                    else word = new Word(++word_index, num, str.substring(index-1, index), WORD.OPERTAOR);
                    index--;
                }//Not identifier, number constant, character constant.
                else
                {
                    switch(pos)
                    {
                        //spaces
                        case ' ': case '\t': case '\r': case '\n':
						    word = null;
						    break;
                        case '[': case ']': case '(': case ')': case '{': case '}': case ',': case '"': case '.': case ';': case '*': case '%': case '>': case '<': case '?': case '#':
                            word = new Word(++word_index, num, String.valueOf(pos));
                            if (Word.isOperator(word.getWordValue())) word.setWordType(Word.OPERATOR);
						    else if (Word.isBoundarySign(word.getWordValue())) word.setWordType(Word.BOUNDARY_SIGN);
                            else word.setWordType(Word.END_SIGN);
                            break;
                        default:
                            word = new Word(++word_index, num, String.valueOf(pos), Word.UNDEFINED);
                            word.setWordLegalFlag(false);

                            error = new Error(error_index, "Undefined identifier.", num, word);
						    error_list.add(error);
						    is_lex_error = true;
                    }
                }
            }
            else
            {
                int i = str.indexOf("*/");
                if (i != -1) 
                {
					is_multiple_line_comment = false;
					index = i + 2;
					continue;
				} 
                else
					break;
            }

            if (!word)
            {
                index++;
                continue;
            }

            word_list.add(word);
            index++;
        }
    }

    public void outputLexResult(PrintWriter printwriter)
    {
        printWriter.println("Legal words:");
		printWriter.println("--------------------------------------------------");
		printWriter.printf("%-10s%-10s%-20s%-10s", "No.", "Word", "Type", "Line No.");
		printWriter.println("");

        Word word;
		for (int i = 0; i < word_list.size(); i++) 
        {
			word = word_list.get(i);
			printWriter.printf("%-10d%-10s%-20s%-10d", word.getWordId(), word.getWordValue(), word.getWordType(), word.getWordLine());
			printWriter.println("");
		}

		printWriter.println("--------------------------------------------------");
		printWriter.println("");
		printWriter.println("Error words:");
		printWriter.println("--------------------------------------------------");

		if (is_lex_error) 
        {
			printWriter.printf("%-10s%-10s%-10s%-20s", "No.", "Word", "Line No.", "Info");
			printWriter.println("");

			Error error;
			for (int i = 0; i < error_list.size(); i++) 
            {
				error = error_list.get(i);
				printWriter.printf("%-10d%-10s%-10d%-20s", error.getErrorId(), error.getErrorWord().getWordValue(), error.getErrorLine(), error.getErrorInfo());
				printWriter.println("");
				System.err.printf("Error (in lexer): Line: %d, %s, %s\n", error.getErrorLine(), error.getErrorWord().getWordValue(), error.getErrorInfo());
			}

			System.err.println("Error: Failed to lexically analyze input text.\n");
			printWriter.println("");
			printWriter.println("--------------------------------------------------");
			printWriter.println("Error: Some error occurs during lexically analysis.");
			printWriter.println("--------------------------------------------------");
		} 
        else 
        {
			printWriter.println("                       None");
			printWriter.println("--------------------------------------------------");
			printWriter.println("");
			printWriter.println("--------------------------------------------------");
			printWriter.println("Info: Successfully lexically analyzed input text.");
			printWriter.println("--------------------------------------------------");
		}

		printWriter.close();
    }
}