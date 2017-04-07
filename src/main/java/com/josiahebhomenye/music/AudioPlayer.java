package com.josiahebhomenye.music;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ByteProcessor;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer extends ChannelInboundHandlerAdapter {
	private SourceDataLine line;

	public AudioPlayer(AudioFormat format) throws Exception{
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format);
		line.start();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Music client online");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
		byte[] array = Unpooled.copiedBuffer((ByteBuf)data).array();
		line.write(array, 0, array.length);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		line.drain();
		line.stop();
		line.close();
		ctx.channel().close();
	}
}
