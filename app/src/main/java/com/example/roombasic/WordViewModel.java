package com.example.roombasic;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

//ViewModel 管理介面(屏幕)數據 再螢幕旋轉、切換語言、避免數據丟失
//LiveData 數據監聽，當發生變化時，自動刷新介面
//extends AndroidViewModel
//Generate -> Constructor
public class WordViewModel extends AndroidViewModel {
    //private WordDao wordDao;
    private final WordRepository wordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    public LiveData<List<Word>> getAllWordsLive() {
        return wordRepository.getAllWordsLive();
    }

    public LiveData<List<Word>> findWordsWithPattern(String pattern) {
        return wordRepository.findWordsWithPattern(pattern);
    }

    void insertWords(Word... words) {
        wordRepository.insertWords(words);
    }

    void updateWords(Word... words) {
        wordRepository.updateWords(words);
    }

    void deleteWords(Word... words) {
        wordRepository.deleteWords(words);
    }

    void deleteAllWords() {
        wordRepository.deleteAllWords();
    }


}
