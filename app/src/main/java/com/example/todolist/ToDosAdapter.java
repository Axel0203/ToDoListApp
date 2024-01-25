package com.example.todolist;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToDosAdapter extends RecyclerView.Adapter<ToDosAdapter.ToDoViewHolder>{

    private OnItemClickListener onItemClickListener;
    private final ToDo[] toDos;

    public ToDosAdapter(ToDo[] toDos) {
        this.toDos = toDos;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mio_layout_item,parent,false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder,@SuppressLint("RecyclerView") int position) {
        holder.bind(toDos[position]);
        holder.itemView.setOnClickListener(view -> {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.toDos.length;
    }

    static class ToDoViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView date;
        private final TextView description;

        public ToDoViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.cardTitle);
            date = itemView.findViewById(R.id.cardDate);
            description = itemView.findViewById(R.id.cardDesc);
        }

        public void bind(ToDo todo){
            title.setText(todo.getTitle());
            date.setText(todo.getDateString("d MMMM"));
            description.setText(todo.getDescription());
        }
    }
}
