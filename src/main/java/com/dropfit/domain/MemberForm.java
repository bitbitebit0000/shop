package com.dropfit.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    private String name;
    private String email;
    private String password;
    private String city;
    private String street;
    private String zipcode;
}

