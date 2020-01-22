package com.pravesh.triviaapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.pravesh.triviaapp.R;
import com.pravesh.triviaapp.data.AppController;

import com.pravesh.triviaapp.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // initialisation

    int currentIndex = 0;
    int highScoreStored = 0;
    ArrayList<Question> questionArrayList;
    TextView question, highScore, currentScore, questionIndex;
    int your_Score = 0;
    Button btnTrue, btnFalse;
    ImageButton btnNext, btnPrevious;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding views in xml with java

        cardView = findViewById(R.id.cardView);
        question = findViewById(R.id.question);
        highScore = findViewById(R.id.high_score);
        currentScore = findViewById(R.id.your_score);
        questionIndex = findViewById(R.id.questioNo);
        btnFalse = findViewById(R.id.btnFalse);
        btnNext = findViewById(R.id.btnNext);
        btnTrue = findViewById(R.id.btnTrue);
        btnPrevious = findViewById(R.id.btnPrevious);

        // instantiation of ArrayList is very important or it may lead to NullPointerException

        questionArrayList = new ArrayList<>();

        //using OnClickListener interface

        btnTrue.setOnClickListener(this);
        btnFalse.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);

        //setting the view for onCreate

        currentScore.setText(getString(R.string.your_score,your_Score));

        //using SharedPres to store high score

        SharedPreferences sharedPreferences = getSharedPreferences("HS", MODE_PRIVATE);
        highScoreStored = sharedPreferences.getInt("highScore", 0);
        //concatenation in setText gives warning so it this way
        highScore.setText(getString(R.string.highs,highScoreStored));

        //Sending JSON request using volley

        String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String question = response.getJSONArray(i).getString(0);
                                boolean answer = response.getJSONArray(i).getBoolean(1);


                                //Add question objects to list
                                questionArrayList.add(new Question(question, answer));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        question.setText(questionArrayList.get(currentIndex).getQuestion());
                        questionIndex.setText(getString(R.string.sign,currentIndex,questionArrayList.size()));


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
        // using request queue and adding the request
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                // for changing the questionNo and updating it

                currentIndex = (currentIndex + 1) % questionArrayList.size();
                updateQuestion(currentIndex);
                break;
            case R.id.btnPrevious:
                if (currentIndex > 0) {
                    currentIndex = (currentIndex - 1) % questionArrayList.size();
                }
                updateQuestion(currentIndex);
                break;
            case R.id.btnTrue:
                checkAnswer(true, currentIndex);
                updateHighScore();
                break;
            case R.id.btnFalse:
                checkAnswer(false, currentIndex);
                updateHighScore();
                break;
        }
    }

    private void updateHighScore() {

        // storing the current score iff it is > storedHighScore

        if (highScoreStored < your_Score) {
            highScore.setText(getString(R.string.highs,your_Score));
            SharedPreferences sharedPreferences = getSharedPreferences("HS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highScore", your_Score);
            editor.apply();
        }
    }

    private void checkAnswer(boolean answer, int index) {

        // checking for answer and adding score if correct
        boolean ans = questionArrayList.get(index).getAnswer();
        if (answer == ans) {
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            your_Score += 1;
            goGreen();

        } else {
            shake();
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
        }
        currentScore.setText(getString(R.string.your_score,your_Score));
    }

    private void goGreen() {
        // adding animation programmatically

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(80);
        alphaAnimation.setRepeatCount(4);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void shake() {

        // adding animation using xml

        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        cardView.startAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void updateQuestion(int index) {
        question.setText(questionArrayList.get(index).getQuestion());
        questionIndex.setText(getString(R.string.sign,index,questionArrayList.size()));

    }
}
