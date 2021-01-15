package tokenizer;

import error.ErrorCode;
import error.TokenizeError;
import util.Pos;

import java.util.regex.*;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isAlphabetic(peek) || peek == '_')
            return lexIdentOrKeyword();
        else if (Character.isDigit(peek))
            return lexUIntOrDouble();
        else if (peek == '"')
            return lexString();
        else if (peek == '\'')
            return lexChar();
        else
            return lexOperatorOrAnnotationOrUnknown();
    }

    private Token lexUIntOrDouble() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
        Pos startPos = it.currentPos();
        StringBuilder str = new StringBuilder();
        while (Character.isDigit(it.peekChar()) ||
                it.peekChar() == 'e' ||
                it.peekChar() == 'E' ||
                it.peekChar() == '+' ||
                it.peekChar() == '-' ||
                it.peekChar() == '.'
        )
            str.append(it.nextChar());
        String strr = str.toString();
        if (Pattern.matches("\\d+", strr))
            return new Token(TokenType.UINT_LITERAL, Long.parseLong(strr), startPos, it.currentPos());
        else if (Pattern.matches("\\d+.\\d+([eE][-+]?\\d+)?", strr))
            return new Token(TokenType.DOUBLE_LITERAL, Double.parseDouble(strr), startPos, it.currentPos());
        else
            throw new TokenizeError(ErrorCode.InvalidInput, startPos);
    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        Pos startPos = it.currentPos();
        StringBuilder str = new StringBuilder();
        while (Character.isDigit(it.peekChar()) ||
                Character.isAlphabetic(it.peekChar()) ||
                it.peekChar() == '_') {
            str.append(it.nextChar());
        }
        String strr = str.toString();
        Token token = new Token(null, strr, startPos, it.currentPos());
        switch (strr) {
            case "fn":
                token.setTokenType(TokenType.FN_KW);
                break;
            case "let":
                token.setTokenType(TokenType.LET_KW);
                break;
            case "const":
                token.setTokenType(TokenType.CONST_KW);
                break;
            case "as":
                token.setTokenType(TokenType.AS_KW);
                break;
            case "while":
                token.setTokenType(TokenType.WHILE_KW);
                break;
            case "if":
                token.setTokenType(TokenType.IF_KW);
                break;
            case "else":
                token.setTokenType(TokenType.ELSE_KW);
                break;
            case "return":
                token.setTokenType(TokenType.RETURN_KW);
                break;
            case "break":
                token.setTokenType(TokenType.BREAK_KW);
                break;
            case "continue":
                token.setTokenType(TokenType.CONTINUE_KW);
                break;
            default:
                token.setTokenType(TokenType.IDENT);
        }
        return token;
    }

    private Token lexOperatorOrAnnotationOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.PLUS, '+', it.previousPos(), it.currentPos());
            case '-':
                if (it.peekChar() == '>') {
                    it.nextChar();
                    return new Token(TokenType.ARROW, "->", it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.MINUS, '-', it.previousPos(), it.currentPos());
            case '*':
                return new Token(TokenType.MUL, '*', it.previousPos(), it.currentPos());
            case '/':
                if (it.peekChar() == '/') {
                    it.nextChar();
                    while (it.nextChar() != '\n') ;
                    return nextToken();
                }
                return new Token(TokenType.DIV, '/', it.previousPos(), it.currentPos());
            case '=':
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.EQ, "==", it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.ASSIGN, '=', it.previousPos(), it.currentPos());
            case '!':
                if (it.nextChar() == '=')
                    return new Token(TokenType.NEQ, "!=", it.previousPos(), it.currentPos());
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            case '<':
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.LE, "<=", it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.LT, '<', it.previousPos(), it.currentPos());
            case '>':
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.GE, ">=", it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.GT, '>', it.previousPos(), it.currentPos());
            case '(':
                return new Token(TokenType.L_PAREN, '(', it.previousPos(), it.currentPos());
            case ')':
                return new Token(TokenType.R_PAREN, ')', it.previousPos(), it.currentPos());
            case '{':
                return new Token(TokenType.L_BRACE, '{', it.previousPos(), it.currentPos());
            case '}':
                return new Token(TokenType.R_BRACE, '}', it.previousPos(), it.currentPos());
            case ',':
                return new Token(TokenType.COMMA, ',', it.previousPos(), it.currentPos());
            case ':':
                return new Token(TokenType.COLON, ':', it.previousPos(), it.currentPos());
            case ';':
                return new Token(TokenType.SEMICOLON, ';', it.previousPos(), it.currentPos());
            default:
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private Token lexString() throws TokenizeError {
        Pos startPos = it.currentPos();
        StringBuilder str = new StringBuilder();
        it.nextChar();
        while (true) {
            char ch = it.nextChar();
            if (ch == '\\') {
                ch = it.nextChar();
                switch (ch) {
                    case '\\':
                        str.append('\\');
                        break;
                    case '"':
                        str.append('"');
                        break;
                    case '\'':
                        str.append('\'');
                        break;
                    case 'n':
                        str.append('\n');
                        break;
                    case 'r':
                        str.append('\r');
                        break;
                    case 't':
                        str.append('\t');
                        break;
                    default:
                        throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }
            } else if (ch != '"')
                str.append(ch);
            else
                break;
        }
        return new Token(TokenType.STRING_LITERAL, str.toString(), startPos, it.currentPos());
    }

    private Token lexChar() throws TokenizeError {
        Pos startPos = it.currentPos();
        it.nextChar();
        char ch = it.nextChar();
        int value;
        if (ch == '\\') {
            ch = it.nextChar();
            switch (ch) {
                case '\\':
                    value = '\\';
                    break;
                case '"':
                    value = '"';
                    break;
                case '\'':
                    value = '\'';
                    break;
                case 'n':
                    value = '\n';
                    break;
                case 'r':
                    value = '\r';
                    break;
                case 't':
                    value = '\t';
                    break;
                default:
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            }
        } else if (ch != '\'')
            value = ch;
        else
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        ch = it.nextChar();
        if (ch != '\'')
            throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        return new Token(TokenType.CHAR_LITERAL, value, startPos, it.currentPos());
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
