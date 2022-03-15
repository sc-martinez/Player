package ModelTests;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AzlyricsConncector extends LyricsConnector implements Runnable{

    private String URL;
    private String artist;
    private String title;
    private String text;

    public AzlyricsConncector(String artist,String title){
        this.artist=artist;
        this.title=title;
        this.URL="azlyrics";
    }


    @Override
    protected String  findWeb() {
        String key="AIzaSyDbdxrTsdm5pgTHnHJKHV9XPEuwv6IaOjg";
        String qry="azlyrics/"+artist.toLowerCase().replaceAll(" ","")+"/"+title.toLowerCase().replaceAll(" ","");
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
                    if(link.matches("^https://www.azlyrics.com/lyrics/.*")){
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
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @Override
    protected void setText(String _url) {
        Pattern pattern = Pattern.compile("<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->(.*?)</div><br><br><!-- MxM banner -->");
        StringBuilder html=new StringBuilder();
        try {
            URL url=new URL(_url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String htmlString=html.toString();
        Matcher matcher=pattern.matcher(htmlString);
        if (matcher.find()) {
            text=matcher.group(1).replaceAll("<br>","\n");
            text=text.replaceAll("<i>"," ");
            text=text.replaceAll("</i>"," ");
        }
    }

    public String returnLyrics(){
        System.out.println("hmh\n"+text);
        return text;
    }




    @Override
    public void run() {
        String link=findWeb();
        if(link!=null){
            setText(link);
        }
        else {
            System.out.println("puste");
        }
    }
}
