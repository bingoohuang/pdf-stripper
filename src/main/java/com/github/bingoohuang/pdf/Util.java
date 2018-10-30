package com.github.bingoohuang.pdf;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.joda.time.DateTime;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Util {
    /**
     * 规整时间日期表示字符串（例如：2018-10-23 21:47，2018/10/23 21:48:15，2018-10-23T21:48:15.235）。
     *
     * @param dateTimeStr 日期表示字符串
     * @return DateTime对象
     */

    public static DateTime regular(String dateTimeStr) {
        if (StringUtils.isEmpty(dateTimeStr)) return null;

        return new DateTimeRegular(dateTimeStr).regular();
    }

    /**
     * 四舍五入到指定小数位数。
     *
     * @param value 数值字符串
     * @param scale 指定小数位数
     * @return 四舍五入后的结果
     */
    public static String roundHalfUp(String value, int scale) {
        return new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 从输入流装载PDF文档对象。
     *
     * @param is 输入流
     * @return PDF文档对象
     */
    @SneakyThrows
    public static PDDocument loadPdf(InputStream is) {
        return PDDocument.load(is);
    }

    /**
     * 从字节数组装载PDF文档对象。
     *
     * @param bytes 字节数组
     * @return PDF文档对象
     */
    @SneakyThrows
    public static PDDocument loadPdf(byte[] bytes) {
        return PDDocument.load(bytes);
    }

    public static File loadClasspathFile(String classpath) {
        val filePath = Util.class.getResource("/").getPath() + classpath;
        return new File(filePath);
    }

    @SneakyThrows
    public static String loadClasspathResAsString(String classpath) {
        @Cleanup val is = loadClassPathRes(classpath);
        return CharStreams.toString(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    /**
     * 从类路径加载资源文件。
     *
     * @param classpath 类路径
     * @return 输入流
     */
    public static InputStream loadClassPathRes(String classpath) {
        return Util.class.getClassLoader().getResourceAsStream(classpath);
    }

    /**
     * 保存PdfImage图片对象为文件。
     *
     * @param pdfImage PdfImage图片对象
     */
    @SneakyThrows
    public static void saveImage(PdfImage pdfImage) {
        ImageIO.write(pdfImage.getImage(), pdfImage.getSuffix(),
                new File(pdfImage.getName() + "." + pdfImage.getSuffix()));
    }

    /**
     * 将文本写入文件。
     *
     * @param text     文本
     * @param fileName 文件名
     */
    @SneakyThrows
    public static void writeFile(String text, String fileName) {
        val file = new File(fileName);
        val sink = Files.asCharSink(file, Charsets.UTF_8);
        sink.write(text);
    }

    /**
     * 将输入流写入文件。
     *
     * @param is       输入流
     * @param fileName 文件名
     */
    @SneakyThrows
    public static void writeFile(InputStream is, String fileName) {
        val file = new File(fileName);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        Files.write(buffer, file);
    }


    /**
     * 网络下载内容对象
     */
    @Value @ToString(exclude = "content")
    public static class DownloadContent {
        private final String fileName;
        private final byte[] content;
    }

    /**
     * 从指定链接下载资源。
     *
     * @param fileUrl 指定链接
     * @return 下载内容
     */
    @SneakyThrows
    public static DownloadContent download(String fileUrl) {
        val url = new URL(fileUrl);
        @Cleanup("disconnect") val httpConn = (HttpURLConnection) url.openConnection();
        val responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("No file to download. Server replied HTTP code: " + responseCode);
        }

        val fileName = parseFileName(fileUrl, httpConn);
        val content = ByteStreams.toByteArray(httpConn.getInputStream());
        return new DownloadContent(fileName, content);
    }

    /**
     * 解析网络下载的文件名。
     *
     * @param fileUrl  下载路径
     * @param httpConn HTTP连接
     * @return 解析后文件名
     */
    private static String parseFileName(String fileUrl, HttpURLConnection httpConn) {
        val disposition = httpConn.getHeaderField("Content-Disposition");
        if (disposition != null) {
            int index = disposition.indexOf("filename="); // extracts file name from header field
            if (index > 0) {
                val sub = disposition.substring(index + 9);
                return restoreUtf8FileName(sub);
            }
        }

        // extracts file name from URL
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    /**
     * 将ISO编码的文件名重置为UTF-8编码的文件名。
     *
     * @param isoFileName ISO编码的文件名
     * @return UTF-8编码的文件名
     */
    private static String restoreUtf8FileName(String isoFileName) {
        val fileNameISOBytes = isoFileName.getBytes(StandardCharsets.ISO_8859_1);
        val fileNameUTF8 = new String(fileNameISOBytes, StandardCharsets.UTF_8);
        return isoFileName.length() == fileNameUTF8.length() ? isoFileName : fileNameUTF8;
    }
}
