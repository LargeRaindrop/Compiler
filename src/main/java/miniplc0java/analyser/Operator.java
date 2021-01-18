package miniplc0java.analyser;

import miniplc0java.tokenizer.TokenType;

public class Operator {
    static int priMatrix[][]={
            {1,1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {1,1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {1,1,1,1,-1,1,1,1,1,1,1,1,-1,-1},
            {1,1,1,1,-1,1,1,1,1,1,1,1,-1,-1},
            {-1,-1,-1,-1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,0,0,-1,-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {-1,-1,-1,-1,-1,1,1,1,1,1,1,1,-1,-1},
            {1,1,1,1,-1,1,1,1,1,1,1,1,-1,-1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    static TokenType[] oprList = {TokenType.PLUS, TokenType.MINUS, TokenType.MUL, TokenType.DIV, TokenType.L_PAREN,
            TokenType.R_PAREN, TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE, TokenType.EQ, TokenType.NEQ,
            TokenType.FAN, TokenType.None};

    public static int getPos(TokenType tokenType) {
        for (int i = 0; i < oprList.length - 1; i++) {
            if (oprList[i] == tokenType)
                return i;
        }
        return -1;
    }

    public static int[][] getPriMatrix() {
        return priMatrix;
    }
}
