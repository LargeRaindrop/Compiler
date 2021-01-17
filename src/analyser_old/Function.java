package analyser_old;

import instruction.Instruction;

import java.util.List;

public class Function {
    private String name;
    private VarType retType;
//    private int stackOffset;
    private int retSlots;
    private int paramSlots;
    private int locSlots;
    private List<Instruction> body;
    private List<Parameter> params;

    public Function() {}

    public Function(String name, VarType retType, int retSlots, int paramSlots, int locSlots, List<Instruction> body, List<Parameter> params) {
        this.name = name;
        this.retType = retType;
//        this.stackOffset = stackOffset;
        this.retSlots = retSlots;
        this.paramSlots = paramSlots;
        this.locSlots = locSlots;
        this.body = body;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VarType getRetType() {
        return retType;
    }

    public void setRetType(VarType retType) {
        this.retType = retType;
    }

//    public int getStackOffset() {
//        return stackOffset;
//    }
//
//    public void setStackOffset(int stackOffset) {
//        this.stackOffset = stackOffset;
//    }

    public int getRetSlots() {
        return retSlots;
    }

    public void setRetSlots(int retSlots) {
        this.retSlots = retSlots;
    }

    public int getParamSlots() {
        return paramSlots;
    }

    public void setParamSlots(int paramSlots) {
        this.paramSlots = paramSlots;
    }

    public int getLocSlots() {
        return locSlots;
    }

    public void setLocSlots(int locSlots) {
        this.locSlots = locSlots;
    }

    public List<Instruction> getBody() {
        return body;
    }

    public void setBody(List<Instruction> body) {
        this.body = body;
    }

    public List<Parameter> getParams() {
        return params;
    }

    public void setParams(List<Parameter> params) {
        this.params = params;
    }
}
