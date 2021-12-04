package com.example.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    static int SIGN_IN_CODE = 1;
    RelativeLayout activity;
    FirebaseListAdapter<com.example.wechat.Message> adapter;
    ImageButton button;

    FirebaseAuth auth;

    // Идентификатор канала
    private static String CHANNEL_ID = "RoboCode chanel";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(activity, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                allMessages();

            }else{
                Snackbar.make(activity, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = findViewById(R.id.activity_main);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textField = findViewById(R.id.field);
                if(textField.getText().toString().equals(""))
                    return;
                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        textField.getText().toString()
                        )
                );
                textField.setText("");
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        }
        else{
            Snackbar.make(activity, "Вы авторизованы", Snackbar.LENGTH_SHORT).show();
            allMessages();
        }
    }

    private void allMessages() {
        ListView listOfMessages = findViewById(R.id.listOfMessages);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.text, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                TextView mess_user, mess_time, mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_text = v.findViewById(R.id.message_text);
                mess_time = v.findViewById(R.id.message_time);

                mess_text.setText(model.getTextMassage());
                mess_user.setText(model.getUserName());
                mess_time.setText(DateFormat.format("HH:mm", model.getMessageTime()));
            }

        };

        listOfMessages.setAdapter(adapter);
    }
}

