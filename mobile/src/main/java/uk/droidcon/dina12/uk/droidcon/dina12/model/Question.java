package uk.droidcon.dina12.uk.droidcon.dina12.model;

/**
 * Created by mailat on 31.10.15.
 */
public class Question {

    String image_left;
    String image_right;
    String category;
    String correct_answer;
    String question_text;
    String question_mp3;
    String weight;

    public Question() {
        // empty default constructor, necessary for Firebase to be able to deserialize questions
    }

    public String getImage_left() {
        return image_left;
    }

    public void setImage_left(String image_left) {
        this.image_left = image_left;
    }

    public String getImage_right() {
        return image_right;
    }

    public void setImage_right(String image_right) {
        this.image_right = image_right;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }

    public String getQuestion_mp3() {
        return question_mp3;
    }

    public void setQuestion_mp3(String question_mp3) {
        this.question_mp3 = question_mp3;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


}
