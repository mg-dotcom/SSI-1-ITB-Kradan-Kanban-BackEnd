package ssi1.integrated.exception.respond;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ssi1.integrated.dtos.FileInfoDTO;
import ssi1.integrated.utils.FileSizeFormatter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class FileErrorResponse {
    private final String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    private final int status;
    private final String message;
    private final String instance;
    private List<ValidationFileError> fileErrors; // List for file errors


    @Getter
    @RequiredArgsConstructor
    private static class ValidationFileError {
        private final String fileName; // Store the file name for errors
        private final String fileSize;
    }


    // Method to add a file error
    public void addValidationFileError(FileInfoDTO fileInfo) {
        if (fileErrors == null) {
            fileErrors = new ArrayList<>();
        }
        fileErrors.add(new ValidationFileError( fileInfo.getFileName() ,  FileSizeFormatter.formatFileSize(fileInfo.getFileSize())));
    }
}
