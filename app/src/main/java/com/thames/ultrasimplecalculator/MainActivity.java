package com.thames.ultrasimplecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.mariuszgromada.math.mxparser.*;

public class MainActivity extends AppCompatActivity {

    private EditText enterValue;
    private TextView resultToWords;
    public EnglishNumberToWords englishNumberToWords = new EnglishNumberToWords();
    public VietnameseNumberToWords vietnameseNumberToWords = new VietnameseNumberToWords();

    public int language = 0;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ultrasimple Calculator");
        }
        toolbar.inflateMenu(R.menu.setting);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        enterValue = findViewById(R.id.enterEquation);
        enterValue.setShowSoftInputOnFocus(false);

        resultToWords = findViewById(R.id.resultToWords);
        resultToWords.setShowSoftInputOnFocus(false);

        enterValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(R.string.display).equals(enterValue.getText().toString()) ||
                        getString(R.string.display2).equals(enterValue.getText().toString())){
                    enterValue.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.setting_author:
                startAboutUs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAboutUs() {
        Intent intent = new Intent(this, AboutUs.class);
        startActivity(intent);
    }


    private void updateText (String stringToAdd){
        String oldString = enterValue.getText().toString();
        int cursorPosition = enterValue.getSelectionStart();
        String leftString = oldString.substring(0,cursorPosition);
        String rightString = oldString.substring(cursorPosition);

        if(getString(R.string.display).equals(enterValue.getText().toString()) ||
                getString(R.string.display2).equals(enterValue.getText().toString()))
        {
            enterValue.setText(stringToAdd);
            enterValue.addTextChangedListener(new NumberTextWatcherForThousand(enterValue));
            enterValue.setSelection(cursorPosition + 1);
        } else {
            enterValue.setText(String.format("%s%s%s",leftString,stringToAdd,rightString));
            enterValue.addTextChangedListener(new NumberTextWatcherForThousand(enterValue));
            enterValue.setSelection(cursorPosition + 1);
        }
    }

    public void setEnglish (View view){
        language = 0;
        enterValue.setText(R.string.display);
    }

    public void setVietnamese (View view){
        language = 1;
        enterValue.setText(R.string.display2);
    }

    public void equalsBTN(View view) {
        String userExpression = enterValue.getText().toString();
        userExpression = userExpression.replaceAll("x","*");
        userExpression = userExpression.replaceAll("÷","/");

        Expression expression = new Expression(userExpression);

        String result = String.valueOf(expression.calculate());
        enterValue.setText(result);
        int resultLength = result.length();
        enterValue.setSelection(resultLength);

        int resultNumber;
        resultNumber = (int) expression.calculate();
        if(language == 0) {
        resultToWords.setText("In words: " + englishNumberToWords.convert(resultNumber));}
        else if (language == 1) {
            resultToWords.setText("Bằng chữ: " + vietnameseNumberToWords.convert(resultNumber));
        }
    }

    public void zeroBTN(View view){
        updateText("0");
    }

    public void clearBTN(View view) {
        enterValue.setText("");
        resultToWords.setText("");
    }

    public void backspaceBTN(View view) {
        int cursorPosition = enterValue.getSelectionStart();
        int textLength = enterValue.getText().length();

             if (cursorPosition != 0 && textLength != 0){
                 SpannableStringBuilder selection = (SpannableStringBuilder) enterValue.getText();
                 selection.replace(cursorPosition - 1, cursorPosition,"");
                 enterValue.setText(selection);
                 enterValue.setSelection(cursorPosition - 1);
             }
    }

    public void parenthesesBTN(View view) {
        int cursorPosition = enterValue.getSelectionStart();
        int openParentheses = 0;
        int closedParentheses = 0;
        int textLength = enterValue.getText().length();

        for (int i = 0; i < cursorPosition; i++) {
            if(enterValue.getText().toString().substring(i,i+1).equals("(")){
                openParentheses += 1;
            }
            if(enterValue.getText().toString().substring(i,i+1).equals(")")){
                closedParentheses += 1;
            }
        }

        if (openParentheses == closedParentheses ||
                enterValue.getText().toString().substring(textLength-1,textLength).equals(")")){
            updateText("(");
        } else if (closedParentheses < openParentheses &&
                !enterValue.getText().toString().substring(textLength-1,textLength).equals(")")){
            updateText(")");
        }
        enterValue.setSelection(cursorPosition + 1);
    }

    public void plusMinusBTN(View view) {
        updateText("-");
    }

    public void exponentBTN(View view) {
        updateText("^");
    }

    public void divideBTN(View view) {
        updateText("÷");
    }

    public void sevenBTN(View view) {
        updateText("7");
    }

    public void eightBTN(View view) {
        updateText("8");
    }

    public void nineBTN(View view) {
        updateText("9");
    }

    public void multiplyBTN(View view) {
        updateText("x");
    }

    public void fourBTN(View view) {
        updateText("4");
    }

    public void fiveBTN(View view) {
        updateText("5");
    }

    public void sixBTN(View view) {
        updateText("6");
    }

    public void subtractBTN(View view) {
        updateText("-");
    }

    public void oneBTN(View view) {
        updateText("1");
    }

    public void twoBTN(View view) {
        updateText("2");
    }

    public void threeBTN(View view) {
        updateText("3");
    }

    public void addBTN(View view) {
        updateText("+");
    }

    public void pointBTN(View view) {
        updateText(".");
    }

}