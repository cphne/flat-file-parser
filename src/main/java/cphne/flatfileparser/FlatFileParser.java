package cphne.flatfileparser;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Declares operations for parsing and writing of files formatted as a flat file
 * <p>
 * Parse functionality supports multiple variants of input types. Parses the file and maps its content into Objects 
 * of a provided type. The flat file format is specified in the Class the content should be mapped into via the
 * {@link Field} annotation. 
 * <p>
 * Transferring/writing objects to a flat file works similar to the parse operations. The Class of objects that 
 * should be written is required to provide the flat file format via the {@link Field} annotation.
 */
public interface FlatFileParser {

    /**
     * Parse a file and map the contained data to Objects
     *
     * @param file  the file which contents to parse
     * @param clazz  the type the data should be mapped to
     * @return {@code List<T>} the list of objects the data was mapped to
     * @param <T> the type of objects in which the data will be mapped
     * @throws IOException if the file cant be read or opened
     * @throws ParserException if the parser cant parse the file
     */
    <T> List<T> parse(File file, Class<T> clazz) throws IOException, ParserException;

    /**
     * Parse a file and map the contained data to Objects
     *
     * @param path  the path to a file which contents to parse
     * @param clazz  the type the data should be mapped to
     * @return {@code List<T>} the list of objects the data was mapped to
     * @param <T> the type of objects in which the data will be mapped
     * @throws IOException if the file cant be read or opened
     * @throws ParserException if the parser cant parse the file
     */
    <T> List<T> parse(Path path, Class<T> clazz) throws IOException, ParserException;

    /**
     * Parse data of an InputStream and map the contained data to Objects
     *
     * @param inputStream  the stream which contains the contents to parse
     * @param clazz  the type the data should be mapped to
     * @return {@code List<T>} the list of objects the data was mapped to
     * @param <T> the type of objects in which the data will be mapped
     * @throws IOException if the stream cant be read
     * @throws ParserException if the parser cant parse the file
     */
    <T> List<T> parse(InputStream inputStream, Class<T> clazz) throws IOException, ParserException;

    /**
     * Parse a content of a BufferedReader and map the contained data to Objects
     *
     * @param reader  the reader which contents to parse
     * @param clazz  the type the data should be mapped to
     * @return {@code List<T>} the list of objects the data was mapped to
     * @param <T> the type of objects in which the data will be mapped
     * @throws IOException if the reader cant be read
     * @throws ParserException if the parser cant parse the file
     */
    <T> List<T> parse(BufferedReader reader, Class<T> clazz) throws IOException, ParserException;

    /**
     * Convert a list of Objects to flat file
     * 
     * @param stream  OutputStream to write the data to
     * @param data  List of objects to convert
     * @param <T> the type of objects which will be converted
     * @throws IOException if the data cant be written to the stream
     * @throws ParserException if the parser cant convert the objects to the specified flat file format
     */
    <T> void write(OutputStream stream, List<T> data) throws IOException, ParserException;
}
