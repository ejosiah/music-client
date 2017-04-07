package com.josiahebhomenye.music;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.net.InetSocketAddress;

/**
 * Created by jay on 05/04/2017.
 */
public class Main {

	public static void main(String...args) throws Exception{
		if(args.length < 2){
			System.out.println("Usuage: host port");
			System.exit(0);
		}
		InetSocketAddress remoteAddress = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
		EventLoopGroup group = new NioEventLoopGroup();
		final ChannelFuture cf = new Bootstrap()
				.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<Channel>() {
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline()
							.addLast(new AudioFormatDecoder())
							.addLast(new AudioPlayerInitializer());

					}
				})
				.connect(remoteAddress).sync();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> join(cf) ));

	}

	@SneakyThrows
	static void join(ChannelFuture cf){
		cf.channel().close().sync();
		System.out.println("Audio player offline");
	}
}
