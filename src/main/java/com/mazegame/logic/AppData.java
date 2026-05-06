package com.mazegame.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AppData {
    private static final String APP_DIR = ".jfxmaze";

    private AppData() {
    }

    public static Path resolve(String fileName) throws IOException {
        Path directory = Path.of(System.getProperty("user.home"), APP_DIR);
        Files.createDirectories(directory);
        return directory.resolve(fileName);
    }
}
