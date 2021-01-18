package miniplc0java;

import miniplc0java.analyser.Analyser;
import miniplc0java.analyser.Function;
import miniplc0java.analyser.Global;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App
{
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
//        System.exit(1);
        File fd = new File("libs\\calc.txt");
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
        System.out.println("\n" + analyser.get_start());
        System.out.println("Functions: " + funcs.size());
        for (Function func: funcs)
            System.out.println(func);
    }
}