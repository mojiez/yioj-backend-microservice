package com.atyichen.yiojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.atyichen.yiojbackendcommon.common.ErrorCode;
import com.atyichen.yiojbackendcommon.constant.CommonConstant;
import com.atyichen.yiojbackendcommon.exception.BusinessException;
import com.atyichen.yiojbackendcommon.exception.ThrowUtils;
import com.atyichen.yiojbackendcommon.utils.SqlUtils;
import com.atyichen.yiojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.atyichen.yiojbackendmodel.model.entity.Question;
import com.atyichen.yiojbackendmodel.model.entity.User;
import com.atyichen.yiojbackendmodel.model.vo.QuestionVO;
import com.atyichen.yiojbackendquestionservice.mapper.QuestionMapper;
import com.atyichen.yiojbackendquestionservice.service.QuestionService;
import com.atyichen.yiojbackendserviceclient.service.UserFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        // 创建时，参数不能为空 add默认为true
        if (add) {
            // 如果add为true， 说明创建时参数不为空
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }

        // 如果说add为false， 或者说都不为空
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        Long questionId = question.getId();
//        // todo 1. 关联查询用户信息 这里VO里面没有需要查用户表才能展示的
//        Long userId = post.getUserId();
//        User user = null;
//        if (userId != null && userId > 0) {
//            user = userService.getById(userId);
//        }
//        UserVO userVO = userService.getUserVO(user);
//        postVO.setUser(userVO);
//        // todo 2. 已登录，获取用户点赞、收藏状态
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
//            postThumbQueryWrapper.in("postId", postId);
//            postThumbQueryWrapper.eq("userId", loginUser.getId());
//            PostThumb postThumb = postThumbMapper.selectOne(postThumbQueryWrapper);
//            postVO.setHasThumb(postThumb != null);
//            // 获取收藏
//            QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>();
//            postFavourQueryWrapper.in("postId", postId);
//            postFavourQueryWrapper.eq("userId", loginUser.getId());
//            PostFavour postFavour = postFavourMapper.selectOne(postFavourQueryWrapper);
//            postVO.setHasFavour(postFavour != null);
//        }
        return questionVO;
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {


        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        String tags = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        // 当前页号
        int current = questionQueryRequest.getCurrent();
        // 页面大小
        int pageSize = questionQueryRequest.getPageSize();
        // sortField表示需要排序的字段名称。可以是对象的某个属性
        String sortField = questionQueryRequest.getSortField();
        // 表示排序的顺序 asc：升序排序 desc： 降序排序
        String sortOrder = questionQueryRequest.getSortOrder();
        // 拼接查询条件
//        if (StringUtils.isNotBlank(title)) {
//            // 整个表达式的条件会作为一个整体， 用and逻辑运算连接到其他条件
//            queryWrapper.and(qw -> qw.like("title", title).or().like("content", searchText));
//        }
//        if (StringUtils.isNotBlank(content)) {
//            queryWrapper.and(qw -> qw.like("title", content).or().like("content", searchText));
//        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);

        List<String> tagList = JSONUtil.toList(tags, String.class);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 从里面是Question的page里面封装出 里面是QuestionVO的Page并返回
     * 我有了Post 就有PostId， 也有对应创建用户的UserID
     * 1。 要查User的信息（集中查 放进map中）
     * 2. 要查点赞记录（当前用户是否给这个帖子点过赞） （集中查当前用户给PostList中的所有点赞情况 放进Map中， 不然就只能每次用一个PostId和一个loginUser去查点赞表）
     * 3. 要查收藏记录 与2同理
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        // 如果里面没有记录， 直接返回
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }

        // 1. 关联查询用户信息
        // 查询这套题是哪个用户创建的 map(Question::getUserId) 等价于 map(post -> {post.getUserId()}) 表示对每个post对象调用getUserId方法， 最后得到一个全是UserId的流
        // 这里的Set的元素的值是可以重复的， 因为Long没有重写？？ 错！ Long已经重写了equals方法和hashCode方法， 所以不允许值重复！！
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet()); // 得到了所有创建过题目的userId的集合（去重）

        // 根据用户id 查询用户信息 用一个Map存起来
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();

        // 获取登陆的用户信息
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser != null) {
            // 获取每个题目的Id
            Set<Long> questionIdSet = questionList.stream().map(Question::getId).collect(Collectors.toSet());
        }
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }
}




