package ge.edu.sangu.giorgi.datatransferapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Clock {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "EEE, dd LLL uuuu HH:mm:ss", Locale.ENGLISH
    );
    public static void getClock(Label clockLabel) {
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    LocalDateTime now = LocalDateTime.now();
                    clockLabel.setText(TIME_FORMATTER.format(now));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }
}
