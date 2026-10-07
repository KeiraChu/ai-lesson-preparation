package com.ai.service;

import com.ai.result.PageResult;
import com.ai.result.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ：褚婧雯
 * @date ：2025/4/12 18:51
 * @description ：文件接口
 */
public interface FileService {
    /**
     * 文件上传
     * @param file
     * @param id
     * @return
     */
    Result upload(MultipartFile file, String id);

    /**
     * 分页查询文件
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult pageQuery(String id, Integer pageNum, Integer pageSize);

    /**
     * 根据文件id删除文件
     * @param id
     * @return
     */
    Result deleteFile(String id);
}
