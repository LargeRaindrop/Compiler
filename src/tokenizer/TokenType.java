package tokenizer;

public enum TokenType {
    FN_KW,          // fn
    LET_KW,         // let
    CONST_KW,       // const
    AS_KW,          // as
    WHILE_KW,       // while
    IF_KW,          // if
    ELSE_KW,        // else
    RETURN_KW,      // return
    BREAK_KW,       // break
    CONTINUE_KW,    // continue

    UINT_LITERAL,   // 无符号整数
    STRING_LITERAL, // 字符串常量
    DOUBLE_LITERAL, // 浮点数常量
    CHAR_LITERAL,   // 字符常量

    PLUS,           // +
    MINUS,          // -
    MUL,            // *
    DIV,            // /
    ASSIGN,         // =
    EQ,             // ==
    NEQ,            // !=
    LT,             // <
    GT,             // >
    LE,             // <=
    GE,             // >=
    L_PAREN,        // (
    R_PAREN,        // )
    L_BRACE,        // {
    R_BRACE,        // }
    ARROW,          // ->
    COMMA,          // ,
    COLON,          // :
    SEMICOLON,      // ;

    FAN,            // 取反
    COMMENT,        // 注释
    IDENT,          // 标识符
    None,
    EOF;

    @Override
    public String toString() {
        switch (this) {
            case FN_KW:
                return "fn";
            case LET_KW:
                return "let";
            case CONST_KW:
                return "const";
            case AS_KW:
                return "as";
            case WHILE_KW:
                return "while";
            case IF_KW:
                return "if";
            case ELSE_KW:
                return "else";
            case RETURN_KW:
                return "return";
            case BREAK_KW:
                return "break";
            case CONTINUE_KW:
                return "continue";

            case UINT_LITERAL:
                return "uint";
            case STRING_LITERAL:
                return "string";
            case DOUBLE_LITERAL:
                return "double";
            case CHAR_LITERAL:
                return "char";

            case PLUS:
                return "PlusSign";
            case MINUS:
                return "MinusSign";
            case MUL:
                return "MulSign";
            case DIV:
                return "DivSign";
            case ASSIGN:
                return "AssignSign";
            case EQ:
                return "EQSign";
            case NEQ:
                return "NEQSign";
            case LT:
                return "LTSign";
            case GT:
                return "GTSign";
            case LE:
                return "LESign";
            case GE:
                return "GESign";
            case L_PAREN:
                return "LeftBracket";
            case R_PAREN:
                return "RightBracket";
            case L_BRACE:
                return "LeftBigBracket";
            case R_BRACE:
                return "RightBigBracket";
            case ARROW:
                return "ArrowSign";
            case COMMA:
                return "CommaSign";
            case COLON:
                return "ColonSign";
            case SEMICOLON:
                return "SemicolonSign";

            case FAN:
                return "fan";
            case COMMENT:
                return "Comment";
            case IDENT:
                return "Identifier";
            case None:
                return "NullToken";
            case EOF:
                return "EOF";

            default:
                return "InvalidToken";
        }
    }
}
