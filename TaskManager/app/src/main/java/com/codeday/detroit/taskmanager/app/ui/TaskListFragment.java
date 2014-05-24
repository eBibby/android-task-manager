package com.codeday.detroit.taskmanager.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeday.detroit.taskmanager.app.CDLog;
import com.codeday.detroit.taskmanager.app.GlobalContext;
import com.codeday.detroit.taskmanager.app.MainActivity;
import com.codeday.detroit.taskmanager.app.R;
import com.codeday.detroit.taskmanager.app.dao.DatabaseAccessor;
import com.codeday.detroit.taskmanager.app.domain.TaskList;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    public static String TAG = "TaskListFragment";

    private View rootView;

    private AlertDialog dialog;
    private ListView list;
    private List<TaskList> taskLists;

    private MainActivity.MenuInteractionListener menuInteractionListener;

    public static TaskListFragment getInstance() {
        TaskListFragment frag = new TaskListFragment();
        return frag;
    }

    public TaskListFragment() {
        taskLists = new ArrayList<TaskList>();
        menuInteractionListener = new MainActivity.MenuInteractionListener() {
            @Override
            public void onAddButtonPressed() {
                CDLog.debugLog(TAG, "Add Button Pressed!");
                showNewListDialog();
            }
        };
    }

    public MainActivity.MenuInteractionListener getMenuInteractionListener() {
        return menuInteractionListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        list = (ListView) rootView.findViewById(R.id.list);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new RetrieveListsTask().execute();
        createNewListDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void createNewListDialog() {
        AlertDialog.Builder builder;
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_list, null);

        final EditText listName = (EditText) layout.findViewById(R.id.name);
        final TextView cancelButton = (TextView) layout.findViewById(R.id.cancel);
        final TextView saveButton = (TextView) layout.findViewById(R.id.save);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(listName.getWindowToken(), 0);
                }
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = listName.getText().toString();

                if ( name != null && name.length() > 0 ) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(listName.getWindowToken(), 0);
                    }
                    dialog.dismiss();

                    TaskList taskList = new TaskList();
                    taskList.name = name;
                    taskList.numberOfTasks = 0;
                    taskList.numberOfCompletedTasks = 0;
                    taskList.isComplete = false;

                    new AddListToDatabaseTask().execute(new TaskList[] { taskList });

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.list_name_not_valid), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(listName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listName.setText("");
            }
        });
    }

    private void showNewListDialog() {
        if ( dialog != null ) {
            dialog.show();
        }
    }

    private class AddListToDatabaseTask extends AsyncTask<TaskList, Void, Boolean> {

        @Override
        protected Boolean doInBackground(TaskList... params) {
            if ( params.length > 0 )
                return new DatabaseAccessor().addList(params[0]);
            else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_list_database), Toast.LENGTH_SHORT).show();
            else
                new RetrieveListsTask().execute();
        }
    }

    private class RetrieveListsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            taskLists.clear();
            List<TaskList> result = new DatabaseAccessor().getAllLists();
            if ( result != null )
                taskLists.addAll(result);
            return taskLists != null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_retrieving_lists), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity().getApplicationContext(), "Number of lists: " + taskLists.size(), Toast.LENGTH_SHORT).show();
        }
    }
}
