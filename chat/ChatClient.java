import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Formatter;

class ClientExecutor{
	private static final String EXPLANATION = "";
	private static final String HELP = "default";

	public static void main(String[] args) {
		// 変数の初期化
		String inputCommand;
		String ipAddr;
		// 最初の説明を表示する。
		System.out.println(EXPLANATION);

		// ユーザー名を入力する。
		Scanner scan = new Scanner(System.in);
		System.out.print("ユーザー名を入力してください。(必須): ");
		String userName = scan.nextLine();
		if (userName == ""){
			System.out.println("ERROR! | ユーザー名を入力してください。");
			return;
		}

		// IPアドレスを取得する。
		try{
			ipAddr = InetAddress.getLocalHost().getHostAddress();
		}catch(UnknownHostException e){
			ipAddr = "0.0.0.0";
		}

		// 通信用のクライアントインスタンスを作成する。
		ChatClient clientTmp = new ChatClient(userName, ipAddr);

		while (true){
			// コマンドライン上の値の表示のためのフォーマット文字列
			Formatter fm = new Formatter();
			// 日時の取得
			String dateStr = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
			fm.format("User: %s|Time: %s|(CTRL-C)入力で終了>>>", userName, dateStr);
			System.out.print(fm);
			// コマンドの取得
			inputCommand = scan.nextLine();

			switch(inputCommand){
				case "help":
					System.out.println(HELP);
					break;

				case "getThread":
					System.out.println(">>>>>| 現在存在しているスレッドを取得します。");
					System.out.println(clientTmp.connectServer(inputCommand) + "\n");
					break;

				case "makeThread":
					System.out.println(">>>>>| 新しいスレッドを作成します。");
					System.out.println(clientTmp.connectServer(inputCommand) + "\n");
					break;

				case "sendMessage":
					System.out.println(">>>>>| メッセージを送信します。");
					System.out.println(clientTmp.connectServer(inputCommand) + "\n");
					break;

				case "getMessage":
					System.out.println(">>>>>| メッセージを取得します。");
					System.out.println(clientTmp.connectServer(inputCommand) + "\n");
					break;

				default:
					System.out.println("Command:" + inputCommand + "は存在しません。");
					System.out.println(HELP + "\n");
					break;
			}
		}
	}
}

class ChatClient {
	// サーバーのIPアドレスを指定する。
	private static final String DESTINATION_IP = "192.168.56.1";
	private String userName;
	private String ipAddr;

	public ChatClient(String userName, String ipAddr){
		this.userName = userName;
		this.ipAddr = ipAddr;
	}

	public String connectServer(String command){
		Socket socketTmp = null;
		BufferedReader csInput = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		String result = "";
		String threadName = "";

		try{
			// クライアント側のソケットを作成する。
			socketTmp = new Socket(DESTINATION_IP, 80);
			// クライアント側からサーバへの送信用
			writer = new PrintWriter(socketTmp.getOutputStream(), true);
			// サーバ側からの受取用
			reader = new BufferedReader(new InputStreamReader(socketTmp.getInputStream()));
			// サーバーに送信しないメッセージを取得する。
			Scanner scan = new Scanner(System.in);

			switch(command){
				case "getThread":
					// サーバーへのデータの送信
					writer.println("getThread");

					// サーバーからのデータの受信
					result = reader.readLine();
					break;

				case "makeThread":
					// サーバーへの送信データの準備
					System.out.print("スレッド名を入力してください。>");
				  threadName = scan.nextLine();
					System.out.print("スレッドを公開しますか?(yes/no)。>");
					String publicTmp = scan.nextLine();

					// サーバへのデータの送信
					writer.println(String.format("makeThread %s %s %s", threadName, publicTmp, this.userName));

					// サーバーからの結果受信
					result = reader.readLine();
				 	break;

				case "sendMessage":
					// サーバーへの送信データの準備
					System.out.print("スレッド名を入力してください。>");
					threadName = scan.nextLine();
					System.out.print("送信するメッセージを入力してください。>");
					String messageTmp = scan.nextLine();

					// サーバーへのデータ送信
					writer.println(String.format("sendMessage %s %s %s", threadName, messageTmp, this.userName));

					// サーバーからの結果受信
					result = reader.readLine();
					break;

				case "getMessage":
					// サーバーへのデータ送信
					writer.println("getMessage");

					// サーバーからの結果受信
					result = reader.readLine();
					break;
			}

		}catch (Exception e){
			System.out.println("ERROR!| ネットワーク接続中に異常が発生しました。");
			result = e.toString();

		}finally{
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (csInput != null) {
					csInput.close();
				}
				if (socketTmp != null) {
					socketTmp.close();
				}
			} catch (IOException e) {
				System.out.println("ERROR!| ネットワーク終了中に異常が発生しました。");
				result = e.toString();
			} finally{
        System.out.println("Finished!| クライアントの処理を終了します。");
		}
	}
	return result;
}
}
