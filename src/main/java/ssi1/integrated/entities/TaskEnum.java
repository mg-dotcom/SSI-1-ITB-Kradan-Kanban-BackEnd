package ssi1.integrated.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskEnum {
    TO_DO("To do"),

    DOING("Doing"),
    DONE("Done"),
    NO_STATUS("No status");

    private final String status;

    TaskEnum(String status){
        this.status=status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }
}
