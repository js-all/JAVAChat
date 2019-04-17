package pouloulou_inc.zimbawejava;

import android.app.Application;

import io.socket.client.Socket;

public class GlobalClass extends Application {
    private Socket socket = null;
    private int id = -1;
    public void setSocket(Socket value) {
        socket = value;
    }
    public Socket getSocket() {
        return socket;
    }
    public  void setId(int value) {
        id = value;
    }
    public  int getId() {
        return id;
    }
}
