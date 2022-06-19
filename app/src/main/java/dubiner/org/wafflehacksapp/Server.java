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
                "    <p>Acceleration X: </p><div id=\"x\"></div>\n" +
                "    <p>Acceleration Y: </p><div id=\"y\"></div>\n" +
                "    <p>Acceleration Z: </p><div id=\"z\"></div><br>\n" +
                "    <button type=\"button\" id=\"pause\" onclick=\"pause()\"> Pause</button>\n" +
                "    <div id=\"test\"></div>\n"     +
                "\n" +
                "\n" +
                "    <script>\n" +
                "       var isPaused = 0;" +
                "       function pause(){" +
                "           isPaused++" +
                "}\n"+

                "var sumX = 0;\n"+
                "var sumY = 0;\n"+
                "var sumZ = 0;\n"+
                "var averageListX = [0,0,0,0,0];\n"+
                "var averageListY = [0,0,0,0,0];\n"+
                "var averageListZ = [0,0,0,0,0];\n"+


                "         var aX = 0;\n" +
                "            var aY = 0;\n" +
                "            var aZ = 0;\n" +
                "\n" +
                "            var vX = 0;\n" +
                "            var vY = 0;\n" +
                "            var vZ = 0;\n" +
                "\n" +
                "            var pvX = 0;\n" +
                "            var pvY = 0;\n" +
                "            var pvZ = 0;\n" +
                "\n" +
                "            var X = 0;\n" +
                "            var Y = 0;\n" +
                "            var Z = 0;\n" +
                "\n" +
                "            var pX = 0;\n" +
                "            var pY = 0;\n" +
                "            var pZ = 0;\n" +
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
                "            x: [aX],\n" +
                "            y: [aY],\n" +
                "            z: [aZ],\n" +
                "            opacity: [opacity],\n" +
                "            line: {\n" +
                "                width: lineWidth,\n" +
                "                color: 'rgb(' + r + ',' + g + ',' + b + ')',\n" +
                "                reversescale: false\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        Plotly.newPlot(\"test\", [data], { height: 640 });\n" +
                "\n " +
                "\n" +
                "        setInterval(() => {\n" +
                "          if(isPaused%2 == 0){\n" +
                "            $(\"#x\").load(\"http://" + MainActivity.address + "/data/accelerationX\");\n" +
                "            $(\"#y\").load(\"http://" + MainActivity.address + "/data/accelerationY\");\n" +
                "            $(\"#z\").load(\"http://" + MainActivity.address + "/data/accelerationZ\");\n" +
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
                "            //console.log(\"accelerationX: \" + accelerationX);\n" +
                "            //console.log(\"accelerationY: \" + accelerationY);\n" +
                "            //console.log(\"accelerationZ: \" + accelerationZ);\n" +
                "\n" +
                "            $.getJSON(\"http://" + MainActivity.address + "/data/\", function (data) {\n" +
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
                "            aX = Math.round(json.accelerationX);\n" +
                "            aY = Math.round(json.accelerationY);\n" +
                "            aZ = Math.round(json.accelerationZ);\n" +
                "            r = json.r;\n" +
                "            g = json.g;\n" +
                "            b = json.b;\n" +
                "            opacity = json.opacity;\n" +
                "            lineWidth = json.strokeWidth;\n" +
                "" +
                "            " +



                "            averageListX.push(aX);\n" +
                "            sumX-=averageListX[0];\n" +
                "            averageListX.shift();\n" +
                "            sumX+=aX;\n" +
                "            averageListY.push(aY);\n" +
                "            sumY-=averageListY[0];\n" +
                "            averageListY.shift();\n" +
                "            sumY+=aY;\n" +
                "            averageListZ.push(aZ);\n" +
                "            sumZ-=averageListZ[0];\n" +
                "            averageListZ.shift();\n" +
                "            sumZ+=aZ;\n" +



                "            aX = sumX/5;\n"+
                "            aY = sumY/5;\n"+
                "            aZ = sumZ/5;\n"+



                "               \n" +
                "            console.log(\"aX: \" + aX);   \n" +
                "            console.log(\"vX: \" + vX);   \n" +
                "            console.log(\"X: \" + X);   \n" +
                "            vX = accelarationToVelocity(aX, pvX, 0.001)\n" +
                "            vY = accelarationToVelocity(aY, pvY, 0.001)\n" +
                "            vZ = accelarationToVelocity(aZ, pvZ, 0.001) \n" +
                "            X = velocityToPos(vX, pX, 0.001);\n" +
                "            Y = velocityToPos(vY, pY, 0.001);\n" +
                "            Z = velocityToPos(vZ, pZ, 0.001);\n" +
                "            Plotly.extendTraces(\"test\",\n" +
                "                {\n" +
                "                    x: [[X]],\n" +
                "                    y: [[Y]],\n" +
                "                    z: [[Z]],\n" +
                "                    // opacity: [opacity],\n" +
                "                    // line: {\n" +
                "                    //     width: [lineWidth],\n" +
                "                    //     color: 'rgb(' + r + ',' + g + ',' + b + ')',\n" +
                "                    //     reversescale: false\n" +
                "                    // }\n" +
                "                }, [0]);\n" +
                "\n" +
                "            pX = X;\n" +
                "            pY = Y;\n" +
                "            pZ = Z;\n" +
                "                pvX =  vX;\n" +
                "                pvY =  vY;\n" +
                "                pvZ =  vZ;\n" +
                "\n" +
                "        }}, 1);\n" +
                "\n" +
                "        function accelarationToVelocity(a, pV, t) {\n" +
                "            var v = a*t;\n" +
                "            return (v + pV)*0.9\n" +
                "           }\n"+
                "         function velocityToPos(v, pD, t){\n" +
                "                var d = v*t;\n" +
                "                return d + pD;\n" +
                "            }\n" +
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
