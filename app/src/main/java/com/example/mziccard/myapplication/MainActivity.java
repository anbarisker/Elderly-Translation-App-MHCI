package com.example.mziccard.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // todo API_KEY should not be stored in plain sight
    private static final String API_KEY = "AIzaSyCFqLS9OoSgJR1KwJ3KNTQEblFDgU6gNB8";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String voiceInput="";
    private TextView textView;
    final Handler textViewHandler = new Handler();
    private String voiceInputted = "";
    private  Button btnContinue;
    private String languageValue = "zh";
    private Switch sChinese,sMalay,sTamil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView btnSpeak = (TextView) findViewById(R.id.btnSpeak);
        textView = (TextView) findViewById(R.id.text_view);

        sChinese = (Switch) findViewById(R.id.switchChinese);
        sMalay = (Switch) findViewById(R.id.switchMalay);
        sTamil = (Switch) findViewById(R.id.switchTamil);

        sChinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    sMalay.setChecked(false);
                    sTamil.setChecked(false);
                }
            }
        });

        sMalay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    sChinese.setChecked(false);
                    sTamil.setChecked(false);
                }
            }
        });

        sTamil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    sMalay.setChecked(false);
                    sChinese.setChecked(false);
                }
            }
        });


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(sMalay.isChecked())
                {
                    languageValue = "ms";
                }
                else if(sTamil.isChecked())
                {
                    languageValue = "ta";
                }
                else if(!sTamil.isChecked() && !sChinese.isChecked() && !sMalay.isChecked())
                {
                    languageValue = "zh";
                }

                askSpeechInput();

            }
        });

        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blah();
            }
        });




    }
    private void blah()
    {
        Log.i("Executing function", "Blah");
        if(voiceInput != "")
        {

            Log.i("Voice Inputted = ", voiceInput);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    TranslateOptions options = TranslateOptions.newBuilder()
                            .setApiKey(API_KEY)
                            .build();
                    Translate translate = options.getService();

                    final Translation translation =
                            translate.translate(voiceInput,
                                    Translate.TranslateOption.targetLanguage("en"));
                    textViewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (textView != null) {
                                textView.setText(translation.getTranslatedText());
                            }
                        }
                    });
                    return null;
                }
            }.execute();

        }


    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageValue);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    voiceInput = result.get(0);
                    Log.i("Text spoke", voiceInput);
                }
                break;
            }
        }
    }
}
