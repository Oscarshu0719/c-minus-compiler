import compiler.Lexer;
import compiler.Parser;

import java.io.*;
import java.util.Scanner;

/**
 * Main class.
 */
public class Main {
    /**
     * Program entry.
     * @param args No use.
     */
    public static void main(String[] args) {
        // Get the input file path.
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.next();
        String inputText = "";
        try {
            inputText = readFile(filePath);
        } catch (IOException ex) {
            System.err.println("Error: Failed to read the input file.");
            ex.printStackTrace();
            System.exit(1);
        }

        // Input folder.
        String fileFolder;
        String fileName;
        int index = filePath.lastIndexOf('\\');
        if (index != -1) {
            fileFolder = filePath.substring(0, index);
            fileName = filePath.substring(index + 1);
        } else {
            // Root folder.
            fileFolder = System.getProperty("user.dir");
            fileName = filePath;
        }

        fileFolder += "\\output_" + fileName;
        boolean createFolderFlag = true;
        File file = new File(fileFolder);
        if (!file.exists()) {
            createFolderFlag = file.mkdirs();
        }

        // If the folder is created unsuccessfully, exit the program.
        if (!createFolderFlag) {
            System.err.printf("Error: Failed to create folder \"%s\".\n", fileFolder);
            System.exit(1);
        }

        // Parse.
        Lexer lexAnalyzer = new Lexer(inputText);
        Parser parser = new Parser(lexAnalyzer);

        String outputFilePath;
        FileOutputStream fileOutput;
        BufferedOutputStream bufferedOutput;
        OutputStreamWriter OutputWriter;
        PrintWriter printWriter;

        try {
            outputFilePath = fileFolder + "\\lex_result.txt";
            fileOutput = new FileOutputStream(outputFilePath);
            bufferedOutput = new BufferedOutputStream(fileOutput);
            OutputWriter = new OutputStreamWriter(bufferedOutput, "utf-8");
            printWriter = new PrintWriter(OutputWriter);

            // Lexical analysis.
            lexAnalyzer.outputLexResult(printWriter);

            // Failed to lexically analyze input text.
            if (lexAnalyzer.isLexError()) {
                System.err.println("Error: The program exited due to previous errors.");
                System.exit(0);
            }

            System.out.println("Info: Successfully lexically analyzed.\n");

            outputFilePath = fileFolder + "\\yacc_result.txt";
            fileOutput = new FileOutputStream(outputFilePath);
            bufferedOutput = new BufferedOutputStream(fileOutput);
            OutputWriter = new OutputStreamWriter(bufferedOutput, "utf-8");
            printWriter = new PrintWriter(OutputWriter);

            // Yacc.
            parser.grammarAnalyze();
            parser.outputYaccResult(printWriter);

            // Failed to parse input text.
            if (parser.isParseError()) {
                System.err.println("Error: The program exited due to previous errors.");
                System.exit(0);
            }

            System.out.println("Info: Successfully parsed.\n");

            outputFilePath = fileFolder + "\\quadruple.txt";
            fileOutput = new FileOutputStream(outputFilePath);
            bufferedOutput = new BufferedOutputStream(fileOutput);
            OutputWriter = new OutputStreamWriter(bufferedOutput, "utf-8");
            printWriter = new PrintWriter(OutputWriter);

            // Generate intermediate code.
            parser.outputQuadruple(printWriter);

            System.out.println("Info: Successfully generated intermediate code.\n");

            outputFilePath = fileFolder + "\\target_code.txt";
            fileOutput = new FileOutputStream(outputFilePath);
            bufferedOutput = new BufferedOutputStream(fileOutput);
            OutputWriter = new OutputStreamWriter(bufferedOutput, "utf-8");
            printWriter = new PrintWriter(OutputWriter);

            // Generate target code.
            parser.outputTargetCode(printWriter);

            System.out.println("Info: Successfully generated target code.\n");
        } catch (IOException ex) {
            System.err.println("Error: Failed to open the output file.");
            System.exit(1);
        }
    }

    /**
     * Read file.
     * @param fileName The destination file.
     * @return File text.
     * @throws IOException Cannot read the destination file.
     */
    private static String readFile(String fileName) throws IOException {
        String str;

        StringBuilder stringBuilder = new StringBuilder();
        FileInputStream fileInput = new FileInputStream(fileName);
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        InputStreamReader inputReader = new InputStreamReader(bufferedInput, "UTF-8");

        BufferedReader bufferedReader = new BufferedReader(inputReader);
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str).append('\n');
        }
        bufferedReader.close();

        return stringBuilder.toString();
    }
}
