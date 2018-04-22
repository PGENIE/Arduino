package companydomain.arduinoproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static companydomain.arduinoproject.R.id.timeText;

public class MainActivity extends AppCompatActivity{

    TextView timeBox;
    TextView windowBox;
    TextView weatherBox;
    TextView locationBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new SendServer().execute("http://172.20.10.3:3000/getState");
        //setUpdate();
    }



    public void openClicked(View V){
        Toast.makeText(getApplicationContext(), "OPEN CLICKED", Toast.LENGTH_SHORT).show();
        new SendServer().execute("http://172.20.10.3:3000/openWindow");
    }

    public void closeClicked(View v){
        //Toast.makeText(getApplicationContext(), "CLOSE CLICKED", Toast.LENGTH_SHORT).show();
        new SendServer().execute("http://172.20.10.3:3000/closeWindow");
    }

    public void refreshClicked(View v){
        new SendServer().execute("http://172.20.10.3:3000/getState");
    }

    public class SendServer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                //making JSONObject and save key value
                // Toast.makeText(getApplicationContext(),id+" "+password, Toast.LENGTH_SHORT).show();
                //JSONObject jsonObject = new JSONObject();
                //jsonObject.accumulate("username", id);
                // jsonObject.accumulate("password",password);
                String body = "hello";
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{

                    URL url = new URL(urls[0]);

                    //connect

                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");//Using post method
                    //con.setRequestProperty("Cache-Control", "no-cache");//cache setting
                    //con.setRequestProperty("Content-Type", "application/json");//send "application JSON form"
                    //con.setRequestProperty("Accept", "text/html");//responseof server, get data by html
                    con.setDoOutput(true);//Outstream, post data send
                    con.setDoInput(true);//Inputstream, get response from server
                    con.connect();

                    //making stream for sending server

                    OutputStream outStream = con.getOutputStream();

                    //making buffer and put it
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

                    //Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();

                    //Log.i("json",jsonObject.toString());
                    writer.write(body);
                    writer.flush();
                    writer.close();//get buffer

                    //get data from server

                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while((line = reader.readLine()) != null){

                        buffer.append(line);

                    }

                    return buffer.toString();//return value from server.

                } catch (MalformedURLException e){

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();

                } finally {

                    if(con != null){
                        con.disconnect();
                    }

                    try {
                        if(reader != null){
                            reader.close();//close buffer
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            long now;
            Date date;
            SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


            super.onPostExecute(result);
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); //value from server "successful / invalid"
            //message=result;
            //,기준으로 파싱해서 배열에 넣기 3개짜리. 그래서 state이면 배열 0 1 2 쓰고 open/close면 배열 0번만 쓰면 됨

            // String example = "hello1,hello2,";
            //String[] parseValues = example.split(",");
            String[] parseValues = result.split(",");

            // Log.i("tag_location",parseValues[0]);
            // Log.i("tag_window",parseValues[1]);\
            // Log.i("tag_weather",parseValues[2]);

            if(parseValues[0].equals("windowSet=0") || parseValues[0].equals("windowSet=1") )//open or close clicked
            {
                Toast.makeText(getApplicationContext(), "press Refresh button after 10 sec...", Toast.LENGTH_LONG).show();
            }
            else //getState setting하는부분
            {
                timeBox = (TextView) findViewById(timeText);
                windowBox = (TextView) findViewById(R.id.windowText);
                weatherBox = (TextView) findViewById(R.id.weatherText);
                locationBox = (TextView) findViewById(R.id.locationText);

                now = System.currentTimeMillis();
                date = new Date(now);
                timeBox.setText(dateForm.format(date));

                String location = parseValues[0];
                String weather = parseValues[2];

                locationBox.setText(location);
                weatherBox.setText(weather);

                if (parseValues[1].equals("0")) {
                    String window = "closed";
                    windowBox.setText(window);
                } else if (parseValues[1].equals("1")) {
                    String window = "opened";
                    windowBox.setText(window);
                } else {
                    String window = "before getting info";
                    windowBox.setText(window);
                }


            }

        }

    }

    public void setUpdate(String time, String location, String window, String weather){
    }
}
