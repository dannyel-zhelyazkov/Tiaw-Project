package dny.apps.tiaw.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadImage(MultipartFile multipartFile) throws IOException;
}
