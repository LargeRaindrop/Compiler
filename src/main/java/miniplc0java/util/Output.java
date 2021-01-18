package miniplc0java.util;

import miniplc0java.analyser.Function;
import miniplc0java.analyser.Global;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.instruction.OprType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Output {
    Map<OprType, Integer> operations = Operation.getOperations();
    List<Global> globalTable;
    List<Function> functionTable;
    Function _start;
    List<Byte> output;
    int magic = 0x72303b3e;
    int version = 0x00000001;

    public Output(List<Global> globalTable, List<Function> functionTable, Function _start) {
        this.globalTable = globalTable;
        this.functionTable = functionTable;
        this._start = _start;
        this.output = new ArrayList<>();
    }

    public void transfer() {
        addInt(4, magic);
        addInt(4, version);

        addInt(4, globalTable.size());

        for (Global global : globalTable) {
            addInt(1, global.getIsConst());
            if (global.getItems() == null) {
                addInt(4, 8);
                addLong(8, 0L);
            } else {
                addInt(4, global.getItems().length());
                addString(global.getItems());
            }
        }

        addInt(4, functionTable.size() + 1);

        transferFunction(_start);

        for (Function function : functionTable)
            transferFunction(function);
    }

    private void transferFunction(Function function) {
        addInt(4, function.getId());
        addInt(4, function.getRetSlots());
        addInt(4, function.getParamSlots());
        addInt(4, function.getLocSlots());
        addInt(4, function.getBody().size());
        List<Instruction> instructions = function.getBody();
        for (Instruction instruction : instructions) {
            OprType op = instruction.getOp();
            int opInt = operations.get(op);
            addInt(1, opInt);
            if (instruction.getX() != -1) {
                if (opInt == 1)
                    addLong(8, instruction.getX());
                else
                    addInt(4, (int) instruction.getX());
            }
        }
    }

    private void addInt(int length, int x) {
        int start = 8 * (length - 1);
        for (int i = 0; i < length; i++) {
            int part = x >> (start - i * 8) & 0xFF;
            byte b = (byte) part;
            output.add(b);
        }
    }

    private void addLong(int length, long x) {
        int start = 8 * (length - 1);
        for (int i = 0; i < length; i++) {
            long part = x >> (start - i * 8) & 0xFF;
            byte b = (byte) part;
            output.add(b);
        }
    }


    private void addString(String x) {
        for (int i = 0; i < x.length(); i++) {
            char c = x.charAt(i);
            output.add((byte) c);
        }
    }

    public byte[] get_output() {
        byte[] _output = new byte[output.size()];
        for (int i = 0; i < output.size() ; i++)
            _output[i] = output.get(i);
        return _output;
    }
}
