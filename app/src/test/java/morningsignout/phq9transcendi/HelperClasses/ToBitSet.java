package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToBitSet implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        assertEquals(String.class, source.getClass(), "Expecting a String object");

        String bitString = (String) source;
        BitSet bitSet = new BitSet(bitString.length());

        for (int i = 0; i < bitString.length(); i++) {
            bitSet.set(i, bitString.charAt(i) == '1'); // char ('1' or '0') to bool
        }

        return bitSet;
    }
}
