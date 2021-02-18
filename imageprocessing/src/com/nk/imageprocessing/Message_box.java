package com.nk.imageprocessing;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Message_box
{
	public static int info_message = 0;

	public static int warning_message = 1;

	private static Stage stage;

	private static Text text;

	private static Button button;

	private static VBox group;

	private static Font my_font;

	public static void show(String message, String title, int message_type)
	{
		my_font = Font.font("Arial", 20);
		stage = new Stage();
		stage.setTitle(title);
		stage.setResizable(false);
		text = new Text();
		text.setText(message);
		text.setWrappingWidth(500);
		text.setTextOrigin(VPos.CENTER);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFont(my_font);
		text.setTranslateY(10);
		button = new Button();
		button.setText("OK");
		button.prefWidth(100);
		button.prefHeight(25);
		button.setPrefWidth(100);
		button.setPrefHeight(25);
		button.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				stage.close();
			}
		});
		group = new VBox(30, text, button);
		group.setLayoutY(10);
		group.setAlignment(Pos.TOP_CENTER);
		group.prefWidth(300);
		group.prefHeight(200);
		group.setPrefWidth(300);
		group.setPrefHeight(200);
		stage.setScene(new Scene(group));
		if (message_type == warning_message)
		{
			stage.getIcons().add(new Image(ImageProcessing.class.getResourceAsStream("imgs/warning.png")));
		}
		else if (message_type == info_message)
		{
			stage.getIcons().add(new Image(ImageProcessing.class.getResourceAsStream("imgs/info.png")));
		}
		stage.setWidth(text.getWrappingWidth() + 40);
		stage.setHeight(170);
		stage.show();
	}
}
