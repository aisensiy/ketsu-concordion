package specs.model;

public class Capability {
    private final String stackId;
    private final String stackName;
    private final String solutionId;
    private final String solutionName;

    public String getStackId() {
        return stackId;
    }

    public String getStackName() {
        return stackName;
    }

    public String getSolutionId() {
        return solutionId;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public Capability(String stackId, String stackName, String solutionId, String solutionName) {

        this.stackId = stackId;
        this.stackName = stackName;
        this.solutionId = solutionId;
        this.solutionName = solutionName;
    }
}
