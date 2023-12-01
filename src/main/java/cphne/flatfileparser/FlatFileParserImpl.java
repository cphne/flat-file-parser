package cphne.flatfileparser;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the {@link FlatFileParser} interface
 */
class FlatFileParserImpl implements FlatFileParser {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FlatFileParserImpl.class);

    /**
     * The character to use to remaining space of a field
     */
    private final String padCharacter;

    /**
     * Default constructor. Defines {@code " "} as the default {@link FlatFileParserImpl#padCharacter}
     */
    public FlatFileParserImpl() {
        this.padCharacter = " ";
    }

    /**
     * 
     * @param padCharacter  the Character to use for padding fields
     */
    public FlatFileParserImpl(String padCharacter) {
        this.padCharacter = padCharacter;
    }

    @Override
    public <T> List<T> parse(File file, Class<T> clazz) throws IOException, ParserException {
        return parse(file.toPath(), clazz);
    }

    @Override
    public <T> List<T> parse(Path path, Class<T> clazz) throws IOException, ParserException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            return parse(bufferedReader, clazz);
        }
    }

    @Override
    public <T> List<T> parse(InputStream inputStream, Class<T> clazz) throws IOException, ParserException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return parse(bufferedReader, clazz);
        }
    }

    @Override
    public <T> List<T> parse(BufferedReader reader, Class<T> clazz) throws IOException, ParserException {
        List<T> targets = new ArrayList<>();
        String line = reader.readLine();
        while (line != null && !line.isBlank()) {
            log.debug("Parsing line {}", line);
            try {
                targets.add(parse(line, clazz));
            } catch (ReflectiveOperationException e) {
                throw new ParserException(e);
            }
            line = reader.readLine();
        }
        return targets;
    }

    private <T> T parse(String line, Class<T> clazz) throws ReflectiveOperationException, ParserException {
        LineParser<T> lineParser = new LineParserImpl<>(line, clazz.getDeclaredConstructor().newInstance());
        List<java.lang.reflect.Field> fields = getFields(clazz);
        if (fields.isEmpty()) {
            throw new ParserException("Cant parse data, no field definitions defined for class %s.".formatted(clazz));
        }
        for (java.lang.reflect.Field field : fields) {
            lineParser.parse(field);
        }
        return lineParser.getTarget();
    }

    private static <T> List<java.lang.reflect.Field> getFields(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Field.class))
                .toList();
    }


    @Override
    public <T> void write(OutputStream stream, List<T> dataList) throws IOException, ParserException {
        if (dataList.isEmpty()) {
            log.warn("Provided data to is empty, there is nothing to convert or write.");
        }
        for (T concreteObject : dataList) {
            List<java.lang.reflect.Field> fields = getFields(concreteObject.getClass());
            StringBuilder row = new StringBuilder();
            for (java.lang.reflect.Field field : fields) {
                try {
                    row.append(computeColumn(concreteObject, field));
                } catch (ReflectiveOperationException e) {
                    throw new ParserException(e);
                }
            }
            row.append("%n".formatted());
            stream.write(row.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private <T> String computeColumn(
            T concreteObject,
            java.lang.reflect.Field field
    ) throws IllegalAccessException, InvocationTargetException {
        Method getter = findGetter(field, concreteObject);
        String data = getter.invoke(concreteObject).toString();
        return data + padding(field, data.length());
    }

    private String padding(java.lang.reflect.Field field, int dataLength) {
        int fieldLength = field.getAnnotation(Field.class).end() - field.getAnnotation(Field.class).start();
        int unusedSpaceLength = fieldLength - dataLength;
        return padCharacter.repeat(unusedSpaceLength);
    }

    private <T> Method findGetter(java.lang.reflect.Field field, T instance) {
        Method setter = Arrays.stream(instance.getClass().getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> m.getName().startsWith("get"))
                .filter(m -> m.getName()
                        .equals("get%s%s".formatted(field.getName().substring(0, 1).toUpperCase(),
                                field.getName().substring(1)
                        )))
                .findFirst()
                .orElseThrow();
        log.debug("Found getter for field {} with name {}.", field.getName(), setter.getName());
        return setter;
    }

}
