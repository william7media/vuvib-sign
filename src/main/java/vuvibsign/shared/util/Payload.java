package vuvibsign.shared.util;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Payload<T> {

    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;

    private Payload(@NonNull Status status, @Nullable T data,
                    @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Payload<T> success(@NonNull T data) {
        return new Payload<>(Status.SUCCESS, data, null);
    }

    public static <T> Payload<T> error(String msg, @Nullable T data) {
        return new Payload<>(Status.ERROR, data, msg);
    }

    public enum Status {SUCCESS, ERROR}
}
