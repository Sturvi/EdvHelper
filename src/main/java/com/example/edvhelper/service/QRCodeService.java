package com.example.edvhelper.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class QRCodeService {

    public File generateQRCode(String text) {
        String baseUrl = "https://monitoring.e-kassa.gov.az/#/index?doc=";
        String finalUrl = baseUrl + text;
        int size = 250; // Размер изображения QR-кода

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(finalUrl, BarcodeFormat.QR_CODE, size, size);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Создаем временный файл
            Path tempFilePath = Files.createTempFile("qr_code_", ".png");
            File tempFile = tempFilePath.toFile();
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", tempFilePath);

            return tempFile;
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
