package com.example.demo.DTO.article;

import com.example.demo.common.enums.ArticleType;
import lombok.Data;

@Data
public class ArticleQueryDTO {
    private int pageNum = 1;
    private int pageSize = 10;

    private String title;
    private ArticleType articleType;
}
