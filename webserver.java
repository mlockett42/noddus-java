import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONObject;

import com.noddus.test.NoddusTestProto.NoddusTest;

import java.util.TimerTask;

class WebServer {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting webserver");
        protobuf_output = new FileOutputStream("/opt/project/output/proto.output");

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {

                    @Override
                    public void run() {
                      try {
                        synchronized (protobuf_output) {
                          protobuf_output.close();
                          protobuf_output = new FileOutputStream("/opt/project/output/proto.output");
                        }
                      } catch (FileNotFoundException exception) {
                        System.out.println("FileNotFoundException recreating proto.output");
                      } catch (IOException exception) {
                        System.out.println("IOException recreating proto.output");
                      }
                    }
                }, 15000, 15000);
    }

    static public FileOutputStream protobuf_output;

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            //System.out.println("Request method = " + t.getRequestMethod());
            if (t.getRequestMethod().equals("POST")) {
              System.out.println("Is POST");

              StringBuilder body = new StringBuilder();
              try (InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8.name())) {
                  char[] buffer = new char[256];
                  int read;
                  while ((read = reader.read(buffer)) != -1) {
                      body.append(buffer, 0, read);
                  }
              }
              t.getRequestBody().close();
              System.out.println("body ="+ body);
              JSONObject obj;
              try{
                obj = new JSONObject(body.toString());
                System.out.println("name ="+ obj.getString("name"));
                System.out.println("id ="+ obj.getInt("id"));
              }catch(org.json.JSONException exception){
                // how you handle the exception
                // e.printStackTrace();
                String response = "There was an exception processing JSON";
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
              }
              if (obj.length() != 2) {
                String response = "The JSON is not correctly formed";
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
              }
              NoddusTest tp = NoddusTest.newBuilder()
                .setName(obj.getString("name"))
                .setId(obj.getInt("id")).build();
              // Write the data to the disk
              //FileOutputStream output = new FileOutputStream("/opt/project/output/proto.output");
              synchronized (protobuf_output) {
                tp.writeTo(protobuf_output);
              }
              //output.close();
              String response = "This is the response";
              t.sendResponseHeaders(200, response.length());
              OutputStream os = t.getResponseBody();
              os.write(response.getBytes());
              os.close();
            } else {
              System.out.println("Is not POST");
              String response = "There was an error";
              t.sendResponseHeaders(405, response.length());
              OutputStream os = t.getResponseBody();
              os.write(response.getBytes());
              os.close();
            }
        }
    }

}
