package com.example.roombasic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

//適配器
//主UI設計 的RecyclerView 的管理器
//ViewHolder  Generate -> Constructor 管理cell_number.xml、cell_card
// <MyAdapter.MyViewHolder> 為自定義ViewHolder
public class MyAdapter extends ListAdapter<Word, MyAdapter.MyViewHolder> {

    // 用於 extends RecyclerView.Adapter
    //List<Word> allWords = new ArrayList<>(); //ListAdapter 已有 List<word> 可不宣告

    private final boolean useCardView; // Generate -> Constructor
    private final WordViewModel wordViewModel;

    public MyAdapter(boolean useCardView, WordViewModel wordViewModel) { //wordViewModel更新數據庫
        super(new DiffUtil.ItemCallback<Word>() { //兩個列表的差別，差異化在後台處理
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) { //元素是否相同
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) { //內容是否相同
                return (oldItem.getWord().equals(newItem.getWord())
                        && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                        && oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });
        this.useCardView = useCardView;
        this.wordViewModel = wordViewModel;
    }
/*
    public void setAllWords(List<Word> allWords) {//用於 extends RecyclerView.Adapter
        this.allWords = allWords;
    } //ListAdapter 已有 List<word> 可不宣告
 */

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //創建時設定
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView) {
            itemView = layoutInflater.inflate(R.layout.cell_card_2, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal_2, parent, false);
        }
        final MyViewHolder holder = new MyViewHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://cdict.net/?q=" + holder.textViewEnglish.getText()); //網址
                Intent intent = new Intent(Intent.ACTION_VIEW);//瀏覽網站模式
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);//啟動網站
            }
        });

        holder.aSwitchChineseInvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Word word = (Word) holder.itemView.getTag(R.id.word_for_view_holder);//與 onBindViewHolder
                //更新 數據庫
                if (isChecked) {
                    holder.textViewChinese.setVisibility(View.GONE);
                    word.setChineseInvisible(true); //更新 實體類 Word
                } else {
                    holder.textViewChinese.setVisibility(View.VISIBLE);
                    word.setChineseInvisible(false); //更新 實體類 Word
                }
                wordViewModel.updateWords(word);//更新 數據庫
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {//數據綁定
        // 滾動時每一個Viewholder 上方推出螢幕後，下方新的進入螢幕時 就呼叫
        //Word word = allWords.get(position);//用於 extends RecyclerView.Adapter
        Word word = getItem(position);//用於ListAdapter
        holder.itemView.setTag(R.id.word_for_view_holder, word);
        holder.textViewNumber.setText(String.valueOf(position + 1));//編號從1開始
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getChineseMeaning());

        if (word.isChineseInvisible()) { //初始狀態設定
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitchChineseInvisible.setChecked(true);
        } else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseInvisible.setChecked(false);
        }

    }
/*
    @Override
    public int getItemCount() {//返回數量 //用於 extends RecyclerView.Adapter
        return allWords.size();
    }

 */

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        //出現在螢幕上 設序號 (雙重保險) 與 recyclerView.setItemAnimator 功能差不多
        super.onViewAttachedToWindow(holder);
        holder.textViewNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }

    //static 防止內存洩漏
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber, textViewEnglish, textViewChinese;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch aSwitchChineseInvisible;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            aSwitchChineseInvisible = itemView.findViewById(R.id.switchChineseInvisible);
        }
    }
}
