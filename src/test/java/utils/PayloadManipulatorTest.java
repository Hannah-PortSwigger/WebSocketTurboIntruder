package utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PayloadManipulatorTest
{
    private static Stream<Arguments> data()
    {
        return Stream.of(
                arguments("foo", null, "foo"),
                arguments(null, "bar", ""),
                arguments("%s", "bar", "bar"),
                arguments("%s", null, "%s"),
                arguments("%s", "", ""),
                arguments("foo", "bar", "foo"),
                arguments("foo%s", "bar", "foobar"),
                arguments("fo%so", "bar", "fobaro"),
                arguments("%sfoo", "bar", "barfoo"),
                arguments("%sfoo%s", "bar", "barfoobar"),
                arguments("fo%s%so", "bar", "fobarbaro")
        );
    }

    @MethodSource("data")
    @ParameterizedTest
    void givenPayloadWithReplacement_whenManipulated_thenOutputIsCorrect(String payload, String replacement, String expectedOutput)
    {
        PayloadManipulator payloadManipulator = new PayloadManipulator();

        String actualOutput = payloadManipulator.modify(payload, replacement);

        assertThat(actualOutput).isEqualTo(expectedOutput);
    }
}