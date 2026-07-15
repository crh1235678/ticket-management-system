package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.DTO.article.AddArticleDTO;
import com.example.demo.DTO.article.ArticleQueryDTO;
import com.example.demo.DTO.article.UpdateArticleDTO;
import com.example.demo.VO.article.ArticleCatalogVO;
import com.example.demo.VO.article.ArticleDetailVO;
import com.example.demo.VO.article.ArticleVO;
import com.example.demo.common.exception.BussinessException;
import com.example.demo.common.result.ResultCode;
import com.example.demo.entity.Article;
import com.example.demo.mapper.ArticleMapper;
import com.example.demo.service.ArticleService;

import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;


    //=========================公共业务逻辑方法====================================================================

    // 普通用户返回 ArticleCatalogVO
    private IPage<ArticleCatalogVO> listArticleForUser(ArticleQueryDTO query) {
        Page<Article> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(query.getArticleType() != null, Article::getArticleType, query.getArticleType());
        wrapper.like(StringUtils.isNotBlank(query.getTitle()), Article::getTitle, query.getTitle());
        wrapper.orderByDesc(Article::getCreateTime);

        Page<Article> articlePage = articleMapper.selectPage(page, wrapper);

        List<ArticleCatalogVO> voList = articlePage.getRecords().stream().map(article -> {
            ArticleCatalogVO vo = new ArticleCatalogVO();
            BeanUtils.copyProperties(article, vo);
            return vo;
        }).toList();

        Page<ArticleCatalogVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(articlePage.getTotal());
        voPage.setCurrent(articlePage.getCurrent());
        voPage.setSize(articlePage.getSize());

        return voPage;
    }

    // 管理员返回 ArticleVO
    private IPage<ArticleVO> listArticleForAdmin(ArticleQueryDTO query) {
        Page<Article> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(query.getArticleType() != null, Article::getArticleType, query.getArticleType());
        wrapper.like(StringUtils.isNotBlank(query.getTitle()), Article::getTitle, query.getTitle());
        wrapper.orderByDesc(Article::getCreateTime);

        Page<Article> articlePage = articleMapper.selectPage(page, wrapper);

        List<ArticleVO> voList = articlePage.getRecords().stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);
            return vo;
        }).toList();

        Page<ArticleVO> voPage = new Page<>();
        voPage.setRecords(voList);
        voPage.setTotal(articlePage.getTotal());
        voPage.setCurrent(articlePage.getCurrent());
        voPage.setSize(articlePage.getSize());

        return voPage;
    }

    //============================实际业务逻辑==================================================================


    @Override
    public IPage<?> listarticle(ArticleQueryDTO query, boolean isAdmin) {
        if (isAdmin) {
            return listArticleForAdmin(query);  // 返回 IPage<ArticleVO>
        } else {
            return listArticleForUser(query);   // 返回 IPage<ArticleCatalogVO>
        }
    }

    // 查看文章详情
    @Override
    public ArticleDetailVO getDetail(Long id) {

        // 1️⃣ 查询文章
        Article article = articleMapper.selectById(id);

        if (article == null) {
            throw new BussinessException(ResultCode.ARTICLE_NOT_EXIST.getMessage());
        }

        // 2️⃣ 阅读量 +1
        articleMapper.articleViewCount(id);

        // 3️⃣ 转 VO
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        //System.out.println("article = " + vo);
        return vo;
    }

    @Override
    public void addArticle(AddArticleDTO dto) {

        // 校验文章标题
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getTitle, dto.getTitle());
        if (this.count(wrapper) > 0) {
            throw new BussinessException(ResultCode.PARAM_ERROR, "文章标题已存在");
        }

        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        this.save(article);
    }

    @Override
    public void updateArticle(UpdateArticleDTO dto) {
        if (dto.getId() == null) {
            throw new BussinessException(ResultCode.PARAM_ERROR);
        }
        Article article = getById(dto.getId());
        if (article == null) {
            throw new BussinessException(ResultCode.ARTICLE_NOT_EXIST);
        }
        if(articleMapper.updateArticle(dto, article.getVersion()) == 0){
            throw new BussinessException(ResultCode.PARAM_ERROR, "更新失败");
        };
    }

    @Override
    public void like(Long id) {
        System.out.println("id = " + id);
        if (id == null) {
            throw new BussinessException(ResultCode.PARAM_ERROR);
        }

        if(articleMapper.addLikeCount(id) == 0){
            throw new BussinessException(ResultCode.PARAM_ERROR, "更新失败");
        };
    }

}
