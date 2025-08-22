package com.mysite.sbb.Article;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.User.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<Article> getList(){
        return this.articleRepository.findAll();
    }
    public void create(String title, String content, SiteUser user) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setCreateDate(LocalDateTime.now());
        article.setAuthor(user);
        this.articleRepository.save(article);
    }
    public Article getArticle(Integer id){
        Optional<Article> oa =  this.articleRepository.findById(id);
        if (oa.isPresent()) {
            return oa.get();
        } else {
            throw new DataNotFoundException("Article is not found");
        }
    }
    public Page<Article> getListSpec(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Article> spec = search(kw);
        return this.articleRepository.findAll(spec, pageable);
    }
    public Page<Article> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        //return (Page<Article>) this.articleRepository.findAllByKeyword(kw, pageable);
        return this.articleRepository.findAllByKeyword(kw, pageable);
    }
    public void modify(Article article, String title, String content) {
        article.setTitle(title);
        article.setContent(content);
        article.setModifyDate(LocalDateTime.now());
        this.articleRepository.save(article);
    }
    public void delete(Article article) {
        this.articleRepository.delete(article);
    }
    public void vote(Article article, SiteUser siteUser) {
        article.getVoter().add(siteUser);
        this.articleRepository.save(article);
    }

    private Specification<Article> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Article> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Article, SiteUser> u1 = q.join("author", JoinType.LEFT);

                return cb.or(cb.like(q.get("title"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"));    // 질문 작성자
            }
        };
    }
}
