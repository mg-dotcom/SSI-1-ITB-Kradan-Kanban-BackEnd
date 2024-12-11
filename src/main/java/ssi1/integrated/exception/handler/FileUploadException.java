package ssi1.integrated.exception.handler;

import ssi1.integrated.dtos.FileInfoDTO;
import java.util.ArrayList;
import java.util.List;


public class FileUploadException extends RuntimeException {
    private final List<FileInfoDTO> fileErrors;

    public FileUploadException(String message, List<FileInfoDTO> fileErrors) {
        super(message);
        this.fileErrors = fileErrors != null ? new ArrayList<>(fileErrors) : new ArrayList<>();
    }

    public List<FileInfoDTO> getFileErrors() {
        return fileErrors;
    }
}
