package cphne.flatfileparser;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;


/**
 * Implementation of the {@link LineParser} interface.
 *
 * @param <T> @see {@link LineParser}
 */
@RequiredArgsConstructor
class LineParserImpl<T> implements LineParser<T> {

    private static final Logger log = LoggerFactory.getLogger(LineParserImpl.class);

    private final String line;

    private final T target;

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public void parse(Field field) throws ParserException {
        String data = extractData(field);
        Method setter = findSetter(field);
        try {
            invoke(setter, data);
        } catch (ReflectiveOperationException e) {
            throw new ParserException(e);
        }
    }

    private String extractData(Field field) {
        log.info("Working field {}", field.getName());
        cphne.flatfileparser.Field fieldAnnotation = field.getAnnotation(cphne.flatfileparser.Field.class);
        String data = line.substring(fieldAnnotation.start(), fieldAnnotation.end()).trim();
        log.info("Extracted data '{}' with boundaries {},{}", data, fieldAnnotation.start(), fieldAnnotation.end());
        return data;
    }

    private <T> Method findSetter(Field field) throws ParserException {
        Method setter = Arrays.stream(target.getClass().getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> m.getName().startsWith("set"))
                .filter(m -> m.getName()
                        .equals("set%s%s".formatted(field.getName().substring(0, 1).toUpperCase(),
                                field.getName().substring(1)
                        )))
                .findFirst()
                .orElseThrow(() -> new ParserException("No setter available for field '%s'.".formatted(field.getName())));
        log.debug("Found setter for field {} with name {}.", field.getName(), setter.getName());
        return setter;
    }

    private void invoke(
            Method setter, String data
    ) throws IllegalAccessException, InvocationTargetException, ParserException {
        if (setter.getParameterCount() != 1) {
            throw new ParserException("Cant invoke setter '%s', expected exactly one parameter, found %d.".formatted(
                    setter.getName(),
                    setter.getParameterCount()
            ));
        }
        Class<?> parameterType = setter.getParameterTypes()[0];
        log.debug("Identified parameter type {}.", parameterType.getTypeName());
        Object converted = convertData(parameterType, data);
        setter.invoke(target, converted);
    }

    private Object convertData(Class<?> parameterType, String data) throws ParserException {
        try {
            Class<?> type = MethodType.methodType(parameterType).wrap().returnType();
            return type.getConstructor(String.class).newInstance(data);
        } catch (ReflectiveOperationException e) {
            throw new ParserException(e);
        }
    }

}
