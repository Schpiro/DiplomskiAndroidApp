package com.bbilandzi.diplomskiandroidapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.utils.DateTimeUtil;
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
    private TextView recipientName;
    private boolean isGroupChat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);


        messagesContainer = findViewById(R.id.messagesContainer);
        messageInput = findViewById(R.id.messageInput);
        scrollView = findViewById(R.id.scrollView);
        Button sendButton = findViewById(R.id.sendButton);
        recipientName = findViewById(R.id.messenger_title_user_name);

        scrollView.fullScroll(View.FOCUS_DOWN);


        recipientId = getIntent().getLongExtra("recipientId", 1);

        if (getIntent().getStringExtra("recipientType").equals("user")) {
            recipientName.setText(getIntent().getStringExtra("recipientUsername"));
            messageViewModel.getConversationWithUser(recipientId);
        } else {
            recipientName.setText(getIntent().getStringExtra("recipientGroupName"));
            isGroupChat = true;
            messageViewModel.getConversationWithGroup(recipientId);
        }

        messageViewModel.setCurrentChatDetails(recipientId, isGroupChat);
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
            View messageView;
            if (message.getCreatorId().equals(AuthUtils.getUserId(this))) {
                messageView = getLayoutInflater().inflate(R.layout.item_message_user, messagesContainer, false);
            } else {
                messageView = getLayoutInflater().inflate(R.layout.item_message, messagesContainer, false);
            }

            TextView messageText = messageView.findViewById(R.id.messageTextView);
            messageText.setText(message.getMessageBody());

            TextView userIdText = messageView.findViewById(R.id.userIdTextView);
            userIdText.setText(message.getCreator());

            TextView timeText = messageView.findViewById(R.id.timeTextView);
            // Assuming message.getTime() returns the time in a format you can directly display
            timeText.setText(DateTimeUtil.getIsoIntoHours(message.getCreationDate()));

            messagesContainer.addView(messageView);
        }

        // Scroll to the bottom of the ScrollView after adding messages
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    public void sendMessage(View view) {
        String messageBody = messageInput.getText().toString().trim();

        if (!messageBody.isEmpty()) {
            MessageSend messageSend = MessageSend.builder()
                    .creatorId(AuthUtils.getUserId(this))
                    .messageBody(messageBody)
                    .build();
            if (isGroupChat) {
                messageSend.setRecipientGroupId(recipientId);
            } else {
                messageSend.setRecipientId(recipientId);
            }
            messageViewModel.sendMessage(messageSend, isGroupChat);
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
