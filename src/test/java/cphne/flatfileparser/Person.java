package cphne.flatfileparser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {

    @Field(start = 0, end = 8)
    private String firstname;

    @Field(start = 8, end = 16)
    private String lastname;

    @Field(start = 16, end = 19)
    private int age;

    @Field(start = 19, end = 29)
    private String gender;
}
