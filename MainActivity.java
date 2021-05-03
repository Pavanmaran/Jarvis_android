 package com.example.projectvalley;


 import android.annotation.SuppressLint;
 import android.content.Context;
 import android.content.Intent;
 import android.media.AudioAttributes;
 import android.media.AudioManager;
 import android.media.MediaPlayer;
 import android.net.Uri;
 import android.os.Build;
 import android.os.Bundle;
 import android.speech.RecognizerIntent;
 import android.speech.tts.TextToSpeech;
 import android.util.Log;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.RadioButton;
 import android.widget.RadioGroup;
 import android.widget.Switch;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.appcompat.app.AppCompatActivity;

 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.ibm.cloud.sdk.core.security.IamAuthenticator;
 import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
 import com.ibm.watson.text_to_speech.v1.util.WaveUtils;

 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Locale;

//----------------------
//------------------------------


public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "Mn___UgUFPh3rT5r9CmZ6QuW1TzRXx9e3O4SUQHeO0WK";
    private static final String URL = "https://api.eu-gb.text-to-speech.watson.cloud.ibm.com";
    private static final int BUFFER_SIZE = 1024;
    //------------
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference newref;
    public RadioButton rd1 ;
    //public  RadioButton rd2;
    public RadioGroup rdg;

    public TextView textView;
    public String textview_data;
    public String text = "hii";
    private String text_speech;
    public String etext;
    public String value;
    public String rvalue;
    public String sensor_val;
    public String value1;
    public String value3;
    public String sensor2;
    public String sensor3;
    public Button mic;
    public EditText editText;
    private TextToSpeech TTS;
    public boolean switchstate;
    public  String rdtext;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //textView.setText("Hi, I am Sejal. How can i help you ?");
        textView = findViewById(R.id.textview);
        /*
        //rd1 =findViewById(R.id.rd1);
        //rd2 =findViewById(R.id.rd2);
        rdg  = findViewById(R.id.rdg);
        int selectid = rdg.getCheckedRadioButtonId();
        rd1 = findViewById(selectid);
        rdtext =rd1.getText().toString();
*/

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch1 = (Switch) findViewById(R.id.switch1);
        switchstate = switch1.isChecked();

        textview_data = textView.getText().toString();
        EditText editText = findViewById(R.id.editText);
        Button button = findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getsppechinput();

            }
        });


        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int textresult = TTS.setLanguage(Locale.getDefault());
                    TTS.setSpeechRate(0);
                    TTS.setPitch(1);
                    if (textresult == TextToSpeech.LANG_MISSING_DATA || textresult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }


                } else {
                    Log.e("TextToSpeech", "Initialzation failed");
                }
            }

        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("def_strings");
        newref = FirebaseDatabase.getInstance().getReference();

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //value = Objects.requireNonNull(snapshot.getValue()).toString() ;
                sensor_val = snapshot.child("sensors").child("sensor1").getValue().toString();
                sensor2 = snapshot.child("sensors").child("sensor2").getValue().toString();
                value = snapshot.child("strings").child("s1").getValue().toString();
                value1 = snapshot.child("strings").child("s2").getValue().toString();
                value3 = snapshot.child("strings").child("s3").getValue().toString();
                sensor3 = snapshot.child("sensors").child("sensor3").getValue().toString();
                if (value != null) {
                    rvalue = value;
                }
                //textView.setText(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Daya kuch to error he", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getsppechinput();

        // speak();

    }

    private void switchfunc() {
        if (!switchstate) {
            myRef.child(sensor3).setValue(0);
        } else {
            myRef.child(sensor3).setValue(1);
        }
    }


    private void speak() {
        TTS.setSpeechRate(1);
        TTS.speak(textview_data, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void getsppechinput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "your device not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text_speech = result.get(0);
                    //text_speech = text_speech.toString();
                    textView.setText(text_speech);
                    func();


                    run();

                   // speak();


                    //speak();


                    //editText.setText(text);
                    //textView.setText(text);


                }
        }
    }


    private void run() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //String text = editText.getText().toString();
                //String text = editText.getText().toString();
                if (textview_data.length() > 0) {
                    String voice = "en-GB_JamesV3Voice";
                    try {
                        createSoundFile(textview_data, voice);
                        playSoundFile(textview_data + voice);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }





    public void createSoundFile(String text, String voice) throws IOException {
         IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
        com.ibm.watson.text_to_speech.v1.TextToSpeech textToSpeech = new com.ibm.watson.text_to_speech.v1.TextToSpeech(authenticator);
        textToSpeech.setServiceUrl(URL);

        SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                .text(text)
                .accept("audio/mp3")
                .voice(voice)
                .build();

        InputStream inputStream = textToSpeech.synthesize(synthesizeOptions).execute().getResult();
        InputStream in = WaveUtils.reWriteWaveHeader(inputStream);

        String fileName = text + voice;
        FileOutputStream fos = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);

        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = in.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();

        in.close();
        inputStream.close();
    }

    public void playSoundFile(String fileName) throws IOException {
        File file = new File(getApplicationContext().getFilesDir(), fileName);
        Uri fileUri = Uri.parse(file.getPath());
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .build()
            );
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mediaPlayer.setDataSource(getApplicationContext(), fileUri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();
    }

    public void func(){

        //text = "value of moisture";

        if (text_speech.equals(rvalue)){
            //textView.setText(value);
            //textView.setText(sensor_val);
            newref.child("def_strings").child("Sensor4").setValue(10);

           // Toast. makeText(getApplicationContext(),"Strings Match! ",Toast. LENGTH_SHORT).show();
        }
        else{

            Toast. makeText(getApplicationContext(),"Samjhi Nahi :-)",Toast. LENGTH_SHORT).show();
        }
        if (text_speech.equals(value1)){
            textView.setText(sensor2);
            //textView.setText("Samajh gayi sir" );
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sensor2)));
            //Log.i("Video", "video Playing.....");

            //Toast. makeText(getApplicationContext(),"Strings Match! ",Toast. LENGTH_SHORT).show();
        }
        else{

            Toast. makeText(getApplicationContext(),"Samjhi Nahi :-)",Toast. LENGTH_SHORT).show();
        }
        if (text_speech.equals(value3)){
            //textView.setText(value);
            //textView.setText(sensor3);
            newref.child("def_strings").child("Sensor4").setValue(11);
            //switchfunc();


            //Toast. makeText(getApplicationContext(),"Strings Match! ",Toast. LENGTH_SHORT).show();
        }
        textview_data = textView.getText().toString();
    }
}
