package miniplc0java.analyser;

import miniplc0java.error.*;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.OprType;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    Token peekedToken = null;
    List<Symbol> symbolTable = new ArrayList<>();       // 符号表
    List<Global> globalTable = new ArrayList<>();       // 全局符号表
    List<Function> functionTable = new ArrayList<>();   // 函数表
    Function _start;
    int layer = 1;
    Symbol nowFuntion;
    Symbol returnFunction;
    int nextOffset = 0;
    int globalCount = 0;
    int functionCount = 1;
    int localCount = 0;
    Stack<TokenType> op = new Stack<>();
    ArrayList<Instruction> instructions;
    int inCycle = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void analyse() throws CompileError {
        analyseProgram();
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
            Token token = peekedToken;
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
        Token token = peek();
        return token.getTokenType() == tt;
    }


    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        Token token = peek();
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

    /**
     * 根据符号名字查找当前符号表里是否有该名字，如果有则返回位置，如果没有则返回-1
     *
     * @param name
     * @return
     */
    private int searchSymbolByName(String name) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).getName().equals(name)) 
                return i;
        }
        return -1;
    }

    /**
     * 根据Token在符号表里查是否有与该token名字一样的符号，如果有则返回该符号，没有返回null
     *
     * @param token
     * @return
     */
    private Symbol searchSymbolByToken(Token token) {
        String name = (String) token.getValue();
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).getName().equals(name)) 
                return symbolTable.get(i);
        }
        return null;
    }

    /**
     * 添加一个符号
     *
     * @param name          名字
     * @param type          类型
     * @param isInitialized 是否已赋值
     * @param floor         当前层数，遇到函数则+1
     * @param params        函数的参数列表
     * @param curPos        当前 token 的位置（报错用）
     * @param returnType    函数的返回类型
     * @param isParam       该符号是参数吗
     */
    private void addSymbol(String name, boolean isConst, String type, boolean isInitialized, int floor, List<Symbol> params, String returnType, Pos curPos, int isParam, Symbol function, int localId, int globalId) throws AnalyzeError {
        int same = searchSymbolByName(name);
        if (same == -1)
            this.symbolTable.add(new Symbol(name, isConst, type, isInitialized, getNextVariableOffset(), floor, params, returnType, isParam, function, localId, globalId));
        else {
            Symbol symbol = symbolTable.get(same);
            if (symbol.getLayer() == floor)
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
            this.symbolTable.add(new Symbol(name, isConst, type, isInitialized, getNextVariableOffset(), floor, params, returnType, isParam, function, localId, globalId));
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
        int position = searchSymbolByName(name);
        if (position == -1) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        }
        else {
            Symbol update = symbolTable.get(position);
            update.setInit(true);
        }
    }

    private void operatorInstructions(TokenType calculate, List<Instruction> instructions, String type) throws AnalyzeError{
        Instruction instruction;
        switch (calculate) {
            case FAN:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.negi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.negf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case PLUS:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.addi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.addf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case MINUS:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.subi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.subf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case MUL:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.muli, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.mulf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case DIV:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.divi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.divf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case EQ:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.not, -1);
                instructions.add(instruction);
                break;
            case NEQ:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case LT:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.setlt, -1);
                instructions.add(instruction);
                break;
            case GT:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.setgt, -1);
                instructions.add(instruction);
                break;
            case LE:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.setgt, -1);
                instructions.add(instruction);
                instruction = new Instruction(OprType.not, -1);
                instructions.add(instruction);
                break;
            case GE:
                if(type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if(type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.setlt, -1);
                instructions.add(instruction);
                instruction = new Instruction(OprType.not, -1);
                instructions.add(instruction);
                break;
            default:
                break;
        }

    }

    /**
     * 根据函数名字得到函数存储的id
     * @param name
     * @param functionTable
     * @return
     */
    private int getFunctionId(String name, List<Function> functionTable){
        for (int i=0 ; i<functionTable.size(); i++) {
            if (functionTable.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    /**
     * 判断函数有没有返回值
     * @param name
     * @param functionTable
     * @return
     */
    private boolean functionHasReturn(String name, List<Function> functionTable) {
        if (name.equals("getint") || name.equals("getdouble") || name.equals("getchar"))
            return true;
        for (Function function : functionTable) {
            if (function.getName().equals(name)) {
                if (function.getRetType().equals("int") || function.getRetType().equals("double")) return true;
            }
        }
        return false;
    }

    /**
     * 主程序分析函数
     *
     * @throws CompileError
     */
    private void analyseProgram() throws CompileError {
        instructions = new ArrayList<>();
        // program -> decl_stmt* function*
        // decl_stmt -> let_decl_stmt | const_decl_stmt
        while (check(TokenType.LET_KW) || check(TokenType.CONST_KW))
            analyseDeclStmt();
        
        List<Instruction> initInstructions = instructions;
        // function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
        while (check(TokenType.FN_KW)) {
            instructions = new ArrayList<>();
            analyseFunction();
            globalCount++;
            functionCount++;
        }
        
        int mainLoca = searchSymbolByName("main");
        if (mainLoca == -1) {
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        }

        globalTable.add(new Global(1, 6, "_start"));
        Symbol main = symbolTable.get(mainLoca);
        if (!main.getRetType().equals("void")) {
            initInstructions.add(new Instruction(OprType.stackalloc, 1));
            initInstructions.add(new Instruction(OprType.call, functionCount - 1));
            initInstructions.add(new Instruction(OprType.popn, 1));
        } else {
            initInstructions.add(new Instruction(OprType.stackalloc, 0));
            initInstructions.add(new Instruction(OprType.call, functionCount - 1));
        }
        _start = new Function("_start", globalCount, 0, 0, 0, initInstructions, layer, "void");
        globalCount++;
    }

    /**
     * 声明语句分析函数
     * decl_stmt -> let_decl_stmt | const_decl_stmt
     *
     * @throws CompileError
     */
    private void analyseDeclStmt() throws CompileError {
        if (check(TokenType.LET_KW))
            analyseLetDeclStmt();
        else if (check(TokenType.CONST_KW))
            analyseConstDeclStmt();

        if (layer == 1) globalCount++;
        else localCount++;
    }

    /**
     * let声明语句分析函数
     * let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
     *
     * @throws CompileError
     */
    private void analyseLetDeclStmt() throws CompileError {
        String name;
        boolean isConst = false;
        String type;
        boolean isInitialized = false;
        List<Symbol> params = null;
        String exprType = "";
        Token ident;

        expect(TokenType.LET_KW);
        ident = expect(TokenType.IDENT);
        name = (String) ident.getValue();
        expect(TokenType.COLON);
        type = analyseTy();
        if (type.equals("void"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (check(TokenType.ASSIGN)) {
            isInitialized = true;
            Instruction instruction;

            if (layer == 1) {
                instruction = new Instruction(OprType.globa, globalCount);
                instructions.add(instruction);
            } else {
                instruction = new Instruction(OprType.loca, localCount);
                instructions.add(instruction);
            }
            next();
            exprType = analyseExpr();
            while (!op.empty())
                operatorInstructions(op.pop(), instructions, exprType);

            instruction = new Instruction(OprType.store64, -1);
            instructions.add(instruction);
        }

        expect(TokenType.SEMICOLON);
        if ((isInitialized && exprType.equals(type)) || !isInitialized)
            if (layer == 1)
                addSymbol(name, false, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, -1, globalCount);
            else
                addSymbol(name, false, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, localCount, -1);
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (layer == 1) {
            Global global = new Global(0);
            globalTable.add(global);
        }
    }

    /**
     * const声明语句分析函数
     * const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
     *
     * @throws CompileError
     */
    private void analyseConstDeclStmt() throws CompileError {
        String name;
        String type;
        boolean isInitialized = true;
        List<Symbol> params = null;
        String exprType;
        Token ident;
        Instruction instruction;

        expect(TokenType.CONST_KW);
        ident = expect(TokenType.IDENT);
        name = (String) ident.getValue();
        expect(TokenType.COLON);
        type = analyseTy();
        if (type.equals("void"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (layer == 1) {
            instruction = new Instruction(OprType.globa, globalCount);
            instructions.add(instruction);
        } else {
            instruction = new Instruction(OprType.loca, localCount);
            instructions.add(instruction);
        }
        expect(TokenType.ASSIGN);
        exprType = analyseExpr();
        while (!op.empty())
            operatorInstructions(op.pop(), instructions, exprType);

        instruction = new Instruction(OprType.store64, -1);
        instructions.add(instruction);

        expect(TokenType.SEMICOLON);
        if (exprType.equals(type))
            if (layer == 1)
                addSymbol(name, true, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, -1, globalCount);
            else
                addSymbol(name, true, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, localCount, -1);
        else throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (layer == 1) {
            Global global = new Global(1);
            globalTable.add(global);
        }
    }

    /**
     * 判断声明类型分析函数
     *
     * @return
     * @throws CompileError
     */
    private String analyseTy() throws CompileError {
        Token tt = peek();
        if (tt.getValue().equals("void") || tt.getValue().equals("int") || tt.getValue().equals("double")) {
            next();
        }
        else throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        String type = (String) tt.getValue();
        return type;
    }

    /**
     * 表达式分析函数
     * expr ->
     * operator_expr
     * | negate_expr
     * | assign_expr
     * | as_expr
     * | call_expr
     * | literal_expr
     * | ident_expr
     * | group_expr
     * | group_expr
     * 在operator和as里为了消除左递归，可以将其变为
     * expr -> (binary_operator expr||'as' ty)*
     *
     * @return
     * @throws CompileError
     */
    private String analyseExpr() throws CompileError {
        String exprType = "";

        // negate_expr -> '-' expr
        if (check(TokenType.MINUS))
            exprType = analyseNegateExpr();
        else if (check(TokenType.IDENT)) {
            Token ident = next();
            Symbol symbol = searchSymbolByToken(ident);
            boolean isLibrary = false;
            if (symbol == null) {
                symbol = analyseLibrary((String) ident.getValue());
                if (symbol == null)
                    throw new AnalyzeError(ErrorCode.CompileError, ident.getStartPos());
                isLibrary = true;
            }

            // assign_expr -> l_expr '=' expr
            // l_expr -> IDENT
            if (check(TokenType.ASSIGN))
                exprType = analyseAssignExpr(symbol, ident);
            // call_expr -> IDENT '(' call_param_list? ')'
            else if (check(TokenType.L_PAREN))
                exprType = analyseCallExpr(symbol, ident, isLibrary);
            // ident_expr -> IDENT
            else
                exprType = analyseIdentExpr(symbol, ident);
        }
        // literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL
        else if (check(TokenType.UINT_LITERAL) || check(TokenType.DOUBLE_LITERAL) || check(TokenType.STRING_LITERAL) || check(TokenType.CHAR_LITERAL)) {
            exprType = analyseLiteralExpr();
        }
        // group_expr -> '(' expr ')'
        else if (check(TokenType.L_PAREN))
            exprType = analyseGroupExpr();

        while (check(TokenType.AS_KW) ||
                check(TokenType.PLUS) ||
                check(TokenType.MINUS) ||
                check(TokenType.MUL) ||
                check(TokenType.DIV) ||
                check(TokenType.EQ) ||
                check(TokenType.NEQ) ||
                check(TokenType.LT) ||
                check(TokenType.GT) ||
                check(TokenType.LE) ||
                check(TokenType.GE)) {
            // as_expr -> expr 'as' ty
            if (check(TokenType.AS_KW))
                exprType = analyseAsExpr(exprType);
            // operator_expr -> expr binary_operator expr
            else
                exprType = analyseOperatorExpr(exprType);
        }
        if (!exprType.equals(""))
            return exprType;
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    /**
     * 取反表达式分析函数
     * negate_expr -> '-' expr
     *
     * @return
     * @throws CompileError
     */
    private String analyseNegateExpr() throws CompileError {
        expect(TokenType.MINUS);
        op.push(TokenType.FAN);
        String type = analyseExpr();
        if (!type.equals("int") && !type.equals("double"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (!op.empty()) {
            int in = Operator.getPosition(op.peek());
            int out = Operator.getPosition(TokenType.FAN);
            if (Operator.priority[in][out] > 0)
                operatorInstructions(op.pop(), instructions, type);
        }
        return type;
    }

    /**
     * 赋值表达式分析函数
     * assign_expr -> l_expr '=' expr
     * l_expr -> IDENT
     *
     * @param symbol 该赋值表达式左侧的符号
     * @return
     * @throws CompileError
     */
    private String analyseAssignExpr(Symbol symbol, Token ident) throws CompileError {
        if (symbol.getParamId() != -1) {
            Symbol func = symbol.getFunction();
            if (func.getRetType().equals("int"))
                instructions.add(new Instruction(OprType.arga, 1 + symbol.getParamId()));
            else if (func.getRetType().equals("double"))
                instructions.add(new Instruction(OprType.arga, 1 + symbol.getParamId()));
            else
                instructions.add(new Instruction(OprType.arga, symbol.getParamId()));
        }
        else if (symbol.getParamId() == -1 && symbol.getLayer() != 1) {
            instructions.add(new Instruction(OprType.loca, symbol.getLocalId()));
        }
        else {
            instructions.add(new Instruction(OprType.globa, symbol.getGlobalId()));
        }

        expect(TokenType.ASSIGN);
        String exprType = analyseExpr();
        while (!op.empty())
            operatorInstructions(op.pop(), instructions, exprType);

        if (symbol.isConst)
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        else if (symbol.getType().equals(exprType) && (symbol.getType().equals("int") || symbol.getType().equals("double"))) {
            initializeSymbol(symbol.getName(), peekedToken.getStartPos());
            instructions.add(new Instruction(OprType.store64, -1));
            return "void";
        }
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    /**
     * 函数调用表达式分析函数
     * call_expr -> IDENT '(' call_param_list? ')'
     *
     * @param symbol 函数符号symbol
     * @param ident  函数名对应的token
     * @return
     * @throws CompileError
     */
    private String analyseCallExpr(Symbol symbol, Token ident, boolean isLibrary) throws CompileError {
        Instruction instruction;
        if (isLibrary) {
            String name = symbol.getName();
            globalTable.add(new Global(1, name.length(), name));
            instruction = new Instruction(OprType.callname, globalCount);
            globalCount++;
        }
        else {
            if (!symbol.getType().equals("function"))
                throw new AnalyzeError(ErrorCode.CompileError, ident.getStartPos());
            int id = getFunctionId(symbol.getName(), functionTable);
            instruction = new Instruction(OprType.call, id + 1);
        }

        String name = symbol.getName();
        expect(TokenType.L_PAREN);
        op.push(TokenType.L_PAREN);

        if (functionHasReturn(name, functionTable))
            instructions.add(new Instruction(OprType.stackalloc, 1));
        else
            instructions.add(new Instruction(OprType.stackalloc, 0));

        if (!check(TokenType.R_PAREN)) {
            analyseCallParamList(symbol);
        }
        expect(TokenType.R_PAREN);

        op.pop();

        instructions.add(instruction);
        return symbol.getRetType();
    }

    /**
     * 库函数判断
     *
     * @param name 传入函数名字，判断改名字是否属于库函数
     * @return 如果确实是库函数则返回该symbol对象，如果不是就返回null
     * @throws CompileError
     */
    private Symbol analyseLibrary(String name) throws CompileError {
        List<Symbol> params = new ArrayList<>();
        Symbol param = new Symbol();
        String returnType;

        if (name.equals("getint")) {
            returnType = "int";
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else if (name.equals("getdouble")) {
            returnType = "double";
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else if (name.equals("getchar")) {
            returnType = "int";
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else if (name.equals("putint")) {
            returnType = "void";
            param.setType("int");
            params.add(param);
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else if (name.equals("putdouble")) {
            returnType = "void";
            param.setType("double");
            params.add(param);
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else if (name.equals("putchar")) {
            returnType = "void";
            param.setType("int");
            params.add(param);
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else if (name.equals("putstr")) {
            returnType = "void";
            param.setType("string");
            params.add(param);
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else if (name.equals("putln")) {
            returnType = "void";
            return new Symbol(name, false, "function", true, 0, layer, params, returnType, -1, null, -1, -1);
        } else
            return null;

    }

    /**
     * 函数调用参数分析函数
     * call_param_list -> expr (',' expr)*
     *
     * @param symbol 该函数的符号信息
     * @throws CompileError
     */
    private void analyseCallParamList(Symbol symbol) throws CompileError {
        int i = 0;
        List<Symbol> params = symbol.getParams();
        int paramNum = params.size();

        String type = analyseExpr();
        while (!op.empty() && op.peek() != TokenType.L_PAREN)
            operatorInstructions(op.pop(), instructions, type);

        if (!params.get(i).getType().equals(type))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        i++;

        while (check(TokenType.COMMA)) {
            next();
            type = analyseExpr();
            while (!op.empty() && op.peek() != TokenType.L_PAREN)
                operatorInstructions(op.pop(), instructions, type);

            if (!params.get(i).getType().equals(type))
                throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
            while (!op.empty() && op.peek() != TokenType.L_PAREN)
                operatorInstructions(op.pop(), instructions, type);
            i++;
        }
        if (i != paramNum)
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    private String analyseIdentExpr(Symbol symbol, Token ident) throws CompileError {
        if (!symbol.getType().equals("int") && !symbol.getType().equals("double"))
            throw new AnalyzeError(ErrorCode.CompileError, ident.getStartPos());
        if (symbol.getParamId() != -1) {
            Symbol func = symbol.getFunction();
            if (func.getRetType().equals("int"))
                instructions.add(new Instruction(OprType.arga, 1 + symbol.getParamId()));
            else if (func.getRetType().equals("double"))
                instructions.add(new Instruction(OprType.arga, 1 + symbol.getParamId()));
            else
                instructions.add(new Instruction(OprType.arga, symbol.getParamId()));
        }
        else if (symbol.getParamId() == -1 && symbol.getLayer() != 1) {
            instructions.add(new Instruction(OprType.loca, symbol.getLocalId()));
        }
        else {
            instructions.add(new Instruction(OprType.globa, symbol.getGlobalId()));
        }
        instructions.add(new Instruction(OprType.load64, -1));
        return symbol.getType();
    }

    /**
     * 字面量表达式分析函数
     * literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL
     *
     * @return
     * @throws CompileError
     */
    private String analyseLiteralExpr() throws CompileError {
        if (check(TokenType.UINT_LITERAL)) {
            Token token = next();
            instructions.add(new Instruction(OprType.push, (long) token.getValue()));
            return "int";
        } else if (check(TokenType.DOUBLE_LITERAL)) {
            Token token = next();
            String binary = Long.toBinaryString(Double.doubleToRawLongBits((Double) token.getValue()));
            Instruction instruction = new Instruction(OprType.push, toTen(binary));
            instructions.add(instruction);
            return "double";
        } else if (check(TokenType.STRING_LITERAL)) {
            Token token = next();
            String name = (String) token.getValue();
            globalTable.add(new Global(1, name.length(), name));

            instructions.add(new Instruction(OprType.push, globalCount));
            globalCount++;
            return "string";
        } else if (check(TokenType.CHAR_LITERAL)) {
            Token token = next();
            instructions.add(new Instruction(OprType.push, (Integer) token.getValue()));
            return "int";
        } else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    public static long toTen(String a) {
        long aws = 0;
        long xi = 1;
        for (int i = a.length() - 1; i >= 0; i--) {
            if (a.charAt(i) == '1')
                aws += xi;
            xi *= 2;
        }
        return aws;
    }

    /**
     * 括号表达式分析函数
     * group_expr -> '(' expr ')'
     *
     * @return
     * @throws CompileError
     */
    private String analyseGroupExpr() throws CompileError {
        expect(TokenType.L_PAREN);
        op.push(TokenType.L_PAREN);
        String exprType = analyseExpr();
        expect(TokenType.R_PAREN);

        while (op.peek() != TokenType.L_PAREN)
            operatorInstructions(op.pop(), instructions, exprType);

        op.pop();
        return exprType;
    }

    /**
     * 类型转换表达式分析函数
     * as_expr -> expr 'as' ty
     * 消除左递归后变为
     * expr -> ('as' ty)*
     *
     * @return
     * @throws CompileError
     */
    private String analyseAsExpr(String exprType) throws CompileError {
        expect(TokenType.AS_KW);
        String rightType = analyseTy();
        if (exprType.equals("int") && rightType.equals("double")) {
            instructions.add(new Instruction(OprType.itof, -1));
            return "double";
        }
        else if (exprType.equals("double") && rightType.equals("int")) {
            instructions.add(new Instruction(OprType.ftoi, -1));
            return "int";
        }
        else if (exprType.equals(rightType)) {
            return exprType;
        } else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }


    /**
     * 运算符表达式分析函数
     * operator_expr -> expr binary_operator expr
     * binary_operator -> '+' | '-' | '*' | '/' | '==' | '!=' | '<' | '>' | '<=' | '>='
     * 消除左递归后变为
     * expr -> (binary_operator expr)*
     *
     * @param exprType
     * @return
     * @throws CompileError
     */
    private String analyseOperatorExpr(String exprType) throws CompileError {
        Token token = analyseBinaryOperator();

        if (!op.empty()) {
            int in = Operator.getPosition(op.peek());
            int out = Operator.getPosition(token.getTokenType());
            if (Operator.priority[in][out] > 0)
                operatorInstructions(op.pop(), instructions, exprType);
        }
        op.push(token.getTokenType());

        String type = analyseExpr();
        if (exprType.equals(type) && (exprType.equals("int") || exprType.equals("double")))
            return type;
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    /**
     * 二元运算符分析函数
     *
     * @throws CompileError
     */
    private Token analyseBinaryOperator() throws CompileError {
        if (check(TokenType.AS_KW) ||
                check(TokenType.PLUS) ||
                check(TokenType.MINUS) ||
                check(TokenType.MUL) ||
                check(TokenType.DIV) ||
                check(TokenType.EQ) ||
                check(TokenType.NEQ) ||
                check(TokenType.LT) ||
                check(TokenType.GT) ||
                check(TokenType.LE) ||
                check(TokenType.GE)) {
            return next();
        }
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    /**
     * 函数声明分析函数
     * function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
     *
     * @throws CompileError
     */
    private void analyseFunction() throws CompileError {
        localCount = 0;
        String name;
        List<Symbol> params = new ArrayList<>();
        String returnType = "";
        Token ident;

        expect(TokenType.FN_KW);
        ident = expect(TokenType.IDENT);
        name = (String) ident.getValue();
        expect(TokenType.L_PAREN);
        addSymbol(name, true, "function", true, layer, params, returnType, ident.getStartPos(), -1, null, -1, globalCount);
        Symbol symbol = searchSymbolByToken(ident);
        if (!check(TokenType.R_PAREN))
            analyseFunctionParamList(params, symbol);
        expect(TokenType.R_PAREN);

        expect(TokenType.ARROW);
        returnType = analyseTy();
        symbol.setParams(params);
        symbol.setRetType(returnType);
        nowFuntion = symbol;

        int retSlot = 0;
        if (returnType.equals("int")) retSlot = 1;
        else if (returnType.equals("double")) retSlot = 1;
        Function function = new Function(name, globalCount, retSlot, params.size(), localCount, instructions, layer, returnType);
        functionTable.add(function);

        analyseBlockStmt();

        function.setId(globalCount);
        function.setLocSlots(localCount);
        function.setBody(instructions);
        function.setLayer(layer);

        if (symbol.getRetType().equals("void"))
            instructions.add(new Instruction(OprType.ret, -1));
        else if (!returnFunction.getName().equals(nowFuntion.getName())) {
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        }

        Global global = new Global(1, name.length(), name);
        globalTable.add(global);
    }

    /**
     * 函数参数列表分析函数
     * function_param_list -> function_param (',' function_param)*
     *
     * @param params 函数参数列表，填入该参数列表
     * @throws CompileError
     */
    private void analyseFunctionParamList(List<Symbol> params, Symbol symbol) throws CompileError {
        int i = 0;
        params.add(analyseFunctionParam(i, symbol));
        while (check(TokenType.COMMA)) {
            next();
            params.add(analyseFunctionParam(++i, symbol));
        }
    }

    /**
     * 函数参数分析函数
     * function_param -> 'const'? IDENT ':' ty
     *
     * @return
     * @throws CompileError
     */
    private Symbol analyseFunctionParam(int i, Symbol symbol) throws CompileError {
        String name;
        boolean isConst = false;
        String type;
        boolean isInitialized = false;
        Token ident;

        if (check(TokenType.CONST_KW)) {
            isConst = true;
            next();
        }
        ident = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        type = analyseTy();
        name = (String) ident.getValue();
        addSymbol(name, isConst, type, isInitialized, layer + 1, null, "", ident.getStartPos(), i, symbol, -1, -1);
        return searchSymbolByToken(ident);
    }

    /**
     * 代码块分析函数
     * block_stmt -> '{' stmt* '}'
     *
     * @throws CompileError
     */
    private void analyseBlockStmt() throws CompileError {
        layer++;
        expect(TokenType.L_BRACE);
        while (!check(TokenType.R_BRACE))
            analyseStmt();
        expect(TokenType.R_BRACE);

        for (int i = symbolTable.size() - 1; symbolTable.get(i).getLayer() == layer; i--)
            symbolTable.remove(i);
        layer--;
    }

    /**
     * 语句分析函数
     * stmt ->
     * expr_stmt
     * | decl_stmt
     * | if_stmt
     * | while_stmt
     * | return_stmt
     * | block_stmt
     * | empty_stmt
     *
     * @throws CompileError
     */
    private void analyseStmt() throws CompileError {
        //decl_stmt -> let_decl_stmt | const_decl_stmt
        if (check(TokenType.LET_KW) || check(TokenType.CONST_KW))
            analyseDeclStmt();
        // if_stmt -> 'if' expr block_stmt ('else' (block_stmt | if_stmt))?
        else if (check(TokenType.IF_KW))
            analyseIfStmt();
        // while_stmt -> 'while' expr block_stmt
        else if (check(TokenType.WHILE_KW))
            analyseWhileStmt();
        // return_stmt -> 'return' expr? ';'
        else if (check(TokenType.RETURN_KW))
            analyseReturnStmt();
        // block_stmt -> '{' stmt* '}'
        else if (check(TokenType.L_BRACE))
            analyseBlockStmt();
        // empty_stmt -> ';'
        else if (check(TokenType.SEMICOLON))
            analyseEmptyStmt();
        // break_stmt -> 'break' ';'
        else if (check(TokenType.BREAK_KW)) {}
        // continue_stmt -> 'continue' ';'
        else if (check(TokenType.CONTINUE_KW)) {}
        // expr_stmt -> expr ';'
        else
            analyseExprStmt();
    }

    /**
     * if语句分析函数
     * if_stmt -> 'if' expr block_stmt ('else' (block_stmt | if_stmt))?
     *
     * @throws CompileError
     */
    private void analyseIfStmt() throws CompileError {
        expect(TokenType.IF_KW);
        String type = analyseExpr();
        while (!op.empty())
            operatorInstructions(op.pop(), instructions, type);

        if (!type.equals("int") && !type.equals("double"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        instructions.add(new Instruction(OprType.brtrue, 1));
        Instruction jump = new Instruction(OprType.br, 0);
        instructions.add(jump);
        int index = instructions.size();

        analyseBlockStmt();
        int size = instructions.size();

        if (instructions.get(size - 1).getOp().equals("ret")) {
            int distance = instructions.size() - index;
            jump.setX(distance);

            if (check(TokenType.ELSE_KW)) {
                expect(TokenType.ELSE_KW);
                if (check(TokenType.L_BRACE)) {
                    analyseBlockStmt();
                    if (!instructions.get(size - 1).getOp().equals("ret"))
                        instructions.add(new Instruction(OprType.br, 0));
                } else if (check(TokenType.IF_KW))
                    analyseIfStmt();
            }
        } else {
            Instruction jumpInstruction = new Instruction(OprType.br, -1);
            instructions.add(jumpInstruction);
            int j = instructions.size();

            int distance = j - index;
            jump.setX(distance);

            if (check(TokenType.ELSE_KW)) {
                expect(TokenType.ELSE_KW);
                if (check(TokenType.L_BRACE)) {
                    analyseBlockStmt();
                    instructions.add(new Instruction(OprType.br, 0));
                } else if (check(TokenType.IF_KW))
                    analyseIfStmt();
            }
            distance = instructions.size() - j;
            jumpInstruction.setX(distance);
        }

    }

    /**
     * while语句分析函数
     * while_stmt -> 'while' expr block_stmt
     *
     * @throws CompileError
     */
    private void analyseWhileStmt() throws CompileError {
        expect(TokenType.WHILE_KW);

        instructions.add(new Instruction(OprType.br, 0));
        int whileStart = instructions.size();

        String type = analyseExpr();
        while (!op.empty())
            operatorInstructions(op.pop(), instructions, type);

        if (!type.equals("int") && !type.equals("double"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        instructions.add(new Instruction(OprType.brtrue, 1));
        Instruction jumpInstruction = new Instruction(OprType.br, 0);
        instructions.add(jumpInstruction);
        int index = instructions.size();

        inCycle++;
        analyseBlockStmt();
        if (inCycle > 0) inCycle--;

        Instruction instruction = new Instruction(OprType.br, 0);
        instructions.add(instruction);
        int whileEnd = instructions.size();
        instruction.setX(whileStart - whileEnd);

        jumpInstruction.setX(whileEnd - index);
    }


    /**
     * return语句分析函数
     * return_stmt -> 'return' expr? ';'
     *
     * @throws CompileError
     */
    private void analyseReturnStmt() throws CompileError {
        expect(TokenType.RETURN_KW);
        String type = "void";

        if (!nowFuntion.getRetType().equals("void")) {
            instructions.add(new Instruction(OprType.arga, 0));

            type = analyseExpr();
            while (!op.empty())
                operatorInstructions(op.pop(), instructions, type);

            instructions.add(new Instruction(OprType.store64, -1));
        }

        if (!check(TokenType.SEMICOLON))
            type = analyseExpr();

        if (!type.equals(nowFuntion.getRetType()))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        expect(TokenType.SEMICOLON);
        returnFunction = nowFuntion;

        while (!op.empty())
            operatorInstructions(op.pop(), instructions, type);
        instructions.add(new Instruction(OprType.ret, -1));
    }

    /**
     * 空语句分析函数
     * empty_stmt -> ';'
     *
     * @throws CompileError
     */
    private void analyseEmptyStmt() throws CompileError {
        expect(TokenType.SEMICOLON);
    }

    /**
     * 表达式语句
     * expr_stmt -> expr ';
     *
     * @throws CompileError
     */
    private void analyseExprStmt() throws CompileError {
        String exprType = analyseExpr();
        while (!op.empty())
            operatorInstructions(op.pop(), instructions, exprType);
        expect(TokenType.SEMICOLON);
    }

    public List<Global> getGlobalTable() {
        return globalTable;
    }

    public void setGlobalTable(List<Global> globalTable) {
        this.globalTable = globalTable;
    }

    public List<Function> getFunctionTable() {
        return functionTable;
    }

    public void setFunctionTable(List<Function> functionTable) {
        this.functionTable = functionTable;
    }

    public Function get_start() {
        return _start;
    }

    public void set_start(Function _start) {
        this._start = _start;
    }

}
