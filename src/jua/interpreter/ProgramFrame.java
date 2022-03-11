package jua.interpreter;

import jua.runtime.Operand;
import jua.interpreter.instructions.Instruction;

public final class ProgramFrame {

    private final Program program;

    private final Instruction[] instructions;

    private final Operand[] stack;

    private final Operand[] locals;

    private int pc = 0;

    private int tos = 0;

    private boolean runningstate = false;

    ProgramFrame(Program program) {
        this.program = program;
        this.instructions = program.instructions;
        this.stack = new Operand[program.stackSize];
        this.locals = new Operand[program.localsSize];
    }

    public void incPC() {
        pc++;
    }

    @Deprecated
    public void setPC(int newPC) {
        pc = newPC;
    }

    // todo
    public String sourceName() {
        return program.filename;
    }

    public int currentLine() {
        if (runningstate) {
            throw new IllegalStateException();
        }
        return program.getInstructionLine(pc);
    }

    void run(InterpreterRuntime env) {
        Instruction[] instructions = this.instructions;
        int bci = pc;
        runningstate = true;
        try {
            while (true) {
                bci += instructions[bci].run(env);
            }
        } finally {
            pc = bci;
            runningstate = false;
        }
    }


    public void setRunningstate(boolean runningstate) {
        this.runningstate = runningstate;
    }

    // в двух нижеописанных операциях нет смысла
    public void clearStack() {}

    public void clearLocals() {}

    public void push(Operand operand) {
        stack[tos++] = operand;
    }

    public Operand pop() {
        Operand operand = stack[--tos];
        // Я же это удалял...
//        stack[tos] = null;
        return operand;
    }

    public Operand peek() {
        return stack[tos - 1];
    }

    public void dup1_x1() {
        stack[tos] = stack[tos - 1];
        stack[tos - 1] = stack[tos - 2];
        stack[tos - 2] = stack[tos];
        tos++;
    }

    public void dup1_x2() {
        stack[tos] = stack[tos - 1];
        stack[tos - 1] = stack[tos - 2];
        stack[tos - 2] = stack[tos - 3];
        stack[tos - 3] = stack[tos];
        tos++;
    }

    public void dup2_x1() {
        stack[tos + 1] = stack[tos - 1];
        stack[tos] = stack[tos - 2];
        stack[tos - 2] = stack[tos - 3];
        stack[tos - 3] = stack[tos + 1];
        stack[tos - 4] = stack[tos];
        tos += 2;
    }

    public void dup2_x2() {
        stack[tos + 1] = stack[tos - 1];
        stack[tos] = stack[tos - 2];
        stack[tos - 1] = stack[tos - 3];
        stack[tos - 2] = stack[tos - 4];
        stack[tos - 4] = stack[tos + 1];
        stack[tos - 5] = stack[tos];
        tos += 2;
    }

    @Deprecated
    public void duplicate(int count, int x) {
        if (count == 1) {
            for (Operand val = stack[tos - 1]; --x >= 0; )
                stack[tos++] = val;
        } else {
            for (int i = 0; i < x; i++)
                System.arraycopy(stack, (tos - count), stack, (tos + count * i), count);
            tos += count * x;
        }
    }

    @Deprecated
    public void move(int x) {
        Operand temp = stack[tos - 1];
        System.arraycopy(stack, (tos + x - 1), stack, (tos + x), (x < 0) ? -x : x);
        stack[tos + x - 1] = temp;
    }

    public void store(int id, Operand value) {
        locals[id] = value;
    }

    public Operand load(int id) {
        return locals[id];
    }

    public Operand getConstant(int index) {
        return program.constantPool[index];
    }

    public void reportUndefinedVariable(int id) {
        throw InterpreterError.variableNotExists(program.localsNames[id]);
    }

    public void swap() {
        Operand reserve = stack[tos - 2];
        stack[tos - 2] = stack[tos - 1];
        stack[tos - 1] = reserve;
    }

    public void swap_x1() {
        Operand reserve = stack[tos - 3];
        stack[tos - 3] = stack[tos - 2];
        stack[tos - 2] = stack[tos - 1];
        stack[tos - 1] = reserve;
    }
}
