package com.volasoftware.tinder.constants;

import lombok.Getter;

@Getter
public enum Gender {
    FEMALE("female", "/src/img/female.png"),
    MALE("male", "/src/img/male.png"),
    OTHER("other", "/src/img/other.png");

    Gender(String text, String path) {
    }
}
