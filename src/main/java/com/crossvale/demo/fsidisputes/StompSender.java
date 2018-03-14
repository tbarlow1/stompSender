package com.crossvale.demo.fsidisputes;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StompSender {

	private static final String END_OF_FRAME = "\u0000";

	public static void main(String[] args) {
		
		while(true) {
			try {
				run();
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.out.println("Caught Exception...moving on");
			} finally {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException iex) {
					System.out.println("Who disturbed my slumber...");
				}
			}
		}
	}
	
	private static void run() throws Exception {

	      String hostname = System.getenv("STOMP_HOSTNAME");
		  String user = System.getenv("STOMP_USERNAME");
		  String pass = System.getenv("STOMP_PASSWORD");
		  String queue = System.getenv("STOMP_QUEUE");
		  System.out.println("Attempting to connect to " + hostname + " as " + user + " with password: " + pass);
		  Socket socket = new Socket(hostname, 61613);
		  System.out.println("Created socket...");
	      String connectFrame = "CONNECT\n" +
	 	  "accept-version:1.2\n" +
	          "host:" + hostname + "\n" +
		  "login:" + user + "\n" +
		  "passcode:" + pass + "\n" +
	      	  "request-id:1\n" +
		  "\n" +
		  END_OF_FRAME;

	      sendFrame(socket, connectFrame);
	      
	      String response = receiveFrame(socket);
	      System.out.println("response: " + response);

	      String text = "This is a message sent using Stomp 1.2";
	      String message = "SEND\n" +
		  "destination:" + queue + "\n" +
		  "\n" +
		  text +
		  END_OF_FRAME;
	      sendFrame(socket, message);
	      System.out.println("Sent Stomp message: " + text);  

	      String disconnectFrame = "DISCONNECT\n" +
	   	  "\n" +
	     	  END_OF_FRAME;
	      sendFrame(socket, disconnectFrame);

	      socket.close();
	   }

	   private static void sendFrame(Socket socket, String data) throws Exception {
	      byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
	      OutputStream outputStream = socket.getOutputStream();
	      for (int i = 0; i < bytes.length; i++) {
	         outputStream.write(bytes[i]);
	      }
	      outputStream.flush();
	   }

	   private static String receiveFrame(Socket socket) throws Exception {
	      InputStream inputStream = socket.getInputStream();
	      byte[] buffer = new byte[1024];
	      int size = inputStream.read(buffer);

	      byte[] data = new byte[size];
	      System.arraycopy(buffer, 0, data, 0, size);

	      String frame = new String(data, StandardCharsets.UTF_8);
	      return frame;
	   }
}
