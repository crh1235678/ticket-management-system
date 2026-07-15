package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.DTO.article.AddArticleDTO;
import com.example.demo.DTO.article.ArticleQueryDTO;
import com.example.demo.DTO.article.UpdateArticleDTO;
import com.example.demo.VO.article.ArticleDetailVO;
import com.example.demo.entity.Article;


public interface ArticleService extends IService<Article> {

    ArticleDetailVO getDetail(Long id);

    IPage<?> listarticle(ArticleQueryDTO query, boolean isAdmin);

    void addArticle(AddArticleDTO dto);

    void updateArticle(UpdateArticleDTO dto);

    void like(Long id);

}
