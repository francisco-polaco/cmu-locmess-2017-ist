package pt.ulisboa.tecnico.meic.cmu.locmess.domain.exception;

public class PermissionNotGrantedException extends RuntimeException {
    private String permission;

    public PermissionNotGrantedException(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String getMessage() {
        return "Permisson " + permission + " not granted!";
    }
}
