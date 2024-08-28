public class Message {
    private final long id;
    private final String message;

    public Message(long id, String content) {
        this.id = id;
        this.message = content;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
