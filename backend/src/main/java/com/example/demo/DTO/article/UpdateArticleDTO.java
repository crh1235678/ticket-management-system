package com.example.demo.DTO.article;

import com.example.demo.common.enums.ArticleType;
import lombok.Data;

@Data
public class UpdateArticleDTO {
    private Long id;
    private String title;
    private String content;
    private ArticleType articleType;
    private String coverImg;
    private String description;
}
