import com.harium.dotenv.Env;
import com.harium.web.Web;
import spark.ModelAndView;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.*;
import java.util.Map;

import static spark.Spark.*;

public class Server {

    public static void main(String[] args) {
        String host = Env.get("HOST");
        System.out.println(host);

        port(Integer.parseInt(Env.get("PORT")));
        Web.host(host);

        get("/", (request, response) -> {
            Map<String, Object> model = Web.buildModel(request);
            return new ModelAndView(model, "/public/index.html");
        }, new VelocityTemplateEngine());

        serveFile("etyl-examples.jar");
        serveFile("etyl-examples.jar.js");

        Web.acceptEndSlash();
        Web.init();
    }

    private static void serveFile(String filename) {
        head(filename, (request, response) -> {
            serveFile(response, filename);
            return null;
        });

        get(filename, (request, response) -> {
            serveFile(response, filename);
            return null;
        });
    }

    private static void serveFile(Response response, String filename) {
        String path = System.getProperty("user.dir");
        String folder = "/src/main/resources/public/";

        File f = new File(path + folder + filename);

        response.raw().setContentLengthLong(f.length());

        try {
            OutputStream os = response.raw().getOutputStream();
            byte[] buf = new byte[2048];
            InputStream is = new FileInputStream(f);
            int c = 0;
            while ((c = is.read(buf, 0, buf.length)) > 0) {
                os.write(buf, 0, c);
                os.flush();
            }
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
