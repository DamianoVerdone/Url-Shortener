public class ShortUrlDto {

    private String user;
    private int accessNumber;
    private String longUrl;

    public ShortUrlDto(String user, int accessNumber, String longUrl) {
        this.user = user;
        this.accessNumber = accessNumber;
        this.longUrl = longUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getAccessNumber() {
        return accessNumber;
    }

    public void setAccessNumber(int accessNumber) {
        this.accessNumber = accessNumber;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
