package jt.projects.androidcore.notes;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;

import jt.projects.androidcore.R;
import jt.projects.androidcore.common.ConfigInfo;


public class NotesListFragment extends Fragment implements NoteChangeObserver {
    private static final String CURRENT_NOTE = "CurrentNote";
    private int currentPosition = 0;// Текущая позиция
    private ListView notesListView;
    private ArrayList<String> notesList;
    private ArrayAdapter<String> notesListAdapter;
    private MaterialButton buttonAddNote;

    public NotesListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(CURRENT_NOTE, 0);
        }

        notesListView = view.findViewById(R.id.notes_list_listview);
        buttonAddNote = view.findViewById(R.id.notes_button_add);
        buttonAddNote.setOnClickListener(v -> {
            currentPosition = -1;
            showNoteInfo();
        });

        initNotesList();
        if (new ConfigInfo(getContext()).isLandscape()) {
            showNoteInfo();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(CURRENT_NOTE, currentPosition);
        super.onSaveInstanceState(outState);
    }

    private void initNotesList() {
//        LinearLayout layoutView = (LinearLayout) currentView;
//        String[] notes = NotesBaseActivity.getNotesData().getNotesList();
//
//        for (int i = 0; i < notes.length; i++) {
//            String note = notes[i];
//            TextView tw = new TextView(getContext());
//            tw.setText(note);
//            tw.setTextSize(24);
//            layoutView.addView(tw);
//            final int pos = i;
//            tw.setOnClickListener(v -> {
//                currentPosition = pos;
//                showNoteInfo();
//            });
//        }
        try {
            notesList = new ArrayList(Arrays.asList(NotesBaseActivity.getNotesData().getNotesList()));// for listview
            notesListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, notesList);
            notesListView.setAdapter(notesListAdapter);
            notesListView.setOnItemClickListener((parent, view, position, id) -> {
                currentPosition = position;
                showNoteInfo();
            });
        } catch (Exception e) {
            Toast toast = Toast.makeText(requireActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void changeNote(NotesData.Note note, int index) {
        try {
            if (index != -1) {
                notesList.set(index, note.getTopic());
            } else {
                notesList.add(note.getTopic());
            }
            notesListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast toast = Toast.makeText(requireActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void showNoteInfo() {
        if (new ConfigInfo(getContext()).isLandscape()) {
            showLandscapeNoteInfo();
        } else showPortraitNoteInfo();
    }

    private void showPortraitNoteInfo() {
//        Activity notesInfoActivity = requireActivity();
//        final Intent intent = new Intent(NotesListFragment.this.getActivity(), NoteInfoActivity.class);
//        intent.putExtra(NoteInfoFragment.CURRENT_NOTE_INDEX, currentPosition);
//        notesInfoActivity.startActivity(intent);
        NoteInfoFragment noteInfoFragment = NoteInfoFragment.newInstance(currentPosition);
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.notes_list_fragment_container, noteInfoFragment);
        ft.addToBackStack("");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void showLandscapeNoteInfo() {
        NoteInfoFragment noteInfoFragment = NoteInfoFragment.newInstance(currentPosition);
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.notes_info_fragment_container, noteInfoFragment);
        ft.addToBackStack("");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

}