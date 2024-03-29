/*
* Copyright 2012 The Netty Project
*
* The Netty Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package com.gmail.ipshuvaev.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.util.CharsetUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Обработчик входящих http-запросов
 */
public class CustomHttpHandler extends ChannelInboundHandlerAdapter {
    private StringBuilder requestData = new StringBuilder();

    private static final Logger logger = Logger.getLogger(
            CustomHttpHandler.class.getName());
    private RequestDispatcher requestDispatcher;

    public CustomHttpHandler(RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ByteBuf){
            // сборка данных запроса в единое целое

            ByteBuf buf = (ByteBuf)msg;
            String chunk = buf.toString(CharsetUtil.US_ASCII);
            requestData.append(chunk);
        }
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        DefaultHttpRequest request = HttpParser.parse(requestData.toString());
        if(request == null) {
            ctx.close();
            return;
        }
        // обработка запроса после его вычитывании
        requestDispatcher.dispatch(request, new Response(ctx));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}