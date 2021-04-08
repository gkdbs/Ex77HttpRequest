package com.mrhi2020.ex77httprequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etMsg;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitle= findViewById(R.id.et_title);
        etMsg= findViewById(R.id.et_msg);
        tv= findViewById(R.id.tv);
    }

    public void clickGet(View view) {
        //네트워크 작업은 항상 별도 Thread
        new Thread(){
            @Override
            public void run() {

                //서버로 보낼 데이터들
                String title= etTitle.getText().toString();
                String msg= etMsg.getText().toString();

                //GET 방식으로 보낼 서버의 주소
                String serverUrl="http://mrhi2021.dothome.co.kr/Android/getTest.php";

                //URL에는 한글 및 특수문자 사용 불가 - 한글을 URL에 사용될 수 있도록 암호화[인코딩]
                try {
                    title= URLEncoder.encode(title, "utf-8");
                    msg= URLEncoder.encode(msg, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //get 방식은 URL 뒤에 ?로 붙이고 요청파라미터 값들(title, msg)을 전송.
                String getUrl= serverUrl+"?title="+title+"&msg="+msg;

                //서버와 연결작업!
                try {
                    URL url= new URL(getUrl);

                    //Get 방식은 이미 서버주소에 값을 붙어서 전송되기에 별도의 전송작업 필요없음
                    //즉, OutpuStream 은 필요하지 않음

                    //서버(getTest.php)에서 echo된 글씨을 읽어오기 위해 InputStream필요
                    InputStream is= url.openStream();
                    InputStreamReader isr= new InputStreamReader(is);
                    BufferedReader reader= new BufferedReader(isr);

                    final StringBuffer buffer= new StringBuffer();

                    String line= reader.readLine();
                    while (line!=null){
                        buffer.append(line+"\n");
                        line= reader.readLine();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText( buffer.toString() );
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public void clickPost(View view) {
        new Thread(){
            @Override
            public void run() {

                String title= etTitle.getText().toString();
                String msg= etMsg.getText().toString();

                //POST 방식으로 데이터를 보낼 서버 주소
                String serverUrl="http://mrhi2021.dothome.co.kr/Android/postTest.php";

                try {
                    URL url= new URL(serverUrl);
                    //URL은 InputStream만 열수 있음

                    //HTTP 통신 규약에 따라 데이터를 주고받는 역할을
                    //수행하는 URL객체의 조수객체가 있음
                    HttpURLConnection connection= (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");//반드시 대문자
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    //보낼 데이터
                    String data="title="+title+"&msg="+msg;

                    //데이터를 OutputStream을 통해 직접 내보내기
                    OutputStream os= connection.getOutputStream();
                    OutputStreamWriter writer= new OutputStreamWriter(os);

                    writer.write(data, 0, data.length());
                    writer.flush();
                    writer.close();

                    //서버(postTest.php)에서 echo 시킨 문자열 읽어오기
                    InputStream is= connection.getInputStream();
                    InputStreamReader isr= new InputStreamReader(is);
                    BufferedReader reader= new BufferedReader(isr);

                    final StringBuffer buffer= new StringBuffer();
                    String line= reader.readLine();
                    while (line!=null){
                        buffer.append(line+"\n");
                        line= reader.readLine();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(buffer.toString());
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}