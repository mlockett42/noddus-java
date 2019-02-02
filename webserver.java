import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONObject;

class WebServer {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting webserver");
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

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
