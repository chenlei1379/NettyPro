package com.cll.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 运行方法：

public class BIOServer {

	public static void main(String[] args) throws Exception {
		// 线程池机制

		// 思路
		// 1. 创建一个线程池
		// 2. 如果有客户端连接，就创建一个线程，与之通讯（单独写一个方法）
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

		// 创建ServerSocket
		ServerSocket serverSocket = new ServerSocket(6666);
		System.out.println("服务器启动了");

		while (true) {
			// 监听， 等待客户端连接
			final Socket socket = serverSocket.accept();
			System.out.println("连接到一个客户端");
			
			// 就创建一个线程，与之通讯（单独写一个方法）
			newCachedThreadPool.execute(new Runnable() {

				public void run() {
					// 可以和客户端通讯，
					handler(socket);
				}
			});
		}
	}

	public static void handler(Socket socket) {
		try {
			System.out.println(
					"线程信息 id = " + Thread.currentThread().getId() + "名字 = " + Thread.currentThread().getName());
			byte[] bytes = new byte[1024];
			InputStream inputStream = socket.getInputStream();
			while (true) {
				System.out.println(
						"线程信息 id = " + Thread.currentThread().getId() + "名字 = " + Thread.currentThread().getName());

				int read = inputStream.read(bytes);
				if (read != -1) {
					System.out.println(new String(bytes, 0, read));
				} else {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("关闭和client的连接");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
