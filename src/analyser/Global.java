package analyser;

public class Global {
    private String name;
    private VarType type;
    private boolean isConst;
    private boolean isInit;
//    private int stackOffset;

    public Global() {}

    public Global(String name, VarType type, boolean isConst, boolean isInit) {
        this.name = name;
        this.type = type;
        this.isConst = isConst;
        this.isInit = isInit;
//        this.stackOffset = stackOffset;
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
}
