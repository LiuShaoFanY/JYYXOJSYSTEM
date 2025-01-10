package com.schall.jyyxbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.schall.jyyxbackendjudgeservice.judge.JudgeService;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ 消息消费者，负责处理判题任务。
 */
@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    /**
     * 监听消息队列，处理判题任务。
     *
     * @param message     消息内容
     * @param channel     RabbitMQ 通道
     * @param deliveryTag 消息标签
     */
    @SneakyThrows
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("接收到消息: message = {}", message);

        // 1. 检查消息是否为空或无效
        if (message == null || "null".equals(message)) {
            log.warn("接收到空或无效消息，跳过处理。");
            channel.basicNack(deliveryTag, false, false); // 拒绝消息，不重新入队
            return;
        }

        try {
            // 2. 解析消息内容为题目提交 ID
            long questionSubmitId = Long.parseLong(message);
            log.info("开始处理判题任务，questionSubmitId = {}", questionSubmitId);

            // 3. 调用判题服务处理任务
            judgeService.doJudge(questionSubmitId);

            // 4. 确认消息处理成功
            channel.basicAck(deliveryTag, false);
            log.info("判题任务处理成功，questionSubmitId = {}", questionSubmitId);
        } catch (NumberFormatException e) {
            // 5. 处理消息解析失败的情况
            log.error("消息解析失败，message = {}", message, e);
            channel.basicNack(deliveryTag, false, false); // 拒绝消息，不重新入队
        } catch (BusinessException e) {
            // 6. 处理业务异常
            log.error("判题任务处理失败，业务异常: questionSubmitId = {}, 错误信息 = {}", message, e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false); // 拒绝消息，不重新入队
        } catch (Exception e) {
            // 7. 处理其他未知异常
            log.error("判题任务处理失败，未知异常: questionSubmitId = {}", message, e);
            channel.basicNack(deliveryTag, false, true); // 拒绝消息，重新入队以便重试
        }
    }
}