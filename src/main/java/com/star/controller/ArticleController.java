package com.star.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.common.RedisService;
import com.star.exception.ResourceNotFoundException;
import com.star.model.Article;
import com.star.model.ArticleComment;
import com.star.model.CommonResult;
import com.star.model.User;
import com.star.service.ArticleCommentService;
import com.star.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by admin on 2016/6/18.
 */

@Controller
@RequestMapping("/article")
public class ArticleController {


    @Autowired
    private ArticleService articleService;
    @Resource
    private RedisService redisService;
    @Resource
    private ArticleCommentService articleCommentService;

    /**
     * 文章列表
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public ModelAndView index(){
        List<Article> articleList= articleService.queryArticleList();
        ModelAndView mv=new ModelAndView();
        mv.setViewName("articleList");
        mv.addObject("articleList",articleList);
        return mv;
    }
    /**
     * 文章列表
     * @return
     */
    @RequestMapping(value = "/queryArticleList",method = RequestMethod.GET)
    public ModelAndView queryArticleList(){

        List<Article> articleList= articleService.queryArticleList();
        ModelAndView mv=new ModelAndView();
        mv.setViewName("articleList");
        mv.addObject("articleList",articleList);
        return mv;
    }


    /**
     * 插入文章详情
     * @param article
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/insertArticle",method = RequestMethod.POST)
    public String insertArticle(Article article,HttpServletRequest request){
        User user=null;
        ObjectMapper mapper = new ObjectMapper();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("user".equals(cookie.getName())){
                String loginUser = redisService.get(cookie.getValue());
                if (null==loginUser){
                    try {
                        return mapper.writeValueAsString("error");
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        user = mapper.readValue(loginUser, new TypeReference<User>() {});
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (null==user){
            try {
                return mapper.writeValueAsString("error");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
        article.setUid(user.getId());
        article.setAuthor(user.getUsername());
        article.setArticleLength(article.getContent().length());
        articleService.insertArticle(article);
        try {
            return mapper.writeValueAsString("success");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询文章详情
     * @param articleId
     * @return
     */
    @RequestMapping(value = "/queryArticleDetail",method = RequestMethod.GET)
    public ModelAndView queryArticleDetail(@RequestParam int articleId){

        Article article= articleService.queryArticleById(articleId);
        if (null==article){
            throw new ResourceNotFoundException();
        }
        List<ArticleComment> articleComments = articleCommentService.queryArticleCommentListByArticleId(articleId);

        articleService.updateReadTimesById(articleId);
        ModelAndView mv=new ModelAndView();
        mv.setViewName("articleDetail");
        mv.addObject("article",article);
        mv.addObject("articleComments",articleComments);
        return mv;

    }


    /**
     * 保存评论
     * @param articleComment : 评论
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addArticleComment",method = RequestMethod.POST)
    public String addArticleComment(ArticleComment articleComment,HttpServletRequest request){

        CommonResult commonResult =new CommonResult();

        User user=null;
        ObjectMapper mapper = new ObjectMapper();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("user".equals(cookie.getName())){
                String loginUser = redisService.get(cookie.getValue());
                if (null==loginUser){
                    try {
                        commonResult.setResultKey("105");
                        commonResult.setResultValue("用户未登陆");
                        return mapper.writeValueAsString(commonResult);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        user = mapper.readValue(loginUser, new TypeReference<User>() {});
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        if (null==user){
            try {
                commonResult.setResultKey("105");
                commonResult.setResultValue("未找到用户");
                return mapper.writeValueAsString(commonResult);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        articleComment.setUid(user.getId());
        articleCommentService.addArticleComment(articleComment);

        commonResult.setResultKey("0");
        try {
            return mapper.writeValueAsString(commonResult);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;

    }







}
