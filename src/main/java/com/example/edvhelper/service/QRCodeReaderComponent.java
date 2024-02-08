package com.example.edvhelper.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class QRCodeReaderComponent {

    public String readQRCode(File file) {
        try {
            BufferedImage image = ImageIO.read(file);

            MultiFormatReader reader = new MultiFormatReader();
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (IOException | NotFoundException e) {
            // Обработка ошибок, если не удается считать QR-код
            return "Не удалось считать QR-код: " + e.getMessage();
        }
    }
}