package miniplc0java;

import miniplc0java.analyser.Analyser;
import miniplc0java.analyser.Function;
import miniplc0java.analyser.Global;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App
{
    static boolean DEBUG = true;
    static String fileName = "calc";
    static String inFile = "libs\\" + fileName + ".txt";
    static String outFile = "result\\" + fileName + ".o0";

    public static void lexer(Tokenizer tokenizer)
    {
        ArrayList<Token> tokens = new ArrayList<Token>();
        try
        {
            while (true)
            {
                Token token = tokenizer.nextToken();
                if (token.getTokenType().equals(TokenType.EOF))
                    break;
                tokens.add(token);
            }
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
        for (Token token : tokens)
            System.out.println(token);
    }

    public static void main(String[] args) throws FileNotFoundException
    {
        File fd;
        if (DEBUG)
            fd = new File(inFile);
        else
            fd = new File(args[0]);

        Scanner scanner = new Scanner(fd);
        StringIter stringiter = new StringIter(scanner);
        Tokenizer tokenizer = new Tokenizer(stringiter);
//        lexer(tokenizer);
        Analyser analyser = new Analyser(tokenizer);
        try {
            analyser.analyse();
        }
        catch (Exception e) {
            System.exit(1);
        }
        List<Global> globals = analyser.getGlobalTable();
        List<Function> funcs = analyser.getFunctionTable();
        System.out.println("Globals: " + globals.size());
        for (Global global: globals)
            System.out.println(global);
        Function _start = analyser.get_start();
        System.out.println("\n" + _start);
        System.out.println("Functions: " + funcs.size());
        for (Function func: funcs)
            System.out.println(func);

        Output output = new Output(globals, funcs, _start);
        output.transfer();
        byte[] ans = output.get_output();

        FileOutputStream fops;
        if (DEBUG)
            fops = new FileOutputStream(outFile);
        else
            fops = new FileOutputStream(args[1]);
        try {
            fops.write(ans);
        }
        catch (Exception e) {
            System.exit(1);
        }
    }
}