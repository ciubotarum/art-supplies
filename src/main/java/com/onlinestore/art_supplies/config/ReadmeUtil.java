package com.onlinestore.art_supplies.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadmeUtil {
    public static String loadReadme() {
        try {
            return new String(Files.readAllBytes(Paths.get("README.md")));
        } catch (IOException e) {
            throw new RuntimeException("Could not load README.md", e);
        }
    }
}

