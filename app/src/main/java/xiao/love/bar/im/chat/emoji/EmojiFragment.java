package xiao.love.bar.im.chat.emoji;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;


import java.lang.reflect.Field;

import xiao.love.bar.R;

/**
 * Created by guochang on 2015/9/9.
 */
public class EmojiFragment extends Fragment implements AdapterView.OnItemClickListener{
    private Context mContext;
    private LayoutInflater mInflater;
    //表情数据，每个值都是表情资源的名称
    private String[] mEmojiData;
    //gridview中每个item的布局
    private int mItemLayout;
    //gridview要显示的列数
    private int mGridViewColumns;
    private EditText mEditText;
    //删除按钮的position
    private int mDelPos;

    public void init(Context context, EditText editText, String[] emojiData, int emojiType) {
        mContext = context;
        mEditText = editText;
        mEmojiData = emojiData;
        //获取删除按钮所在的位置
        mDelPos = emojiData.length - 1;

        switch (emojiType) {
            case EmojiConstant.EMOJI_TYPE_CLASSICAL:
                mItemLayout = R.layout.emoji_classical_item;
                mGridViewColumns = 7;
                break;
            case EmojiConstant.EMOJI_TYPE_PEACH:
                mItemLayout = R.layout.emoji_peach_item;
                mGridViewColumns = 7;
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mEmojiData = savedInstanceState.getStringArray("emojiData");
        }

        mInflater = inflater;
        View v = inflater.inflate(R.layout.emoji_gridview, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(this);
        gridView.setNumColumns(mGridViewColumns);
        gridView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("emojiData", mEmojiData);
    }

    BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return mEmojiData.length;
        }

        @Override
        public Object getItem(int position) {
            return mEmojiData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(mItemLayout, parent, false);
                holder = new ViewHolder();
                holder.icon = convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String iconName = mEmojiData[position];
            try {
                Field field = R.drawable.class.getField(iconName);
                int resId = Integer.parseInt(field.get(null).toString());
                holder.icon.setBackgroundResource(resId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class ViewHolder {
            public View icon;
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(id == mDelPos){
            //如果是删除按钮则，则抛出删除事件
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            mEditText.dispatchKeyEvent(event);
        }else {
            //插入表情
            String emojiName = (String) mAdapter.getItem((int) id);
            SpannableString spannableString = EmojiParse.parseOneEmoji(mContext, emojiName);

            int insertPos = mEditText.getSelectionStart();
            mEditText.getText().insert(insertPos, spannableString);
        }
    }
}
