package ua.vvat.words4chars;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ua.vvat.words4chars.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private WordsForChars wordsForChars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordsForChars = new WordsForChars(this);
    }

    private WordsForChars.WordsCase wordsCase = null;

    public void findWords(View view) {
        EditText editChars = (EditText) findViewById(R.id.editChars);
        String chars = editChars.getText().toString();
        wordsCase = wordsForChars.getWordsCase(chars);
        TextView textWords = findViewById(R.id.textWords);
        textWords.setText("...");
        textWords.setText(String.join(", ", wordsCase.getFound()));
    }
    public void findWordsFiltered(View view) {
        if (wordsCase == null)
            return;
        EditText editFilter = (EditText) findViewById(R.id.editFilter);
        String mask = editFilter.getText().toString();
        TextView textFiltered = findViewById(R.id.textFiltered);
        textFiltered.setText("...");
        textFiltered.setText(String.join(", ", wordsCase.getFoundFiltered(mask)));
    }
}
