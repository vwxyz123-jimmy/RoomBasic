package com.example.roombasic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
/*
    Button buttonInsert, buttonUpdate, buttonDelete, buttonClear;
    WordViewModel wordViewModel;
    RecyclerView recyclerView;
    Switch aSwitch;
    MyAdapter myAdapter1,myAdapter2;

 */
    //private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        recyclerView = findViewById(R.id.recyclerview);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        myAdapter1 = new MyAdapter(false,wordViewModel);
        myAdapter2 = new MyAdapter(true,wordViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//一維列表(行 LinearLayoutManager)，二維列表(行、列 GridLayoutManager)
        recyclerView.setAdapter(myAdapter1);

        aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//監聽器
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    recyclerView.setAdapter(myAdapter2);
                }else {
                    recyclerView.setAdapter(myAdapter1);
                }
            }
        });

        wordViewModel.getAllWordsLive().observe(this, new Observer<List<Word>>() {//觀察
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<Word> words) {//發生改變時
                int temp = myAdapter1.getItemCount();
                myAdapter1.setAllWords(words);
                myAdapter2.setAllWords(words);
                if (temp != words.size()){ //長度相同不刷新視圖，是否查看中文在MyAdapter裡更新資料庫
                    myAdapter1.notifyDataSetChanged();//告訴 RecyclerView 刷新視圖
                    myAdapter2.notifyDataSetChanged();
                }

            }
        });
        //textView = findViewById(R.id.textViewNumber);
        buttonInsert = findViewById(R.id.buttoninsert);
        buttonClear = findViewById(R.id.buttonclear);
        buttonUpdate = findViewById(R.id.buttonupdate);
        buttonDelete = findViewById(R.id.buttondelete);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] english = {
                        "Hello",
                        "World",
                        "Android",
                        "Google",
                        "Studio",
                        "Project",
                        "Database",
                        "Recycler",
                        "View",
                        "String",
                        "Value",
                        "Integer"
                };
                String[] chinese = {
                        "你好",
                        "世界",
                        "安卓系统",
                        "谷歌公司",
                        "工作室",
                        "项目",
                        "數據庫",
                        "回收站",
                        "視圖",
                        "字符串",
                        "價值",
                        "整數"
                };
                for (int i = 0; i < english.length; i++) {
                    wordViewModel.insertWords(new Word(english[i],chinese[i]));
                }

                //Word word1 = new Word("Hello", "你好");
                //Word word2 = new Word("World", "世界");
                //wordViewModel.insertWords(word1, word2);


            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordViewModel.deleteAllWords();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word("Hi", "你好喔!!");
                word.setId(20);
                wordViewModel.updateWords(word);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word word = new Word("Hi", "你好");
                word.setId(18);
                wordViewModel.deleteWords(word);
            }
        });

        */

        //等待處理
        //navController = Navigation.findNavController(findViewById(R.id.fragment)); //製作返回按鍵
        //NavigationUI.setupActionBarWithNavController(this,navController);

    }

}