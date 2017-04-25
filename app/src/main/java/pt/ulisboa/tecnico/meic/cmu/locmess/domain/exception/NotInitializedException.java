package pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception;

public class NotInitializedException extends RuntimeException {
    private String name;

    public NotInitializedException(String className) {
        name = className;
    }

    @Override
    public String getMessage() {
        return name + " was not initialized.";
    }
}
