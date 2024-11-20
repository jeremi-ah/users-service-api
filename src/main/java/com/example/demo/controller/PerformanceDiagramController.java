package com.example.demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class PerformanceDiagramController {

    private static final String DIAGRAMS_DIR = "diagrams/";
    private static final String FILE_NAME = "all_operations_diagram.puml";

    @GetMapping("/api/diagrams/download")
    public ResponseEntity<Resource> downloadDiagram() throws Exception {
        Path filePath = Paths.get(DIAGRAMS_DIR + FILE_NAME);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + FILE_NAME + "\"")
                    .body(resource);
        } else {
            throw new RuntimeException("Diagram file not found or unreadable");
        }
    }
}
