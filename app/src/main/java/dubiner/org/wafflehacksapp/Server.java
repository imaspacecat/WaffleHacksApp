package dubiner.org.wafflehacksapp;

import io.javalin.Javalin;

public class Server {
    private int port;

    public Server(int port){
        this.port = port;
        Javalin app = Javalin.create();
        app.get("/", ctx -> ctx.result("Hello world"));

        app.ws("/test", ws -> {
            ws.onConnect(ctx -> {
                ctx.send("Hello WebSocket");
                System.out.println("connected");
            });
        });
        app.start(port);
    }


}
