package com.example.demo.VO.article;

import com.example.demo.common.enums.ArticleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDetailVO {

    private Long id;
    private String title;
    private String content;
    private ArticleType articleType;

    private Long viewCount;
    private Long likeCount;

    private LocalDateTime createTime;
}
