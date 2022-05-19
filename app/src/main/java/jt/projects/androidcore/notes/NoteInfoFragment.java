package jt.projects.androidcore.notes;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jt.projects.androidcore.R;
import jt.projects.androidcore.common.ConfigInfo;


public class NoteInfoFragment extends Fragment {
    static final String CURRENT_NOTE_INDEX = "index";
    private int currentNoteIndex = -1;// -1 для добавления, остальные для изменения записей
    private NoteChangePublisher publisher;//обработчик подписок
    private TextInputEditText etTopic;
    private TextInputEditText etDescription;
    private TextInputEditText etAuthor;
    private DatePicker dateOfCreation;
    private MaterialButton bSave;

    public NoteInfoFragment() {
        // Required empty public constructor
    }


    public static NoteInfoFragment newInstance(int index) {
        NoteInfoFragment noteInfoFragment = new NoteInfoFragment();
        // Передача параметра через бандл
        Bundle b = new Bundle();
        b.putInt(CURRENT_NOTE_INDEX, index);
        noteInfoFragment.setArguments(b);
        return noteInfoFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            publisher = ((NoteChangePublisherGetter) getActivity()).getPublisher();
        } catch (Exception e) {
            Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            Bundle args = getArguments();
            if (args != null) {
                currentNoteIndex = args.getInt(CURRENT_NOTE_INDEX);
                NotesData.Note currentNote = NotesMainActivity.getNotesData().getNote(currentNoteIndex);

                etTopic = view.findViewById(R.id.notes_info_topic);
                etDescription = view.findViewById(R.id.notes_info_description);
                etAuthor = view.findViewById(R.id.notes_info_author);
                dateOfCreation = view.findViewById(R.id.notes_info_date_of_creation);
                bSave = view.findViewById(R.id.notes_info_button_save);

                etTopic.setText(currentNote.getTopic());
                etDescription.setText(currentNote.getDescription());
                etAuthor.setText(currentNote.getAuthor());
                int year = currentNote.getDateOfCreation().get(Calendar.YEAR);
                int month = currentNote.getDateOfCreation().get(Calendar.MONTH);
                int day = currentNote.getDateOfCreation().get(Calendar.DAY_OF_MONTH);
                dateOfCreation.init(year, month, day, null);
            }
            initListeners();
        } catch (Exception e) {
            Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void initListeners() {
        bSave.setOnClickListener(v -> {
            NotesData.Note newNote = new NotesData.Note(etTopic.getText().toString(),
                    etDescription.getText().toString(),
                    etAuthor.getText().toString(),
                    new GregorianCalendar(dateOfCreation.getYear(),
                            dateOfCreation.getMonth(), dateOfCreation.getDayOfMonth()));

            if (currentNoteIndex == -1) {
                NotesMainActivity.getNotesData().addNote(newNote);
            } else {
                NotesMainActivity.getNotesData().editNote(newNote, currentNoteIndex);
            }
            publisher.notify(newNote, currentNoteIndex);// уведомляем подписчиков о событии - сохранение заметки
            if (!new ConfigInfo(getActivity()).isLandscape()) {
                requireActivity().finish();
            }
        });
    }
}