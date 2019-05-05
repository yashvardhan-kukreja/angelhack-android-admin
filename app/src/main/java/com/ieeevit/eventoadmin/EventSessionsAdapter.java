package com.ieeevit.eventoadmin;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ieeevit.eventoadmin.Activities.QRActivity;
//import com.ieeevit.eventoadmin.Activities.QRScanner;
import com.ieeevit.eventoadmin.Classes.Session;


import java.util.List;

public class EventSessionsAdapter extends RecyclerView.Adapter<EventSessionsAdapter.EventSessionViewHolder>{

    List<Session> sessionList;
    Context context;
    private static final int REQUEST_CODE_QR_SCAN = 101;

    public EventSessionsAdapter(List<Session> sessions, Context context) {
        this.sessionList = sessions;
        this.context = context;
    }

    @NonNull
    @Override
    public EventSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return (new EventSessionViewHolder(rootView));
    }

    @Override
    public void onBindViewHolder(@NonNull EventSessionViewHolder holder, int position) {
        Session currentSession = sessionList.get(position);

        final String title = currentSession.getName();
        final String description = currentSession.getDescription();
        final String session_id = currentSession.getId();

        holder.sessionTitle.setText(title);
        holder.sessionDescription.setText(description);

        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QRActivity.class);
                intent.putExtra("session_id", session_id);
                intent.putExtra("session", true);
                intent.putExtra("title", title);
                intent.putExtra("desc", description);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public class EventSessionViewHolder extends RecyclerView.ViewHolder{

        TextView sessionTitle, sessionDescription;
        LinearLayout cardItem;
        int a=1;

        public EventSessionViewHolder(View itemView) {
            super(itemView);
            cardItem = itemView.findViewById(R.id.card_item);
            sessionTitle = itemView.findViewById(R.id.item_session_title);
            sessionDescription = itemView.findViewById(R.id.item_session_description);

        }

        }
    }






