fn add(a: int, b: int) -> int {
    return a + b;
}

fn main() -> void {
    let x: int;
    let y: int;
    let z: int;
    x = getint();
    y = getint();
    z = add(x, y);
    putint(z);
}