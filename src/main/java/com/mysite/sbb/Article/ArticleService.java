package com.mysite.sbb.Article;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<Article> getList(){
        return this.articleRepository.findAll();
    }
    public void create(String title, String content) {
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setCreateDate(LocalDateTime.now());
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
}
