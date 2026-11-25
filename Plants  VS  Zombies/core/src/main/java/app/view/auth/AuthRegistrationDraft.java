package app.view.auth;

public final class AuthRegistrationDraft {
    private String username = "";
    private String nickname = "";
    private String email = "";
    private String gender = "";
    private String age = "";
    private String password = "";
    private String passwordConfirm = "";
    private String questionNumber = "";
    private String answer = "";
    private String answerConfirm = "";
    private boolean acceptedTerms;

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = value(username);
    }

    public String nickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = value(nickname);
    }

    public String email() {
        return email;
    }

    public void setEmail(String email) {
        this.email = value(email);
    }

    public String gender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = value(gender);
    }

    public String age() {
        return age;
    }

    public void setAge(String age) {
        this.age = value(age);
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = value(password);
    }

    public String passwordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = value(passwordConfirm);
    }

    public String questionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = value(questionNumber);
    }

    public String answer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = value(answer);
    }

    public String answerConfirm() {
        return answerConfirm;
    }

    public void setAnswerConfirm(String answerConfirm) {
        this.answerConfirm = value(answerConfirm);
    }

    public boolean acceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public void clearStep2() {
        password = "";
        passwordConfirm = "";
        questionNumber = "";
        answer = "";
        answerConfirm = "";
    }

    public void clearAll() {
        username = "";
        nickname = "";
        email = "";
        gender = "";
        age = "";
        clearStep2();
        acceptedTerms = false;
    }

    private static String value(String input) {
        return input == null ? "" : input;
    }
}
