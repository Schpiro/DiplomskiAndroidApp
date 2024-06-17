package com.bbilandzi.diplomskiandroidapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.activity.MessengerActivity;
import com.bbilandzi.diplomskiandroidapp.adapter.UserAdapter;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.viewmodel.ContactViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserListFragment extends Fragment {

    private ContactViewModel contactsViewModel;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(this::onUserClicked);
        recyclerView.setAdapter(userAdapter);

        contactsViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        contactsViewModel.getFetchedUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                Log.d("UserListFragment", "Users fetched in fragment: " + users.size());
                updateUsers(users);
            } else {
                Log.d("UserListFragment", "No users fetched in fragment");
            }
        });

        return view;
    }

    private void updateUsers(List<UserDTO> users) {
        userAdapter.setUsers(users);
        userAdapter.notifyDataSetChanged();
    }

    private void onUserClicked(UserDTO user) {
        Intent intent = new Intent(getContext(), MessengerActivity.class);
        intent.putExtra("recipientId", user.getId());
        intent.putExtra("recipientType", "user");
        startActivity(intent);
    }
}

    /*
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
     */
