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
    ArrayList<Instruction> instructions;
    List<Symbol> symbolTable = new ArrayList<>();   // 符号表
    List<Global> globalTable = new ArrayList<>();   // 全局符号表
    List<Function> funcTable = new ArrayList<>();   // 函数表
    Stack<TokenType> op = new Stack<>();
    Function _start;
    Symbol nowFunc;
    Symbol retFunc;
    int layer = 1;
    int nextOffset = 0;
    int globalCnt = 0;
    int funcCnt = 1;
    int localCnt = 0;
    int inCycle = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void analyse() throws CompileError {
        analyseProgram();
    }

    private Token peek() throws TokenizeError {
        return _peek();
    }

    private Token _peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    private Token next() throws TokenizeError {
        return _next();
    }

    private Token _next() throws TokenizeError {
        if (peekedToken != null) {
            Token token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    private boolean check(TokenType tt) throws TokenizeError {
        return _check(tt);
    }

    private boolean _check(TokenType tt) throws TokenizeError {
        Token token = peek();
        return token.getTokenType() == tt;
    }

    private Token expect(TokenType tt) throws CompileError {
        return _expect(tt);
    }

    private Token _expect(TokenType tt) throws CompileError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    private Symbol getSymbolByToken(Token token) {
        String name = (String) token.getValue();
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).getName().equals(name))
                return symbolTable.get(i);
        }
        return null;
    }

    private int getSymbolByName(String name) {
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).getName().equals(name))
                return i;
        }
        return -1;
    }

    private void addSymbol(String name, boolean isConst, String type, boolean isInitialized, int layer, List<Symbol> params, String returnType, Pos curPos, int isParam, Symbol function, int localId, int globalId) throws AnalyzeError {
        int same = getSymbolByName(name);
        if (same == -1)
            this.symbolTable.add(new Symbol(name, isConst, type, isInitialized, getNextVariableOffset(), layer, params, returnType, isParam, function, localId, globalId));
        else {
            Symbol symbol = symbolTable.get(same);
            if (symbol.getLayer() == layer)
                throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
            this.symbolTable.add(new Symbol(name, isConst, type, isInitialized, getNextVariableOffset(), layer, params, returnType, isParam, function, localId, globalId));
        }
    }

    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        int position = getSymbolByName(name);
        if (position == -1) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            Symbol update = symbolTable.get(position);
            update.setInit(true);
        }
    }

    private void generateOprIns(TokenType calculate, List<Instruction> instructions, String type) throws AnalyzeError {
        Instruction instruction;
        switch (calculate) {
            case FAN:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.negi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.negf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case PLUS:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.addi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.addf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case MINUS:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.subi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.subf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case MUL:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.muli, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.mulf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case DIV:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.divi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.divf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case EQ:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.not, -1);
                instructions.add(instruction);
                break;
            case NEQ:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);
                break;
            case LT:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.setlt, -1);
                instructions.add(instruction);
                break;
            case GT:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if (type.equals("double"))
                    instruction = new Instruction(OprType.cmpf, -1);
                else
                    throw new AnalyzeError(ErrorCode.InvalidInput);
                instructions.add(instruction);

                instruction = new Instruction(OprType.setgt, -1);
                instructions.add(instruction);
                break;
            case LE:
                if (type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if (type.equals("double"))
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
                if (type.equals("int"))
                    instruction = new Instruction(OprType.cmpi, -1);
                else if (type.equals("double"))
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

    private int getFuncId(String name, List<Function> functionTable) {
        for (int i = 0; i < functionTable.size(); i++) {
            if (functionTable.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    private boolean funcRet(String name, List<Function> functionTable) {
        if (name.equals("getint") || name.equals("getdouble") || name.equals("getchar"))
            return true;
        for (Function function : functionTable) {
            if (function.getName().equals(name)) {
                if (function.getRetType().equals("int") || function.getRetType().equals("double")) return true;
            }
        }
        return false;
    }

    private static long convertToDec(String a) {
        long aws = 0;
        long xi = 1;
        for (int i = a.length() - 1; i >= 0; i--) {
            if (a.charAt(i) == '1')
                aws += xi;
            xi *= 2;
        }
        return aws;
    }

    private void initInstructions() {
        instructions = new ArrayList<>();
    }

    private void analyseProgram() throws CompileError {
        initInstructions();
        // program -> decl_stmt* function*
        // decl_stmt -> let_decl_stmt | const_decl_stmt
        while (check(TokenType.LET_KW) || check(TokenType.CONST_KW))
            analyseDeclStmt();

        List<Instruction> initInstructions = instructions;
        // function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
        while (check(TokenType.FN_KW)) {
            initInstructions();
            analyseFunction();
            globalCnt++;
            funcCnt++;
        }

        int mainLoca = getSymbolByName("main");
        if (mainLoca == -1) {
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        }

        globalTable.add(new Global(1, 6, "_start"));
        Symbol main = symbolTable.get(mainLoca);
        if (!main.getRetType().equals("void")) {
            initInstructions.add(new Instruction(OprType.stackalloc, 1));
            initInstructions.add(new Instruction(OprType.call, funcCnt - 1));
            initInstructions.add(new Instruction(OprType.popn, 1));
        } else {
            initInstructions.add(new Instruction(OprType.stackalloc, 0));
            initInstructions.add(new Instruction(OprType.call, funcCnt - 1));
        }
        _start = new Function("_start", globalCnt, 0, 0, 0, initInstructions, layer, "void");
        globalCnt++;
    }

    private void analyseDeclStmt() throws CompileError {
        // decl_stmt -> let_decl_stmt | const_decl_stmt
        if (check(TokenType.LET_KW))
            analyseLetDeclStmt();
        else if (check(TokenType.CONST_KW))
            analyseConstDeclStmt();

        if (layer == 1) globalCnt++;
        else localCnt++;
    }

    private void analyseLetDeclStmt() throws CompileError {
        // let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
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
        type = analyseType();
        if (type.equals("void"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (check(TokenType.ASSIGN)) {
            isInitialized = true;
            Instruction instruction;

            if (layer == 1) {
                instruction = new Instruction(OprType.globa, globalCnt);
                instructions.add(instruction);
            } else {
                instruction = new Instruction(OprType.loca, localCnt);
                instructions.add(instruction);
            }
            next();
            exprType = analyseExpr();
            while (!op.empty())
                generateOprIns(op.pop(), instructions, exprType);

            instruction = new Instruction(OprType.store64, -1);
            instructions.add(instruction);
        }

        expect(TokenType.SEMICOLON);
        if ((isInitialized && exprType.equals(type)) || !isInitialized)
            if (layer == 1)
                addSymbol(name, false, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, -1, globalCnt);
            else
                addSymbol(name, false, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, localCnt, -1);
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (layer == 1) {
            Global global = new Global(0);
            globalTable.add(global);
        }
    }

    private String analyseType() throws CompileError {
        Token tt = peek();
        if (tt.getValue().equals("void") || tt.getValue().equals("int") || tt.getValue().equals("double")) {
            next();
        } else throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        String type = (String) tt.getValue();
        return type;
    }

    private String analyseExpr() throws CompileError {
        /* expr -> operator_expr
         * | negate_expr
         * | assign_expr
         * | as_expr
         * | call_expr
         * | literal_expr
         * | ident_expr
         * | group_expr
         * | group_expr
         */
        String exprType = "";

        // negate_expr -> '-' expr
        if (check(TokenType.MINUS))
            exprType = analyseNegateExpr();
        else if (check(TokenType.IDENT)) {
            Token ident = next();
            Symbol symbol = getSymbolByToken(ident);
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
        } else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    private void analyseConstDeclStmt() throws CompileError {
        // const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
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
        type = analyseType();
        if (type.equals("void"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (layer == 1) {
            instruction = new Instruction(OprType.globa, globalCnt);
            instructions.add(instruction);
        } else {
            instruction = new Instruction(OprType.loca, localCnt);
            instructions.add(instruction);
        }
        expect(TokenType.ASSIGN);
        exprType = analyseExpr();
        while (!op.empty())
            generateOprIns(op.pop(), instructions, exprType);

        instruction = new Instruction(OprType.store64, -1);
        instructions.add(instruction);

        expect(TokenType.SEMICOLON);
        if (exprType.equals(type))
            if (layer == 1)
                addSymbol(name, true, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, -1, globalCnt);
            else
                addSymbol(name, true, type, isInitialized, layer, params, "", ident.getStartPos(), -1, null, localCnt, -1);
        else throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (layer == 1) {
            Global global = new Global(1);
            globalTable.add(global);
        }
    }

    private String analyseAssignExpr(Symbol symbol, Token ident) throws CompileError {
        // assign_expr -> l_expr '=' expr
        // l_expr -> IDENT
        if (symbol.getParamId() != -1) {
            Symbol func = symbol.getFunction();
            if (func.getRetType().equals("int"))
                instructions.add(new Instruction(OprType.arga, 1 + symbol.getParamId()));
            else if (func.getRetType().equals("double"))
                instructions.add(new Instruction(OprType.arga, 1 + symbol.getParamId()));
            else
                instructions.add(new Instruction(OprType.arga, symbol.getParamId()));
        } else if (symbol.getParamId() == -1 && symbol.getLayer() != 1) {
            instructions.add(new Instruction(OprType.loca, symbol.getLocalId()));
        } else {
            instructions.add(new Instruction(OprType.globa, symbol.getGlobalId()));
        }

        expect(TokenType.ASSIGN);
        String exprType = analyseExpr();
        while (!op.empty())
            generateOprIns(op.pop(), instructions, exprType);

        if (symbol.isConst)
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        else if (symbol.getType().equals(exprType) && (symbol.getType().equals("int") || symbol.getType().equals("double"))) {
            initializeSymbol(symbol.getName(), peekedToken.getStartPos());
            instructions.add(new Instruction(OprType.store64, -1));
            return "void";
        } else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    private String analyseNegateExpr() throws CompileError {
        // negate_expr -> '-' expr
        expect(TokenType.MINUS);
        op.push(TokenType.FAN);
        String type = analyseExpr();
        if (!type.equals("int") && !type.equals("double"))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        if (!op.empty()) {
            int in = Operator.getPos(op.peek());
            int out = Operator.getPos(TokenType.FAN);
            if (Operator.getPriMatrix()[in][out] > 0)
                generateOprIns(op.pop(), instructions, type);
        }
        return type;
    }

    private String analyseCallExpr(Symbol symbol, Token ident, boolean isLibrary) throws CompileError {
        // call_expr -> IDENT '(' call_param_list? ')'
        Instruction instruction;
        if (isLibrary) {
            String name = symbol.getName();
            globalTable.add(new Global(1, name.length(), name));
            instruction = new Instruction(OprType.callname, globalCnt);
            globalCnt++;
        } else {
            if (!symbol.getType().equals("function"))
                throw new AnalyzeError(ErrorCode.CompileError, ident.getStartPos());
            int id = getFuncId(symbol.getName(), funcTable);
            instruction = new Instruction(OprType.call, id + 1);
        }

        String name = symbol.getName();
        expect(TokenType.L_PAREN);
        op.push(TokenType.L_PAREN);

        if (funcRet(name, funcTable))
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

    private void analyseCallParamList(Symbol symbol) throws CompileError {
        // call_param_list -> expr (',' expr)*
        int i = 0;
        List<Symbol> params = symbol.getParams();
        int paramNum = params.size();

        String type = analyseExpr();
        while (!op.empty() && op.peek() != TokenType.L_PAREN)
            generateOprIns(op.pop(), instructions, type);

        if (!params.get(i).getType().equals(type))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        i++;

        while (check(TokenType.COMMA)) {
            next();
            type = analyseExpr();
            while (!op.empty() && op.peek() != TokenType.L_PAREN)
                generateOprIns(op.pop(), instructions, type);

            if (!params.get(i).getType().equals(type))
                throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
            while (!op.empty() && op.peek() != TokenType.L_PAREN)
                generateOprIns(op.pop(), instructions, type);
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
        } else if (symbol.getParamId() == -1 && symbol.getLayer() != 1) {
            instructions.add(new Instruction(OprType.loca, symbol.getLocalId()));
        } else {
            instructions.add(new Instruction(OprType.globa, symbol.getGlobalId()));
        }
        instructions.add(new Instruction(OprType.load64, -1));
        return symbol.getType();
    }

    private String analyseLiteralExpr() throws CompileError {
        // literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL
        if (check(TokenType.UINT_LITERAL)) {
            Token token = next();
            instructions.add(new Instruction(OprType.push, (long) token.getValue()));
            return "int";
        } else if (check(TokenType.DOUBLE_LITERAL)) {
            Token token = next();
            String binary = Long.toBinaryString(Double.doubleToRawLongBits((Double) token.getValue()));
            Instruction instruction = new Instruction(OprType.push, convertToDec(binary));
            instructions.add(instruction);
            return "double";
        } else if (check(TokenType.STRING_LITERAL)) {
            Token token = next();
            String name = (String) token.getValue();
            globalTable.add(new Global(1, name.length(), name));

            instructions.add(new Instruction(OprType.push, globalCnt));
            globalCnt++;
            return "string";
        } else if (check(TokenType.CHAR_LITERAL)) {
            Token token = next();
            instructions.add(new Instruction(OprType.push, (Integer) token.getValue()));
            return "int";
        } else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    private String analyseGroupExpr() throws CompileError {
        // group_expr -> '(' expr ')'
        expect(TokenType.L_PAREN);
        op.push(TokenType.L_PAREN);
        String exprType = analyseExpr();
        expect(TokenType.R_PAREN);

        while (op.peek() != TokenType.L_PAREN)
            generateOprIns(op.pop(), instructions, exprType);

        op.pop();
        return exprType;
    }

    private String analyseOperatorExpr(String exprType) throws CompileError {
        // operator_expr -> expr binary_operator expr
        // binary_operator -> '+' | '-' | '*' | '/' | '==' | '!=' | '<' | '>' | '<=' | '>='
        Token token = analyseBinaryOperator();

        if (!op.empty()) {
            int in = Operator.getPos(op.peek());
            int out = Operator.getPos(token.getTokenType());
            if (Operator.getPriMatrix()[in][out] > 0)
                generateOprIns(op.pop(), instructions, exprType);
        }
        op.push(token.getTokenType());

        String type = analyseExpr();
        if (exprType.equals(type) && (exprType.equals("int") || exprType.equals("double")))
            return type;
        else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    private void analyseFunction() throws CompileError {
        // function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
        localCnt = 0;
        String name;
        List<Symbol> params = new ArrayList<>();
        String returnType = "";
        Token ident;

        expect(TokenType.FN_KW);
        ident = expect(TokenType.IDENT);
        name = (String) ident.getValue();
        expect(TokenType.L_PAREN);
        addSymbol(name, true, "function", true, layer, params, returnType, ident.getStartPos(), -1, null, -1, globalCnt);
        Symbol symbol = getSymbolByToken(ident);
        if (!check(TokenType.R_PAREN))
            analyseFunctionParamList(params, symbol);
        expect(TokenType.R_PAREN);

        expect(TokenType.ARROW);
        returnType = analyseType();
        symbol.setParams(params);
        symbol.setRetType(returnType);
        nowFunc = symbol;

        int retSlot = 0;
        if (returnType.equals("int")) retSlot = 1;
        else if (returnType.equals("double")) retSlot = 1;
        Function function = new Function(name, globalCnt, retSlot, params.size(), localCnt, instructions, layer, returnType);
        funcTable.add(function);

        analyseBlockStmt();

        function.setId(globalCnt);
        function.setLocSlots(localCnt);
        function.setBody(instructions);
        function.setLayer(layer);

        if (symbol.getRetType().equals("void"))
            instructions.add(new Instruction(OprType.ret, -1));
        else if (!retFunc.getName().equals(nowFunc.getName())) {
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
        }

        Global global = new Global(1, name.length(), name);
        globalTable.add(global);
    }

    private Symbol analyseFunctionParam(int i, Symbol symbol) throws CompileError {
        // function_param -> 'const'? IDENT ':' ty
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
        type = analyseType();
        name = (String) ident.getValue();
        addSymbol(name, isConst, type, isInitialized, layer + 1, null, "", ident.getStartPos(), i, symbol, -1, -1);
        return getSymbolByToken(ident);
    }

    private void analyseFunctionParamList(List<Symbol> params, Symbol symbol) throws CompileError {
        // function_param_list -> function_param (',' function_param)*
        int i = 0;
        params.add(analyseFunctionParam(i, symbol));
        while (check(TokenType.COMMA)) {
            next();
            params.add(analyseFunctionParam(++i, symbol));
        }
    }

    private void analyseBlockStmt() throws CompileError {
        // block_stmt -> '{' stmt* '}'
        layer++;
        expect(TokenType.L_BRACE);
        while (!check(TokenType.R_BRACE))
            analyseStmt();
        expect(TokenType.R_BRACE);

        for (int i = symbolTable.size() - 1; symbolTable.get(i).getLayer() == layer; i--)
            symbolTable.remove(i);
        layer--;
    }

    private void analyseStmt() throws CompileError {
        // decl_stmt -> let_decl_stmt | const_decl_stmt
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
        else if (check(TokenType.BREAK_KW)) {
        }
        // continue_stmt -> 'continue' ';'
        else if (check(TokenType.CONTINUE_KW)) {
        }
        // expr_stmt -> expr ';'
        else
            analyseExprStmt();
    }

    private void analyseIfStmt() throws CompileError {
        // if_stmt -> 'if' expr block_stmt ('else' (block_stmt | if_stmt))?
        expect(TokenType.IF_KW);
        String type = analyseExpr();
        while (!op.empty())
            generateOprIns(op.pop(), instructions, type);

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

    private void analyseWhileStmt() throws CompileError {
        // while_stmt -> 'while' expr block_stmt
        expect(TokenType.WHILE_KW);

        instructions.add(new Instruction(OprType.br, 0));
        int whileStart = instructions.size();

        String type = analyseExpr();
        while (!op.empty())
            generateOprIns(op.pop(), instructions, type);

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

    private void analyseReturnStmt() throws CompileError {
        // return_stmt -> 'return' expr? ';'
        expect(TokenType.RETURN_KW);
        String type = "void";

        if (!nowFunc.getRetType().equals("void")) {
            instructions.add(new Instruction(OprType.arga, 0));

            type = analyseExpr();
            while (!op.empty())
                generateOprIns(op.pop(), instructions, type);

            instructions.add(new Instruction(OprType.store64, -1));
        }

        if (!check(TokenType.SEMICOLON))
            type = analyseExpr();

        if (!type.equals(nowFunc.getRetType()))
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());

        expect(TokenType.SEMICOLON);
        retFunc = nowFunc;

        while (!op.empty())
            generateOprIns(op.pop(), instructions, type);
        instructions.add(new Instruction(OprType.ret, -1));
    }

    private void analyseEmptyStmt() throws CompileError {
        // empty_stmt -> ';'
        expect(TokenType.SEMICOLON);
    }

    private void analyseExprStmt() throws CompileError {
        // expr_stmt -> expr ';
        String exprType = analyseExpr();
        while (!op.empty())
            generateOprIns(op.pop(), instructions, exprType);
        expect(TokenType.SEMICOLON);
    }

    private String analyseAsExpr(String exprType) throws CompileError {
        // as_expr -> expr 'as' ty
        expect(TokenType.AS_KW);
        String rightType = analyseType();
        if (exprType.equals("int") && rightType.equals("double")) {
            instructions.add(new Instruction(OprType.itof, -1));
            return "double";
        } else if (exprType.equals("double") && rightType.equals("int")) {
            instructions.add(new Instruction(OprType.ftoi, -1));
            return "int";
        } else if (exprType.equals(rightType)) {
            return exprType;
        } else
            throw new AnalyzeError(ErrorCode.CompileError, peekedToken.getStartPos());
    }

    public List<Global> getGlobalTable() {
        return globalTable;
    }

    public void setGlobalTable(List<Global> globalTable) {
        this.globalTable = globalTable;
    }

    public List<Function> getFuncTable() {
        return funcTable;
    }

    public void setFuncTable(List<Function> funcTable) {
        this.funcTable = funcTable;
    }

    public Function get_start() {
        return _start;
    }

    private void set_start(Function _start) {
        this._start = _start;
    }

}
