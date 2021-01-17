package analyser_old;

public class Parameter {
    private String name;
    private VarType type;

    public Parameter() {}

    public Parameter(String name, VarType type) {
        this.name = name;
        this.type = type;
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
}
