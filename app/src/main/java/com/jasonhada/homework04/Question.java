package com.jasonhada.homework04;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable{
    public int Index;
    public String Question;
    public String Url;
    public ArrayList<String> Options;
    public int AnswerIndex;
    public int UserAnswerIndex;

    public Question() {

    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public ArrayList<String> getOptions() {
        return Options;
    }

    public void setOptions(ArrayList<String> options) {
        Options = options;
    }

    public int getAnswerIndex() {
        return AnswerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        AnswerIndex = answerIndex;
    }

    public int getUserAnswerIndex() {
        return UserAnswerIndex;
    }

    public void setUserAnswerIndex(int userAnswerIndex) {
        UserAnswerIndex = userAnswerIndex;
    }
}
