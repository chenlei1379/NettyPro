package com.cll.netty.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class GroupChatServer {

	// 定义属性（选择器）
	private Selector selector;
	
	// ServerSocketChannel 在服务器端监听新的客户端Socket连接
	private ServerSocketChannel listenChannel;
	
	private static final int PORT = 6669;
	
	// 构造器
	// 初始化工作
	public GroupChatServer() {
		try {
			
			// open方法得到一个选择器
			selector = Selector.open();
			// open方法得到一个ServerSocketChannel通道
			listenChannel = ServerSocketChannel.open();
			// bind绑定端口
			listenChannel.socket().bind(new InetSocketAddress(PORT));
			//设置非阻塞模式
			listenChannel.configureBlocking(false);
			// 将该listenChannel注册到selector （注册一个选择器并设置监听事件）
			listenChannel.register(selector, SelectionKey.OP_ACCEPT);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	// 监听
	public void listen() {
		try {
			
			System.out.println("监听线程："+ Thread.currentThread().getName());
			// 循环处理
			while(true) {
				System.out.println("------------1");
				// 阻塞、 监控所有注册的通道 ，当其中有IO操作可以进行时，将对应的SelectionKey加入到内部集合中并返回，参数用来设置超时时间
				int count = selector.select();
				System.out.println("------------2");
				if(count > 0) {// 有事件处理
					//遍历得到selectKeys集合 
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					
					while(iterator.hasNext()) {
						// 取出selectionKey
						SelectionKey key = iterator.next();
						// 监听到accept
						if(key.isAcceptable()) {
							SocketChannel sc = listenChannel.accept();
							sc.configureBlocking(false);
							// 将该sc注册到seletor
							sc.register(selector, SelectionKey.OP_READ);
							// 提示
							System.out.println(sc.getRemoteAddress() +" 上线 ");
							
						}
						// 判断是否可以读
						if(key.isReadable()) { // 通道发送read事件，即通道是可读的状态
							// 处理读（专门写方法。。。）
							readData(key);
						}
						
						// 当前的key删除，防止重复处理
						iterator.remove();
					}
				} else {
					System.out.println("等待... ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 发生异常处理... 
		}
	}
	
	//读取客户端消息
	private void readData(SelectionKey key) {
		
		// 取到关联的channle （SocketChannel，网络IO通道，具体负责进行读写操作，NIO把缓冲区的数据写入通道，或者把通道里的数据读到缓冲区）
		SocketChannel channel = null;
		try {
			// 得到channel
			channel = (SocketChannel) key.channel();
			
			// 创建buffer
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			int count = channel.read(buffer);
			
			// 根据count的值做处理
			if(count > 0 ) {
				// 把缓存区的数据转成字符串
				String msg = new String(buffer.array());
				// 输出该消息
				System.out.println("form 客户端：" + msg);
				// 向其它的客户端转发消息（去掉自己）,专门 写一个方法来处理
				sendInfoToOtherClients(msg, channel);
			}
		} catch (Exception e) {
			try {
				System.out.println(channel.getRemoteAddress() + "离线了.. ");
			
				// 取消注册
				key.channel();
				// 关闭通道
				channel.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	// 转发消息给其它 客户（通道 ）
	private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {
		System.out.println("服务器转发消息中...");
		System.out.println("服务器转发消息给客户端 监听线程："+ Thread.currentThread().getName());
		// 遍历 所有注册到selector 上的SocketChannel，并排除self
		for (SelectionKey key : selector.keys()) {
			Channel targetChannel = key.channel();
			if(targetChannel instanceof SocketChannel && targetChannel != self) {
				// 转型
				SocketChannel dest = (SocketChannel)targetChannel;
				// 将msg存储到buffer
				ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
				// 将buffer的数写入通道
				dest.write(buffer);
			}
			
		}
	}
	
	public static void main(String[] args) {
		// 创建服务器对象 
		GroupChatServer groupChatServer = new GroupChatServer();
		groupChatServer.listen();
	}
	
}
