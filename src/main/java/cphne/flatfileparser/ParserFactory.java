package cphne.flatfileparser;

/**
 * Flat file parser Factory. Providing Methods to create a parser
 */
public interface ParserFactory {

    /**
     * Creates a new parser with the default implementation.
     * 
     * @return the new parser instance
     */
    static FlatFileParser newInstance() {
        return new FlatFileParserImpl();
    }

    /**
     * Creates a new parser with the default implementation.
     * 
     * @param paddingCharacter  the character to be used by the parser to pad fields
     * @return the new parser instance
     */
    static FlatFileParser newInstance(String paddingCharacter) {
        return new FlatFileParserImpl(paddingCharacter);
    }
}
