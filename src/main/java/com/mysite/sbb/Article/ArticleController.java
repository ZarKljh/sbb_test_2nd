package com.mysite.sbb.Article;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/list")
    public String list(Model model){
        List<Article> articleList = this.articleService.getList();
        model.addAttribute("articleList", articleList);
        return "article_list";
    }
    @GetMapping("/create")
    public String articleCreate(ArticleForm articleForm) {
        return "article_form";
    }
    @PostMapping("/create")
    public String articleCreate(@Valid ArticleForm articleForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "article_form";
        }
        this.articleService.create(articleForm.getTitle(), articleForm.getContent());
        return "redirect:/article/list"; // 질문 저장후 질문목록으로 이동
    }
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Article article = this.articleService.getArticle(id);
        model.addAttribute("article", article);
        return "article_detail";
    }

}
