import tokenizer.StringIter;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
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
        File fd = new File("text\\hello.txt");
        Scanner scanner = new Scanner(fd);
        StringIter stringiter = new StringIter(scanner);
        Tokenizer tokenizer = new Tokenizer(stringiter);
        lexer(tokenizer);
    }
}