package com.dgcheshang.cheji.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dgcheshang.cheji.Bean.ValidTimeBean;
import com.dgcheshang.cheji.Bean.VersionBean;
import com.dgcheshang.cheji.CjApplication;
import com.dgcheshang.cheji.R;
import com.dgcheshang.cheji.Tools.DesUtils;
import com.dgcheshang.cheji.netty.conf.NettyConf;
import com.dgcheshang.cheji.networkUrl.NetworkUrl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 有效学时
 * */
public class ValidTimeActivity extends BaseInitActivity implements View.OnClickListener{
    String url="http://wx.dgcheshang.cn/we/webservlets/pxjlcx.sev?fun=1&nid=";
    SharedPreferences sp;
    Context context=ValidTimeActivity.this;
    String xyidcard;
    ListView listview;
    TextView tv_nocontent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid_time);
        initView();
        getData();
    }

    private void initView() {
        sp = getSharedPreferences("student", Context.MODE_PRIVATE); //私有数据
        xyidcard = sp.getString("xyidcard", "");//身份证
        View layout_back = findViewById(R.id.layout_back);
        layout_back.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.listview);
        tv_nocontent = (TextView) findViewById(R.id.tv_nocontent);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back://返回
                finish();
                break;
        }
    }

    public class Adapter extends BaseAdapter {
        @Override
        public int getCount() {
                return 3;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHodler viewHodler = null;
            if(convertView==null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.grid_validtime_item, null);
                viewHodler = new ViewHodler();
                viewHodler.tv_opject  = (TextView) convertView.findViewById(R.id.tv_opject);
                viewHodler.tv_sum_time  = (TextView) convertView.findViewById(R.id.tv_sum_time);
                viewHodler.tv_valid_time  = (TextView) convertView.findViewById(R.id.tv_valid_time);
                viewHodler.tv_sum_lc  = (TextView) convertView.findViewById(R.id.tv_sum_lc);

                convertView.setTag(viewHodler);
            }else {
                viewHodler = (ViewHodler) convertView.getTag();
            }

            if(position==0){
                viewHodler.tv_opject.setText("科目一");
                viewHodler.tv_sum_time.setText(validtimebean.getKm1zxs());
                viewHodler.tv_valid_time.setText(validtimebean.getKm1yxxs());
                viewHodler.tv_sum_lc.setText(validtimebean.getKm1zlc());
            }else if(position==1){
                viewHodler.tv_opject.setText("科目二");
                viewHodler.tv_sum_time.setText(validtimebean.getKm2zxs());
                viewHodler.tv_valid_time.setText(validtimebean.getKm2yxxs());
                viewHodler.tv_sum_lc.setText(validtimebean.getKm2zlc());
            }else if(position==2){
                viewHodler.tv_opject.setText("科目三");
                viewHodler.tv_sum_time.setText(validtimebean.getKm3zxs());
                viewHodler.tv_valid_time.setText(validtimebean.getKm3yxxs());
                viewHodler.tv_sum_lc.setText(validtimebean.getKm3zlc());
            }
            return convertView;
        }
    }

    public  class ViewHodler {
        TextView tv_opject;
        TextView tv_sum_time;
        TextView tv_valid_time;
        TextView tv_sum_lc;
    }

    /**
     *
     * */
    ValidTimeBean validtimebean;
    private void getData() {
        StringRequest request = new StringRequest(Request.Method.POST, url+xyidcard, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG","有效学时="+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String errorcode = jsonObject.getString("errorcode");
                    if(errorcode.equals("0")){
                        String pxjlobj = jsonObject.getString("pxjlobj");
                        Gson gson = new Gson();
                        validtimebean = gson.fromJson(pxjlobj, ValidTimeBean.class);
                        tv_nocontent.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);
                        Adapter adapter = new Adapter();
                        listview.setAdapter(adapter);
                    }else {
                        String message = jsonObject.getString("msg");
                        tv_nocontent.setVisibility(View.VISIBLE);
                        listview.setVisibility(View.GONE);
                        tv_nocontent.setText(message);
                        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TGA","volleyError="+volleyError);
                Toast.makeText(context,"网络请求失败",Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                String key = "9ba45bfd500642328ec03ad8ef1b6e75";// 自定义密钥
                long time = new Date().getTime();
                try {
                    DesUtils des = new DesUtils(key, "utf-8");
                    map.put("timestamp",String.valueOf(time));
                    map.put("accticket",des.encode(String.valueOf(time)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return map;
            }
        };
        CjApplication.getHttpQueue().add(request);

    }

}
