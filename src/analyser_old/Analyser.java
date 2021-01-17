package analyser_old;

import error.AnalyzeError;
import error.CompileError;
import error.ErrorCode;
import error.ExpectedTokenError;
import error.TokenizeError;
import instruction.Instruction;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;
import util.Pos;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    Token peekedToken = null;
    ArrayList<Instruction> instructions = new ArrayList<>();
    List<Symbol> symbolTable = new ArrayList<>();
    List<Global> globalTable = new ArrayList<>();
    List<Function> funcTable = new ArrayList<>();
    int symbolCnt = 0;
    int globalCnt = 0;
    int funcCnt = 0;
    int localCnt = 0;
    int nextOffset = 0;
    int layer = 0;
    Stack<TokenType> op = new Stack<>();

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        return instructions;
    }

    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     *
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     *
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 获取下一个变量的栈偏移
     *
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    private Symbol getSymbolByName(String name) {
        for (int i = symbolCnt - 1; i >= 0; i--) {
            if (symbolTable.get(i).getName().equals(name))
                return symbolTable.get(i);
        }
        return null;
    }

    private Global getGlobalByName(String name) {
        for (int i = globalCnt - 1; i >= 0; i--) {
            if (globalTable.get(i).getName().equals(name))
                return globalTable.get(i);
        }
        return null;
    }

    private Function getFunctionByName(String name) {
        for (int i = funcCnt - 1; i >= 0; i--) {
            if (funcTable.get(i).getName().equals(name))
                return funcTable.get(i);
        }
        return null;
    }

    /**
     * 添加一个符号
     *
     * @param name    名字
     * @param isInit  是否已赋值
     * @param isConst 是否是常量
     * @param curPos  当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private void addSymbol(String name, VarType type, boolean isInit, boolean isConst, int layer,
                           int localId, int paramId, Function function, Pos curPos)
            throws AnalyzeError {
        Symbol namesakeSym = getSymbolByName(name);
        if (namesakeSym != null && namesakeSym.getLayer() == layer)
            throw new AnalyzeError(ErrorCode.CompileError, curPos);
        else {
            symbolTable.add(new Symbol(name, type, isConst, isInit, layer, localId, paramId, function));
            symbolCnt++;
        }
    }

    private void addGlobal(String name, VarType type, boolean isInit, boolean isConst, Pos curPos,
                           int globalId) throws AnalyzeError {
        if (getGlobalByName(name) != null)
            throw new AnalyzeError(ErrorCode.CompileError, curPos);
        else {
            globalTable.add(new Global(name, type, isConst, isInit, globalId));
            globalCnt++;
        }
    }

    private void addFunction(String name, VarType retType, int retSlots, int paramSlots, int locSlots, List<Instruction> body, List<Parameter> params, Pos curPos) throws AnalyzeError {
        if (getFunctionByName(name) != null || getGlobalByName(name) != null)
            throw new AnalyzeError(ErrorCode.CompileError, curPos);
        else {
            funcTable.add(new Function(name, retType, retSlots, paramSlots, locSlots, body, params));
            funcCnt++;
        }
    }

    /**
     * 设置符号为已赋值
     *
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            entry.setInitialized(true);
        }
    }

    /**
     * 获取变量在栈上的偏移
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    /**
     * 获取变量是否是常量
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }

    private void clearInstructions() {
        instructions.clear();
    }

    private void analyseProgram() throws CompileError {
        // program -> (decl_stmt | function)*
        while (!check(TokenType.EOF)) {
            // decl_stmt -> let_decl_stmt | const_decl_stmt
            if (check(TokenType.LET_KW) || check(TokenType.CONST_KW)) {
                analyseDeclStmt();
            }
            // function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
            else if (check(TokenType.FN_KW)) {
                analyseFunction();
            } else
                throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        }
        Function main = getFunctionByName("main");
        if (main == null)
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        clearInstructions();
        if (main.getRetType() != VarType.Void) {
            //加载地址
            instructions.add(new Instruction("stackalloc", 1));
            instructions.add(new Instruction("call", funcCnt - 1));
            instructions.add(new Instruction("popn", 1));
        } else {
            //加载地址
            instructions.add(new Instruction("stackalloc", 0));
            instructions.add(new Instruction("call", funcCnt - 1));
        }
        addFunction("_start", VarType.Void, 0, 0, 0, instructions, null, peekedToken.getStartPos());
        clearInstructions();
    }

    private void analyseDeclStmt() throws CompileError {
        // decl_stmt -> let_decl_stmt | const_decl_stmt
        if (check(TokenType.LET_KW))
            analyseLetDeclStmt();
        else if (check(TokenType.CONST_KW))
            analyseConstDeclStmt();
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    private void analyseLetDeclStmt() throws CompileError {
        // let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
        String name;
        VarType type, exprType = VarType.None;
        boolean isInit = false;
        Token ident;

        expect(TokenType.LET_KW);
        ident = expect(TokenType.IDENT)
        name = (String) ident.getValue();
        expect(TokenType.COLON);
        type = analyseTy();
        if (type == VarType.Void)
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        if (check(TokenType.ASSIGN)) {
            isInit = true;
            if (layer == 0) {
                instructions.add(new Instruction("globa", globalCnt));
            } else {
                instructions.add(new Instruction("loca", localCnt))
            }
            next();
            exprType = analyseExpr();
            while (!op.empty())
                operatorInstructions(op.pop(), instructions, exprType);
            instructions.add(new Instruction("store.64", -1));
        }
        expect(TokenType.SEMICOLON);
        if ((isInit && exprType == type) || !isInit) {
            if (layer == 0)
                addGlobal(name, type, isInit, false, ident.getStartPos());
            else
                addSymbol(name, type, isInit, false, layer, ident.getStartPos());
        } else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    private VarType analyseTy() throws CompileError {
        Token token = peek();
        VarType ret;
        if (token.getValue().equals("void"))
            ret = VarType.Void;
        else if (token.getValue().equals("int"))
            ret = VarType.Int;
        else if (token.getValue().equals("double"))
            ret = VarType.Double;
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        next();
        return ret;
    }

    private VarType analyseExpr() throws CompileError {
        VarType exprType = VarType.None;

        //negate_expr -> '-' expr
        if (check(TokenType.MINUS))
            exprType =
    }
}
