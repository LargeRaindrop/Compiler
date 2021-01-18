package miniplc0java.util;

import miniplc0java.analyser.Function;
import miniplc0java.analyser.Global;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.instruction.OprType;

import java.util.ArrayList;
import java.util.List;

public class Out {
    List<Global> globalTable;
    List<Function> funcTable;
    Function _start;
    List<Byte> output;

    public Out(List<Global> globalTable, List<Function> funcTable, Function _start) {
        this.globalTable = globalTable;
        this.funcTable = funcTable;
        this._start = _start;
        this.output = new ArrayList<>();
    }

    public void perform() {
        addInt(4, 0x72303b3e); // magic
        addInt(4, 0x00000001); // version
        addInt(4, globalTable.size());

        for (Global global : globalTable) {
            addInt(1, global.getIsConst());
            if (global.getItem() == null) {
                addInt(4, 8);
                addLong(8, 0L);
            } else {
                addInt(4, global.getItem().length());
                addString(global.getItem());
            }
        }

        addInt(4, funcTable.size() + 1);

        funcPerform(_start);

        for (Function function : funcTable)
            funcPerform(function);
    }

    private void funcPerform(Function function) {
        addInt(4, function.getId());
        addInt(4, function.getRetSlots());
        addInt(4, function.getParamSlots());
        addInt(4, function.getLocSlots());
        addInt(4, function.getBody().size());
        List<Instruction> instructions = function.getBody();
        for (Instruction instruction : instructions) {
            OprType op = instruction.getOp();
            int opInt = Operation.getHexOperations().get(op);
            addInt(1, opInt);
            if (instruction.getX() != -1) {
                if (opInt == 1)
                    addLong(8, instruction.getX());
                else
                    addInt(4, (int) instruction.getX());
            }
        }
    }

    private void addString(String x) {
        for (int i = 0; i < x.length(); i++) {
            char c = x.charAt(i);
            output.add((byte) c);
        }
    }

    private void addInt(int len, int x) {
        int start = 8 * (len - 1);
        for (int i = 0; i < len; i++) {
            int part = x >> (start - i * 8) & 0xFF;
            byte b = (byte) part;
            output.add(b);
        }
    }

    private void addLong(int len, long x) {
        int start = 8 * (len - 1);
        for (int i = 0; i < len; i++) {
            long part = x >> (start - i * 8) & 0xFF;
            byte b = (byte) part;
            output.add(b);
        }
    }

    public byte[] get_output() {
        byte[] _output = new byte[output.size()];
        for (int i = 0; i < output.size(); i++)
            _output[i] = output.get(i);
        return _output;
    }
}
