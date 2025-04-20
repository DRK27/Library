package com.example.library.data.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QRCodeUtil {

    public static Bitmap generateQRCode(String bookId, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            BitMatrix bitMatrix = qrCodeWriter.encode(bookId, BarcodeFormat.QR_CODE, width, height, hints);
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap generateQRCodeWithLogo(String bookId, int width, int height, Bitmap logo) {
        // Generate the QR code
        Bitmap qrCode = generateQRCode(bookId, width, height);
        
        if (qrCode == null || logo == null) {
            return qrCode;
        }
        
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result.setPixel(x, y, qrCode.getPixel(x, y));
            }
        }
        
        int logoSize = width / 4;
        int logoX = (width - logoSize) / 2;
        int logoY = (height - logoSize) / 2;
        
        Bitmap resizedLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true);
        
        for (int x = 0; x < logoSize; x++) {
            for (int y = 0; y < logoSize; y++) {
                int pixel = resizedLogo.getPixel(x, y);
                if (pixel != Color.TRANSPARENT) {
                    result.setPixel(logoX + x, logoY + y, pixel);
                }
            }
        }
        
        return result;
    }

    public static String extractBookIdFromQRCode(Bitmap qrCodeBitmap) {
        // This is a placeholder. In a real app, you would use a QR code scanner library
        // like ZXing to extract the book ID from the QR code bitmap.
        return null;
    }
} 