package com.example.demo.VO.article;

import com.example.demo.common.enums.ArticleType;
import lombok.Data;

@Data
public class ArticleCatalogVO {
    private Long id;
    private String title;
    private String description;
    private String coverImg;
    private ArticleType articleType;
    private Long viewCount;
    private Long likeCount;
}
