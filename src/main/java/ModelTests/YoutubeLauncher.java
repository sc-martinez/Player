package ModelTests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.awt.Desktop;
import java.net.URI;

public class YoutubeLauncher implements Runnable{
    Song s;
    public YoutubeLauncher(Song s){
        this.s=s;
    }

    public String findYoutubeLink() {
        String key="AIzaSyDbdxrTsdm5pgTHnHJKHV9XPEuwv6IaOjg";
        String qry="youtube/"+s.getArtist().toLowerCase().replaceAll(" ","")+"/"+s.getTitle().toLowerCase().replaceAll(" ","");
        System.out.println(qry);
        try {
            URL url = new URL(
                    "https://www.googleapis.com/customsearch/v1?key=" + key + "&cx=013036536707430787589:_pqjad5hr1a&q=" + qry + "&alt=json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {

                if (output.contains("\"link\": \"")) {
                    String link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(), output.indexOf("\","));
                    if(link.matches("^https://www.youtube.com/.*")){
                        System.out.println(link);
                        return link;
                    }
                }
            }
            conn.disconnect();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return  null;
    }
    private void openYoutube(String link){
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(link));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        openYoutube(findYoutubeLink());
    }
}
