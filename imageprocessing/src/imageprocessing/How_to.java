package imageprocessing;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBuilder;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;

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
        return StageBuilder.create().fullScreen( false ).scene( new Scene( group_how_to ) ).resizable( false ).title( "Information" ).width( Screen.getPrimary().getBounds().getWidth() - 80 ).height( 510 ).build();
    }
    private static VBox prepare_group_how_to()
    {
        String aciklama;
        aciklama = Constants.props.getProperty( "how_to" );
        txt_how_to = TextBuilder.create().text( aciklama ).font( Constants.default_font ).textAlignment( TextAlignment.JUSTIFY ).wrappingWidth( Screen.getPrimary().getBounds().getWidth() - 140 ).translateX( 10 ).translateY( 10 ).build();
        ScrollPane sp = new ScrollPane();
        sp.setPrefWidth( Screen.getPrimary().getBounds().getWidth() - 120 );
        sp.setPrefHeight( 500 );
        sp.setVmax( 500 );
        sp.setHmax( Screen.getPrimary().getBounds().getWidth() - 120 );
        sp.setContent( txt_how_to );
        sp.setVbarPolicy( ScrollPane.ScrollBarPolicy.ALWAYS );
        return VBoxBuilder.create().children( sp ).build();
    }
}
