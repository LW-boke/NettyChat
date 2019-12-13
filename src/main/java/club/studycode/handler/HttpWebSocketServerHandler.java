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
package club.studycode.handler;

import club.studycode.core.ChannelManager;
import club.studycode.entity.Message;
import club.studycode.entity.StateEnum;
import club.studycode.entity.UserInfo;
import club.studycode.utils.MapperUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@Slf4j
public class HttpWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 接收用户发送过来的信息
     *
     * @param ctx 上下文对象
     * @param msg 信息封装类
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Message message = MapperUtils.json2pojo(msg.text(), Message.class);
        switch (message.getStateEnum()) {
            // 新增用户
            case REGISTER:
                // 判断当前用户是否存在
                UserInfo userInfo = ChannelManager.getUserInfo(message.getUserInfo().getUsername());
                if (userInfo != null) {
                    message.getUserInfo().setAuth(false);
                    message.setStateEnum(StateEnum.REGISTER_ERROR);
                    ChannelManager.addChannel(ctx.channel(),message.getUserInfo());
                    ctx.writeAndFlush(new TextWebSocketFrame(MapperUtils.obj2json(message)));
                    ctx.close();
                }
                // 存在
                else {
                    log.debug("新增用户" + saveUserInfo(ctx, message));
                }
                break;
            // 群发信息
            case MSG_ALL:
                ChannelManager.sendMsgAll(ctx.channel(), message);
                log.debug("群发信息 " + message);
                break;
            // 单发信息
            case MSG_ONE:
                // TODO : 未来这里可以实现单人之间的聊天
                break;
            // 群聊信息
            case MSG_GROUP:
                // TODO : 未来这里可以实现群聊聊天
                break;
            // 其他
            default:
                log.error("未知类型信息");
                break;
        }
    }

    /**
     * 客户端断开触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ChannelManager.sendLogoutInfoAll(ctx);
    }

    /**
     * 异常处理
     *
     * @param ctx   上下文对象
     * @param cause 异常对象
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ChannelManager.sendLogoutInfoAll(ctx);
        log.error(cause.getMessage());
    }


    /**
     * 新增用户
     *
     * @param ctx     上下文对象
     * @param message 消息信息
     * @return {@link UserInfo}
     */
    private UserInfo saveUserInfo(ChannelHandlerContext ctx, Message message) {
        UserInfo userInfo = new UserInfo();
        userInfo.setDate(new Date());
        userInfo.setAddr(fileterAddr(ctx.channel().remoteAddress().toString()));
        userInfo.setChannelHandlerContext(ctx);
        userInfo.setUsername(message.getUserInfo().getUsername());
        userInfo.setPic(message.getUserInfo().getPic());
        userInfo.setAuth(true);
        ChannelManager.addChannel(ctx.channel(), userInfo);

        // 返回当前在线人数
        ChannelManager.sendCountAll();

        // 广播通知其他用户新增的信息
        message.setStateEnum(StateEnum.RETURN_USER_LOGIN_INFO);
        ChannelManager.sendMsgAll(ctx.channel(), message);
        return userInfo;
    }


    /**
     * 过滤Ip地址
     *
     * @param addr 要过滤的ip地址
     * @return {@link String}
     */
    private String fileterAddr(String addr) {
        // 过滤ip地址
        if (!StringUtil.isNullOrEmpty(addr)) {
            addr = addr.substring(1);
            return addr;
        }
        return null;
    }


}
