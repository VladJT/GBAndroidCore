package jt.projects.androidcore.notes;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import jt.projects.androidcore.R;
import jt.projects.androidcore.common.ConfigInfo;


public class NotesListFragment extends Fragment implements NoteChangeObserver {
    private static final String CURRENT_NOTE = "CurrentNote";
    private int currentPosition = 0;// Текущая позиция
    private View currentView;

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
        currentView = view;
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
        LinearLayout layoutView = (LinearLayout) currentView;
        String[] notes = NotesBaseActivity.getNotesData().getNotesList();

        for (int i = 0; i < notes.length; i++) {
            String note = notes[i];
            TextView tw = new TextView(getContext());
            tw.setText(note);
            tw.setTextSize(24);
            layoutView.addView(tw);
            final int pos = i;
            tw.setOnClickListener(v -> {
                currentPosition = pos;
                showNoteInfo();
            });
        }
    }

    private void showNoteInfo() {
        if (new ConfigInfo(getContext()).isLandscape()) {
            showLandscapeNoteInfo();
        } else showPortraitNoteInfo();
    }

    private void showPortraitNoteInfo() {
        Activity notesInfoActivity = requireActivity();
        final Intent intent = new Intent(notesInfoActivity, NoteInfoActivity.class);
        intent.putExtra(NoteInfoFragment.CURRENT_NOTE_INDEX, currentPosition);
        notesInfoActivity.startActivity(intent);
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

    @Override
    public void changeNote(NotesData.Note note, int index) {
        initNotesList();
    }
}