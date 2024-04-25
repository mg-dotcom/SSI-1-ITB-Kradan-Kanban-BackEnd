package ssi1.integrated.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskEnum {
    TO_DO("To Do"),

    DOING("Doing"),
    DONE("Done"),
    NO_STATUS("No Status");

    private final String status;

    TaskEnum(String status){
        this.status=status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}
