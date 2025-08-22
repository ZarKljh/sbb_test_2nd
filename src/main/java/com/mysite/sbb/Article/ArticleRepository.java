package com.mysite.sbb.Article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Page<Article> findAll(Pageable pageable);
    Page<Article> findAll(Specification<Article> spec, Pageable pageable);

    @Query("select "
            + "distinct A "
            + "from Article A "
            + "left outer join SiteUser U on A.author=U "
            + "where "
            + "   A.title like %:kw% "
            + "   or A.content like %:kw% "
            + "   or U.username like %:kw% ")
    Page<Article> findAllByKeyword(@Param("kw") String keyword, Pageable pageable);

}
