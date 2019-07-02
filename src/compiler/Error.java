package compiler;

/**
 * Error word info.
 */
public class Error {
	// Error No..
	private int id ;
	// Error message.
	private String info;
	// Error line No..
	private int line ;
	// Error word.
	private Word word;

	/**
	 * Constructor with no argument.
	 */
	public Error(){	}

	/**
	 * Constructor with error Bo., error info, error line Bo., and error word.
	 * @param id Error No..
	 * @param info Error info.
	 * @param line Error line No..
	 * @param word Error word.
	 */
	public Error(int id, String info, int line, Word word) {
		this.id = id;
		this.info = info;
		this.line = line;
		this.word = word;
	}

	/**
	 * Return `id`.
	 * @return `id`.
	 */
	int getErrorId() {
		return this.id;
	}

	/**
	 * Return `info`.
	 * @return `info`.
	 */
	String getErrorInfo() {
		return this.info;
	}

	/**
	 * Return `line`.
	 * @return `line`.
	 */
	int getErrorLine() {
		return this.line;
	}

	/**
	 * Return `word`.
	 * @return `word`.
	 */
	Word getErrorWord() {
		return this.word;
	}

	/**
	 * Set `id`.
	 * @param id `id`.
	 */
	public void setErrorId(int id) {
		this.id = id;
	}

	/**
	 * Set `info`.
	 * @param info `info`.
	 */
	public void setErrorInfo(String info) {
		this.info = info;
	}

	/**
	 * Set `line`.
	 * @param line `line`.
	 */
	public void setErrorLine(int line) {
		this.line = line;
	}

	/**
	 * Set `word`.
	 * @param word `word`.
	 */
	public void setErrorWord(Word word) {
		this.word = word;
	}
}
