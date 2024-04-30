package ssi1.integrated.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskEnum {
    TO_DO,

    DOING,
    DONE,
    NO_STATUS;

//    private final String status;
//
//    TaskEnum(String status){
//        this.status=status;
//    }
//
//    @JsonValue
//    public String getStatus() {
//        return status;
//    }
}
