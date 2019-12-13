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

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.Date;


/**
 * @ClassName: UserInfo.java
 * @Author: Slayer
 * @Date: 2019/12/13 10:25
 * @Description: 用户对象
 */
@Data
public class UserInfo {

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String pic;

    /**
     * 时间
     */
    private Date date;

    /**
     * 地址
     */
    private String addr;

    /**
     * 认证是否通过
     */
    private Boolean auth;

    /**
     * 当前用户通道
     */
    private ChannelHandlerContext channelHandlerContext;
}
