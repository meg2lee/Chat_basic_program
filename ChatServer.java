package com.tjoeun.net.server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	
	static ArrayList<Socket> sockets = new ArrayList<>();
	public static void main(String[] args) {
		/*클라이언트의 접속대기 구현*/
		try {
			ServerSocket ss = new ServerSocket(1234); //port번호 1234로 대기용 ServerSocket 생성
			System.out.println("서버 대기 중...");
			while(true) {
				Socket s = ss.accept(); // client연결 요청 수락시, 통신용 socket생성
				sockets.add(s);
				System.out.printf("%s 접속\n", s.getInetAddress()); // 서버접속자 확인용
				new Thread(new ServerThread(s,sockets)).start(); // 통신용(Runnable interface구현)
				/*main thread(CPU)한개에서 한개의 무한루프만 돌 수 있으므로, 별도의 Thread(CPU)에 떼어줌 */
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}			
		System.out.println("서버 프로그램 종료");
	}

}

class ServerThread implements Runnable{ // 별도의 Thread에 무한루프 뗴어줌
	private Socket s;
	private BufferedReader br;
	private PrintWriter pw;
	private ArrayList<Socket> sockets;
	
	public ServerThread(Socket s, ArrayList<Socket> sockets) {
	/*현재사용중인 컴퓨터의 Socket과 기존에 접속되어있던 Socket을 모두 parameter로 줌*/
		this.s = s;
		this.sockets = sockets;
		try {
			this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			this.pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			
			/*로그인절차 구현*/
			pw.println("서버에 접속 성공, 로그인해주세요"); // 1.데이터전송
			pw.flush();// 메모리가 어느정도 채워지면 한꺼번에 보냄(버퍼세척)
			
			String user = br.readLine(); // 2.데이터 수신
			String[] logData = user.split(","); // 쉼표로 수신받은 데이터 구분 -> 배열로 저장
			
		if(logData[1].startsWith("uid")&& logData[2].startsWith("upwd")) { // 1번 index(두번째입력값)이 uid로 시작, 2번 index(세번째입력값)이 upwd로 시작이면,
			pw.println("PASS"); // 3.데이터 전송
			pw.flush();
		}else {
			pw.println("Log Fail");
			pw.flush();
			}		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override // Runnable interface에 run 메소드 구현
	public void run() { // database broadcasting(타인에게 뿌리기)
		String cMsg = null;
		try {
			while((cMsg=br.readLine())!=null) { // client에서 수신한 값이 null이 아니면,
				/*모든 접속자에게 메시지를 전송(BroadCasting) except myself*/
				for(int i=0;i<sockets.size();i++) { //모든 socket list만큼 루프돌려
					Socket soc = sockets.get(i); // 각 index를 확인하고
					if(soc==s) continue; // 내 socket이라면, 해당 전송데이터 받지 x
					InetAddress ia = soc.getInetAddress(); // 해당 socket의 ip주소 저장
					PrintWriter client = new PrintWriter(new OutputStreamWriter(soc.getOutputStream())) ;
					client.println(cMsg); // client에 다시 전송
					client.flush();
				}
			}
		}catch(Exception e) {
			InetAddress ia = s.getInetAddress();
			System.out.println(ia+"Client left");
			sockets.remove(s); // 나간 클라이언트는 서버에서도 제거함 -> thread소멸
//			e.printStackTrace();
		}		
	}	
}
