package miniplc0java.instruction;

import java.util.HashMap;
import java.util.Map;

public class Operation {
    public static Map<OprType, Integer> getOperations(){
        Map<OprType, Integer> operations = new HashMap<>();
        operations.put(OprType.nop, 0x00);
        operations.put(OprType.push, 0x01);
        operations.put(OprType.pop, 0x02);
        operations.put(OprType.popn, 0x03);
        operations.put(OprType.dup, 0x04);
        operations.put(OprType.loca, 0x0a);
        operations.put(OprType.arga, 0x0b);
        operations.put(OprType.globa, 0x0c);
        operations.put(OprType.load8, 0x10);
        operations.put(OprType.load16, 0x11);
        operations.put(OprType.load32, 0x12);
        operations.put(OprType.load64, 0x13);
        operations.put(OprType.store8, 0x14);
        operations.put(OprType.store16, 0x15);
        operations.put(OprType.store32, 0x16);
        operations.put(OprType.store64, 0x17);
        operations.put(OprType.alloc, 0x18);
        operations.put(OprType.free, 0x19);
        operations.put(OprType.stackalloc, 0x1a);
        operations.put(OprType.addi, 0x20);
        operations.put(OprType.subi, 0x21);
        operations.put(OprType.muli, 0x22);
        operations.put(OprType.divi, 0x23);
        operations.put(OprType.addf, 0x24);
        operations.put(OprType.subf, 0x25);
        operations.put(OprType.mulf, 0x26);
        operations.put(OprType.divf, 0x27);
        operations.put(OprType.divu, 0x28);
        operations.put(OprType.shl, 0x29);
        operations.put(OprType.shr, 0x2a);
        operations.put(OprType.and, 0x2b);
        operations.put(OprType.or, 0x2c);
        operations.put(OprType.xor, 0x2d);
        operations.put(OprType.not, 0x2e);
        operations.put(OprType.cmpi, 0x30);
        operations.put(OprType.cmpu, 0x31);
        operations.put(OprType.cmpf, 0x32);
        operations.put(OprType.negi, 0x34);
        operations.put(OprType.negf, 0x35);
        operations.put(OprType.itof, 0x36);
        operations.put(OprType.ftoi, 0x37);
        operations.put(OprType.shrl, 0x38);
        operations.put(OprType.setlt, 0x39);
        operations.put(OprType.setgt, 0x3a);
        operations.put(OprType.br, 0x41);
        operations.put(OprType.brfalse, 0x42);
        operations.put(OprType.brtrue, 0x43);
        operations.put(OprType.call, 0x48);
        operations.put(OprType.ret, 0x49);
        operations.put(OprType.callname, 0x4a);
        operations.put(OprType.scani, 0x50);
        operations.put(OprType.scanc, 0x51);
        operations.put(OprType.scanf, 0x52);
        operations.put(OprType.printi, 0x54);
        operations.put(OprType.printc, 0x55);
        operations.put(OprType.printf, 0x56);
        operations.put(OprType.prints, 0x57);
        operations.put(OprType.println, 0x58);
        operations.put(OprType.panic, 0xfe);
        return operations;
    }
}
