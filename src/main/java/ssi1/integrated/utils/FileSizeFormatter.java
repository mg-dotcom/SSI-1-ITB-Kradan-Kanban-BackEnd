package ssi1.integrated.utils;

public class FileSizeFormatter {
    public static String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 Bytes";
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}