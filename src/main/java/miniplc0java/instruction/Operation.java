package miniplc0java.instruction;

import java.util.HashMap;
import java.util.Map;

public class Operation {
    static Map<OprType, Integer> hexOperations;
    static {
        hexOperations = new HashMap<>();
        hexOperations.put(OprType.nop, 0x00);
        hexOperations.put(OprType.push, 0x01);
        hexOperations.put(OprType.pop, 0x02);
        hexOperations.put(OprType.popn, 0x03);
        hexOperations.put(OprType.dup, 0x04);
        hexOperations.put(OprType.loca, 0x0a);
        hexOperations.put(OprType.arga, 0x0b);
        hexOperations.put(OprType.globa, 0x0c);
        hexOperations.put(OprType.load8, 0x10);
        hexOperations.put(OprType.load16, 0x11);
        hexOperations.put(OprType.load32, 0x12);
        hexOperations.put(OprType.load64, 0x13);
        hexOperations.put(OprType.store8, 0x14);
        hexOperations.put(OprType.store16, 0x15);
        hexOperations.put(OprType.store32, 0x16);
        hexOperations.put(OprType.store64, 0x17);
        hexOperations.put(OprType.alloc, 0x18);
        hexOperations.put(OprType.free, 0x19);
        hexOperations.put(OprType.stackalloc, 0x1a);
        hexOperations.put(OprType.addi, 0x20);
        hexOperations.put(OprType.subi, 0x21);
        hexOperations.put(OprType.muli, 0x22);
        hexOperations.put(OprType.divi, 0x23);
        hexOperations.put(OprType.addf, 0x24);
        hexOperations.put(OprType.subf, 0x25);
        hexOperations.put(OprType.mulf, 0x26);
        hexOperations.put(OprType.divf, 0x27);
        hexOperations.put(OprType.divu, 0x28);
        hexOperations.put(OprType.shl, 0x29);
        hexOperations.put(OprType.shr, 0x2a);
        hexOperations.put(OprType.and, 0x2b);
        hexOperations.put(OprType.or, 0x2c);
        hexOperations.put(OprType.xor, 0x2d);
        hexOperations.put(OprType.not, 0x2e);
        hexOperations.put(OprType.cmpi, 0x30);
        hexOperations.put(OprType.cmpu, 0x31);
        hexOperations.put(OprType.cmpf, 0x32);
        hexOperations.put(OprType.negi, 0x34);
        hexOperations.put(OprType.negf, 0x35);
        hexOperations.put(OprType.itof, 0x36);
        hexOperations.put(OprType.ftoi, 0x37);
        hexOperations.put(OprType.shrl, 0x38);
        hexOperations.put(OprType.setlt, 0x39);
        hexOperations.put(OprType.setgt, 0x3a);
        hexOperations.put(OprType.br, 0x41);
        hexOperations.put(OprType.brfalse, 0x42);
        hexOperations.put(OprType.brtrue, 0x43);
        hexOperations.put(OprType.call, 0x48);
        hexOperations.put(OprType.ret, 0x49);
        hexOperations.put(OprType.callname, 0x4a);
        hexOperations.put(OprType.scani, 0x50);
        hexOperations.put(OprType.scanc, 0x51);
        hexOperations.put(OprType.scanf, 0x52);
        hexOperations.put(OprType.printi, 0x54);
        hexOperations.put(OprType.printc, 0x55);
        hexOperations.put(OprType.printf, 0x56);
        hexOperations.put(OprType.prints, 0x57);
        hexOperations.put(OprType.println, 0x58);
        hexOperations.put(OprType.panic, 0xfe);
    }

    public static Map<OprType, Integer> getHexOperations() {
        return hexOperations;
    }
}
