package analyser;

public class Symbol {
    private String name;
    private VarType type;
    private boolean isConst;
    private boolean isInit;
//    private int stackOffset;
    private int layer;

    public Symbol(){}

    public Symbol(String name, VarType type, boolean isConst, boolean isInit, int layer) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.isInit = isInit;
//        this.stackOffset = stackOffset;
        this.layer = layer;
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
}
