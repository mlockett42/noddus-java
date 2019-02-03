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
        private void ReturnResponse(HttpExchange t, int statuscode, String response )  throws IOException {
          t.sendResponseHeaders(statuscode, response.length());
          OutputStream os = t.getResponseBody();
          os.write(response.getBytes());
          os.close();
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("POST")) {
              StringBuilder body = new StringBuilder();
              try (InputStreamReader reader = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8.name())) {
                  char[] buffer = new char[256];
                  int read;
                  while ((read = reader.read(buffer)) != -1) {
                      body.append(buffer, 0, read);
                  }
              }
              t.getRequestBody().close();
              JSONObject obj;
              try{
                obj = new JSONObject(body.toString());
              }catch(org.json.JSONException exception){
                // how you handle the exception
                ReturnResponse(t, 400, "There was an exception processing JSON");
                return;
              }
              if (obj.length() != 2) {
                ReturnResponse(t, 400, "The JSON is not correctly formed");
                return;
              }
              NoddusTest tp = NoddusTest.newBuilder()
                .setName(obj.getString("name"))
                .setId(obj.getInt("id")).build();
              // Write the data to the disk
              synchronized (protobuf_output) {
                tp.writeTo(protobuf_output);
              }
              ReturnResponse(t, 200, "Everything is OK");
            } else {
              ReturnResponse(t, 405, "There was an error. Only POST is supported");
            }
        }
    }

}
