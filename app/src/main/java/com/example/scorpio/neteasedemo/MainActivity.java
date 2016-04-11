package com.example.scorpio.neteasedemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scorpio.neteasedemo.domain.NewInfo;
import com.loopj.android.image.SmartImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvNews;
    private static final String TAG = "MainActivity";
    private final int SUCCESS=0;
    private final int FAILED=1;
    private List<NewInfo> newInfoLlist;
    
    private Handler handler = new Handler() {
        
        /*接收消息*/
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case SUCCESS://访问成功，有数据
                    //给Listview列表绑定数据

                    newInfoLlist = (List<NewInfo>) msg.obj;
                    
                    MyAdapter adapter = new MyAdapter();
                    lvNews.setAdapter(adapter);
                    break;
                case FAILED://无数据
                    Toast.makeText(MainActivity.this,"当前网络崩溃了",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        lvNews = (ListView) findViewById(R.id.lv_news);

        //抓取新闻数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获得新闻集合
                List<NewInfo> newInfoList = getNewsFromInternet();
                Message msg = new Message();
                if (newInfoList != null){
                    msg.what = SUCCESS;
                    msg.obj = newInfoList;
                }else {
                    msg.what = FAILED;
                }
                handler.sendMessage(msg);
            }
        }).start();

    }

    /*返回新闻信息*/
    private List<NewInfo> getNewsFromInternet() {
        HttpClient client = null;

        try {
            //定义一个客户端
            client = new DefaultHttpClient();

            //定义get方法
            HttpGet get = new HttpGet("http://10.0.2.2:8080/NetEaseServer/new.xml");

            //执行请求
            HttpResponse response = client.execute(get);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                InputStream is = response.getEntity().getContent();
                List<NewInfo> newInfoList = getNewsListFromInputStream(is);
                return newInfoList;
            } else {

                Log.i(TAG, "访问失败：" + statusCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null){
                client.getConnectionManager().shutdown();//关闭和释放资源
            }
        }
        return null;
    }

    /*从流中解析新闻集合*/
    private List<NewInfo> getNewsListFromInputStream(InputStream is) throws Exception {
        XmlPullParser parser = Xml.newPullParser();//创建一个pull解析器
        parser.setInput(is, "utf-8");//指定解析流和编码

        int evenType = parser.getEventType();

        List<NewInfo> newInfoList = null;
        NewInfo newInfo = null;
        while (evenType != XmlPullParser.END_DOCUMENT) {//如果没有到结尾处，继续循环

            String tagName = parser.getName();//节点名称
            switch (evenType) {
                case XmlPullParser.START_TAG://<new>
                    if ("news".equals(tagName)) {
                        newInfoList = new ArrayList<NewInfo>();
                    } else if ("new".equals(tagName)) {
                        newInfo = new NewInfo();
                    } else if ("title".equals(tagName)) {
                        newInfo.setTitle(parser.nextText());
                    } else if ("detail".equals(tagName)) {
                        newInfo.setDetail(parser.nextText());
                    } else if ("comment".equals(tagName)) {
                        newInfo.setComment(Integer.valueOf(parser.nextText()));
                    } else if ("image".equals(tagName)) {
                        newInfo.setImageUrl(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG://</news>
                    if ("new".equals(tagName)) {
                        newInfoList.add(newInfo);
                    }
                    break;
                default:
                    break;
            }
            evenType = parser.next();//取下一个事件类型
        }
        return newInfoList;
    }
    
    class MyAdapter extends BaseAdapter{

        /*返回列表的总长度*/
        @Override
        public int getCount() {
            return newInfoLlist.size();
        }

        
        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /*返回一个列表的子条目的布局*/
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            
            if (convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.listview_item,null);
                
            }else {
                view = convertView;
                
            }
            
            //重新赋值，不会产生缓存对象中原有数据中保留的现象
            SmartImageView sivIcon = (SmartImageView) view.findViewById(R.id.siv_listview_item_icon);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_listview_item_title);
            TextView tvDetail = (TextView) view.findViewById(R.id.tv_listview_item_detail);
            TextView tvComment = (TextView) view.findViewById(R.id.tv_listview_item_comment);
            
            NewInfo newInfo = newInfoLlist.get(position);
            
            sivIcon.setImageUrl(newInfo.getImageUrl());//设置图片
            tvTitle.setText(newInfo.getTitle());//设置标题
            tvDetail.setText(newInfo.getDetail());//设置详细
            tvComment.setText(newInfo.getComment()+"跟帖");//设置跟帖数量
            return view;
        }
    }
}
