package nju.sentistrength.project.web;

import nju.sentistrength.project.core.Result;
import nju.sentistrength.project.service.TextService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/text")
public class TextController {
    @Resource
    private TextService textService;

    @PostMapping("/basic")
    public Result<String> analyzeText(@RequestBody String text) {
        return textService.analyzeText(text);
    }

    @PostMapping("/file")
    public ResponseEntity<InputStreamResource> analyzeFile(HttpServletResponse response, @RequestParam MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        return textService.analyzeFile(response, file, httpServletRequest);
    }

    @PostMapping("/corpus")
    public Result<String> setOptions(@RequestParam("options") String[] options){
        return textService.setOptions(options);
    }

    @PostMapping("/machine_learning")
    public ResponseEntity<InputStreamResource> runMachineLearning(HttpServletResponse response, @RequestParam MultipartFile file, HttpServletRequest httpServletRequest) throws IOException {
        return textService.runMachineLearning(response, file, httpServletRequest);
    }
}
