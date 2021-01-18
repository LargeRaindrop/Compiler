package miniplc0java.instruction;

public enum OprType {
    nop,
    push,
    pop,
    popn,
    dup,
    loca,
    arga,
    globa,
    load8,
    load16,
    load32,
    load64,
    store8,
    store16,
    store32,
    store64,
    alloc,
    free,
    stackalloc,
    addi,
    subi,
    muli,
    divi,
    addf,
    subf,
    mulf,
    divf,
    divu,
    shl,
    shr,
    and,
    or,
    xor,
    not,
    cmpi,
    cmpu,
    cmpf,
    negi,
    negf,
    itof,
    ftoi,
    shrl,
    setlt,
    setgt,
    br,
    brfalse,
    brtrue,
    call,
    ret,
    callname,
    scani,
    scanc,
    scanf,
    printi,
    printc,
    printf,
    prints,
    println,
    panic,
    none;

    @Override
    public String toString() {
        switch (this) {
            case nop:
                return "nop";
            case push:
                return "push";
            case pop:
                return "pop";
            case popn:
                return "popn";
            case dup:
                return "dup";
            case loca:
                return "loca";
            case arga:
                return "arga";
            case globa:
                return "globa";
            case load8:
                return "load.8";
            case load16:
                return "load.16";
            case load32:
                return "load.32";
            case load64:
                return "load.64";
            case store8:
                return "store.8";
            case store16:
                return "store.16";
            case store32:
                return "store.32";
            case store64:
                return "store.64";
            case alloc:
                return "alloc";
            case free:
                return "free";
            case stackalloc:
                return "stackalloc";
            case addi:
                return "add.i";
            case subi:
                return "sub.i";
            case muli:
                return "mul.i";
            case divi:
                return "div.i";
            case addf:
                return "add.f";
            case subf:
                return "sub.f";
            case mulf:
                return "mul.f";
            case divf:
                return "div.f";
            case divu:
                return "div.u";
            case shl:
                return "shl";
            case shr:
                return "shr";
            case and:
                return "and";
            case or:
                return "or";
            case xor:
                return "xor";
            case not:
                return "not";
            case cmpi:
                return "cmp.i";
            case cmpu:
                return "cmp.u";
            case cmpf:
                return "cmp.f";
            case negi:
                return "neg.i";
            case negf:
                return "neg.f";
            case itof:
                return "itof";
            case ftoi:
                return "ftoi";
            case shrl:
                return "shrl";
            case setlt:
                return "set.lt";
            case setgt:
                return "set.gt";
            case br:
                return "br";
            case brfalse:
                return "br.false";
            case brtrue:
                return "br.ture";
            case call:
                return "call";
            case ret:
                return "ret";
            case callname:
                return "callname";
            case scani:
                return "scan.i";
            case scanc:
                return "scan.c";
            case scanf:
                return "scan.f";
            case printi:
                return "print.i";
            case printc:
                return "print.c";
            case printf:
                return "print.f";
            case prints:
                return "print.s";
            case println:
                return "println";
            case panic:
                return "panic";
            case none:
                return "none";
            default:
                return "InvalidOperation";
        }
    }
}
