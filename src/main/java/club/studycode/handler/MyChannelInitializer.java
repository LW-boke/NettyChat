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

import club.studycode.commons.ChatConstants;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // HttpServerCodec 是netty提供处理http的解码器
        pipeline.addLast(new HttpServerCodec());
        // ChunkedWriteHandler 表示是以块方式写
        pipeline.addLast(new ChunkedWriteHandler());
         /*
           说明：
           1、http在传输的过程中是分段的。HttpObjectAggregator 就是可以将多个段聚合
           2、这就是为什么 当浏览器发送大量数据时 就会发出多次http请求
              8192 表示每次聚合的长度字节
         */
        pipeline.addLast(new HttpObjectAggregator(8192));
        /*
           说明：
           1、对应websocket 它的数据是以 帧形式传递
           2、可以看到WebSocketFrame 下面有六个子类
           3、浏览器请求时，ws://localhost:6666/socket 表示请求的uri
           4、WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议，保存长连接
           5、是通过一个状态码 101
         */
        pipeline.addLast(new WebSocketServerProtocolHandler(ChatConstants.WEBSOCKET_PATH));
        pipeline.addLast(new IdleStateHandler(0, 0, 60 * 3, TimeUnit.SECONDS));
        pipeline.addLast(new UserEventHandler());
        // 新增自定义的handler 处理业务逻辑
        pipeline.addLast(new HttpWebSocketServerHandler());

    }
}
