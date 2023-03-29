package com.example.roombasic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class WordsFragment extends Fragment {
    //WordDatabase wordDatabase;
    //TextView textView;
    //WordDao wordDao;
    //LiveData<List<Word>> allWordsLive;
    private WordViewModel wordViewModel;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter1, myAdapter2;
    private LiveData<List<Word>> filterWords; //過濾後的Word 用於 搜尋功能
    private static final String VIEW_TYPE_SHP = "view_type_shp";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";
    private List<Word> allWords;//支持滑動
    private boolean undoAction;//撤銷刪除
    private DividerItemDecoration dividerItemDecoration;//分隔線

    public WordsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true); //Fragment 本身Menu 不顯示
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) { //載入自設menu
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();//「搜尋」的引用
        searchView.setMaxWidth(1000); //設定寬度 防止擠到標題

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { //內容改變時
            @Override
            public boolean onQueryTextSubmit(String query) { //按下確定鍵(Submit) 呼叫
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { //內容改變時
                Log.d("mycology", "onQueryTextChange" + newText);
                String pattern = newText.trim();
                filterWords.removeObservers(getViewLifecycleOwner()); //避免發生重複觀察 將onViewCreated原有觀察移除
                filterWords = wordViewModel.findWordsWithPattern(pattern);//獲取 過濾後 的數據
                filterWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    //fragment 在切換的時候 View 被摧毀了，這個觀察(observe) 也會被消掉
                    //避免回來時 一直生成 observe
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = myAdapter1.getItemCount();
                        allWords = words;
                        //myAdapter1.setAllWords(words);
                        //myAdapter2.setAllWords(words);
                        if (temp != words.size()) { //長度(數量)相同不刷新視圖，是否查看中文在MyAdapter裡更新資料庫
                            //myAdapter1.notifyDataSetChanged();//告訴 RecyclerView 刷新全部視圖
                            //myAdapter2.notifyDataSetChanged();//告訴 RecyclerView 刷新全部視圖
                            myAdapter1.submitList(words);//差異化比對
                            myAdapter2.submitList(words);//差異化比對
                        }
                    }
                });
                return true;
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearData:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity()); //彈出對話框
                builder.setTitle(R.string.ClearData);
                builder.setPositiveButton(R.string.Certain, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //按下確定
                        wordViewModel.deleteAllWords();
                    }
                });
                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //什麼都不做
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.switchViewType:
                //儲存偏好設定值
                SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
                SharedPreferences.Editor editor = shp.edit();
                if (viewType) {//true 為卡片模式
                    recyclerView.setAdapter(myAdapter1);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, false);
                } else {//false 為一般模式
                    recyclerView.setAdapter(myAdapter2);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, true);
                }
                editor.apply();//數據存進去
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //onActivityCreated 被廢除，取而代之的是在 onViewCreated 創建
        //如果我們實在需要獲得 Activity 的 onCreate 事件通知，可以通過在 onAttach(Context) 中通過 LifecycleObserver 來獲取
        super.onViewCreated(view, savedInstanceState);

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        myAdapter1 = new MyAdapter(false, wordViewModel);
        myAdapter2 = new MyAdapter(true, wordViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));//一維列表(行 LinearLayoutManager)，二維列表(行、列 GridLayoutManager)
        //recyclerView.setAdapter(myAdapter1);

        recyclerView.setItemAnimator(new DefaultItemAnimator() { //簡單動畫
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) { //新增項目&動畫完後，更新序號
                super.onAnimationFinished(viewHolder);
                //節省資源 螢幕內刷新
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) { //避免有空的指針 邏輯要求
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition(); //取回第一個元素的位置
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition(); //取回最後一個元素的位置

                    for (int i = firstPosition; i <= lastPosition; i++) { //更新序號
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);

                        if (holder != null) { //避免有空的指針 邏輯要求
                            holder.textViewNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }
            }
        });

        SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
        //製作分隔線
        dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);

        if (viewType) {
            recyclerView.setAdapter(myAdapter2);
        } else {
            recyclerView.setAdapter(myAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        filterWords = wordViewModel.getAllWordsLive();
        /*
        wordDatabase = Room.databaseBuilder(this,WordDatabase.class,"word_database")
                .allowMainThreadQueries()
                .build();
        wordDao = wordDatabase.getWordDao();
        allWordsLive = wordDao.getAllWordsLive();
         */
        filterWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {//觀察
            //fragment 在切換的時候 View 被摧毀了，這個觀察(observe) 也會被消掉
            //避免回來時 一直生成 observe
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<Word> words) {//發生改變時
                int temp = myAdapter1.getItemCount();
                allWords = words;
                //myAdapter1.setAllWords(words);
                //myAdapter2.setAllWords(words);
                if (temp != words.size()) { //長度相同不刷新視圖，是否查看中文在MyAdapter裡更新資料庫
                    //myAdapter1.notifyItemChanged(0);//告訴 RecyclerView 刷新 0號位置
                    //myAdapter1.notifyDataSetChanged();//告訴 RecyclerView 刷新全部視圖
                    //myAdapter2.notifyDataSetChanged();//告訴 RecyclerView 刷新全部視圖

                    if (temp < words.size() && !undoAction) {//刪除、撤銷刪除時false，新增項目時true
                        recyclerView.smoothScrollBy(0, -200); //向上滑200像素
                    }
                    undoAction = false;
                    myAdapter1.submitList(words);//差異化比對
                    myAdapter2.submitList(words);//差異化比對
                }

                /*
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < words.size(); i++) {
                    Word word = words.get(i);
                    text.append(word.getId()).append(":").append(word.getWord()).append("=").append(word.getChineseMeaning()).append("\n");

                }
                textView.setText(text.toString());

                 */
            }
        });

        //可左右滑動
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //支持拖動
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //支持滑動
                Word wordToDelete = allWords.get(viewHolder.getAdapterPosition()); //獲取滑動對象
                wordViewModel.deleteWords(wordToDelete);//刪除對象

                //提供撤銷機會
                Snackbar.make(requireActivity().findViewById(R.id.wordsFragmentView), "刪除了一個單字", Snackbar.LENGTH_SHORT)
                        .setAction("撤銷", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                undoAction = true;
                                wordViewModel.insertWords(wordToDelete);//重新加回去
                            }
                        }).show();

            }

            //設計垃圾桶、位置、尺寸
            final Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_delete_forever_24);
            final Drawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin;
                if (icon != null) {
                    iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                    int iconLeft, iconRight, iconTop, iconBottom;
                    int backTop, backBottom, backLeft, backRight;
                    backTop = itemView.getTop();
                    backBottom = itemView.getBottom();
                    iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    iconBottom = iconTop + icon.getIntrinsicHeight();
                    if (dX > 0) {
                        backLeft = itemView.getLeft();
                        backRight = itemView.getRight() + (int) dX;
                        background.setBounds(backLeft, backTop, backRight, backBottom);
                        iconLeft = itemView.getLeft() + iconMargin;
                        iconRight = iconLeft + icon.getIntrinsicWidth();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    } else if (dX < 0) {
                        backRight = itemView.getRight();
                        backLeft = itemView.getRight() + (int) dX;
                        background.setBounds(backLeft, backTop, backRight, backBottom);
                        iconRight = itemView.getRight() - iconMargin;
                        iconLeft = iconRight - icon.getIntrinsicWidth();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    } else {
                        background.setBounds(0, 0, 0, 0);
                        icon.setBounds(0, 0, 0, 0);
                    }
                    background.draw(c);
                    icon.draw(c);
                }
            }

        }).attachToRecyclerView(recyclerView); //附加到recyclerView

        FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });
    }
}