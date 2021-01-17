package analyser;

public enum VarType {
    Int,
    Double,
    Void;

    @Override
    public String toString() {
        switch (this)
        {
            case Int:
                return "int";
            case Double:
                return "double";
            case Void:
                return "void";
            default:
                return "InvalidVarType";
        }
    }
}
