package miniplc0java.analyser;

import miniplc0java.instruction.Instruction;

import java.util.List;

public class Function {
    String name;
    Integer id;
    Integer retSlots;
    Integer paramSlots;
    Integer locSlots;
    List<Instruction> body;
    int layer;
    String retType;

    public Function(String name, Integer id, Integer retSlots, Integer paramSlots, Integer locSlots, List<Instruction> body, int layer, String retType){
        this.name = name;
        this.id = id;
        this.retSlots = retSlots;
        this.paramSlots = paramSlots;
        this.locSlots = locSlots;
        this.body = body;
        this.layer = layer;
        this.retType = retType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRetSlots() {
        return retSlots;
    }

    public void setRetSlots(Integer retSlots) {
        this.retSlots = retSlots;
    }

    public Integer getParamSlots() {
        return paramSlots;
    }

    public void setParamSlots(Integer paramSlots) {
        this.paramSlots = paramSlots;
    }

    public Integer getLocSlots() {
        return locSlots;
    }

    public void setLocSlots(Integer locSlots) {
        this.locSlots = locSlots;
    }

    public List<Instruction> getBody() {
        return body;
    }

    public void setBody(List<Instruction> body) {
        this.body = body;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String getRetType() {
        return retType;
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    @Override
    public String toString() {
        return "Function: " +
                "name=" + name +
                "id=" + id +
                "retSlots=" + retSlots +
                "paramSlots=" + paramSlots +
                "locSlots=" + locSlots +
                "body=" + body +
                "layer=" + layer +
                "returnType=" + retType +
                "}";
    }
}
