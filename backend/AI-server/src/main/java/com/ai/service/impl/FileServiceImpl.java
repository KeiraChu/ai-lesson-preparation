package com.ai.service.impl;

import com.ai.context.UserContext;
import com.ai.entity.MyFile;
import com.ai.mapper.FileMapper;
import com.ai.properties.FileProperties;
import com.ai.result.PageResult;
import com.ai.result.Result;
import com.ai.service.FileService;
import com.ai.utils.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * @author ：褚婧雯
 * @date ：2025/4/12 18:51
 * @description ：文件接口实现
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, MyFile> implements FileService {

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx", "pptx", "xlsx", "txt", "md");

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private FileProperties fileProperties;

    /**
     * 文件上传
     * @param file
     * @param id
     * @return
     */
    @Override
    public Result upload(MultipartFile file,String id) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件为空或超过 20MB 限制");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("文件名或扩展名无效");
        }
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("仅支持 PDF、DOCX、PPTX、XLSX、TXT 和 Markdown 文件");
        }
        String safeOriginalName = Paths.get(fileName).getFileName().toString();
        String newFileName = UUID.randomUUID() + "." + extension;
        try {
            Path uploadDirectory = Paths.get(fileProperties.getUploadPath()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDirectory);
            Path destination = uploadDirectory.resolve(newFileName).normalize();
            if (!destination.startsWith(uploadDirectory)) {
                throw new IllegalArgumentException("非法文件路径");
            }
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MyFile myFile = new MyFile();
        myFile.setId(idWorker.nextId());
        myFile.setUserid(UserContext.getUserId());
        myFile.setTitle(safeOriginalName);
        myFile.setCreateTime(LocalDateTime.now().toDate());
        myFile.setUrl("http://"+fileProperties.getIpPort()+"/uploads/"+newFileName);
        fileMapper.insert(myFile);
        return Result.success(myFile);
    }

    /**
     * 分页查询文件
     * @param id 用户id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult pageQuery(String id, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<MyFile> page = fileMapper.pageQuery(String.valueOf(UserContext.getUserId()));
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据文件id删除文件
     * @param id
     * @return
     */
    @Override
    public Result deleteFile(String id) {
        MyFile myFile = fileMapper.selectById(id);
        if (myFile == null || !UserContext.getUserId().equals(myFile.getUserid())) {
            throw new IllegalArgumentException("文件不存在或无权访问");
        }
        myFile.setDeleted(0);
        fileMapper.updateById(myFile);
        return Result.success("删除成功");
    }
}
