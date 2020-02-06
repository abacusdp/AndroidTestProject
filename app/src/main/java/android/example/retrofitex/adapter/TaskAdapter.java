package android.example.retrofitex.adapter;

import android.example.retrofitex.Main2Activity;
import android.example.retrofitex.R;
import android.example.retrofitex.models.Task;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private static final String TAG = TaskAdapter.class.getName();

    private Main2Activity activity;
    private List<Task> list;
    Realm realm;

    public TaskAdapter(Main2Activity activity, List<Task> list){
        this.activity = activity;
        this.list = list;
        realm = Realm.getDefaultInstance();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task task = list.get(position);
        holder.taskName.setText(task.getName());
        holder.isTaskDone.setChecked(task.getDone());
        holder.deleteTask.setOnClickListener(v -> {
            realm.beginTransaction();
            list.get(position).deleteFromRealm();
            realm.commitTransaction();

            notifyDataSetChanged();
        });
        holder.isTaskDone.setOnClickListener(v -> {
            realm.beginTransaction();

            list.get(position).setDone(holder.isTaskDone.isChecked());
            notifyDataSetChanged();

            realm.commitTransaction();


        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView taskName;
        CheckBox isTaskDone;
        ImageView deleteTask;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_item_name);
            isTaskDone = itemView.findViewById(R.id.task_item_done);
            deleteTask = itemView.findViewById(R.id.delete_item);
        }
    }
}
