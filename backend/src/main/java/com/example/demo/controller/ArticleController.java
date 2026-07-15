package com.example.demo.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.DTO.article.AddArticleDTO;
import com.example.demo.DTO.article.ArticleQueryDTO;
import com.example.demo.DTO.article.UpdateArticleDTO;
import com.example.demo.DTO.flight.FlightDTO;
import com.example.demo.VO.article.ArticleDetailVO;
import com.example.demo.common.result.Result;
import com.example.demo.service.ArticleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleController {

    @Resource
    private ArticleService articleService;

    // ========================= 通用接口 ===================================================================================
    @GetMapping("/article/list")
    @SaCheckLogin
    public Result<IPage<?>> list(ArticleQueryDTO  query) {
        boolean isAdmin = StpUtil.hasRole("admin");
        return Result.data(articleService.listarticle(query, isAdmin));
    }


    // ========================= 用户接口 ===================================================================================
    @GetMapping("/article/{id}")
    @SaCheckRole("user")
    public ArticleDetailVO getDetail(@PathVariable Long id) {
        return articleService.getDetail(id);
    }

    @PostMapping("/article/like")
    @SaCheckRole("user")
    public Result<String> like(@RequestParam Long id) {
        articleService.like(id);
        return Result.success();
    }

    // ========================= 管理员接口 ==================================================================================

    // 添加文章
    @PostMapping("/article/add")
    @SaCheckRole("admin")
    public Result<String> add(@Valid @RequestBody AddArticleDTO dto) {
        articleService.addArticle(dto);
        return Result.success();
    }

    // 删除文章（支持批量）
    @PostMapping("/article/del")
    @SaCheckRole("admin")
    public Result<String> del(@RequestParam List<Long> ids) {
        articleService.removeByIds(ids);
        return Result.success();
    }

    // 修改文章
    @PostMapping("/article/update")
    @SaCheckRole("admin")
    public Result<String> update(@Valid @RequestBody UpdateArticleDTO dto) {
        articleService.updateArticle(dto);
        return Result.success();
    }
}
