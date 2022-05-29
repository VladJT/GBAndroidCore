package jt.projects.androidcore.notes;

import static jt.projects.androidcore.notes.NotesConstants.*;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jt.projects.androidcore.R;
import jt.projects.androidcore.common.ConfigInfo;


public class NotesListFragment extends Fragment {
    private static final String CURRENT_NOTE = "CurrentNote";
    private int currentPosition = 0;// Текущая позиция
    private ListView notesListView;
    private ArrayList<String> notesList;
    private ArrayAdapter<String> notesListAdapter;
    private MaterialButton buttonAddNote;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNoteInfoChangeListener();
    }

    private void setNoteInfoChangeListener() {
        getParentFragmentManager().setFragmentResultListener(FRAGMENT_RESULT_NOTES_DATA, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                int index = bundle.getInt(EDITED_NOTE_INDEX);
//                    if (index != -1) {
//                        notesList.set(index, NotesBaseActivity.getNotesData().getNote(index).getTopic());
//                    } else {
//                        notesList.add(NotesBaseActivity.getNotesData().getNote(index).getTopic());
//                    }
//                    notesListAdapter.notifyDataSetChanged();
                // TODO пока так, в будущем надо оптимизировать и переделать на RecyclerView
                initNotesList();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle("Список заметок");
        }

        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_NOTE, 0);
        }

        initButtonAdd(view);

        notesListView = view.findViewById(R.id.notes_list_listview);
        initNotesList();

        if (ConfigInfo.isLandscape(requireContext())) {
            showNoteInfo();
        }
    }

    private void initButtonAdd(@NonNull View view) {
        buttonAddNote = view.findViewById(R.id.notes_button_add);
        buttonAddNote.setOnClickListener(v -> {
            currentPosition = -1;
            showNoteInfo();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(CURRENT_NOTE, currentPosition);
        super.onSaveInstanceState(outState);
    }

    private void initNotesList() {
        try {
            notesList = new ArrayList(Arrays.asList(NotesBaseActivity.getNotesData().getNotesList()));// for listview
            notesListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, notesList);
            notesListView.setAdapter(notesListAdapter);
            notesListView.setOnItemClickListener((parent, view, position, id) -> {
                currentPosition = position;
                showNoteInfo();
            });

            notesListView.setOnItemLongClickListener((parent, view, position, id) -> {
                PopupMenu popupMenu = new PopupMenu(requireActivity(), view);
                requireActivity().getMenuInflater().inflate(R.menu.notes_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_popup_edit_note:
                                currentPosition = position;
                                showNoteInfo();
                                return true;
                            case R.id.action_popup_delete_note:
                                currentPosition = 0;
                                NotesMainActivity.getNotesData().deleteNote(position);
                                initNotesList();
                                return true;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            });

        } catch (Exception e) {
            Toast toast = Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private void showNoteInfo() {
        showFragment(NoteInfoFragment.newInstance(currentPosition));
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (ConfigInfo.isLandscape(requireContext())) {
            ft.replace(R.id.notes_info_fragment_container, fragment);
        } else {
            ft.replace(R.id.notes_list_fragment_container, fragment);
        }
        ft.addToBackStack("");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}