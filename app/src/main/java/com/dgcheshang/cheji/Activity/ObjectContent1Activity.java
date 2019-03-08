package com.dgcheshang.cheji.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dgcheshang.cheji.Bean.ObjectBean;
import com.dgcheshang.cheji.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 培训列表
 * */
public class ObjectContent1Activity extends BaseInitActivity implements View.OnClickListener{

    Context context=ObjectContent1Activity.this;

    public static final int LOGIN_CONTENT_SUCCESS = 1;

    String objecttwo="{\"twoobject\":[{\"name1\":\"基础驾驶\",\"number\":11},{\"name1\":\"场地驾驶\",\"number\":12},{\"name1\":\"综合驾驶及考核\",\"number\":13}]}";
    String objectthree="{\"threeobject\":[{\"name1\":\"跟车行驶\",\"number\":21},{\"name1\":\"变更车道\",\"number\":22},{\"name1\":\"靠边停车\",\"number\":23},{\"name1\":\"掉头\",\"number\":24},{\"name1\":\"通过路口\",\"number\":25},{\"name1\":\"通过人行横道\",\"number\":26},{\"name1\":\"通过学校区域\",\"number\":27},{\"name1\":\"通过公共汽车站\",\"number\":28},{\"name1\":\"会车\",\"number\":29},{\"name1\":\"超车\",\"number\":30},{\"name1\":\"夜间驾驶\",\"number\":31},{\"name1\":\"恶劣条件下的驾驶\",\"number\":32},{\"name1\":\"山区道路驾驶\",\"number\":33},{\"name1\":\"高速公路驾驶\",\"number\":34},{\"name1\":\"行驶路线选择\",\"number\":35},{\"name1\":\"综合驾驶及考核\",\"number\":36}]}";
    String objecttype;
    List<ObjectBean> objectBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_content1);
        initView();
    }

    /**
     * 初始化布局
     * */
    private void initView() {
        Bundle extras = getIntent().getExtras();
        objecttype = extras.getString("objecttype");//获取StudyChangeActivity的培训部分：2为第二部分，3为第三部分
        View layout_back = findViewById(R.id.layout_back);//返回
        TextView tv_title = (TextView) findViewById(R.id.tv_title);//标题
        ListView listView = (ListView) findViewById(R.id.listView);
        JSONObject jsonObject = null;
        try {
            Gson gson = new Gson();
            if(objecttype.toString().equals("2")){
                tv_title.setText("第二部分");
                jsonObject = new JSONObject(objecttwo);
                String twoobject = jsonObject.getString("twoobject");
                objectBean = gson.fromJson(twoobject, new TypeToken<List<ObjectBean>>() {
                }.getType());

            }else if(objecttype.toString().equals("3")){
                tv_title.setText("第三部分");
                jsonObject = new JSONObject(objectthree);
                String twoobject = jsonObject.getString("threeobject");
                objectBean = gson.fromJson(twoobject, new TypeToken<List<ObjectBean>>() {
                }.getType());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        layout_back.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("pxnr",objectBean.get(position).getName1());//传值培训内容
                intent.putExtra("kcbh",objectBean.get(position).getNumber());//传培训课程编号
                intent.putExtra("objecttype",objecttype);//培训第几部分
                setResult(LOGIN_CONTENT_SUCCESS,intent);
                finish();
            }
        });
    }



    /**
     * 点击监听
     * */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_back://返回
                finish();
                break;
        }
    }

    /**
     * list适配器
     * */
    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return   objectBean.size();

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
            ViewHodler viewHodler=null;
            if(convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.object_list_item,null);
                viewHodler = new ViewHodler();
                viewHodler.tv_content = (TextView) convertView.findViewById(R.id.tv_content);

                convertView.setTag(viewHodler);
            }else {
                viewHodler = (ViewHodler) convertView.getTag();
            }

            ObjectBean objectBean = ObjectContent1Activity.this.objectBean.get(position);

            viewHodler.tv_content.setText(objectBean.getName1());

            return convertView;
        }
    }
    public class ViewHodler{
        TextView tv_content;

    }

}
