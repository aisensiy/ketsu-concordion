package specs.model;

public class Qualification {
    private String solution;
    private String stack;

    public Qualification(String solution_name, String stack_name) {

        this.solution = solution_name;
        this.stack = stack_name;
    }

    public String getSolution() {
        return solution;
    }

    public String getStack() {
        return stack;
    }
}
