package com.idt.compiler.interpreter.domain.controlStructures;

import java.util.Stack;

public class ControlStructureManager {

    Stack<ControlStructure> structureStack;

    public ControlStructureManager() {
        structureStack = new Stack<>();
    }

    public void newStructure(ControlStructure controlStructure) {
        this.structureStack.push(controlStructure);
    }

    public ControlStructure pop() {
        return this.structureStack.pop();
    }
}
