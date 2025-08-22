package com.mysite.sbb.Article;

import com.mysite.sbb.User.SiteUser;
import com.mysite.sbb.User.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "kw", defaultValue = "") String kw){
        Page<Article> paging = this.articleService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "article_list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String articleCreate(ArticleForm articleForm) {
        return "article_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String articleCreate(@Valid ArticleForm articleForm, BindingResult bindingResult, Principal principal) {
        if(bindingResult.hasErrors()) {
            return "article_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.articleService.create(articleForm.getTitle(), articleForm.getContent(), siteUser);
        return "redirect:/article/list"; // 질문 저장후 질문목록으로 이동
    }
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Article article = this.articleService.getArticle(id);
        model.addAttribute("article", article);
        return "article_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String articleModify(ArticleForm articleForm, @PathVariable("id") Integer id, Principal principal) {
        Article article = this.articleService.getArticle(id);
        if(!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        articleForm.setTitle(article.getTitle());
        articleForm.setContent(article.getContent());
        return "article_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String articleModify(@Valid ArticleForm articleForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "article_form";
        }
        Article article = this.articleService.getArticle(id);
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.articleService.modify(article, articleForm.getTitle(), articleForm.getContent());
        return String.format("redirect:/article/detail/%s", id);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String articleDelete(Principal principal, @PathVariable("id") Integer id) {
        Article article = this.articleService.getArticle(id);
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.articleService.delete(article);
        return "redirect:/";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String articleVote(Principal principal, @PathVariable("id") Integer id) {
        Article article = this.articleService.getArticle(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.articleService.vote(article, siteUser);
        return String.format("redirect:/article/detail/%s", id);
    }

}
