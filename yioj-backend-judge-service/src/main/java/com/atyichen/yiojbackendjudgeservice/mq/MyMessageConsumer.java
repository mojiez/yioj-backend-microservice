package com.atyichen.yiojbackendjudgeservice.mq;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.atyichen.yiojbackendcommon.common.ErrorCode;
import com.atyichen.yiojbackendcommon.exception.BusinessException;
import com.atyichen.yiojbackendjudgeservice.judge.JudgeService;
import com.atyichen.yiojbackendmodel.model.entity.QuestionSubmit;
import com.atyichen.yiojbackendmodel.model.vo.QuestionSubmitVO;
import com.atyichen.yiojbackendserviceclient.service.QuestionFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;
    @Resource
    private QuestionFeignClient questionFeignClient;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        try {
            QuestionSubmitVO questionSubmitVO = judgeService.doJudge(questionSubmitId);
//            QuestionSubmit questionSubmit = new QuestionSubmit();
//            BeanUtil.copyProperties(questionSubmitVO, questionSubmit);
//            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(questionSubmitVO.getJudgeInfo()));
//            boolean b = questionFeignClient.updateQuestionSubmitById(questionSubmit);
//            if (!b) {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"最后一次更新questionSubmit失败");
//            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(deliveryTag, false, false);
        }
    }

}
