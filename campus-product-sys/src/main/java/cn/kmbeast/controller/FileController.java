package cn.kmbeast.controller;

import cn.kmbeast.utils.IdFactoryUtil;
import cn.kmbeast.utils.PathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件前端控制器
 *
 * @since 2024-03-22
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${my-server.api-context-path}")
    private String API;

    private final static String URL = "http://localhost:21090";

    /**
     * 文件上传
     *
     * @param multipartFile 文件流
     * @return 响应
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        String uuid = IdFactoryUtil.getFileId();
        String fileName = uuid + multipartFile.getOriginalFilename();
        String fileUrl = URL + API + "/file/getFile?fileName=" + fileName;
        Map<String, Object> result = new HashMap<>();
        try {
            if (uploadFileInternal(multipartFile, fileName)) {
                result.put("code", 200);
                result.put("data", fileUrl);
                return result;
            }
        } catch (IOException e) {
            //
        }
        result.put("code", 400);
        result.put("msg", "文件上传异常");
        return result;
    }

    /**
     * 视频上传
     *
     * @param multipartFile 文件流
     * @return 响应
     */
    @PostMapping("/video/upload")
    public Map<String, Object> videoUpload(@RequestParam("file") MultipartFile multipartFile) {
        String uuid = IdFactoryUtil.getFileId();
        String fileName = uuid + multipartFile.getOriginalFilename();
        String fileUrl = URL + API + "/file/getFile?fileName=" + fileName;
        Map<String, Object> result = new HashMap<>();
        try {
            if (uploadFileInternal(multipartFile, fileName)) {
                result.put("code", 200);
                result.put("data", fileUrl);
                return result;
            }
        } catch (IOException e) {
            //
        }
        result.put("code", 400);
        result.put("msg", "文件上传异常");
        return result;
    }

    /**
     * 上传文件到磁盘
     */
    private boolean uploadFileInternal(MultipartFile multipartFile, String fileName) throws IOException {
        File fileDir = new File(PathUtils.getClassLoadRootPath() + "/pic");
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return false;
            }
        }
        File file = new File(fileDir.getAbsolutePath() + "/" + fileName);
        if (file.exists()) {
            if (!file.delete()) {
                return false;
            }
        }
        if (file.createNewFile()) {
            multipartFile.transferTo(file);
            return true;
        }
        return false;
    }

    /**
     * 查看图片资源
     *
     * @param imageName 文件名
     * @param response  响应
     * @throws IOException 异常
     */
    private String getContentTypeByExtension(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }

    @GetMapping("/getFile")
    public void getImage(@RequestParam("fileName") String imageName,
                         HttpServletResponse response) throws IOException {
        // 防止路径穿越
        if (imageName.contains("..") || imageName.contains("/") || imageName.contains("\\")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        File fileDir = new File(PathUtils.getClassLoadRootPath() + "/pic");
        File image = new File(fileDir.getAbsolutePath() + "/" + imageName);
        if (!image.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(getContentTypeByExtension(imageName));
        response.setContentLengthLong(image.length());
        try (FileInputStream fileInputStream = new FileInputStream(image);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] bytes = new byte[4096];
            int len;
            while ((len = fileInputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
        }
    }

}
