package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static org.junit.jupiter.api.Assertions.*;

public class ToScoreStateContainer implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context)
            throws ArgumentConversionException
    {
        assertEquals(String.class, source.getClass(), "Expecting a String object");

        String[] arrayStrings = ((String) source).split(",");
        String scoreString = arrayStrings[0];
        String visitedString = arrayStrings[1];
        int[] scoreValues = parseToIntArray(scoreString);
        boolean[] visitedValues = parseToBooleanArray(visitedString);

        return new ScoreStateContainer(scoreString, visitedString, scoreValues, visitedValues);
    }

    private int[] parseToIntArray(String s) {
        int[] ints = new int[s.length()];
        final int BASE_10_RADIX = 10;

        for (int i = 0; i < s.length(); i++)
            ints[i] = Character.digit(s.charAt(i), BASE_10_RADIX);
        return ints;
    }

    private boolean[] parseToBooleanArray(String s) {
        boolean[] booleans = new boolean[s.length()];

        for (int i = 0; i < s.length(); i++)
            booleans[i] = (s.charAt(i) == '1');

        return booleans;
    }
}
