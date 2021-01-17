package analyser_old;

public class Symbol {
    private String name;
    private VarType type;
    private boolean isConst;
    private boolean isInit;
//    private int stackOffset;
    private int layer;
    private int localId;
    private int paramId;
    private Function function;

    public Symbol(){}

    public Symbol(String name, VarType type, boolean isConst, boolean isInit, int layer, int localId, int paramId, Function function) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.isInit = isInit;
        this.layer = layer;
        this.localId = localId;
        this.paramId = paramId;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VarType getType() {
        return type;
    }

    public void setType(VarType type) {
        this.type = type;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

//    public int getStackOffset() {
//        return stackOffset;
//    }
//
//    public void setStackOffset(int stackOffset) {
//        this.stackOffset = stackOffset;
//    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getParamId() {
        return paramId;
    }

    public void setParamId(int paramId) {
        this.paramId = paramId;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
