package com.nk.imageprocessing;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class How_to
{
	private static Stage stage_how_to;

	private static Text txt_how_to;

	public static void show()
	{
		stage_how_to = prepare_stage_how_to();
		stage_how_to.show();
	}

	private static Stage prepare_stage_how_to()
	{
		VBox group_how_to = prepare_group_how_to();
		Stage stage = new Stage();
		stage.setFullScreen(false);
		stage.setScene(new Scene(group_how_to));
		stage.setResizable(false);
		stage.setTitle("Information");
		stage.setWidth(Screen.getPrimary().getBounds().getWidth() - 80);
		stage.setHeight(510);
		return stage;
	}

	private static VBox prepare_group_how_to()
	{
		String aciklama;
		aciklama = Constants.props.getProperty("how_to");
		txt_how_to = new Text();
		txt_how_to.setText(aciklama);
		txt_how_to.setFont(Constants.default_font);
		txt_how_to.setTextAlignment(TextAlignment.JUSTIFY);
		txt_how_to.setWrappingWidth(Screen.getPrimary().getBounds().getWidth() - 140);
		txt_how_to.setTranslateX(10);
		txt_how_to.setTranslateY(10);
		ScrollPane sp = new ScrollPane();
		sp.setPrefWidth(Screen.getPrimary().getBounds().getWidth() - 130);
		sp.setPrefHeight(500);
		sp.setVmax(500);
		sp.setHmax(Screen.getPrimary().getBounds().getWidth() - 120);
		sp.setContent(txt_how_to);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		return new VBox(sp);
	}
}
