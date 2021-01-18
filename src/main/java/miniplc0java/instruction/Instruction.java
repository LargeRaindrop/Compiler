package miniplc0java.instruction;

import java.util.ArrayList;
import java.util.Objects;

public class Instruction {
    OprType op;
    long x;

    public Instruction(OprType op, long x) {
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

    public OprType getOp() {
        return op;
    }

    public void setOp(OprType op) {
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
//        return "" + op + " " + x;
        switch (op) {
            case push:
            case popn:
            case loca:
            case arga:
            case globa:
            case stackalloc:
            case br:
            case brtrue:
            case brfalse:
            case call:
            case callname:
                return "" + op + " " + x;
            default:
                return "" + op;
        }
    }
}
