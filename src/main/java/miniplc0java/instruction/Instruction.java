package miniplc0java.instruction;

import java.util.Objects;

public class Instruction {
    private String op;
    private long x;

    public Instruction(String op, long x) {
        this.op = op;
        this.x = x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return op == that.op && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, x);
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "" + op + " " + x;
    }
}
