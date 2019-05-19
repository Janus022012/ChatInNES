import java.lang.Exception;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Formatter;

class ServerExecutor {
	private static final String EXPLANATION = "";
	private static final String HELP = "";

	public static void main(String[] args){
		// 変数の初期化
		String dataDir;
		String ipAddr;
		String dateStr = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
		ServerSocket sSocket = null;
		Socket socket = null;
		BufferedReader reader = null;
		PrintWriter writer = null;

		// 最初の説明を表示する。
		System.out.println(EXPLANATION);

		// パスの入力
		Scanner scan = new Scanner(System.in);
		System.out.print("会話データを格納するディレクトリを指定してください。>");
		dataDir = scan.nextLine();
		if (dataDir.equals("default")){
			// デフォルト値を設定する。
			dataDir = "D:\\JavaProjects\\chatInNES\\chat\\conv";
			System.out.println(String.format("デフォルトパス(%s)に設定しました。", dataDir));
		}

		// TODO フォルダが存在するかどうかを判定する。

		// IPアドレスを取得する。
		System.out.print("サーバーを立てるIPアドレスを指定してください。(必須)>");
		ipAddr = scan.nextLine();
		if (ipAddr.equals("default")){
			try{
				ipAddr = InetAddress.getLocalHost().getHostAddress();
			}catch(UnknownHostException e){
				// throw Exception("ERROR! | IPアドレスを入力してください");
			}
		}

		// サーバーを立てる
		try{
			sSocket = new ServerSocket();
			sSocket.bind(new InetSocketAddress(ipAddr, 80));

			System.out.println(String.format("%s> IP_ADDRESS %sで接続を待機中です。", dateStr, ipAddr));

			while (true){
					Socket socketTmp = sSocket.accept();
					ChatThread threadTmp = new ChatThread(socketTmp);
					threadTmp.start();

			}
		}catch(IOException e){
			e.printStackTrace();
		}finally {
			try {
        if (sSocket != null) {
          sSocket.close();
        }
      } catch (IOException e) {}
	}
}
}


class ChatThread extends Thread {
	private Socket socket;

	public ChatThread(Socket socket){
		this.socket = socket;
	}

	public void run(){
		try{
			String line;
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			while ( (line = in.readLine()) != null ) {
				// TODO 受信したメッセージに合わせて、処理を変更する。
        System.out.println(socket.getRemoteSocketAddress() + " 受信: " + line);
				out.println(line);
        System.out.println(socket.getRemoteSocketAddress() + " 送信: " + line);
			}
		} catch (IOException e) {
      e.printStackTrace();
		} finally{
			try {
        if (socket != null) {
          socket.close();
        }
      } catch (IOException e) {
      System.out.println("切断されました " + socket.getRemoteSocketAddress());
    }
	}
}
}
