package imageprocessing;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Op_count_stage extends Application
{
    private ComboBox<Integer> combobox_op_count;
    private ImageProcessing main_window;
    private EventHandler<ActionEvent> locale_tr_action;
    private EventHandler<ActionEvent> locale_en_action;
    @Override
    public void start( final Stage primaryStage ) throws Exception
    {
        check_screen_res();
        Constants.default_font = Font.font( "Arial", 18 );
        main_window = new ImageProcessing();
        get_props( "TR" );
        primaryStage.setResizable( false );
        VBox root = new VBox( 10 );
        VBox vbox_op_count = new VBox( 10 );
        combobox_op_count = new ComboBox<>();
        combobox_op_count.setPrefWidth( 290 );
        combobox_op_count.setStyle( "-fx-font-size : 16pt" );
        for ( int i = 3; i < 10; i++ )
        {
            combobox_op_count.getItems().add( i + 1 );
        }
        combobox_op_count.getSelectionModel().selectFirst();
        final Text txt_op_count = new Text( Constants.props.getProperty( "txt_op_count" ) );
        vbox_op_count.setTranslateX( 10 );
        vbox_op_count.setTranslateY( 10 );
        txt_op_count.setFont( Constants.default_font );
        final Button btn_ok = new Button( Constants.props.getProperty( "btn_ok" ) );
        btn_ok.setFont( Constants.default_font );
        btn_ok.setPrefWidth( 290 );
        btn_ok.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                main_window.set_op_count( combobox_op_count.getSelectionModel().getSelectedItem() );
                main_window.start( new Stage() );
                primaryStage.close();
            }
        } );
        combobox_op_count.setOnKeyTyped( new EventHandler<KeyEvent>()
        {
            @Override
            public void handle( KeyEvent event )
            {
                if ( event.getCode() == KeyCode.UNDEFINED )
                {
                    btn_ok.onActionProperty().get().handle( null );
                }
            }
        } );
        vbox_op_count.getChildren().addAll( txt_op_count, combobox_op_count, btn_ok );
        HBox hbox_locale = new HBox( 10 );
        hbox_locale.setTranslateX( 10 );
        hbox_locale.setTranslateY( 10 );
        ToggleGroup some_group = new ToggleGroup();
        RadioButton locale_TR = new RadioButton( "TR" );
        RadioButton locale_EN = new RadioButton( "EN" );
        locale_TR.setSelected( true );
        locale_tr_action = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                get_props( "TR" );
                txt_op_count.setText( Constants.props.getProperty( "txt_op_count" ) );
                btn_ok.setText( Constants.props.getProperty( "btn_ok" ) );
                primaryStage.setTitle( Constants.props.getProperty( "op_count_stage.title" ) );
            }
        };
        locale_en_action = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                get_props( "EN" );
                txt_op_count.setText( Constants.props.getProperty( "txt_op_count" ) );
                btn_ok.setText( Constants.props.getProperty( "btn_ok" ) );
                primaryStage.setTitle( Constants.props.getProperty( "op_count_stage.title" ) );
            }
        };
        locale_TR.setOnAction( locale_tr_action );
        locale_EN.setOnAction( locale_en_action );
        locale_TR.setFont( Constants.default_font );
        locale_EN.setFont( Constants.default_font );
        locale_TR.setToggleGroup( some_group );
        locale_EN.setToggleGroup( some_group );
        hbox_locale.getChildren().addAll( locale_TR, locale_EN );
        root.getChildren().addAll( hbox_locale, vbox_op_count );
        Scene scene = new Scene( root, 300, 170 );
        primaryStage.setTitle( Constants.props.getProperty( "op_count_stage.title" ) );
        primaryStage.setScene( scene );
        primaryStage.setOnCloseRequest( primary_closing() );
        primaryStage.show();
    }
    private EventHandler<WindowEvent> primary_closing()
    {
        return new EventHandler<WindowEvent>()
        {
            @Override
            public void handle( WindowEvent event )
            {
                main_window.set_op_count( combobox_op_count.getSelectionModel().getSelectedItem() );
                main_window.start( new Stage() );
            }
        };
    }

    private void get_props( String locale )
    {
        Constants.props = new Properties();
        try ( InputStream input = locale.equals( "TR" ) ? new FileInputStream( "src\\imageprocessing\\strings_tr_TR.properties" ) : new FileInputStream( "src\\imageprocessing\\strings_en_US.properties" ) )
        {
            Constants.props.load( input );
        }
        catch ( Exception ex )
        {
            System.out.println( ex.getMessage() );
        }
        main_window.set_props( Constants.props );
        Operation_types.set_all( Constants.props );
    }

    private void check_screen_res()
    {
        if ( Screen.getPrimary().getBounds().getWidth() < 1280 || Screen.getPrimary().getBounds().getHeight() < 720 )
        {
            Message_box.show( Constants.props.getProperty( "res_warning" ), Constants.props.getProperty( "warning" ), Message_box.warning_message );
        }
    }
}
