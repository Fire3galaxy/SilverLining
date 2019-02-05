package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static org.junit.jupiter.api.Assertions.*;

public class ToIntArray implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        assertEquals(String.class, source.getClass(), "Expecting a String object");

        String scoreString = (String) source;
        int[] scoreValues = new int[scoreString.length()];
        final int BASE_10_RADIX = 10;

        for (int i = 0; i < scoreString.length(); i++)
            scoreValues[i] = Character.digit(scoreString.charAt(i), BASE_10_RADIX);

        return scoreValues;
    }
}
