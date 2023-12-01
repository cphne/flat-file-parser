package cphne.flatfileparser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents the definition of a field for a record in a flat file
 * <p>
 * Define the start and end positions of a field. Is required by {@link FlatFileParser} to parse records of a flat file
 * and the referenced fields.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    /**
     * 
     * @return the start position of a field
     */
    int start();

    /**
     * 
     * @return the end position of a field
     */
    int end();
}
