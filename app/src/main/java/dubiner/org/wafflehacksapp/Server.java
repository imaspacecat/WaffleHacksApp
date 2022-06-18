package dubiner.org.wafflehacksapp;

import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import io.javalin.Javalin;

public class Server {
    private int port;

    public Server(int port){
        this.port = port;
        Javalin app = Javalin.create().start(port);
        app.get("/", ctx -> ctx.result("Hello world"));
        System.out.println("Server started...");
    }


}
