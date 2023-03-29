package com.example.roombasic;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AddFragment extends Fragment {
    private Button buttonSubmit;
    private EditText editTextEnglish, editTextChinese;
    private WordViewModel wordViewModel;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        buttonSubmit = requireActivity().findViewById(R.id.buttonSubmit);
        editTextEnglish = requireActivity().findViewById(R.id.editTextTextEnglish);
        editTextChinese = requireActivity().findViewById(R.id.editTextTextChinese);
        buttonSubmit.setEnabled(false);

        //鍵盤自動跳出
        editTextEnglish.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextEnglish, 0);

        TextWatcher textWatcher = new TextWatcher() { //監聽 editTextEnglish、editTextChinese
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String english = editTextEnglish.getText().toString().trim();//trim()切掉前後的空格

                String chinese = editTextChinese.getText().toString().trim();//trim()切掉前後的空格

                buttonSubmit.setEnabled(!english.isEmpty() && !chinese.isEmpty());//editTextEnglish、editTextChinese 都有值時 true
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextEnglish.addTextChangedListener(textWatcher);
        editTextChinese.addTextChangedListener(textWatcher);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = editTextEnglish.getText().toString().trim();//trim()切掉前後的空格
                String chinese = editTextChinese.getText().toString().trim();//trim()切掉前後的空格
                Word word = new Word(english, chinese);//更新 實體類 Word
                wordViewModel.insertWords(word);//更新 數據庫

                //返回 WordsFragment
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_addFragment_to_wordsFragment);
                //navController.navigateUp();//返回上一個 Fragment
                //鍵盤自動收回
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }
}