package com.nk.imageprocessing;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Cam_capture
{
	private final ImageView image_on_screen;

	private final Stage primaryStage;

	private int count = 0;

	private final Timer timer;

	private String cap_info;

	public Stage get_stage()
	{
		return primaryStage;
	}

	public Cam_capture(int cap_width, int cap_height, int fps)
	{
		primaryStage = new Stage(StageStyle.DECORATED);
		VBox root = new VBox(10);
		image_on_screen = new ImageView();
		image_on_screen.setFitWidth(cap_width);
		image_on_screen.setFitHeight(cap_height);
		image_on_screen.setTranslateX(10);
		image_on_screen.setTranslateY(10);
		image_on_screen.fitWidthProperty().bind(primaryStage.widthProperty().subtract(35));
		image_on_screen.fitHeightProperty().bind(primaryStage.heightProperty().subtract(95));
		cap_info = "Resolution = " + cap_width + " X " + cap_height + " @" + fps;
		final Text txt_resolution = new Text(cap_info);
		txt_resolution.setFont(Font.font("Arial", 18));
		txt_resolution.setTranslateX(10);
		txt_resolution.setTranslateY(10);
		root.getChildren().addAll(txt_resolution, image_on_screen);
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						txt_resolution.setText(cap_info + " / " + count);
						count = 0;
					}
				});
			}
		}, 1000, 1000);
		Scene scene = new Scene(root, cap_width + 10, cap_height + 40);
		primaryStage.setTitle("Frame Capturing");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void set_frame(final Image new_image)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				image_on_screen.setImage(new_image);
				count++;
			}
		});
	}
}
