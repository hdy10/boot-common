package com.github.hdy.common.util;

import cn.hutool.core.util.ImageUtil;
import cn.hutool.extra.qrcode.QrConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * 二维码工具栏
 *
 * @author 贺大爷
 * @date 2020/02/11
 */
public class QrCodeUtil extends cn.hutool.extra.qrcode.QrCodeUtil {
    // 默认宽高
    private final static int width = 200;
    private final static int height = 200;

    public static byte[] generatePng(String content) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        generate(content, "png", out);
        return out.toByteArray();
    }

    public static void generate(String content, String imageType, OutputStream out) {
        BufferedImage image = generate(content, width, height);
        ImageUtil.write(image, imageType, out);
    }

    public static File generate(String content, File targetFile) {
        BufferedImage image = generate(content, width, height);
        ImageUtil.write(image, targetFile);
        return targetFile;
    }

    public static BufferedImage generate(String content) {
        return generate(content, new QrConfig(width, height));
    }

    public static String decode(URL imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(imageUrl);
        return decode(image);
    }
}
