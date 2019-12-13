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
package club.studycode.core;

import club.studycode.entity.Message;
import club.studycode.entity.StateEnum;
import club.studycode.entity.UserInfo;
import club.studycode.utils.MapperUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ClassName: ChannelManager.java
 * @Author: Slayer
 * @Date: 2019/12/11 10:34
 * @Description: 通道管理
 */
@Slf4j
public class ChannelManager {

    /**
     * 管理通道的集合
     */
    private static ConcurrentHashMap<Channel, UserInfo> channels = new ConcurrentHashMap<>();


    /**
     * 发送用户退出登录信息
     *
     * @param ctx
     */
    public static void sendLogoutInfoAll(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();

        // 将当前退出的用户发送给其他在线用户
        UserInfo userInfo = ChannelManager.getChannel(channel);

        // 没有认证直接返回
        if (userInfo.getAuth() != null && !userInfo.getAuth()) {
            ChannelManager.removeChannel(channel);
            return;
        }

        Message message = new Message();
        message.setUserInfo(userInfo);
        message.setDate(new Date());
        message.setStateEnum(StateEnum.RETURN_USER_LOGOUT_INFO);
        ChannelManager.sendMsgAll(channel, message);
        ChannelManager.removeChannel(channel);
        log.debug("有一个客户端已断开连接");
        // 发送当前在线人数到客户端
        ChannelManager.sendCountAll();
        ctx.close();
    }

    /**
     * 返回当前在线人数
     *
     * @return {@link int}
     */
    public static int count() {
        return channels.size();
    }


    /**
     * 群发在线人数
     */
    public static void sendCountAll() {
        Message message = new Message();
        message.setStateEnum(StateEnum.COUNT);
        message.setCount(ChannelManager.count());
        channels.forEach((ch, userInfo) -> {
            try {
                userInfo.getChannelHandlerContext().writeAndFlush(new TextWebSocketFrame(MapperUtils.obj2json(message)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 群发信息
     *
     * @param channel 当前通道
     * @param message 发送的信息集合
     */
    public static void sendMsgAll(Channel channel, Message message) {
        channels.forEach((ch, userInfo) -> {
            if (ch != channel) {
                try {
                    userInfo.getChannelHandlerContext().writeAndFlush(new TextWebSocketFrame(MapperUtils.obj2json(message)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 新增在线用户
     *
     * @param channel  当前用户通道
     * @param userInfo 当前用户信息
     */
    public static void addChannel(Channel channel, UserInfo userInfo) {
        channels.put(channel, userInfo);
    }


    /**
     * 获取用户信息
     *
     * @param channel 当前通道
     * @return {@link UserInfo}
     */
    public static UserInfo getChannel(Channel channel) {
        return channels.get(channel);
    }


    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return {@link UserInfo}
     */
    public static UserInfo getUserInfo(String username) {
        for (Map.Entry<Channel, UserInfo> entry : channels.entrySet()) {
            UserInfo userInfo = entry.getValue();
            if (userInfo.getUsername().equals(username)) {
                return userInfo;
            }
        }
        return null;
    }

    /**
     * 删除通道
     *
     * @param channel 当前通道
     */
    public static void removeChannel(Channel channel) {
        channels.remove(channel);
    }


}
