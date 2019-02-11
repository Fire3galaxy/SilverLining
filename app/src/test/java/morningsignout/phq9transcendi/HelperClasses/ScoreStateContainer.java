package morningsignout.phq9transcendi.HelperClasses;

public class ScoreStateContainer {
    public String scoreString;
    public String visitedString;
    public int[] scoreValues;
    public boolean[] visitedValues;

    public ScoreStateContainer(String scoreString,
                               String visitedString,
                               int[] scoreValues,
                               boolean[] visitedValues) {
        this.scoreString = scoreString;
        this.visitedString = visitedString;
        this.scoreValues = scoreValues;
        this.visitedValues = visitedValues;
    }
}
