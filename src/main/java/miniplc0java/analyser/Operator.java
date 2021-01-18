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

    public static int getPos(TokenType tokenType){
        if(tokenType == TokenType.PLUS){
            return 0;
        }
        else if(tokenType == TokenType.MINUS){
            return 1;
        }
        else if(tokenType == TokenType.MUL){
            return 2;
        }
        else if(tokenType == TokenType.DIV){
            return 3;
        }
        else if(tokenType == TokenType.L_PAREN){
            return 4;
        }
        else if(tokenType == TokenType.R_PAREN){
            return 5;
        }
        else if(tokenType == TokenType.LT){
            return 6;
        }
        else if(tokenType == TokenType.GT){
            return 7;
        }
        else if(tokenType == TokenType.LE){
            return 8;
        }
        else if(tokenType == TokenType.GE){
            return 9;
        }
        else if(tokenType == TokenType.EQ){
            return 10;
        }
        else if(tokenType == TokenType.NEQ){
            return 11;
        }
        else if(tokenType == TokenType.FAN){
            return 12;
        }
        return -1;
    }

    public static int[][] getPriMatrix() {
        return priMatrix;
    }
}
