package com.ai.controller;

import com.ai.entity.MyFile;
import com.ai.result.PageResult;
import com.ai.result.Result;
import com.ai.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
/**
 * @author ：褚婧雯
 * @date ：2025/4/12 18:41
 * @description ：文件
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;


    /**
     * 文件上传
     * @param file
     * @param id
     * @return
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file,@RequestParam("id") String id){
        return fileService.upload(file,id);
    }

    /**
     * 分页查询文件
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping
    public Result<PageResult> getFile(@RequestParam("id")String id,
                                      @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                      @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        PageResult pageResult = fileService.pageQuery(id,pageNum,pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据文件id删除文件
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteFile(@RequestParam("id") String id){
        return fileService.deleteFile(id);
    }


}
