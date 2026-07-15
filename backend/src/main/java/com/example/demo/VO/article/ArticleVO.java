package com.example.demo.VO.article;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.demo.common.enums.ArticleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleVO {

    private Long id;
    private String title;
    private String content;
    private String description;
    private String coverImg;
    private ArticleType articleType;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
