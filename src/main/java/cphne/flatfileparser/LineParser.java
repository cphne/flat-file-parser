package cphne.flatfileparser;

import java.lang.reflect.Field;

/**
 * An interface which defines behaviour for parsing a single line of a flat file.
 * 
 * @param <T>  the type of object to which the data is added
 */
public interface LineParser<T> {

    /**
     * Read data from the flat file and write the extracted value to the provided target object
     * @param field  the field which to write data to
     * @throws ParserException if the identified setter method does not have a single parameter, or if a {@link ReflectiveOperationException}
     * is thrown in which case the exception is wrapped in a {@link ParserException}
     */
    void parse(Field field) throws ParserException;

    /**
     * Retrieve the modified instance
     * 
     * @return {@code T} the instance which holds the fields and where the data was/will be written to
     */
    T getTarget();

}
