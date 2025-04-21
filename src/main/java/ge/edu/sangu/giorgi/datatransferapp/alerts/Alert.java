package ge.edu.sangu.giorgi.datatransferapp.alerts;

public class Alert {
    public Notification notification;

    public Alert(Notification notification){
        this.notification = notification;
    }

    public void show(String message){
        notification.showAlert(message);
    }
}
