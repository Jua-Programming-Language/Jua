package jua.interpreter.states;

import jua.interpreter.Environment;
import jua.tools.CodePrinter;

public class Ifgt extends JumpState {

    private final long value;

    public Ifgt(long value) {
        this.value = value;
    }

    @Override
    public void print(CodePrinter printer) {
        printer.printName("ifgt");
        printer.printOperand(value);
        super.print(printer);
    }

    @Override
    public void run(Environment env) {
        if (env.popInt() > value) {
            env.setPC(destination);
        } else {
            env.nextPC();
        }
    }
}
