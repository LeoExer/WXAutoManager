package com.leo.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.common.Config;
import com.leo.common.util.DataManager;
import com.leo.wxautomanager.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_REPLY = "reply";
    private static final String KEY_LUCKY_MONEY = "lucky_money";
    private static final String KEY_SELECT_ID = "select_id";
    private static final String KEY_AUTO_REPLY_TEXT = "reply_text";

    SwitchCompat swReply;
    SwitchCompat swLuckyMoney;
    ListView lvContents;
    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.getInstance().init(this);
        initData();
        initView();
    }

    private void initData() {
        Config.isOpenAutoReply = DataManager.getInstance().getBoolean(KEY_REPLY);
        Config.isOpenAutoOpenLuckyMoney = DataManager.getInstance().getBoolean(KEY_LUCKY_MONEY);
        Config.SelectId = DataManager.getInstance().getInt(KEY_SELECT_ID);
        Config.AutoReplyText = DataManager.getInstance().getString(KEY_AUTO_REPLY_TEXT);
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                addData(MainActivity.this);
            }
        });

        swReply = (SwitchCompat) findViewById(R.id.sw_auto_reply);
        swReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Config.isOpenAutoReply = true;
                    DataManager.getInstance().putBoolean(KEY_REPLY, true);
                } else {
                    Config.isOpenAutoReply = false;
                    DataManager.getInstance().putBoolean(KEY_REPLY, false);
                }
            }
        });
        swReply.setChecked(Config.isOpenAutoReply);

        swLuckyMoney = (SwitchCompat) findViewById(R.id.sw_auto_open_lucky_money);
        swLuckyMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Config.isOpenAutoOpenLuckyMoney = true;
                    DataManager.getInstance().putBoolean(KEY_LUCKY_MONEY, true);
                } else {
                    Config.isOpenAutoOpenLuckyMoney = false;
                    DataManager.getInstance().putBoolean(KEY_LUCKY_MONEY, false);
                }
            }
        });
        swLuckyMoney.setChecked(Config.isOpenAutoOpenLuckyMoney);

        List<String> data = new ArrayList<>();
        data.add("hello world");
        data.add("我很忙");
        data.add("我现在没空，稍后回复");
        mAdapter = new MyAdapter(this, data, Config.SelectId);
        lvContents = (ListView) findViewById(R.id.lv_reply_content);
        lvContents.setAdapter(mAdapter);
    }


    private void addData(final Context context) {
        final Dialog dialog = new Dialog(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        dialog.setContentView(contentView);
        final EditText editText = (EditText) contentView.findViewById(R.id.ed_add_text);
        contentView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /* ==== Adapter ==== */
    private class MyAdapter extends BaseAdapter {

        List<String> mData;
        LayoutInflater mInflater;
        int mSelectId;

        MyAdapter(Context context, List<String> data, int selectId) {
            mData = data;
            mInflater = LayoutInflater.from(context);
            mSelectId = selectId;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.listview_item, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.checkBox = (AppCompatCheckBox) convertView.findViewById(R.id.cb_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView.setText(mData.get(position));
            viewHolder.checkBox.setChecked((mSelectId == position));
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        mSelectId = position;
                        notifyDataSetChanged();
                        // 保存数据
                        DataManager.getInstance().putInt(KEY_SELECT_ID, mSelectId);
                        String text = mData.get(position);
                        Config.AutoReplyText = text;
                        DataManager.getInstance().putString(KEY_AUTO_REPLY_TEXT, text);
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView textView;
            AppCompatCheckBox checkBox;
        }
    }

}
