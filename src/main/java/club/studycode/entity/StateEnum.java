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


/**
 * @ClassName: StateEnum.java
 * @Author: Slayer
 * @Date: 2019/12/11 11:11
 * @Description: 消息枚举类
 */
public enum StateEnum {

    /**
     * 注册用户
     */
    REGISTER(4000, "注册"),

    /**
     * 认证用户失败
     */
    REGISTER_ERROR(4001,"认证失败"),

    /**
     * 返回在线人数
     */
    COUNT(4002, "返回在线人数"),


    /**
     * 广播给所有用户当前登录的用户
     */
    RETURN_USER_LOGIN_INFO(4002, "用户登录"),


    /**
     * 广播给所有用户当前退出的用户
     */
    RETURN_USER_LOGOUT_INFO(4003, "用户退出"),
    /**
     * 发送所有人
     */
    MSG_ALL(5000, "群发"),

    /**
     * 私聊
     */
    MSG_ONE(5001, "单发"),

    /**
     * 群聊
     */
    MSG_GROUP(5002, "群聊");

    /**
     * @param state 状态码
     * @param value 状态码简介
     */
    StateEnum(int state, String value) {

    }

    StateEnum() {

    }


}
