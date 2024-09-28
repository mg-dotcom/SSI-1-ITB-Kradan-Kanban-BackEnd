package ssi1.integrated.utils;

public class UriExtractor {
    // Method to extract the board ID from the URI
    public static String extractBoardId(String uri) {
        String[] parts = uri.split("/v3/boards/");
        if (parts.length > 1) {
            return parts[1].split("/")[0]; // Get the first part after the board segment
        }
        return null; 
    }

    // Method to extract the task ID from the URI
    public static String extractTaskId(String uri) {
        // Split the URI based on "/tasks/"
        String[] parts = uri.split("/tasks/");
        if (parts.length > 1) {
            // Get the part after "/tasks/" and split it by "/"
            return parts[1].split("/")[0]; // Return the first part after the task segment
        }
        return null;
    }

    // Method to extract the task ID from the URI
    public static String extractStatusId(String uri) {
        // Split the URI based on "/statuses/"
        String[] parts = uri.split("/statuses/");
        if (parts.length > 1) {
            // Get the part after "/tasks/" and split it by "/"
            return parts[1].split("/")[0]; // Return the first part after the task segment
        }
        return null;
    }
}
