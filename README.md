# flat-file-parser
Implementation of a parser for flat file databases.

Flat file parser is a small java project that allows developers to parse and write flat files in a simple an
concise manner.

Provides an Interface to parse flat files and map its contents into java objects.
To represent and process the flat file definition/structure the project follows declarative approach make use of
annotations. Also allows to create flat files using the same definition as when parsing existing files.

## Prerequisites
Before you begin, ensure you have met the following requirements:
* You can support Java 17
* Since this project is not published to the central repository, make sure you can add .jar files to your classpath

## 'Installing'

To install flat file parser, follow these steps:  
Download the latest *.jar release from the release tab and add the downloaded .jar to your classpath

## Using flat-file-parser

To use flat-file-parser, follow these steps:
* Create a Class with fields specifying the record format
* Use the factory to create a parser instance
* call `parse` to parse a flat file and map its data into a list of objects

```java
import cphne.flatfileparser.Field;
import cphne.flatfileparser.FlatFileParser;
import cphne.flatfileparser.ParserFactory;

import java.nio.file.Path;

class Main() {

    static class DataObject {
        @Field(start = 0, end = 3)
        private int age;
        
        @Field(start = 3, end = 10)
        private String name;
        
        // getters and setters...
    }

    public static void main(String[] args) {
        FlatFileParser parser = ParserFactory.newInstance();
        List<DataObject> data = parser.parse(Path.of("path/to/your/flatfile"), DataObject.class);
        // process read data...
    }
}
```

## Contributing to flat-file-parser
This is my first 'public' project on GitHub, I would be grateful for any constructive feedback. Should you have 
suggestions about features, see the contact section of this readme.

For a general guideline, to contribute to flat-file-parser, follow these steps:

1. Fork this repository.
2. Create a branch: `git checkout -b <branch_name>`.
3. Make your changes and commit them: `git commit -m '<commit_message>'`
4. Push to the original branch: `git push origin <project_name>/<location>`
5. Create the pull request.


## Contact

If you want to contact me you can reach me at cph.neven@gmail.com

## License
<!--- If you're not sure which open license to use see https://choosealicense.com/--->

This project uses the following license: [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html).
