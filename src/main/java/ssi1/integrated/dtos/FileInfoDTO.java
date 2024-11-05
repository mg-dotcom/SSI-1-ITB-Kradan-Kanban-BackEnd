package ssi1.integrated.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileInfoDTO {
    private  String fileName;
    private  long fileSize;
}
