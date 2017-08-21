package com.lenovohit.administrator.contactsdemo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Contact>mContactList = new ArrayList<>();
    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private ContactAdapter mAdapter;
    private AlphabetIndexer mIndexer;
    private ListView mLvList;
    private LinearLayout mLinearLayout;
    private TextView mTvTitle;
    //上一个可见元素
    private int lastFirstVisibleItem = -1;
    private Button alphabetButton;
    private RelativeLayout sectionToastLayout;
    private TextView sectionToastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLvList = (ListView) findViewById(R.id.contactsList);
        mLinearLayout = (LinearLayout) findViewById(R.id.title_layout);
        mTvTitle = (TextView) findViewById(R.id.title);
        alphabetButton = (Button) findViewById(R.id.alphabetButton);
        sectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
        sectionToastText = (TextView) findViewById(R.id.section_toast_text);
        Cursor dataFromSql = getDataFromSql();
        startManagingCursor(dataFromSql);
        mIndexer = new AlphabetIndexer(dataFromSql, 1, alphabet);
        mAdapter = new ContactAdapter(this, R.layout.list_item,mContactList, mIndexer);
        mLvList.setAdapter(mAdapter);
        setupContactsListView();
        setAlpabetListener();
    }
    public Cursor getDataFromSql(){
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri,
                new String[] { "display_name", "sort_key" }, null, null, "sort_key");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String sortKey = getSortKey(cursor.getString(1));
                Contact contact = new Contact();
                contact.setName(name);
                contact.setSortKey(sortKey);
                mContactList.add(contact);
            } while (cursor.moveToNext());
        }
          return  cursor;
    }
    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKeyString
     *            数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }
    /**
     * 为联系人ListView设置监听事件，根据当前的滑动状态来改变分组的显示位置，从而实现挤压动画的效果。
     */
    private void setupContactsListView() {
        mLvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                int section = mIndexer.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = mIndexer.getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLinearLayout.getLayoutParams();
                    params.topMargin = 0;
                    mLinearLayout.setLayoutParams(params);
                    mTvTitle.setText(String.valueOf(alphabet.charAt(section)));
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = mLinearLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLinearLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            mLinearLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                mLinearLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });
    }
    private void setAlpabetListener() {
        alphabetButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float alphabetHeight = alphabetButton.getHeight();
                float y = event.getY();
                int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
                if (sectionPosition < 0) {
                    sectionPosition = 0;
                } else if (sectionPosition > 26) {
                    sectionPosition = 26;
                }
                String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
                int position = mIndexer.getPositionForSection(sectionPosition);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alphabetButton.setBackgroundResource(R.mipmap.a_z_click);
                        sectionToastLayout.setVisibility(View.VISIBLE);
                        sectionToastText.setText(sectionLetter);
                        mLvList.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sectionToastText.setText(sectionLetter);
                        mLvList.setSelection(position);
                        break;
                    default:
                        alphabetButton.setBackgroundResource(R.mipmap.a_z);
                        sectionToastLayout.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }
}
