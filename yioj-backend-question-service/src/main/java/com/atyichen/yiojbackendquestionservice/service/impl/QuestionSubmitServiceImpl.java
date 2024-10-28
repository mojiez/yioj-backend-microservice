package com.atyichen.yiojbackendquestionservice.service.impl;

import com.atyichen.yiojbackendcommon.common.ErrorCode;
import com.atyichen.yiojbackendcommon.exception.BusinessException;
import com.atyichen.yiojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.atyichen.yiojbackendmodel.model.entity.Question;
import com.atyichen.yiojbackendmodel.model.entity.QuestionSubmit;
import com.atyichen.yiojbackendmodel.model.entity.User;
import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;
import com.atyichen.yiojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.atyichen.yiojbackendquestionservice.mq.MyMessageProducer;
import com.atyichen.yiojbackendquestionservice.service.QuestionService;
import com.atyichen.yiojbackendquestionservice.service.QuestionSubmitService;
import com.atyichen.yiojbackendserviceclient.service.JudgeFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 *
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {
    @Resource
    private MyMessageProducer myMessageProducer;
    @Resource
    private QuestionService questionService;
    @Autowired
    @Lazy
    private JudgeFeignClient judgeFeignClient;
//    @Autowired
//    public void setJudgeService(JudgeService judgeService) {
//        this.judgeService = judgeService;
//    }
    @Override
    public QuestionSubmitVO doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交
        long userId = loginUser.getId();
        // 每个用户串行提交？？
        // todo 这里原本是每个用户串行点赞， 改为每个用户串行提交（可行， 因为可能统计提交数）
        // 锁必须要包裹住事务方法
        // 这段代码的目的是在使用 AOP（面向切面编程）时，通过 AopContext.currentProxy() 获取当前的代理对象，并在对用户提交的问题进行处理时使用 synchronized 关键字进行同步。下面是逐步解析这段代码的每个部分：
        QuestionSubmitService questionSubmitService = (QuestionSubmitService) AopContext.currentProxy();
        // String.valueOf(userId).intern()：首先将 userId 转换为字符串，然后调用 intern() 方法。intern() 方法会返回一个字符串常量池中的字符串实例，确保所有相同的字符串字面量指向同一个内存地址。
        // 不同的用户同时提交是不冲突的
        synchronized (String.valueOf(userId).intern()) {
            //将 userId 的字符串形式作为锁对象，这意味着对于同一个 userId，在多个线程中只允许一个线程进入 synchronized 块，其他线程会被阻塞，直到当前线程释放锁。
            // 如果是不同的 userId，这段代码中的 synchronized 块会根据不同的 userId 创建不同的锁对象，因此不同的用户提交操作可以同时执行，而不会相互阻塞。
            /**
             * 	•	当两个不同的 userId 被同时传入时，线程在进入 synchronized 块时会根据不同的锁对象（字符串）来进行锁定。这意味着：
             * 	•	线程 A 调用 synchronized 块时，如果 userId = 1，它会获取 "1" 的锁。
             * 	•	线程 B 调用 synchronized 块时，如果 userId = 2，它会获取 "2" 的锁。
             * 	•	由于这两个锁是不同的，因此线程 A 和线程 B 可以同时执行各自的 doQuestionSubmitInner 方法，而不会互相阻塞。
             */

            /**
             * 	•	对于不同的 userId：由于它们产生不同的锁，多个线程可以并发执行各自的操作，而不会互相影响。这确保了系统在处理不同用户提交时的高效性。
             * 	•	对于相同的 userId：如果多个线程尝试同时提交相同用户的问题，它们会被串行化处理，因为它们共享同一个锁。这样可以避免数据冲突或不一致的状态。
             */
            return questionSubmitService.doQuestionSubmitInner(userId, questionSubmitAddRequest);
        }
    }
//    /**
//     * 封装了事务的方法
//     *
//     * @param userId
//     * @param questionSubmitAddRequest
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public QuestionSubmitVO doQuestionSubmitInner(long userId, QuestionSubmitAddRequest questionSubmitAddRequest) {
//        QuestionSubmit questionSubmit = new QuestionSubmit();
//        BeanUtils.copyProperties(questionSubmitAddRequest, questionSubmit);
//        QueryWrapper<QuestionSubmit> questionSubmitQueryWrapper = new QueryWrapper<>(questionSubmit);
//        // 这里查的是  SELECT id,language,code,judgeInfo,status,questionId,userId,createTime,updateTime,isDelete FROM question_submit WHERE language=? AND code=? AND questionId=? AND userId=? AND isDelete=0
//        // 都要相同 感觉很合理
//        QuestionSubmit oldQuestionSubmit = this.getOne(questionSubmitQueryWrapper);
//        boolean result;
//        // 如果说之前已经提交过 更新提交次数就行
//
//        /**
//         * 注意！！ 这样的写法是错误的
//         * 如果你在代码中直接 new 一个被声明为 Spring Bean 的类（例如 @Service、@Component、@Repository 等），
//         * 那么 Spring 的管理和注解所带来的特性将不适用于这个通过 new 创建的实例。
//         * 也就是说，该类上的 Spring 注解声明将变得无效，因为这个实例并未被 Spring 容器管理。
//         *
//         * 为了确保 Spring 的特性和功能有效，你应当始终通过 Spring 容器来获取这些 Bean，
//         * 例如使用 @Autowired 或者通过 ApplicationContext.getBean() 获取实例，而不是使用 new 来创建实例。
//         * 这样才能确保 Spring 的依赖注入、AOP、生命周期管理等功能都能正常工作。
//         */
////        JudgeService judgeService = new JudgeServiceImpl();
//
//
//        if (oldQuestionSubmit != null) {
//            // 修改提交次数
//            result = questionService.update()
//                    .eq("id", questionSubmitAddRequest.getQuestionId())
//                    .setSql("submitNum = submitNum + 1")
//                    .update();
//            if (result) {
//                // 执行判题
//                return judgeFeignClient.doJudge(oldQuestionSubmit.getId());
//
//            }else {
//                // 删除失败
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            }
//        }else {
//            // 之前没有提交过
//            result = this.save(questionSubmit);
//            if (result) {
//                result = questionService.update()
//                        .eq("id", questionSubmit.getQuestionId())
//                        .setSql("submitNum = submitNum + 1")
//                        .update();
//                if (result) {
//                    QuestionSubmitVO questionSubmitVO = judgeFeignClient.doJudge(questionSubmit.getId());
//                    return questionSubmitVO;
//                }else {
//                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交次数失败");
//                }
//            }else {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存题目提交失败");
//            }
//        }
//    }

    /**
     * 封装了事务的方法
     * 消息队列版本
     *
     * @param userId
     * @param questionSubmitAddRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionSubmitVO doQuestionSubmitInner(long userId, QuestionSubmitAddRequest questionSubmitAddRequest) {
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitAddRequest, questionSubmit);
        QueryWrapper<QuestionSubmit> questionSubmitQueryWrapper = new QueryWrapper<>(questionSubmit);
        // 这里查的是  SELECT id,language,code,judgeInfo,status,questionId,userId,createTime,updateTime,isDelete FROM question_submit WHERE language=? AND code=? AND questionId=? AND userId=? AND isDelete=0
        // 都要相同 感觉很合理
        QuestionSubmit oldQuestionSubmit = this.getOne(questionSubmitQueryWrapper);
        boolean result;
        // 如果说之前已经提交过 更新提交次数就行

        /**
         * 注意！！ 这样的写法是错误的
         * 如果你在代码中直接 new 一个被声明为 Spring Bean 的类（例如 @Service、@Component、@Repository 等），
         * 那么 Spring 的管理和注解所带来的特性将不适用于这个通过 new 创建的实例。
         * 也就是说，该类上的 Spring 注解声明将变得无效，因为这个实例并未被 Spring 容器管理。
         *
         * 为了确保 Spring 的特性和功能有效，你应当始终通过 Spring 容器来获取这些 Bean，
         * 例如使用 @Autowired 或者通过 ApplicationContext.getBean() 获取实例，而不是使用 new 来创建实例。
         * 这样才能确保 Spring 的依赖注入、AOP、生命周期管理等功能都能正常工作。
         */
//        JudgeService judgeService = new JudgeServiceImpl();


        if (oldQuestionSubmit != null) {
            // 修改提交次数
            result = questionService.update()
                    .eq("id", questionSubmitAddRequest.getQuestionId())
                    .setSql("submitNum = submitNum + 1")
                    .update();
            if (result) {
                // 执行判题
                // 把题目id放进消息队列 等待处理
                /**
                 * // 发送消息
                 * myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
                 * // 执行判题服务
                 * //        CompletableFuture.runAsync(() -> {
                 * //            judgeFeignClient.doJudge(questionSubmitId);
                 * //        });
                 */
                myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(oldQuestionSubmit.getId()));
                // todo 将判题状态改为判题中
                return new QuestionSubmitVO();
//                return judgeFeignClient.doJudge(oldQuestionSubmit.getId());

            }else {
                // 删除失败
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }else {
            // 之前没有提交过
            result = this.save(questionSubmit);
            if (result) {
                result = questionService.update()
                        .eq("id", questionSubmit.getQuestionId())
                        .setSql("submitNum = submitNum + 1")
                        .update();
                if (result) {
//                    QuestionSubmitVO questionSubmitVO = judgeFeignClient.doJudge(questionSubmit.getId());
//                    return questionSubmitVO;
                    myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmit.getId()));
                    // todo 将判题状态改为判题中
                    return new QuestionSubmitVO();
                }else {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新题目提交次数失败");
                }
            }else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存题目提交失败");
            }
        }
    }


}




