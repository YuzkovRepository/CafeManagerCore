package com.example.CafeManagerCore.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Service
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public ImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImageBytes(byte[] imageBytes, String folder, String name) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(imageBytes,
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", name
                ));
        return uploadResult.get("url").toString();
    }

    public byte[] downloadImage(String imageUrl) throws IOException {
        // Извлекаем public_id из URL Cloudinary
        String publicId = extractPublicIdFromUrl(imageUrl);
        return downloadImageByPublicId(publicId);
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Пример URL: https://res.cloudinary.com/cloudname/image/upload/v123456789/folder/filename.jpg
            URL url = new URL(imageUrl);
            String path = url.getPath();

            // Ищем часть после /upload/
            int uploadIndex = path.indexOf("/upload/");
            if (uploadIndex != -1) {
                String afterUpload = path.substring(uploadIndex + 8); // +8 для пропуска "/upload/"

                // Убираем версию если есть (v123456789/)
                if (afterUpload.startsWith("v")) {
                    int versionEnd = afterUpload.indexOf("/");
                    if (versionEnd != -1) {
                        afterUpload = afterUpload.substring(versionEnd + 1);
                    }
                }

                // Убираем расширение файла
                int dotIndex = afterUpload.lastIndexOf(".");
                if (dotIndex != -1) {
                    afterUpload = afterUpload.substring(0, dotIndex);
                }

                return afterUpload;
            }

            throw new IOException("Неверный формат URL Cloudinary: " + imageUrl);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка извлечения public_id из URL: " + imageUrl, e);
        }
    }

    public byte[] downloadImageByPublicId(String publicId) throws IOException {
        try {
            // Скачиваем изображение как byte array
            String url = cloudinary.url().generate(publicId);

            // Альтернативный способ через API Cloudinary
            Map result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            String secureUrl = (String) result.get("secure_url");

            // Скачиваем по URL
            return downloadFromUrl(secureUrl);

        } catch (Exception e) {
            throw new IOException("Ошибка загрузки изображения из Cloudinary: " + e.getMessage(), e);
        }
    }

    private byte[] downloadFromUrl(String url) throws IOException {
        try (InputStream in = new URL(url).openStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        }
    }
}
