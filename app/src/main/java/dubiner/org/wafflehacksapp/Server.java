package dubiner.org.wafflehacksapp;

import com.google.gson.Gson;

import io.javalin.Javalin;

public class Server {
    private int port;
    private Gson gson = new Gson();
    private Javalin app;

    public Server(int port){
        this.port = port;

        app = Javalin.create().start(port);

        app.get("/", ctx -> ctx.html("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js\"></script>\n" +
                "    <script src=\"https://cdn.plot.ly/plotly-2.12.1.js\" charset=\"utf-8\"></script>\n" +
                "    <script src='https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.min.js'></script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <p>Hello world</p>\n" +
                "    <div id=\"x\"></div>\n" +
                "    <div id=\"y\"></div>\n" +
                "    <div id=\"z\"></div>\n" +
                "    <div id=\"test\"></div>\n" +
                "\n" +
                "\n" +
                "    <script>\n" +
                "        var accelerationX = 0;\n" +
                "        var accelerationY = 0;\n" +
                "        var accelerationZ = 0;\n" +
                "\n" +
                "        // use variables to keep updating graph\n" +
                "        var previousAccelerationX = accelerationX;\n" +
                "        var previousAccelerationY = accelerationY;\n" +
                "        var previousAccelerationZ = accelerationZ;\n" +
                "        var r = 0;\n" +
                "        var g = 0;\n" +
                "        var b = 0;\n" +
                "        var opacity = 0;\n" +
                "        var lineWidth = 0;\n" +
                "        var json;\n" +
                "\n" +
                "\n" +
                "        // draw initial graph\n" +
                "        var data = {\n" +
                "            type: 'scatter3d',\n" +
                "            mode: 'lines',\n" +
                "            x: [accelerationX],\n" +
                "            y: [accelerationY],\n" +
                "            z: [accelerationZ],\n" +
                "            opacity: [opacity],\n" +
                "            line: {\n" +
                "                width: lineWidth,\n" +
                "                color: 'rgb(' + r + ',' + g + ',' + b + ')',\n" +
                "                reversescale: false\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        Plotly.newPlot(\"test\", [data], { height: 640 });\n" +
                "\n" +
                "        setInterval(() => {\n" +
                "            $(\"#x\").load(\"http://localhost:3030/data/accelerationX\");\n" +
                "            $(\"#y\").load(\"http://localhost:3030/data/accelerationY\");\n" +
                "            $(\"#z\").load(\"http://localhost:3030/data/accelerationZ\");\n" +
                "\n" +
                "            // $.get(\"http://localhost:3030/data/accelerationX\", function (data) {\n" +
                "            //     accelerationX = data;\n" +
                "            // });\n" +
                "\n" +
                "            // $.get(\"http://localhost:3030/data/accelerationY\", function (data) {\n" +
                "            //     accelerationY = data;\n" +
                "            // });\n" +
                "\n" +
                "            // $.get(\"http://localhost:3030/data/accelerationZ\", function (data) {\n" +
                "            //     accelerationZ = data;\n" +
                "            // });\n" +
                "\n" +
                "            console.log(\"accelerationX: \" + accelerationX);\n" +
                "            console.log(\"accelerationY: \" + accelerationY);\n" +
                "            console.log(\"accelerationZ: \" + accelerationZ);\n" +
                "\n" +
                "            $.getJSON(\"http://localhost:3030/data/\", function (data) {\n" +
                "                json = data;\n" +
                "            });\n" +
                "            console.log(json);\n" +
                "            // json = {\n" +
                "            //     \"r\": 0,\n" +
                "            //     \"g\": 0,\n" +
                "            //     \"b\": 0,\n" +
                "            //     \"opacity\": 0,\n" +
                "            //     \"strokeWidth\": 0,\n" +
                "            //     \"accelerationX\": -2.2083645,\n" +
                "            //     \"accelerationY\": 9.161459,\n" +
                "            //     \"accelerationZ\": 2.7252295\n" +
                "            // };\n" +
                "\n" +
                "\n" +
                "            accelerationX = json.accelerationX;\n" +
                "            accelerationY = json.accelerationY;\n" +
                "            accelerationZ = json.accelerationZ;\n" +
                "            r = json.r;\n" +
                "            g = json.g;\n" +
                "            b = json.b;\n" +
                "            opacity = json.opacity;\n" +
                "            lineWidth = json.strokeWidth;\n" +
                "\n" +
                "\n" +
                "\n" +
                "            Plotly.extendTraces(\"test\",\n" +
                "                {\n" +
                "                    x: [[accelarationToPos(accelerationX, previousAccelerationX, 1)]],\n" +
                "                    y: [[accelarationToPos(accelerationY, previousAccelerationY, 1)]],\n" +
                "                    z: [[accelarationToPos(accelerationZ, previousAccelerationZ, 1)]],\n" +
                "                    // opacity: [opacity],\n" +
                "                    // line: {\n" +
                "                    //     width: [lineWidth],\n" +
                "                    //     color: 'rgb(' + r + ',' + g + ',' + b + ')',\n" +
                "                    //     reversescale: false\n" +
                "                    // }\n" +
                "                }, [0]);\n" +
                "\n" +
                "            previousAccelerationX = accelerationX;\n" +
                "            previousAccelerationY = accelerationY;\n" +
                "            previousAccelerationZ = accelerationZ;\n" +
                "\n" +
                "        }, 1000);\n" +
                "\n" +
                "        function accelarationToPos(a, previous, T) {\n" +
                "            return a * (Math.pow(T, 2)) + previous;\n" +
                "        }\n" +
                "\n" +
                "    </script>\n" +
                "</body>\n" +
                "\n" +
                "</html>"));

        app.get("/data/{name}", ctx -> {
            if(MainActivity.data.containsKey(ctx.pathParam("name"))) {
                ctx.result(String.valueOf(MainActivity.data.get(ctx.pathParam("name"))));
            } else{
                ctx.result("Please enter a valid name");
            }
        });

        app.get("/data", ctx -> {
            ctx.contentType("application/json").result(gson.toJson(MainActivity.data));
        });
    }

    public void stop(){
        app.stop();
    }

}
