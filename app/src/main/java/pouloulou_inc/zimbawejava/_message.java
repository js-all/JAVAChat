package pouloulou_inc.zimbawejava;

public class _message {
    public String author = "";
    public String _content = "";
    public _message(String a,String b) {
        this.author = a;
        this._content = b;
    };
    public void setAuthor(String a) {
        this.author = a;
    }
    public void set_content(String a) {
        this._content = a;
    }
    public _message() {
        set_content("");
        setAuthor("");
    }

    public String getAuthor() {
        return author;
    }

    public String get_content() {
        return _content;
    }
}
