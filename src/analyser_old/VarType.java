package analyser_old;

public enum VarType {
    Int,
    Double,
    Void,
    None;

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
            case None:
                return "none";
            default:
                return "InvalidVarType";
        }
    }
}
