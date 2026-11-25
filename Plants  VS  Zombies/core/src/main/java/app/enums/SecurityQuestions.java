package app.enums;

public enum SecurityQuestions {
    FAVORITE_COLOR(1, "What is your favorite color?"),
    FAVORITE_FOOD(2, "What is your favorite food?"),
    FAVORITE_BOOK(3, "What is your favorite movie?"),
    PET_NAME(4, "What was your first pet’s name?"),
    MOTHER_NAME(5, "What was your mother’s maiden name?"),
    SCHOOL_NAME(6, "What was the name of your first school?"),
    CITY_NAME(7, "What city were you born in?")




    ;
    private final int number;
    private final String question;

    SecurityQuestions(int number, String question) {
        this.number = number;
        this.question = question;
    }

    public int getNumber() {
        return number;
    }

    public String getQuestion() {
        return question;
    }
    public static SecurityQuestions getQuestionByNumber(int number) {
        for(SecurityQuestions question : values()) {
            if(question.getNumber() == number) {
                return question;
            }
        }
        return null;
    }


}
