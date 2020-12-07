package com.tjoeun.net.client;

// io(inout output)은 데이터의 stream을 담당하는 패키지
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
			// byte데이터를 문자로 출력하는 stream
			
			//로그인 기능 구현
			String logMsg = br.readLine(); // 1.데이터 수신(client 초기입력값을 읽어옴)
			System.out.println(logMsg); 
			Scanner kbd = new Scanner(System.in); // 키보드에서 입력받음
			if(logMsg!=null) { // client 입력값이 있는 경우,
				System.out.println("로그인:"); //로그인요청 메세지 출력
				String logInput = kbd.nextLine(); // 키보드 입력값 저장
				pw.println(logInput); // 2.데이터 전송(사용자 키보드 입력값)
				pw.flush(); // 남은 데이터 없이 모두 전송(출력하려는 데이터 값이 적을때 유용)
				String logRes = br.readLine(); // 3.데이터 수신(로그인을 위한 client입력값을 서버에서 로직을 돌려 결과값 읽어옴)
				System.out.println(logRes);
				if(!logRes.equals("PASS")) { // 만약 결과값이 "PASS"가 아닐경우,
					System.out.println("로그인 실패");
					System.out.println("프로그램 종료...");
					return; //로그인 프로그램 종료
				}
				uid = logInput.split(",")[1]; // 1번 index에 있는 문자열을 uid에 저장 ->
			}
			
//				String msg = br.readLine(); // Server에서 온 데이터를 받음
//				System.out.printf("서버에서 온 메시지:%s\n",msg);
			new Thread(new NetInput(br)).start(); //서버에서 오는 데이터 처리 thread(CPU)->무한루프1
			
//			Scanner kbd = new Scanner(System.in);
			System.out.println("메시지 입력:");
			
			// 메세지 입력 구현
			/*키보드에서 데이터를 입력받을 준비 + 서버에서 오는 데이터를 받을 준비*/
			while(true) {  // 무한루프2
//				System.out.println("보낼 메시지:"); // keyboard에서 입력받아 출력
				String line = kbd.nextLine(); // Blocking (입력받기 전까지 대기상태)
				pw.println(uid+": "+line); // 로그인시 입력된 id값에 keyboard에서 입력받은 메세지 내용을 붙여서 출력
				pw.flush(); // 남은 데이터없이 모두 전송					
			}
			
		} catch (IOException e) {
			System.err.println("접속요청 실패");
			e.printStackTrace();
		}
		System.out.println("클라이언트 종료됨");

	}
}

class NetInput implements Runnable{ // Runnable interface구현(or Thread 상속)으로 run method(무한루프) 분리

	private BufferedReader br; // br객체 생성 및 초기화
	public NetInput(BufferedReader br) { // 생성자 parameter로 br을 받아 해당 class에서 사용될 수 있도록 함
		this.br = br;
	}
	@Override
	public void run() { // Runnable interface의 run method override
		String msg = null;
		try {
			String firstMsg = br.readLine(); // br에서 문자열 읽어옴
			
			while((msg=br.readLine())!=null) { // null이 아니라면 무한루프 반복
				System.out.printf("\n%s\n",msg);
				System.out.print("보낼 메시지:");
			}
		} catch (IOException e) {			
			e.printStackTrace();
		}
	
	}
}
