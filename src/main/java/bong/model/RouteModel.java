package bong.model;

import bong.routeFinding.Instruction;

import java.util.ArrayList;
import java.util.List;

public class RouteModel {
    private float[] drawableRoute;
    private List<Instruction> instructions = new ArrayList<>();

    public boolean hasRoute() {
        return drawableRoute != null;
    }

    public float[] getDrawableRoute() {
        return drawableRoute;
    }

    public void setDrawableRoute(float[] drawableRoute) {
        this.drawableRoute = drawableRoute;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void clear() {
        drawableRoute = null;
        instructions.clear();
    }

}
