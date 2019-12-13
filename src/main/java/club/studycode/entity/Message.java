/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package club.studycode.entity;

import club.studycode.utils.DateToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;


/**
 *  @ClassName: Message.java
 *  @Author: Slayer
 *  @Date: 2019/12/13 10:25
 *  @Description: 消息对象
 */
@Data
public class Message {

    /**
     * 消息ID
     */
    private String id;

    /**
     * 消息类型
     */
    private StateEnum stateEnum;

    /**
     * 消息内容
     */
    private String msg;


    /**
     * 消息时间
     */
    @JsonSerialize(using = DateToStringSerializer.class)
    private Date date;

    /**
     * 发送给谁 (私发模式使用)
     */
    private String peerName;

    /**
     * 总人数 (发送在线人数时需要)
     */
    private int count;

    /**
     * 当前信息的用户
     */
    private UserInfo userInfo;
}
