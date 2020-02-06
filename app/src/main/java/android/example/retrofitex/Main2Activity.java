package android.example.retrofitex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.example.retrofitex.models.Task;
import android.example.retrofitex.adapter.TaskAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = Main2Activity.class.getName();
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setTitle("Tasks");


        Intent intent = getIntent();
        String message = intent.getStringExtra("onboard_id");
        Log.d(TAG, "onCreate: " + message);

        realm = Realm.getDefaultInstance();
        RealmResults<Task> tasks = realm.where(Task.class).findAll().sort("id", Sort.ASCENDING);
        final TaskAdapter adapter;
        adapter = new TaskAdapter(this, tasks);

        RecyclerView recyclerView = findViewById(R.id.task_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        Button button = findViewById(R.id.button_id);
        EditText editText = findViewById(R.id.add_task);
        button.setOnClickListener(v -> {
            String task = editText.getText().toString();
            if (!task.isEmpty()) {
                Log.d(TAG, "onClick: " + task);

            } else {
                Log.d(TAG, "onClick: Task is empty");

                realm.beginTransaction();

                Task task1 = new Task();
                task1.setId((String.valueOf(System.currentTimeMillis())));
                task1.setName(task);

                realm.copyToRealmOrUpdate(task1);

                realm.commitTransaction();

                adapter.notifyDataSetChanged();

                editText.setText("");
            }

        });
    }
}
