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
package club.studycode;

import club.studycode.netty.NettyHttpBootstrap;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName: NettyServer.java
 * @Author: Slayer
 * @Date: 2019/12/11 13:45
 * @Description: Netty服务启动类
 */
@Slf4j
public class NettyServer {
    public static void main(String[] args) {
        NettyHttpBootstrap.start("localhost", 8080);
    }
}
