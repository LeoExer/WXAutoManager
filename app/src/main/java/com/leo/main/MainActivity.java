package com.leo.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leo.common.Config;
import com.leo.common.util.DensityUtils;
import com.leo.common.util.SPHelper;
import com.leo.service.AutoOpenLuckyMoneyService;
import com.leo.service.AutoReplyService;
import com.leo.wxautomanager.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_REPLY = "reply";
    private static final String KEY_LUCKY_MONEY = "lucky_money";
    private static final String KEY_SELECT_ID = "select_id";
    private static final String KEY_AUTO_REPLY_TEXT = "reply_text";

    private MyAdapter mAdapter;

    private final List<String> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SPHelper.getInstance().init(this);
        initData();
        initView();
    }

    private void initData() {
        Config.isOpenAutoReply = SPHelper.getInstance().getBoolean(KEY_REPLY);
        Config.isOpenAutoOpenLuckyMoney = SPHelper.getInstance().getBoolean(KEY_LUCKY_MONEY);
        int selectId = SPHelper.getInstance().getInt(KEY_SELECT_ID);
        if (selectId < 0 || selectId > 2) {
            selectId = 0;
        }
        Config.SelectId = selectId;
        Config.AutoReplyText = SPHelper.getInstance().getString(KEY_AUTO_REPLY_TEXT);

        // 自动回复默认文本
        mData.add("Hey~");
        mData.add("你已被拉进黑名单");
        mData.add("忙碌.jpg");
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddReplyView(MainActivity.this);
            }
        });

        SwitchCompat swReply = (SwitchCompat) findViewById(R.id.sw_auto_reply);
        swReply.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkAutoReplyService(MainActivity.this);
                    Config.isOpenAutoReply = true;
                    SPHelper.getInstance().putBoolean(KEY_REPLY, true);
                } else {
                    Config.isOpenAutoReply = false;
                    SPHelper.getInstance().putBoolean(KEY_REPLY, false);
                }
            }
        });
        swReply.setChecked(Config.isOpenAutoReply);

        SwitchCompat swLuckyMoney = (SwitchCompat) findViewById(R.id.sw_auto_open_lucky_money);
        swLuckyMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkAutoOpenLuckyMoneyService(MainActivity.this);
                    Config.isOpenAutoOpenLuckyMoney = true;
                    SPHelper.getInstance().putBoolean(KEY_LUCKY_MONEY, true);
                } else {
                    Config.isOpenAutoOpenLuckyMoney = false;
                    SPHelper.getInstance().putBoolean(KEY_LUCKY_MONEY, false);
                }
            }
        });
        swLuckyMoney.setChecked(Config.isOpenAutoOpenLuckyMoney);


        mAdapter = new MyAdapter(this, mData, Config.SelectId);
        ListView lvContents = (ListView) findViewById(R.id.lv_reply_content);
        lvContents.setAdapter(mAdapter);
    }


    private final int ADD_TEXT_LIMIT = 8;
    private void showAddReplyView(final Context context) {
        final RelativeLayout contentView = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.setMargins(DensityUtils.dp2px(context, 30), 0,
                DensityUtils.dp2px(context, 20), 0);
        final EditText editText =  new EditText(context);
        contentView.addView(editText, params);

        final Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("添加自动回复文本")
                .setView(contentView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (text.length() > 0) {
                            if (mData.size() < ADD_TEXT_LIMIT) {
                                addText(text);
                                Snackbar.make(contentView, "添加文本成功", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            } else {
                                Snackbar.make(contentView, "添加文本已到上限", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void addText(String text) {
            mData.add(text);
            mAdapter.notifyDataSetChanged();
    }

    private void checkAutoReplyService(Context context) {
        if (!AutoReplyService.isConnected()) {
            showAccessibilityServiceSettings(context);
        }
    }

    private void checkAutoOpenLuckyMoneyService(Context context) {
        if (!AutoOpenLuckyMoneyService.isConnected()) {
            showAccessibilityServiceSettings(context);
        }
    }

    private Dialog mDialog = null;
    private void showAccessibilityServiceSettings(Context context) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new AlertDialog.Builder(context)
                .setMessage("使用微信自动服务需要打开辅助服务, 去设置?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        mDialog.show();
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
                        SPHelper.getInstance().putInt(KEY_SELECT_ID, mSelectId);
                        String text = mData.get(position);
                        Config.AutoReplyText = text;
                        SPHelper.getInstance().putString(KEY_AUTO_REPLY_TEXT, text);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }
}
