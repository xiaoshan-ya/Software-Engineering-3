package nju.sentistrength.project.service;

import nju.sentistrength.project.core.Result;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public interface TextService {
    Result analyzeText(String text);

    ResponseEntity<InputStreamResource> analyzeFile(HttpServletResponse response, MultipartFile file, HttpServletRequest httpServletRequest) throws IOException;

    Result setOptions(String[] options);

    ResponseEntity<InputStreamResource> runMachineLearning(HttpServletResponse response, MultipartFile file, HttpServletRequest httpServletRequest) throws IOException;
}
