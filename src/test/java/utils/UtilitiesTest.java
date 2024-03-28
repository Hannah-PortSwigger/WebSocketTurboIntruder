package utils;

import burp.api.montoya.MontoyaExtension;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.FakeByteArray;
import burp.api.montoya.core.Range;
import burp.api.montoya.internal.MontoyaObjectFactory;
import burp.api.montoya.internal.ObjectFactoryLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.stubbing.Answer;

import java.util.stream.Stream;

import static burp.api.montoya.core.FakeRange.rangeOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static utils.Utilities.insertPlaceholder;

@ExtendWith(MontoyaExtension.class)
class UtilitiesTest
{
    @BeforeEach
    void configureMocks()
    {
        MontoyaObjectFactory factory = ObjectFactoryLocator.FACTORY;

        when(factory.byteArray(eq(new byte[0]))).thenAnswer((Answer<ByteArray>) i -> new FakeByteArray(new byte[0]));
    }
    
    private static Stream<Arguments> data() {
        return Stream.of(
                arguments("0123456789", rangeOf(0, 1), "a", "a123456789"),
                arguments("0123456789", rangeOf(0, 3), "a", "a3456789"),
                arguments("0123456789", rangeOf(2, 3), "a", "01a3456789"),
                arguments("0123456789", rangeOf(2, 5), "a", "01a56789"),
                arguments("0123456789", rangeOf(9, 10), "a", "012345678a"),
                arguments("0123456789", rangeOf(7, 10), "a", "0123456a")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void insertPlaceHolderTest(String data, Range range, String placeholder, String expectedOutput)
    {
        ByteArray byteArray = new FakeByteArray(data);

        ByteArray modifiedData = insertPlaceholder(byteArray, range, placeholder);

        assertThat(modifiedData.toString()).isEqualTo(expectedOutput);
    }
}