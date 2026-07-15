package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.DTO.article.UpdateArticleDTO;
import com.example.demo.common.enums.OrderStatus;
import com.example.demo.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


public interface ArticleMapper extends BaseMapper<Article> {

    @Update("""
      UPDATE article
      SET view_count = view_count + 1
      WHERE id = #{id}
      """)
    int articleViewCount(Long id);

    @Update("""
      UPDATE article
      SET like_count = like_count + 1
      WHERE id = #{id}
      """)
    int addLikeCount(Long id);

    @Update("""
    UPDATE article
    SET title = #{article.title},
        content = #{article.content},
        article_Type = #{article.articleType},
        cover_Img = #{article.coverImg},
        description = #{article.description},
        version = version + 1
    WHERE id = #{article.id}
      AND version = #{version}
""")
    int updateArticle(@Param("article") UpdateArticleDTO article,
                      @Param("version") Integer version);
}
