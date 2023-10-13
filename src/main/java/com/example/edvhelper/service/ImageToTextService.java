package com.example.edvhelper.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ImageToTextService {

    private final ITesseract tesseract;

    public ImageToTextService() {
        this.tesseract = new Tesseract();

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Путь для Windows
            this.tesseract.setDatapath("C:\\Users\\Sturvi\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata");
        } else if (os.contains("nux") || os.contains("nix") || os.contains("mac")) {
            // Путь для Linux/Unix/Mac
            this.tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        } else {
            throw new RuntimeException("Unsupported operating system");
        }

        this.tesseract.setLanguage("eng"); // Установка языка на английский
    }

    public String extractText(File image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new RuntimeException("Error while extracting text from image", e);
        }
    }
}

