package com.eatgo.eatgo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Home extends FragmentActivity {

    // REQUEST_CODE_MENU
    private static final int CODE_ADDFRIENDS = 104;
    private static final String[] EATERY_NAME = {"한식", "일식", "중식", "양식", "기타"};

    // DB 제어 관련
    private DBManager dbmgr;
    private SQLiteDatabase sdb;
    private SQLiteStatement stmt;
    private Cursor cursor;
    private String id;
    private String sql;

    // 홈 화면
    private TextView title;
    private LinearLayout eatgo_menubar;

    // 탭
    private int indexOfSelectedTab;
    private int indexOfClickedTab;

    private LinearLayout eatgoTab;
    private LinearLayout searchTab;
    private LinearLayout friendsTab;
    private LinearLayout mypageTab;
    private LinearLayout moreTab;

    // 탭 제어 관련
    private LinearLayout[] tabSelected = new LinearLayout[5];
    private boolean[] isTabSelected = {true, false, false, false, false};
    private Button[] tabButton = new Button[5];

    // 리스너 및 인플레이터
    private TabClickListener tabClickListener = new TabClickListener();
    private LayoutInflater inflater;

    // 하단바
    private Button eatgo;
    private Button search;
    private Button freinds;
    private Button mypage;
    private Button more;

    /* -------------------------------------------------------------------------------------------------------------------- */

    // 1. 먹자GO 탭
    private TextView area;
    private Button selectArea;
    private Button refresh;
    private Button filter;
    private Button addFriends;

    private TextView eatgoFriends;
    private String eatgoFriendsList;
    private ArrayList<String> addedFriendsList = new ArrayList<String>();

    private ListView eatGoListView;
    private EatGoAdaptor eatGoAdaptor;

    // 2. 검색 탭
    private EditText searchText;
    private Button searchBtn;

    // 3. 친구의 먹자GO 탭
    private ArrayList<String > friendsList = new ArrayList<String>();
    private ListView friendsListView;
    ArrayAdapter<String> friendsAdaptor;
    private AdapterView.OnItemClickListener friendsClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), FriendsPage.class);
            intent.putExtra("id", friendsList.get(position));
            startActivity(intent);
        }
    };

    // 4.나의 먹자GO 탭
    private MyEatery[] myEatery = new MyEatery[5];
    private Button[] scoreMinus = new Button[5];
    private Button[] scorePlus = new Button[5];
    private EditText[] scoreET = new EditText[5];
    private EditText kor, jap, chn, wes, etc;
    private CheckBox[] exceptCB = new CheckBox[5];
    private CheckBox kore, jape, chne, wese, etce;
    private Button myInit, myApply;

    // 5. 더 보기 탭
    private Button maker;

    /*
     * 먹자GO 어플리케이션 실행!!
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*
         * 0. 홈 화면
         */

        title = (TextView) findViewById(R.id.title);
        eatgo_menubar = (LinearLayout) findViewById(R.id.eatgo_menubar);

        tabSelected[0] = eatgoTab = (LinearLayout) findViewById(R.id.eatgoTab);
        tabSelected[1] = searchTab = (LinearLayout) findViewById(R.id.searchTab);
        tabSelected[2] = friendsTab = (LinearLayout) findViewById(R.id.friendsTab);
        tabSelected[3] = mypageTab = (LinearLayout) findViewById(R.id.mypageTab);
        tabSelected[4] = moreTab = (LinearLayout) findViewById(R.id.moreTab);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tab_eatgo, eatgoTab, true);
        inflater.inflate(R.layout.tab_search, searchTab, true);
        inflater.inflate(R.layout.tab_friends, friendsTab, true);
        inflater.inflate(R.layout.tab_mypage, mypageTab, true);
        inflater.inflate(R.layout.tab_more, moreTab, true);

        tabButton[0] = eatgo = (Button) findViewById(R.id.eatgo);
        tabButton[1] = search = (Button) findViewById(R.id.search);
        tabButton[2] = freinds = (Button) findViewById(R.id.friends);
        tabButton[3] = mypage = (Button) findViewById(R.id.mypage);
        tabButton[4] = more = (Button) findViewById(R.id.more);

        for(int i=0; i<5; i++)
            tabButton[i].setOnClickListener(tabClickListener);

        Intent receivedIntent = getIntent();
        id = receivedIntent.getStringExtra("id");
        Toast.makeText(getApplicationContext(), id + "님 환영합니다!", Toast.LENGTH_SHORT).show();

        /* -------------------------------------------------------------------------------------------------------------------- */


        /*
         * 1. 먹자GO 탭
         */

        area = (TextView) findViewById(R.id.area);
        selectArea = (Button) findViewById(R.id.selectArea);
        refresh = (Button) findViewById(R.id.refresh);
        filter = (Button) findViewById(R.id.filter);
        addFriends = (Button) findViewById(R.id.addFriends);

        // 1-1. 지역 선택 버튼
        selectArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "업데이트 예정입니다! ^_^♡", Toast.LENGTH_SHORT).show();
            }
        });

        // 1-2. 새로고침 버튼

        eatgoFriends = (TextView) findViewById(R.id.eatgoFriends);
        eatGoAdaptor = new EatGoAdaptor();
        eatGoListView = (ListView) findViewById(R.id.eatGoList);
        eatGoListView.setAdapter(eatGoAdaptor);

        addedFriendsList.add(id);
        refresh();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        // 1-3. 필터 설정 버튼
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "필터 설정 버튼입니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 1-4. 친구 추가 버튼
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddFriends.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, CODE_ADDFRIENDS);
            }
        });

        /* -------------------------------------------------------------------------------------------------------------------- */



        /*
         * 2. 검색 탭
         */

        /* -------------------------------------------------------------------------------------------------------------------- */

        searchText = (EditText) findViewById(R.id.searchText);
        searchBtn = (Button) findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "업데이트 예정입니다! ^_^♡", Toast.LENGTH_SHORT).show();
                searchText.setText("");
            }
        });


        /*
         * 3. 친구의 먹자GO 탭
         */

        friendsListView = (ListView) findViewById(R.id.friendsListView);
        friendsListView.setOnItemClickListener(friendsClickListener);


        /* -------------------------------------------------------------------------------------------------------------------- */




        /*
         * 4. 나의 먹자GO 탭
         */

        scoreMinus[0] = (Button) findViewById(R.id.korMinus);
        scoreMinus[1] = (Button) findViewById(R.id.japMinus);
        scoreMinus[2] = (Button) findViewById(R.id.chnMinus);
        scoreMinus[3] = (Button) findViewById(R.id.wesMinus);
        scoreMinus[4] = (Button) findViewById(R.id.etcMinus);

        scorePlus[0] = (Button) findViewById(R.id.korPlus);
        scorePlus[1] = (Button) findViewById(R.id.japPlus);
        scorePlus[2] = (Button) findViewById(R.id.chnPlus);
        scorePlus[3] = (Button) findViewById(R.id.wesPlus);
        scorePlus[4] = (Button) findViewById(R.id.etcPlus);

        scoreET[0] = kor = (EditText) findViewById(R.id.kor);
        scoreET[1] = jap = (EditText) findViewById(R.id.jap);
        scoreET[2] = chn = (EditText) findViewById(R.id.chn);
        scoreET[3] = wes = (EditText) findViewById(R.id.wes);
        scoreET[4] = etc = (EditText) findViewById(R.id.etc);

        exceptCB[0] = kore = (CheckBox) findViewById(R.id.kore);
        exceptCB[1] = jape = (CheckBox) findViewById(R.id.jape);
        exceptCB[2] = chne = (CheckBox) findViewById(R.id.chne);
        exceptCB[3] = wese = (CheckBox) findViewById(R.id.wese);
        exceptCB[4] = etce = (CheckBox) findViewById(R.id.etce);

        for(int i=0; i<5; i++)
            myEatery[i] = new MyEatery(getApplicationContext(), scoreMinus[i], scorePlus[i], scoreET[i], exceptCB[i]);

        myInit = (Button) findViewById(R.id.myInit);
        myApply = (Button) findViewById(R.id.myApply);

        myInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<5; i++) {
                    scoreET[i].setText("0");
                    exceptCB[i].setChecked(false);
                }
            }
        });

        myApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbmgr = new DBManager(getApplicationContext());
                    sdb = dbmgr.getWritableDatabase();
                    stmt = sdb.compileStatement("UPDATE eatgo SET kor=?, jap=?, chn=?, wes=?, etc=?, " +
                            "kore=?, jape=?, chne=?, wese=?, etce=? WHERE id='" + id + "';");

                    for(int i=0; i<5; i++ ) {
                        stmt.bindLong(1+i, Integer.parseInt(scoreET[i].getText().toString()));
                        stmt.bindLong(6+i, exceptCB[i].isChecked() ? 1 : 0);
                    }
                    stmt.executeUpdateDelete();
                    Toast.makeText(getApplicationContext(), "나의 먹자GO가 업데이트 되었습니다!", Toast.LENGTH_SHORT).show();

                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    cursor.close();
                    dbmgr.close();
                }
            }
        });


        /* -------------------------------------------------------------------------------------------------------------------- */




        /*
         * 5. 더 보기 탭
         */

        Button btnstaff = (Button) findViewById(R.id.buttonStaff);
        btnstaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Home.this);
                dlg.setTitle("제작진");
                dlg.setMessage("아태물류학과 민찬영, 소비자학과 유상우");
                dlg.setPositiveButton("확인", null);
                dlg.show();
            }
        });

        /* -------------------------------------------------------------------------------------------------------------------- */

    }

     /*
     * 6. Result가 있는 finish
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 인텐트 통째로 매개변수에 들어감

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case CODE_ADDFRIENDS:
                if (resultCode == RESULT_OK) {
                    addedFriendsList = data.getStringArrayListExtra("addedFriendsList"); // 매개변수로 받은 인텐트로 부터 확장된 데이터를 검색합니다. 여기서는 검색을 원하는 문자는"Array<string> 객체 addedFriendsList"
                    addedFriendsList.add(id); // 아이디를 추가합니다
                }
                break;
        }

    }

    /* -------------------------------------------------------------------------------------------------------------------- */


    /*
     * 7. 새로고침 메소드
     */

    protected void refresh() {

        try {
            dbmgr = new DBManager(getApplicationContext());
            sdb = dbmgr.getReadableDatabase();

            sql = "SELECT * FROM eatgo WHERE "; // String 객체 sql에다가 eatgo 테이블의 모든 열을 조건 없이 넣습니다.
            eatgoFriendsList = ""; // String 객체 eatgoFriendsList를 비워줍니다.

            for(int i=0; i<addedFriendsList.size(); i++) { //추가된 친구 수 만큼 배열을 돌려줍니다
                sql += "id='" + addedFriendsList.get(i) + "'";
                eatgoFriendsList += addedFriendsList.get(i);
                if(i < addedFriendsList.size()-1) {
                    sql += " OR ";
                    eatgoFriendsList += ", "; // eatgoFriendsList에 쉼표를 추가합니다.
                }
                else sql += ";"; //
            }

            eatgoFriends.setText(eatgoFriendsList); // eatgoFriends에 잘 긁어모은 글자(id와 숨표 등)들을 세팅해줍니다.

            cursor = sdb.rawQuery(sql, null); //

            FriendData friendData; //  FriendData 객체 선언
            ArrayList<FriendData> addedFriendsData = new ArrayList<FriendData>(); // 새로운 ArrayList 선언

            while(cursor.moveToNext()) {
                friendData = new FriendData();
                friendData.setId(cursor.getString(0));
                friendData.setKor(cursor.getInt(2));
                friendData.setJap(cursor.getInt(3));
                friendData.setChn(cursor.getInt(4));
                friendData.setWes(cursor.getInt(5));
                friendData.setEtc(cursor.getInt(6));
                friendData.setKore(cursor.getInt(7));
                friendData.setJape(cursor.getInt(8));
                friendData.setChne(cursor.getInt(9));
                friendData.setWese(cursor.getInt(10));
                friendData.setEtce(cursor.getInt(11));
                addedFriendsData.add(friendData);
            } // 움직일 수 있을 때 까지 0,2,3,4,5,6,7,8,9,10,11 열에 해당하는 데이터들을 쭉 세팅해줍니다.

            ArrayList<Eatery> eateryList = new ArrayList<Eatery>();

            for(int i=0; i<EATERY_NAME.length; i++)
                eateryList.add(new Eatery(EATERY_NAME[i])); // EATERY_NAME 개수 만큼 배열리스트 eateryList에 추가해줌.

            Eatery eateryKor = eateryList.get(0);
            Eatery eateryJap = eateryList.get(1);
            Eatery eateryChn = eateryList.get(2);
            Eatery eateryWes = eateryList.get(3);
            Eatery eateryEtc = eateryList.get(4);

            for(int i=0; i<addedFriendsData.size(); i++) { // 친구데이터 사이즈만큼 반복
                FriendData now = addedFriendsData.get(i); // friendData 객체 now를 새로 선언해 한 번 반복할 때 마다 프렌즈데이터(i)값 얻어서 아래 문장들 반복

                eateryKor.setTot(now.getKor());
                eateryJap.setTot(now.getJap());
                eateryChn.setTot(now.getChn());
                eateryWes.setTot(now.getWes());
                eateryEtc.setTot(now.getEtc()); // 한 번 세팅

                if(now.getKore() != 0) {
                    eateryKor.setNumExp(now.getKore());
                    eateryKor.setWhoExp(now.getId());
                }
                if(now.getJape() != 0) {
                    eateryJap.setNumExp(now.getJape());
                    eateryJap.setWhoExp(now.getId());
                }
                if(now.getChne() != 0) {
                    eateryChn.setNumExp(now.getChne());
                    eateryChn.setWhoExp(now.getId());
                }
                if(now.getWese() != 0) {
                    eateryWes.setNumExp(now.getWese());
                    eateryWes.setWhoExp(now.getId());
                }
                if(now.getEtce() != 0) {
                    eateryEtc.setNumExp(now.getEtce());
                    eateryEtc.setWhoExp(now.getId());
                } // 체크박스에 제외 체크했을 때 제외자 수와 제외자 이름을 설정해주는 코드
            }

            for(int i=0; i<EATERY_NAME.length; i++)
                eateryList.get(i).setAvg(addedFriendsData.size()); //평균을 구해줍니다.

            Collections.sort(eateryList, new Comparator<Eatery>() {
                @Override
                public int compare(Eatery o1, Eatery o2) {
                    if( (o1.getNumExp() > o2.getNumExp()) || ( (o1.getNumExp() == o2.getNumExp()) && (o1.getAvg() < o2.getAvg()) ) ) return 1;
                    else if( (o1.getNumExp() < o2.getNumExp()) || ( (o1.getNumExp() == o2.getNumExp()) && (o1.getAvg() > o2.getAvg()) ) ) return -1;
                    else return 0; // Eatery 2개를 비교해서 제외, 평균 순으로 정렬시켜주는 메소드
                }
            });

            eatGoAdaptor.clearEatery(); // eatery 리스트를 한 번 비워주고

            for(int i=0; i<eateryList.size(); i++) //
                eatGoAdaptor.addEatery(eateryList.get(i));

            eatGoAdaptor.setRank(0);
            eatGoAdaptor.notifyDataSetChanged();

        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            dbmgr.close();
        }

    }

    /* -------------------------------------------------------------------------------------------------------------------- */



     /*
     * 8. 탭 전환 하단바 리스너
     */

    private class TabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            for(int i=0; i<5; i++) {
                if(v == tabButton[i]) {
                    indexOfClickedTab = i;
                    break;
                }
            }

            if(indexOfSelectedTab != indexOfClickedTab) {
                tabSelected[indexOfClickedTab].setVisibility(View.VISIBLE);
                tabSelected[indexOfSelectedTab].setVisibility(View.GONE);
                tabButton[indexOfClickedTab].setBackgroundColor(Color.parseColor("#CC0000"));
                tabButton[indexOfSelectedTab].setBackgroundColor(Color.parseColor("#575757"));

                switch(indexOfClickedTab) {

                    case 2: // 친구의 먹자GO 탭이 선택된 경우
                        try {
                            dbmgr = new DBManager(getApplicationContext());
                            sdb = dbmgr.getReadableDatabase();
                            cursor = sdb.rawQuery("SELECT * FROM eatgo WHERE id NOT IN ('" + id + "');", null);

                            String friendsID;
                            friendsList.clear();

                            while(cursor.moveToNext()) {
                                friendsID = cursor.getString(0);
                                friendsList.add(friendsID);
                            }

                            friendsAdaptor = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, friendsList);
                            friendsListView.setAdapter(friendsAdaptor);

                        } catch (SQLiteException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        } finally {
                            cursor.close();
                            dbmgr.close();
                        }
                        break;

                    case 3: // 나의 먹자GO 탭이 선택된 경우
                        try {
                            dbmgr = new DBManager(getApplicationContext());
                            sdb = dbmgr.getWritableDatabase();
                            cursor = sdb.rawQuery("SELECT * FROM eatgo WHERE id='" + id + "';", null);
                            cursor.moveToNext();

                            for(int i=0; i<5; i++ ) {
                                scoreET[i].setText(cursor.getString(i+2));
                                if(cursor.getInt(i+7) == 1) exceptCB[i].setChecked(true);
                                else exceptCB[i].setChecked(false);
                            }

                        } catch (SQLiteException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        } finally {
                            cursor.close();
                            dbmgr.close();
                        }
                        break;
                } // 딱히 받아올 값이 없는 검색, 더보기 탭에는 필요 없어서 안 만듬. (먹자고 탭에서는 새로고침할 때 데이터 받아올 때 이런 내용이 들어감)

                indexOfSelectedTab = indexOfClickedTab;
            }

        }

    }

    /* -------------------------------------------------------------------------------------------------------------------- */

}