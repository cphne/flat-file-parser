package cphne.flatfileparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FlatFileParserImplTest {

    private final Path singleRecordPath = Path.of("src/test/resources/simple.txt");

    private static final FlatFileParser parser = ParserFactory.newInstance();

    private static final FlatFileParser debugParser = ParserFactory.newInstance(".");

    @Nested
    class WriteTest {
        @Nested
        class SingleRecordTest {

            @Test
            void write() throws ParserException, IOException {
                Person p = new Person();
                p.setFirstname("parry");
                p.setLastname("hotter");
                p.setAge(16);
                p.setGender("male");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                parser.write(byteArrayOutputStream, List.of(p));
                assertThat(byteArrayOutputStream.toString()).isEqualToNormalizingNewlines(Files.readString(Path.of("src/test/resources" +
                        "/simple" + ".txt")));
            }

            @Test
            void testFree() throws ParserException, IOException {
                Person p = new Person();
                p.setFirstname("hodd");
                p.setLastname("toward");
                p.setAge(54);
                p.setGender("male");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                debugParser.write(byteArrayOutputStream, List.of(p));
                assertThat(byteArrayOutputStream.toString()).isEqualToNormalizingNewlines("""
                        hodd....toward..54.male......
                        """);
            }

            @Test
            void testExceededSize() throws ParserException, IOException {
                Person p = new Person();
                p.setFirstname("hoddtodd");
                p.setLastname("toward");
                p.setAge(54);
                p.setGender("male");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                debugParser.write(byteArrayOutputStream, List.of(p));
                assertThat(byteArrayOutputStream.toString()).isEqualToNormalizingNewlines("""
                        hoddtoddtoward..54.male......
                        """);
            }

            @Test
            void givenProvidedDataIsEmptyThenReturnsEmptyFile() throws ParserException, IOException {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                parser.write(stream, List.of());
                assertThat(stream.toByteArray()).isEmpty();
            }
        }

        @Nested
        class MultipleRecordsTest {
            @Test
            void test() throws ParserException, IOException {
                List<Person> data = new ArrayList<>();
                for (int i = 1; i < 4; i++) {
                    Person p = new Person();
                    p.setGender("female");
                    p.setFirstname("Raifod-%d".formatted(i));
                    p.setLastname("Arnone-%d".formatted(i));
                    p.setAge(i);
                    data.add(p);
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                debugParser.write(byteArrayOutputStream, data);
                assertThat(byteArrayOutputStream.toString()).isEqualToNormalizingNewlines("""
                        Raifod-1Arnone-11..female....
                        Raifod-2Arnone-22..female....
                        Raifod-3Arnone-33..female....
                        """);
            }
        }

    }

    @Nested
    class ParseTest {

        @Nested
        class GivenMultipleRecordsTest {

            private List<Person> persons;

            @BeforeEach
            void setUp() throws ParserException, IOException {
                persons = parser.parse(Path.of("src/test/resources/multiline.txt"), Person.class);
            }

            @Test
            void parsesFirstRecordTest() {
                Person p = persons.get(0);
                assertThat(p.getFirstname()).isEqualTo("parry");
                assertThat(p.getLastname()).isEqualTo("hotter");
                assertThat(p.getGender()).isEqualTo("male");
                assertThat(p.getAge()).isEqualTo(16);
            }

            @Test
            void parsesSecondRecordTest() {
                Person p = persons.get(1);
                assertThat(p.getFirstname()).isEqualTo("germine");
                assertThat(p.getLastname()).isEqualTo("hranger");
                assertThat(p.getGender()).isEqualTo("female");
                assertThat(p.getAge()).isEqualTo(15);
            }

        }

        @Nested
        class GivenSingleRecordTest {


            private List<Person> result;

            @BeforeEach
            void setUp() throws ParserException, IOException {
                result = parser.parse(singleRecordPath, Person.class);
            }

            @Test
            void resultContainsSingleElement() {
                assertThat(result).hasSize(1);
            }

            @Test
            void parsesData() {
                Person person = result.get(0);
                assertThat(person.getFirstname()).isEqualTo("parry");
                assertThat(person.getLastname()).isEqualTo("hotter");
                assertThat(person.getAge()).isEqualTo(16);
                assertThat(person.getGender()).isEqualTo("male");
            }
        }

        @Test
        void givenFlatFileIsEmptyThenReturnsEmptyList() throws ParserException, IOException {
            List<Person> persons = parser.parse(new ByteArrayInputStream(new byte[0]), Person.class);
            assertThat(persons).isEmpty();
        }

        @Nested
        class ThrowsAnIOExceptionTest {
            @Test
            void givenFileDoesNotExist() {
                assertThatExceptionOfType(IOException.class).isThrownBy(() -> parser.parse(new File("not/a/valid/path"),
                        Person.class
                ));
            }

            @Test
            void givenPathDoesNotExist() {
                assertThatExceptionOfType(IOException.class).isThrownBy(() -> parser.parse(Path.of("not/a/valid/path"),
                        Person.class
                ));
            }

            @Test
            @Disabled(
                    "It seems BufferedReader ignores the fact that the stream has already been read. I expected an " + "exception to be thrown when attempting to read from an already processed stream, but not " + "juice."
            )
            void givenStreamIsNotReadable() throws IOException {
                InputStream stream = new ByteArrayInputStream("test".getBytes());
                stream.readAllBytes();
                assertThatExceptionOfType(IOException.class).isThrownBy(() -> parser.parse(stream, Person.class));
            }
        }

        @Nested
        class ThrowsAParserExceptionTest {
            @Test
            void givenTheTypeDoesNotContainAnyFieldDefinitions() {
                assertThatExceptionOfType(ParserException.class).isThrownBy(() -> parser.parse(singleRecordPath,
                                NoFieldsDefinedDummy.class
                        ))
                        .withMessage("Cant parse data, no field definitions defined for class %s.".formatted(
                                NoFieldsDefinedDummy.class));
            }

            @Test
            void givenSetterMethodForFieldIsMissing() {
                assertThatExceptionOfType(ParserException.class).isThrownBy(() -> parser.parse(singleRecordPath,
                                MissingSetterDummy.class
                        ))
                        .withMessage("No setter available for field 'isMissingASetter'.");
            }

            @Test
            void givenSetterDoesNotHaveOneParameter() {
                assertThatExceptionOfType(ParserException.class).isThrownBy(() -> parser.parse(singleRecordPath,
                                SetterWithTwoParametersDummy.class
                        ))
                        .withMessage("Cant invoke setter 'setInvalid', expected exactly one parameter, found 2.");
            }

            @Test
            void givenDataCantBeConvertedToTypeOfParameter() {
                assertThatExceptionOfType(ParserException.class).isThrownBy(() -> parser.parse(singleRecordPath,
                                FieldWithNonConvertableType.class
                        ))
                        .withCauseInstanceOf(NoSuchMethodException.class);
            }

            static class NoFieldsDefinedDummy {
                private int undefinedField;
            }

            static class MissingSetterDummy {
                @Field(start = 0, end = 8)
                private String isMissingASetter;
            }

            static class SetterWithTwoParametersDummy {
                @Field(start = 0, end = 8)
                private String invalid;

                public SetterWithTwoParametersDummy setInvalid(String invalid, int anotherParameter) {
                    this.invalid = invalid + anotherParameter;
                    return this;
                }
            }

            static class FieldWithNonConvertableType {

                @Field(start = 0, end = 2)
                private NonConvertableType type;

                public FieldWithNonConvertableType setType(NonConvertableType type) {
                    this.type = type;
                    return this;
                }

                static class NonConvertableType {

                }
            }
        }
    }
}
