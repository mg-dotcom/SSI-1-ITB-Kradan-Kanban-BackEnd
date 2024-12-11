package ssi1.integrated.utils;

public class UriExtractor {
    public static String extractBoardId(String uri) {
        String[] parts = uri.split("/v3/boards/");
        if (parts.length > 1) {
            return parts[1].split("/")[0];
        }
        return null;
    }

    public static String extractTaskId(String uri) {
        String[] parts = uri.split("/tasks/");
        if (parts.length > 1) {
            return parts[1].split("/")[0];
        }
        return null;
    }

    public static String extractStatusId(String uri) {
        String[] parts = uri.split("/statuses/");
        if (parts.length > 1) {
            return parts[1].split("/")[0];
        }
        return null;
    }
}
