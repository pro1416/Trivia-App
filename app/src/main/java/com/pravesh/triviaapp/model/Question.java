package com.pravesh.triviaapp.model;


// model class that will store question and answer as a object in arraylist
public class Question {
    private String question;
    private boolean answer;

    public Question(String question, boolean answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }


    public boolean getAnswer() {
        return answer;
    }


}
