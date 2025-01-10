package bong.model;

import bong.canvas.LinePath;
import bong.routeFinding.Instruction;

import java.util.ArrayList;
import java.util.List;

public class RouteModel {
    private LinePath drawableRoute;
    private List<Instruction> instructions = new ArrayList<>();

    public boolean hasRoute() {
        return drawableRoute != null;
    }

    public LinePath getDrawableRoute() {
        return drawableRoute;
    }

    public void setDrawableRoute(LinePath drawableRoute) {
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
