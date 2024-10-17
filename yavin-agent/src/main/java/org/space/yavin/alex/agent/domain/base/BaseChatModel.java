package org.space.yavin.alex.agent.domain.base;


import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;
import org.space.yavin.alex.agent.domain.base.model.Message;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.space.yavin.alex.agent.common.constant.LlmConstants.DEFAULT_SYSTEM_MESSAGE;
import static org.space.yavin.alex.agent.common.constant.LlmConstants.SYSTEM;

/**
 * @author yyHuangfu
 * @create 2024/10/17
 */

@Getter
public abstract class BaseChatModel {

    /**
     * LLM 聊天接口。
     * <p>
     * 参数:
     * - messages: 输入的消息列表。
     * - functions: 输入的函数调用，支持 OpenAI 格式。
     * - stream: 是否使用流式生成。
     * - extra_generate_cfg: 额外的 LLM 生成超参数。
     * <p>
     * 返回:
     * 由 LLM 生成的消息列表响应。
     */
    public Flux<List<Message>> streamChat(List<Message> messages,
                                          List<Map<String, String>> functions,
                                          Map<String, String> cfg) {
        List<Message> cloneMessages = new ArrayList<>(messages);
        if (!SYSTEM.equals(cloneMessages.get(0).getRole())) {
            messages.add(0, new Message(SYSTEM, DEFAULT_SYSTEM_MESSAGE, null, null));
        }
        // 针对cfg参数做不同的逻辑处理
        boolean functionMode = isFunctionMode(functions);
        Flux<List<Message>> output;
        if (functionMode) {
            output = _chatWithFunction();
        } else {
            output = _chatStream(
                    messages,
                    cfg
            );
        }
        // output解析和校验
        return output;
    }

    protected abstract Flux<List<Message>> _chatStream(List<Message> messages, Map<String, String> cfg);

    protected abstract List<Message> _chatNoStream();

    protected abstract Flux<List<Message>> _chatWithFunction();


    // ------------------ private -----------------------
    private static boolean isFunctionMode(List<Map<String, String>> functions) {
        boolean functionMode;
        if (CollectionUtil.isEmpty(functions)) {
            functionMode = false;
        } else {
            functionMode = true;
        }
        return functionMode;
    }
}