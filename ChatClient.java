package com.tjoeun.net.client;

// 데이터의 stream을 담당하는 패키지
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

	public static void main(String[] args) {
		String ip = "localhost";
		int port = 1234;
		/*서버에 연결하기 위해서는 ip주소 및 port번호 要*/
		String uid = null;
		
		try {
			Socket s = new Socket(ip,port); // Socket생성 및 연결요청
			System.out.println("접속요청 성공");
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			// client에서 입력되는 데이터를 받을 수 있는 stream
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			
			//로그인 기능 구현
			String logMsg = br.readLine(); // 1.데이터 수신(client입력값을 읽어옴)
			System.out.println(logMsg); // 해당 값 출력
			Scanner kbd = new Scanner(System.in); // 키보드에서 입력받음
			if(logMsg!=null) { // client 입력값이 있는 경우,
				System.out.println("로그인:"); //로그인 메세지 출력
				String logInput = kbd.nextLine(); // 키보드 입력값
				pw.println(logInput); // 2.데이터 전송
				pw.flush();
				String logRes = br.readLine(); // 3.데이터 수신
				System.out.println(logRes);
				if(!logRes.equals("PASS")) {
					System.out.println("로그인 실패");
					System.out.println("프로그램 종료...");
					return;
				}
				uid = logInput.split(",")[1]; // 1번 index에 있는 문자열을 uid에 저장
			}
			
//				String msg = br.readLine(); // Server에서 온 데이터를 받음
//				System.out.printf("서버에서 온 메시지:%s\n",msg);
			new Thread(new NetInput(br)).start(); //서버에서 오는 데이터 처리 thread
			
//			Scanner kbd = new Scanner(System.in);
			System.out.println("메시지 입력:");
			/*키보드에서 데이터를 입력받을 준비 + 서버에서 오는 데이터를 받을 준비*/
			while(true) { // keyboard에서 입력받아 출력
//				System.out.println("보낼 메시지:");
				String line = kbd.nextLine(); // Blocking
				pw.println(uid+": "+line); //서버로 전송
				pw.flush();					
			}
			
		} catch (IOException e) {
			System.err.println("접속요청 실패");
			e.printStackTrace();
		}
		System.out.println("클라이언트 종료됨");

	}
}

class NetInput implements Runnable{

	private BufferedReader br;
	public NetInput(BufferedReader br) {
		this.br = br;
	}
	@Override
	public void run() {
		String msg = null;
		try {
			String firstMsg = br.readLine();
			
			while((msg=br.readLine())!=null) {
				System.out.printf("\n%s\n",msg);
				System.out.print("보낼 메시지:");
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}
	
	}
}
