package com.example.securestorage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securestorage.R;
import com.example.securestorage.database.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<Note> notes;

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() { return notes.size(); }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle, tvContent, tvDate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle   = itemView.findViewById(R.id.tvNoteTitle);
            tvContent = itemView.findViewById(R.id.tvNoteContent);
            tvDate    = itemView.findViewById(R.id.tvNoteDate);
        }

        public void bind(Note note) {
            tvTitle.setText(note.getTitle());
            tvContent.setText(note.getContent());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(note.getCreatedAt())));
        }
    }
}