package ssi1.integrated.enums;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum TaskStatus {
    TO_DO,
    DOING,
    DONE,
    NO_STATUS;

    private static final Map<String, TaskStatus> valueMap = new HashMap<>();

    static {
        // Populate the valueMap with mappings between lowercase database values and enum constants
        valueMap.put("to do", TO_DO);
        valueMap.put("doing", DOING);
        valueMap.put("done", DONE);
        valueMap.put("no status", NO_STATUS);
    }

    public static TaskStatus fromDatabaseValue(String value) {
        // Convert the database value to lowercase and retrieve the corresponding enum constant from the valueMap
        return valueMap.get(value.toLowerCase());
    }
}
