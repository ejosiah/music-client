package com.josiahebhomenye.music;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.sound.sampled.AudioFormat;

/**
 * Created by jay on 05/04/2017.
 */
public class AudioPlayerInitializer extends SimpleChannelInboundHandler<AudioFormat> {

	protected void channelRead0(ChannelHandlerContext ctx, AudioFormat format) throws Exception {
		AudioPlayer player = new AudioPlayer(format);
		ctx.pipeline().remove(this);
		ctx.pipeline().addLast(player);
		System.out.println("Audio player initialized");
	}
}
