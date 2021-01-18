package miniplc0java.analyser;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    String name;
    boolean isConst;
    String type;
    boolean isInit;
    int stackOffset;
    int layer;
    List<Symbol> params = new ArrayList<>();
    String retType;
    // 如果不是局部变量则为-1
    int localId;
    // 如果不是全局变量则为-1
    int globalId;

    //如果是参数则代表参数id，否则为-1
    int paramId;
    //如果是参数则代表所属函数，否则为-1
    Symbol function;

    //构造函数
    public Symbol(String name, boolean isConst, String type, boolean isInit, int stackOffset, int layer, List<Symbol> params, String retType, int paramId, Symbol function, int localId, int globalId){
        this.name = name;
        this.isConst = isConst;
        this.type = type;
        this.isInit = isInit;
        this.stackOffset = stackOffset;
        this.layer = layer;
        this.params = params;
        this.retType = retType;
        this.paramId = paramId;
        this.function = function;
        this.localId = localId;
        this.globalId = globalId;
    }
    public Symbol(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public int getStackOffset() {
        return stackOffset;
    }

    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public List<Symbol> getParams() {
        return params;
    }

    public void setParams(List<Symbol> params) {
        this.params = params;
    }

    public String getRetType() {
        return retType;
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getGlobalId() {
        return globalId;
    }

    public void setGlobalId(int globalId) {
        this.globalId = globalId;
    }

    public int getParamId() {
        return paramId;
    }

    public void setParamId(int paramId) {
        this.paramId = paramId;
    }

    public Symbol getFunction() {
        return function;
    }

    public void setFunction(Symbol function) {
        this.function = function;
    }
}
