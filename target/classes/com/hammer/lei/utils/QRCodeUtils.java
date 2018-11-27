package com.hammer.lei.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class QRCodeUtils {

    private static final int black = 0xFF000000;
    private static final int  white = 0xFFFFFFFF;


    public static String decode(InputStream input)
            throws IOException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(input))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
        return qrCodeResult.getText();
    }

    public static String generateQR(String text, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> params = new HashMap<>();
        params.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        params.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, params);
        return toAscii(bitMatrix);
    }

    private static String toAscii(BitMatrix bitMatrix) {
        StringBuilder builder = new StringBuilder();
        for (int r = 0; r < bitMatrix.getHeight(); r++) {
            for (int c = 0; c < bitMatrix.getWidth(); c++) {
                if (!bitMatrix.get(r, c)) {
                    builder.append("\033[47m   \033[0m");
                } else {
                    builder.append("\033[40m   \033[0m");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }


    public static String generateQRBase64(String text, int width, int height) throws WriterException,IOException {
        Map<EncodeHintType, Object> params = new HashMap<>();
        params.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        params.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, params);
        BufferedImage qrImageBuffer = createQRImageBuffers(bitMatrix);
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        ImageIO.write(qrImageBuffer, "png", os);
        Base64 base64 = new Base64();
        return new String(base64.encode(os.toByteArray()));
    }


    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? black : white);
            }
        }
        return image;
    }


    public static void writeToFile(BitMatrix matrix, String format, File file)
        throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        ImageIO.write(image, format, file);
    }

    public static void createQRImage(String content, int width, int height, String path, String fileName) throws Exception {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        if (StringUtils.isNotBlank(path)) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
        } else {
            path = "";
        }
        String suffix = "jpg";
        if (fileName.indexOf(".") <= -1) {
            fileName = fileName + "." + suffix;
        } else {
            suffix = fileName.split("[.]")[1];
        }
        fileName = path + fileName;
        File file = new File(fileName);
        writeToFile(bitMatrix, suffix, file);
    }


    public static BufferedImage createQRImageBuffer(String content, int width, int height) throws  Exception{
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage image = toBufferedImage(bitMatrix);
        return image;
    }

    public static BufferedImage createQRImageBuffers(BitMatrix bitMatrix){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BufferedImage image = toBufferedImage(bitMatrix);
        return image;
    }
}
