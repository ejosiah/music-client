package com.josiahebhomenye.music;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import javax.sound.sampled.AudioFormat;
import java.util.List;

public class AudioFormatDecoder extends ByteToMessageDecoder {
	private static final int SIZE_OF_INT = 4;
	private ByteBuf buffer = Unpooled.buffer();

	protected void decode(ChannelHandlerContext ctx, ByteBuf data, List<Object> out) throws Exception{
		buffer.writeBytes(Unpooled.copiedBuffer(data));
		if(buffer.readableBytes() < SIZE_OF_INT){
			return;
		}
		int length = buffer.readInt();
		if(buffer.readableBytes() < length){
			return;
		}
		decode(buffer, out);
		ByteBuf buf = Unpooled.copiedBuffer("Ready".getBytes());
		ctx.channel().writeAndFlush(buf);
		ctx.pipeline().remove(this);
	}

	private void decode(ByteBuf data, List<Object> out) throws Exception {
		boolean bigEndian = data.readBoolean();
		int channels = data.readInt();
		AudioFormat.Encoding encoding =  getEncoding(data.readInt());
		float frameRate = data.readFloat();
		int frameSize = data.readInt();
		float sampleRate = data.readFloat();
		int sampleSizeInBits = data.readInt();

		AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels
				, frameSize, frameRate, bigEndian);
		out.add(format);
	}

	private AudioFormat.Encoding getEncoding(int encoding){
		if(encoding == 1){
			return AudioFormat.Encoding.ALAW;
		}else if(encoding == 2){
			return AudioFormat.Encoding.PCM_FLOAT;
		}else if(encoding == 3){
			return  AudioFormat.Encoding.PCM_SIGNED;
		}else if(encoding == 4){
			return AudioFormat.Encoding.PCM_UNSIGNED;
		}else if(encoding == 5){
			return AudioFormat.Encoding.ULAW;
		}else{
			throw new IllegalArgumentException("unknown format");
		}
	}
}
