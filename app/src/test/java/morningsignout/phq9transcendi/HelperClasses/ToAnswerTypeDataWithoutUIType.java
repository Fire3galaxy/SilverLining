package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static org.junit.jupiter.api.Assertions.*;

class ToAnswerTypeDataWithoutUIType implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        assertEquals(String.class, source.getClass(), "Expecting a String object");

        // Split single comma-separated list of strings into array of strings
        String sourceStr = String.valueOf(source);
        String[] parsedValues = sourceStr.split(", ");

        // Convert sourceStr into a map of answer type to corresponding answers
        String answerType = parsedValues[0];                         // First string is answer type
        String[] answerValues = new String[parsedValues.length - 1]; // Rest are answers
        System.arraycopy(parsedValues, 1, answerValues, 0, parsedValues.length - 1);

        return new SingleAnswerTypeData(answerType, "", answerValues);
    }
}
