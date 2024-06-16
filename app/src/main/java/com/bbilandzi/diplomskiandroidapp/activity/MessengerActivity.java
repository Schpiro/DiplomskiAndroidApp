package com.bbilandzi.diplomskiandroidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.MessageDTO;
import com.bbilandzi.diplomskiandroidapp.model.MessageSend;
import com.bbilandzi.diplomskiandroidapp.viewmodel.MessageViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MessengerActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private MessageViewModel messageViewModel;
    private EditText messageInput;
    private ScrollView scrollView;
    private Long recipientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        recipientId = getIntent().getLongExtra("userId", 0);

        messagesContainer = findViewById(R.id.messagesContainer);
        messageInput = findViewById(R.id.messageInput);
        scrollView = findViewById(R.id.scrollView);
        Button sendButton = findViewById(R.id.sendButton);
        messageViewModel.getConversationWithUser(recipientId);

        messageViewModel.getFetchedMessages().observe(this, this::displayMessages);

        sendButton.setOnClickListener(this::sendMessage);

        messageInput.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                sendMessage(v);
                return true;
            }
            return false;
        });
    }

    private void displayMessages(List<MessageDTO> messages) {
        messagesContainer.removeAllViews();

        for (MessageDTO message : messages) {
            TextView textView = new TextView(this);
            textView.setText(message.getMessageBody());
            // Add other properties like layout params, margins, etc.

            // Add the TextView to the usersContainer
            messagesContainer.addView(textView);
        }
    }

    public void sendMessage(View view) {
        String messageBody = messageInput.getText().toString().trim();

        if (!messageBody.isEmpty()) {
            MessageSend messageSend = MessageSend.builder()
                    .recipientId(recipientId)
                    .messageBody(messageBody)
                    .build();
            messageViewModel.sendMessage(messageSend);
            View messageView = getLayoutInflater().inflate(R.layout.message_item, messagesContainer, false);
            TextView messageText = messageView.findViewById(R.id.messageTextView);
            messageText.setText(messageBody);
            messagesContainer.addView(messageView);
            messageInput.setText("");
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        }
    }

    public void navigateToContactsActivity(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }
}
