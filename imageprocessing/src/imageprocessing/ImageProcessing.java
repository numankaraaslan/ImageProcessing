package imageprocessing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.swing.filechooser.FileSystemView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import static org.opencv.imgcodecs.Imgcodecs.imencode;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class ImageProcessing
{
    static
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    }

    private LinkedList<Operation> operations;
    private LinkedList<ComboBox> comboboxes;
    private int number_of_operations, horizontal_count;
    private VideoCapture capture;
    private Window main_window;
    private String file_path;
    private Timer timer;
    private long refreshMilisecond = 33;
    private Cam_capture capture_view;
    private Button btn_capture_camera;
    private Button btn_capture_file;
    private Button btn_capture_image;
    private Button btn_save_ops;
    private Button btn_load_ops;
    private Button btn_reset_ops;
    private Button btn_how_to;
    private String desktop_path;
    private TextField textfield_width;
    private TextField textfield_height;
    private boolean load_from_file;
    private Text txt_X;
    //most used
    private Image final_image;
    private MatOfByte mem;
    private Mat processed_frame;

    public void start( final Stage primaryStage )
    {
        operations = new LinkedList<>();
        comboboxes = new LinkedList<>();
        Operator.processors = ( int ) ( Runtime.getRuntime().availableProcessors() * 1.5 );
        Operator.threads = new Deteriorationthread[ Operator.processors ];
        load_from_file = false;
        capture = new VideoCapture();
        desktop_path = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "\\";
        final VBox root = new VBox( 10 );
        int window_width, window_height;
        window_width = number_of_operations < horizontal_count ? ( number_of_operations * 295 ) + 10 : ( horizontal_count * 295 ) + 10;
        window_height = ( int ) ( number_of_operations < horizontal_count ? 320 : Math.ceil( ( double ) number_of_operations / ( double ) horizontal_count ) * 300 );
        if ( horizontal_count < 4 )
        {
            window_height += 50;
        }
        final Scene scene = new Scene( root, window_width, window_height );
        root.setStyle( "-fx-background-color: linear-gradient(#ffffff, #66ccff)" );
        VBox hbox_buttons = build_main_buttons();
        VBox hbox_ops = build_hbox_ops();
        root.getChildren().addAll( hbox_buttons, hbox_ops );
        primaryStage.setTitle( "Realtime Image Processing" );
        primaryStage.setScene( scene );
        primaryStage.setResizable( false );
        main_window = primaryStage.getOwner();
        primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>()
        {
            @Override
            public void handle( WindowEvent event )
            {
                capture.release();
                capture_view = null;
                System.exit( 1 );
            }
        } );
        primaryStage.show();
    }

    public void set_op_count( int op_count )
    {
        this.number_of_operations = op_count;
    }

    public void set_horizontal_count( int horizontal_count )
    {
        this.horizontal_count = horizontal_count;
    }

    public void set_props( Properties props )
    {
        Constants.props = props;
    }

    private VBox build_main_buttons()
    {
        btn_capture_camera = new Button( Constants.props.getProperty( "btn_capture_camera" ) );
        btn_capture_file = new Button( Constants.props.getProperty( "btn_capture_file" ) );
        btn_capture_image = new Button( Constants.props.getProperty( "btn_capture_image" ) );
        btn_save_ops = new Button( Constants.props.getProperty( "btn_save_ops" ) );
        btn_load_ops = new Button( Constants.props.getProperty( "btn_load_ops" ) );
        btn_reset_ops = new Button( Constants.props.getProperty( "btn_reset_ops" ) );
        btn_how_to = new Button( "?" );
        btn_capture_image.setDisable( true );
        btn_capture_camera.setOnAction( btn_capture_camera_action() );
        btn_save_ops.setOnAction( btn_save_ops_action() );
        btn_load_ops.setOnAction( btn_load_ops_action() );
        btn_reset_ops.setOnAction( btn_reset_ops_action() );
        btn_how_to.setOnAction( btn_how_to_action() );
        btn_capture_file.setOnAction( btn_capture_file_action() );
        btn_capture_image.setOnAction( btn_capture_image_action() );
        btn_capture_camera.setFont( Constants.default_font );
        btn_save_ops.setFont( Constants.default_font );
        btn_load_ops.setFont( Constants.default_font );
        btn_reset_ops.setFont( Constants.default_font );
        btn_how_to.setFont( Constants.default_font );
        btn_capture_file.setFont( Constants.default_font );
        btn_capture_image.setFont( Constants.default_font );
        btn_reset_ops.setStyle( "-fx-background-color: Red;" );
        textfield_width = new TextField( "1280" );
        textfield_height = new TextField( "720" );
        textfield_height.setFont( Constants.default_font );
        textfield_width.setFont( Constants.default_font );
        textfield_width.setPrefWidth( 80 );
        textfield_height.setPrefWidth( 80 );
        txt_X = new Text( "X" );
        txt_X.setTranslateY( 5 );
        txt_X.setFont( Constants.default_font );
        VBox vbox_buttons = null;
        if ( horizontal_count < 4 )
        {
            HBox hbox_control_buttons = new HBox( 10, textfield_width, txt_X, textfield_height, btn_capture_camera, btn_capture_file );
            HBox hbox_opton_buttons = new HBox( 10, btn_capture_image, btn_save_ops, btn_load_ops, btn_reset_ops, btn_how_to );
            vbox_buttons = new VBox( 10, hbox_control_buttons, hbox_opton_buttons );
        }
        else
        {
            vbox_buttons = new VBox( 10, new HBox( 10, textfield_width, txt_X, textfield_height, btn_capture_camera, btn_capture_file, btn_capture_image, btn_save_ops, btn_load_ops, btn_reset_ops, btn_how_to ) );
        }
        vbox_buttons.setTranslateX( 10 );
        vbox_buttons.setTranslateY( 10 );
        return vbox_buttons;
    }

    private VBox build_hbox_ops()
    {
        VBox vbox_ops_all = new VBox( 10 );
        ArrayList<HBox> hbox_ops_lines = new ArrayList<>();
        HBox hbox_ops_line = new HBox( 10 );
        hbox_ops_line.setMinHeight( 270 );
        hbox_ops_lines.add( hbox_ops_line );
        for ( int i = 0; i < number_of_operations; i++ )
        {
            Group vbox_ops = build_vbox_ops( i );
            hbox_ops_line.getChildren().add( vbox_ops );
            Operation some_operation = new Operation( i );
            operations.add( some_operation );
            if ( ( i + 1 ) % horizontal_count == 0 )
            {
                hbox_ops_line = new HBox( 10 );
                hbox_ops_line.setMinHeight( 270 );
                hbox_ops_lines.add( hbox_ops_line );
            }
        }
        vbox_ops_all.getChildren().addAll( hbox_ops_lines );
        vbox_ops_all.setTranslateX( 10 );
        vbox_ops_all.setTranslateY( 10 );
        return vbox_ops_all;
    }

    private Group build_vbox_ops( final int op_number )
    {
        final Group vbox_operation = new Group();
        Text txt_operation = new Text( Constants.props.getProperty( "txt_operation" ) + ( op_number + 1 ) );
        txt_operation.setFont( Constants.default_font );
        final ComboBox combobox_operations = new ComboBox();
        final Button btn_apply = new Button( Constants.props.getProperty( "btn_apply" ) );
        combobox_operations.getItems().addAll( Operation_types.get_all() );
        combobox_operations.setStyle( "-fx-font-size : 14pt" );
        combobox_operations.setPrefWidth( 285 );
        combobox_operations.setTranslateY( 10 );
        combobox_operations.getSelectionModel().selectFirst();
        final VBox vbox_crop_rgb = new VBox( 5 );
        Text txt_crop_low = new Text( Constants.props.getProperty( "txt_crop_low" ) );
        txt_crop_low.setFont( Constants.default_font );
        final TextField textfield_crop_low = new TextField();
        textfield_crop_low.setPrefWidth( 70 );
        textfield_crop_low.setFont( Constants.default_font );
        Text txt_crop_high = new Text( Constants.props.getProperty( "txt_crop_high" ) );
        txt_crop_high.setFont( Constants.default_font );
        final TextField textfield_crop_high = new TextField();
        textfield_crop_high.setFont( Constants.default_font );
        textfield_crop_high.setPrefWidth( 70 );
        vbox_crop_rgb.getChildren().addAll( new HBox( 5, txt_crop_low, textfield_crop_low ), new HBox( 5, txt_crop_high, textfield_crop_high ) );
        vbox_crop_rgb.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.crop_RGB ) );
        final VBox vbox_clahe = new VBox( 5 );
        final Text txt_clahe_clip_limit = new Text( Constants.props.getProperty( "txt_clahe_clip_limit" ) + ": 40" );
        txt_clahe_clip_limit.setFont( Constants.default_font );
        final Slider slider_clahe_limit = new Slider( 0, 100, 40 );
        slider_clahe_limit.setPrefWidth( 280 );
        slider_clahe_limit.setStyle( "-fx-font-size : 14pt" );
        slider_clahe_limit.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_clahe_clip_limit.setText( Constants.props.getProperty( "txt_clahe_clip_limit" ) + ": " + ( int ) slider_clahe_limit.getValue() );
                btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
            }
        } );
        final Text txt_clahe_clip_size = new Text( Constants.props.getProperty( "txt_clahe_clip_size" ) + ": 8" );
        txt_clahe_clip_size.setFont( Constants.default_font );
        final Slider slider_clahe_size = new Slider( 1, 50, 8 );
        slider_clahe_size.setPrefWidth( 280 );
        slider_clahe_size.setStyle( "-fx-font-size : 14pt" );
        slider_clahe_size.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_clahe_clip_size.setText( Constants.props.getProperty( "txt_clahe_clip_size" ) + ": " + ( int ) slider_clahe_size.getValue() );
                btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
            }
        } );
        vbox_clahe.getChildren().addAll( txt_clahe_clip_limit, slider_clahe_limit, txt_clahe_clip_size, slider_clahe_size );
        vbox_clahe.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.clahe ) );
        final VBox vbox_convolution = new VBox( 5 );
        Text txt_convolution = new Text( Constants.props.getProperty( "txt_convolution" ) );
        txt_convolution.setFont( Constants.default_font );
        final TextField textfield_convolution_1_1 = new TextField();
        final TextField textfield_convolution_1_2 = new TextField();
        final TextField textfield_convolution_1_3 = new TextField();
        final TextField textfield_convolution_2_1 = new TextField();
        final TextField textfield_convolution_2_2 = new TextField();
        final TextField textfield_convolution_2_3 = new TextField();
        final TextField textfield_convolution_3_1 = new TextField();
        final TextField textfield_convolution_3_2 = new TextField();
        final TextField textfield_convolution_3_3 = new TextField();
        textfield_convolution_1_1.setPrefWidth( 70 );
        textfield_convolution_2_1.setPrefWidth( 70 );
        textfield_convolution_3_1.setPrefWidth( 70 );
        textfield_convolution_1_2.setPrefWidth( 70 );
        textfield_convolution_2_2.setPrefWidth( 70 );
        textfield_convolution_3_2.setPrefWidth( 70 );
        textfield_convolution_1_3.setPrefWidth( 70 );
        textfield_convolution_2_3.setPrefWidth( 70 );
        textfield_convolution_3_3.setPrefWidth( 70 );
        textfield_convolution_1_1.setFont( Constants.default_font );
        textfield_convolution_2_1.setFont( Constants.default_font );
        textfield_convolution_3_1.setFont( Constants.default_font );
        textfield_convolution_1_2.setFont( Constants.default_font );
        textfield_convolution_2_2.setFont( Constants.default_font );
        textfield_convolution_3_2.setFont( Constants.default_font );
        textfield_convolution_1_3.setFont( Constants.default_font );
        textfield_convolution_2_3.setFont( Constants.default_font );
        textfield_convolution_3_3.setFont( Constants.default_font );
        vbox_convolution.getChildren().add( txt_convolution );
        vbox_convolution.getChildren().add( new HBox( 5, textfield_convolution_1_1, textfield_convolution_1_2, textfield_convolution_1_3 ) );
        vbox_convolution.getChildren().add( new HBox( 5, textfield_convolution_2_1, textfield_convolution_2_2, textfield_convolution_2_3 ) );
        vbox_convolution.getChildren().add( new HBox( 5, textfield_convolution_3_1, textfield_convolution_3_2, textfield_convolution_3_3 ) );
        vbox_convolution.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.convolution ) );
        final VBox vbox_brightnesss = new VBox( 5 );
        final Text txt_brightnesss = new Text( Constants.props.getProperty( "txt_brightnesss" ) + ": 0" );
        txt_brightnesss.setFont( Constants.default_font );
        final Slider slider_brightnesss = new Slider( -255, 255, 0 );
        slider_brightnesss.setPrefWidth( 280 );
        slider_brightnesss.setStyle( "-fx-font-size : 14pt" );
        slider_brightnesss.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_brightnesss.setText( Constants.props.getProperty( "txt_brightnesss" ) + ": " + ( int ) slider_brightnesss.getValue() );
                btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
            }
        } );
        vbox_brightnesss.getChildren().addAll( txt_brightnesss, slider_brightnesss );
        vbox_brightnesss.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.brightnesss ) );
        final VBox vbox_contrast = new VBox( 5 );
        final Text txt_contrast = new Text( Constants.props.getProperty( "txt_contrast" ) + ": 1" );
        txt_contrast.setFont( Constants.default_font );
        final Slider slider_contrast = new Slider( 0, 5, 1 );
        slider_contrast.setPrefWidth( 280 );
        slider_contrast.setStyle( "-fx-font-size : 14pt" );
        slider_contrast.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_contrast.setText( Constants.props.getProperty( "txt_contrast" ) + ": " + new DecimalFormat( "#.##" ).format( slider_contrast.getValue() ) );
                btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
            }
        } );
        vbox_contrast.getChildren().addAll( txt_contrast, slider_contrast );
        vbox_contrast.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.contrast ) );
        final VBox vbox_deter = new VBox( 5 );
        final Text txt_deter_coef = new Text( Constants.props.getProperty( "txt_deter_coef" ) + ": 40" );
        txt_deter_coef.setFont( Constants.default_font );
        final Slider slider_deter_coef = new Slider( 1, 255, 40 );
        slider_deter_coef.setPrefWidth( 280 );
        slider_deter_coef.setStyle( "-fx-font-size : 14pt" );
        slider_deter_coef.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_deter_coef.setText( Constants.props.getProperty( "txt_deter_coef" ) + ": " + ( int ) slider_deter_coef.getValue() );
                btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
            }
        } );
        vbox_deter.getChildren().addAll( txt_deter_coef, slider_deter_coef );
        vbox_deter.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.deterioration ) );
        final VBox vbox_blur = new VBox( 5 );
        final Text txt_blur_kernel_size = new Text( Constants.props.getProperty( "txt_blur_kernel_size" ) + ": 40" );
        txt_blur_kernel_size.setFont( Constants.default_font );
        final Slider slider_blur_kernel_size = new Slider( 1, 200, 40 );
        slider_blur_kernel_size.setPrefWidth( 280 );
        slider_blur_kernel_size.setStyle( "-fx-font-size : 14pt" );
        slider_blur_kernel_size.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_blur_kernel_size.setText( Constants.props.getProperty( "txt_blur_kernel_size" ) + ": " + ( int ) slider_blur_kernel_size.getValue() );
                btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
            }
        } );
        vbox_blur.getChildren().addAll( txt_blur_kernel_size, slider_blur_kernel_size );
        vbox_blur.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.blurred ) );
        final VBox vbox_ghost_size = new VBox( 5 );
        final Text txt_ghost_size = new Text( Constants.props.getProperty( "txt_ghost_size" ) + ": 10" );
        txt_ghost_size.setFont( Constants.default_font );
        final Slider slider_ghost_size = new Slider( 2, 100, 0 );
        slider_ghost_size.setPrefWidth( 280 );
        slider_ghost_size.setStyle( "-fx-font-size : 14pt" );
        slider_ghost_size.valueProperty().addListener( new ChangeListener<Number>()
        {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue )
            {
                txt_ghost_size.setText( Constants.props.getProperty( "txt_ghost_size" ) + ": " + ( int ) slider_ghost_size.getValue() );
                btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
            }
        } );
        vbox_ghost_size.getChildren().addAll( txt_ghost_size, slider_ghost_size );
        vbox_ghost_size.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.ghost ) );
        final VBox vbox_set_rgb = new VBox( 5 );
        Text txt_set_rgb = new Text( Constants.props.getProperty( "txt_set_rgb" ) );
        txt_set_rgb.setFont( Constants.default_font );
        final TextField textfield_set_rgb_R = new TextField();
        final TextField textfield_set_rgb_G = new TextField();
        final TextField textfield_set_rgb_B = new TextField();
        textfield_set_rgb_R.setPrefWidth( 70 );
        textfield_set_rgb_G.setPrefWidth( 70 );
        textfield_set_rgb_B.setPrefWidth( 70 );
        textfield_set_rgb_R.setFont( Constants.default_font );
        textfield_set_rgb_G.setFont( Constants.default_font );
        textfield_set_rgb_B.setFont( Constants.default_font );
        vbox_set_rgb.getChildren().addAll( txt_set_rgb, new HBox( 5, textfield_set_rgb_R, textfield_set_rgb_G, textfield_set_rgb_B ) );
        vbox_set_rgb.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.set_RGB ) );
        final VBox vbox_change_rgb = new VBox( 5 );
        Text txt_change_rgb = new Text( Constants.props.getProperty( "txt_change_rgb" ) );
        txt_change_rgb.setFont( Constants.default_font );
        final TextField textfield_change_rgb_R = new TextField();
        final TextField textfield_change_rgb_G = new TextField();
        final TextField textfield_change_rgb_B = new TextField();
        textfield_change_rgb_R.setPrefWidth( 70 );
        textfield_change_rgb_G.setPrefWidth( 70 );
        textfield_change_rgb_B.setPrefWidth( 70 );
        textfield_change_rgb_R.setFont( Constants.default_font );
        textfield_change_rgb_G.setFont( Constants.default_font );
        textfield_change_rgb_B.setFont( Constants.default_font );
        vbox_change_rgb.getChildren().addAll( txt_change_rgb, new HBox( 5, textfield_change_rgb_R, textfield_change_rgb_G, textfield_change_rgb_B ) );
        vbox_change_rgb.visibleProperty().bind( combobox_operations.getSelectionModel().selectedItemProperty().asString().isEqualTo( Operation_types.change_RGB ) );
        btn_apply.setFont( Constants.default_font );
        btn_apply.setTranslateY( 60 );
        final String original_style = btn_apply.getStyle();
        combobox_operations.getSelectionModel().selectedItemProperty().addListener( new ChangeListener()
        {
            @Override
            public void changed( ObservableValue observable, Object oldValue, Object newValue )
            {
                if ( !oldValue.equals( newValue ) )
                {
                    Operation this_operation = find_operation( op_number );
                    if ( newValue.toString().equals( Operation_types.crop_RGB ) )
                    {
                        textfield_crop_low.setText( this_operation.get_crop_values()[0] + "" );
                        textfield_crop_high.setText( this_operation.get_crop_values()[1] + "" );
                        btn_apply.setTranslateY( 130 );
                    }
                    else if ( newValue.toString().equals( Operation_types.clahe ) )
                    {
                        slider_clahe_limit.setValue( this_operation.get_clahe_values()[0] );
                        slider_clahe_size.setValue( this_operation.get_clahe_values()[1] );
                        btn_apply.setTranslateY( 180 );
                    }
                    else if ( newValue.toString().equals( Operation_types.convolution ) )
                    {
                        textfield_convolution_1_1.setText( this_operation.get_convolution()[0][0] + "" );
                        textfield_convolution_1_2.setText( this_operation.get_convolution()[0][1] + "" );
                        textfield_convolution_1_3.setText( this_operation.get_convolution()[0][2] + "" );
                        textfield_convolution_2_1.setText( this_operation.get_convolution()[1][0] + "" );
                        textfield_convolution_2_2.setText( this_operation.get_convolution()[1][1] + "" );
                        textfield_convolution_2_3.setText( this_operation.get_convolution()[1][2] + "" );
                        textfield_convolution_3_1.setText( this_operation.get_convolution()[2][0] + "" );
                        textfield_convolution_3_2.setText( this_operation.get_convolution()[2][1] + "" );
                        textfield_convolution_3_3.setText( this_operation.get_convolution()[2][2] + "" );
                        btn_apply.setTranslateY( 210 );
                    }
                    else if ( newValue.toString().equals( Operation_types.brightnesss ) )
                    {
                        slider_brightnesss.setValue( this_operation.get_brightnesss() );
                        btn_apply.setTranslateY( 130 );
                    }
                    else if ( newValue.toString().equals( Operation_types.ghost ) )
                    {
                        slider_ghost_size.setValue( this_operation.get_ghost_size() );
                        btn_apply.setTranslateY( 130 );
                    }
                    else if ( newValue.toString().equals( Operation_types.contrast ) )
                    {
                        slider_contrast.setValue( this_operation.get_contrast() );
                        btn_apply.setTranslateY( 130 );
                    }
                    else if ( newValue.toString().equals( Operation_types.deterioration ) )
                    {
                        slider_deter_coef.setValue( this_operation.get_deterioration() );
                        btn_apply.setTranslateY( 130 );
                    }
                    else if ( newValue.toString().equals( Operation_types.blurred ) )
                    {
                        slider_blur_kernel_size.setValue( this_operation.get_blur_size() );
                        btn_apply.setTranslateY( 130 );
                    }
                    else if ( newValue.toString().equals( Operation_types.set_RGB ) )
                    {
                        textfield_set_rgb_R.setText( this_operation.get_RGB_values()[0] + "" );
                        textfield_set_rgb_G.setText( this_operation.get_RGB_values()[1] + "" );
                        textfield_set_rgb_B.setText( this_operation.get_RGB_values()[2] + "" );
                        btn_apply.setTranslateY( 130 );
                    }
                    else if ( newValue.toString().equals( Operation_types.change_RGB ) )
                    {
                        textfield_change_rgb_R.setText( this_operation.get_change_RGB_values()[0] + "" );
                        textfield_change_rgb_G.setText( this_operation.get_change_RGB_values()[1] + "" );
                        textfield_change_rgb_B.setText( this_operation.get_change_RGB_values()[2] + "" );
                        btn_apply.setTranslateY( 150 );
                    }
                    else
                    {
                        btn_apply.setTranslateY( 60 );
                    }
                    if ( !load_from_file )
                    {
                        btn_apply.setStyle( "-fx-background-color: LightSalmon;" );
                    }
                    else
                    {
                        btn_apply.setStyle( original_style );
                    }
                }
            }
        } );
        btn_apply.setOnAction( new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                Operation this_operation = find_operation( op_number );
                if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.binary ) )
                {
                    this_operation.set_op_name( Operation_types.binary );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.blurred ) )
                {
                    this_operation.set_op_name( Operation_types.blurred );
                    this_operation.set_blur_size( ( int ) slider_blur_kernel_size.getValue() );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.ghost ) )
                {
                    this_operation.set_op_name( Operation_types.ghost );
                    this_operation.set_ghost_size( ( int ) slider_ghost_size.getValue() );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.deterioration ) )
                {
                    this_operation.set_op_name( Operation_types.deterioration );
                    this_operation.set_deterioration( ( int ) slider_deter_coef.getValue() );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.edge_detect ) )
                {
                    this_operation.set_op_name( Operation_types.edge_detect );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.grayscale ) )
                {
                    this_operation.set_op_name( Operation_types.grayscale );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.negative ) )
                {
                    this_operation.set_op_name( Operation_types.negative );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.dilate ) )
                {
                    this_operation.set_op_name( Operation_types.dilate );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.erode ) )
                {
                    this_operation.set_op_name( Operation_types.erode );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.crop_RGB ) )
                {
                    this_operation.set_op_name( Operation_types.crop_RGB );
                    this_operation.set_crop_values( Integer.parseInt( textfield_crop_low.getText().equals( "" ) ? "0" : textfield_crop_low.getText() ), Integer.parseInt( textfield_crop_high.getText().equals( "" ) ? "255" : textfield_crop_high.getText() ) );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.clahe ) )
                {
                    this_operation.set_op_name( Operation_types.clahe );
                    this_operation.set_clahe_values( ( int ) slider_clahe_limit.getValue(), ( int ) slider_clahe_size.getValue() );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.convolution ) )
                {
                    double[][] convolution = new double[ 3 ][];
                    convolution[0] = new double[]
                    {
                        Double.parseDouble( textfield_convolution_1_1.getText() ), Double.parseDouble( textfield_convolution_1_2.getText() ), Double.parseDouble( textfield_convolution_1_3.getText() )
                    };
                    convolution[1] = new double[]
                    {
                        Double.parseDouble( textfield_convolution_2_1.getText() ), Double.parseDouble( textfield_convolution_2_2.getText() ), Double.parseDouble( textfield_convolution_2_3.getText() )
                    };
                    convolution[2] = new double[]
                    {
                        Double.parseDouble( textfield_convolution_3_1.getText() ), Double.parseDouble( textfield_convolution_3_2.getText() ), Double.parseDouble( textfield_convolution_3_3.getText() )
                    };
                    this_operation.set_op_name( Operation_types.convolution );
                    this_operation.set_convolution( convolution );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.brightnesss ) )
                {
                    this_operation.set_op_name( Operation_types.brightnesss );
                    this_operation.set_brightnesss( ( int ) slider_brightnesss.getValue() );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.contrast ) )
                {
                    this_operation.set_op_name( Operation_types.contrast );
                    this_operation.set_contrast( slider_contrast.getValue() );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.set_RGB ) )
                {
                    this_operation.set_op_name( Operation_types.set_RGB );
                    int R_value = textfield_set_rgb_R.getText().equals( "" ) ? -1 : Integer.parseInt( textfield_set_rgb_R.getText() );
                    int G_value = textfield_set_rgb_G.getText().equals( "" ) ? -1 : Integer.parseInt( textfield_set_rgb_G.getText() );
                    int B_value = textfield_set_rgb_B.getText().equals( "" ) ? -1 : Integer.parseInt( textfield_set_rgb_B.getText() );
                    this_operation.set_RGB_values( R_value, G_value, B_value );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.change_RGB ) )
                {
                    this_operation.set_op_name( Operation_types.change_RGB );
                    int R_value = textfield_change_rgb_R.getText().equals( "" ) ? 0 : Integer.parseInt( textfield_change_rgb_R.getText() );
                    int G_value = textfield_change_rgb_G.getText().equals( "" ) ? 0 : Integer.parseInt( textfield_change_rgb_G.getText() );
                    int B_value = textfield_change_rgb_B.getText().equals( "" ) ? 0 : Integer.parseInt( textfield_change_rgb_B.getText() );
                    this_operation.change_RGB_values( R_value, G_value, B_value );
                }
                else if ( combobox_operations.getSelectionModel().getSelectedItem().toString().equals( Operation_types.normal ) )
                {
                    this_operation.set_op_name( Operation_types.normal );
                }
                btn_apply.setStyle( original_style );
            }
        } );
        comboboxes.add( combobox_operations );
        vbox_crop_rgb.setTranslateY( 60 );
        vbox_convolution.setTranslateY( 60 );
        vbox_contrast.setTranslateY( 60 );
        vbox_blur.setTranslateY( 60 );
        vbox_deter.setTranslateY( 60 );
        vbox_ghost_size.setTranslateY( 60 );
        vbox_brightnesss.setTranslateY( 60 );
        vbox_set_rgb.setTranslateY( 60 );
        vbox_change_rgb.setTranslateY( 60 );
        vbox_clahe.setTranslateY( 60 );
        vbox_operation.getChildren().addAll( txt_operation, combobox_operations, vbox_crop_rgb, vbox_convolution, vbox_contrast, vbox_brightnesss, vbox_set_rgb, vbox_change_rgb, vbox_deter, vbox_blur, vbox_ghost_size, vbox_clahe, btn_apply );
        return vbox_operation;
    }

    private void startGrabbing()
    {
        if ( capture.isOpened() )
        {
            capture_view = new Cam_capture( ( int ) capture.get( Videoio.CAP_PROP_FRAME_WIDTH ), ( int ) capture.get( Videoio.CAP_PROP_FRAME_HEIGHT ), ( int ) ( 1000 / refreshMilisecond ) );
            capture_view.get_stage().setOnCloseRequest( cam_capture_closing() );
            for ( Operation operation : operations )
            {
                if ( operation.get_op_name().equals( Operation_types.ghost ) )
                {
                    operation.initialize_ghost_mats();
                }
            }
            TimerTask frameGrabber = new TimerTask()
            {
                @Override
                public void run()
                {
                    capture_view.set_frame( grabFrame() );
                }
            };
            timer = new Timer();
            timer.schedule( frameGrabber, 0, refreshMilisecond );
        }
        else
        {
            System.err.println( "Impossible to open the camera connection..." );
        }
    }

    private Image grabFrame()
    {
        final_image = null;
        Mat original_frame = new Mat();
        if ( capture.isOpened() )
        {
            capture.read( original_frame );
            if ( !original_frame.empty() )
            {
                processed_frame = Operator.do_operations( original_frame, operations );
                mem = new MatOfByte();
                imencode( ".bmp", processed_frame, mem );
                final_image = new Image( new ByteArrayInputStream( mem.toArray() ) );
            }
        }
        return final_image;
    }

    private Operation find_operation( int number_to_find )
    {
        for ( Operation some_operation : operations )
        {
            if ( some_operation.get_operation_number() == number_to_find )
            {
                return some_operation;
            }
        }
        return null;
    }

    private EventHandler<ActionEvent> btn_capture_camera_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                btn_capture_image.setDisable( false );
                capture.release();
                capture.open( 0 );
                if ( capture.get( Videoio.CAP_PROP_FPS ) != 0 )
                {
                    refreshMilisecond = ( int ) ( 1000 / ( int ) capture.get( Videoio.CAP_PROP_FPS ) ) - 1;
                }
                capture.set( Videoio.CAP_PROP_FRAME_WIDTH, textfield_width.getText().equals( "" ) ? -1 : Integer.parseInt( textfield_width.getText() ) );
                capture.set( Videoio.CAP_PROP_FRAME_HEIGHT, textfield_height.getText().equals( "" ) ? -1 : Integer.parseInt( textfield_height.getText() ) );
                startGrabbing();
            }
        };
    }

    private EventHandler<ActionEvent> btn_capture_file_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                FileChooser file_chooser = new FileChooser();
                file_chooser.setTitle( Constants.props.getProperty( "file_chooser.title" ) );
                FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter( Constants.props.getProperty( "file_chooser.ext" ), "*.avi", "*.mpeg", "*.mp4", "*.mkv" );
                file_chooser.getExtensionFilters().add( extension );
                file_chooser.setSelectedExtensionFilter( extension );
                file_chooser.setInitialDirectory( new File( desktop_path ) );
                file_path = "";
                File selected_file = file_chooser.showOpenDialog( main_window );
                if ( selected_file != null )
                {
                    file_path = selected_file.getAbsolutePath();
                }
                if ( !file_path.equals( "" ) )
                {
                    capture.release();
                    capture.open( file_path );
                    if ( capture.get( Videoio.CAP_PROP_FPS ) != 0 )
                    {
                        refreshMilisecond = ( long ) ( 1000 / capture.get( Videoio.CAP_PROP_FPS ) );
                    }
                    startGrabbing();
                    btn_capture_image.setDisable( false );
                }
            }
        };
    }

    private EventHandler<ActionEvent> btn_capture_image_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                LocalDateTime now = java.time.LocalDateTime.now();
                Imgcodecs.imwrite( desktop_path + "Saved " + now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear() + "-" + now.getHour() + "-" + now.getMinute() + "-" + now.getSecond() + ".bmp", processed_frame );
                Message_box.show( Constants.props.getProperty( "msg_image_saved" ), "Info", Message_box.info_message );
            }
        };
    }

    private EventHandler<ActionEvent> btn_save_ops_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                String ops_info = "";
                for ( Operation some_operation : operations )
                {
                    if ( some_operation.get_op_name().equals( Operation_types.binary ) )
                    {
                        ops_info += Operation_types.op_binary + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.blurred ) )
                    {
                        ops_info += Operation_types.op_blurred + ";" + some_operation.get_blur_size() + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.blurred ) )
                    {
                        ops_info += Operation_types.op_ghost + ";" + some_operation.get_ghost_size() + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.deterioration ) )
                    {
                        ops_info += Operation_types.op_deterioration + ";" + some_operation.get_deterioration() + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.brightnesss ) )
                    {
                        ops_info += Operation_types.op_brightnesss + ";" + some_operation.get_brightnesss() + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.contrast ) )
                    {
                        ops_info += Operation_types.op_contrast + ";" + some_operation.get_contrast() + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.convolution ) )
                    {
                        ops_info += Operation_types.op_convolution + ";" + some_operation.get_convolution()[0][0] + "," + some_operation.get_convolution()[0][1] + "," + some_operation.get_convolution()[0][2] + "," + some_operation.get_convolution()[1][0] + "," + some_operation.get_convolution()[1][1] + "," + some_operation.get_convolution()[1][2] + "," + some_operation.get_convolution()[2][0] + "," + some_operation.get_convolution()[2][1] + "," + some_operation.get_convolution()[2][2] + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.crop_RGB ) )
                    {
                        ops_info += Operation_types.op_crop_RGB + ";" + some_operation.get_crop_values()[0] + "," + some_operation.get_crop_values()[1] + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.clahe ) )
                    {
                        ops_info += Operation_types.op_clahe + ";" + some_operation.get_clahe_values()[0] + "," + some_operation.get_clahe_values()[1] + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.edge_detect ) )
                    {
                        ops_info += Operation_types.op_edge_detect + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.grayscale ) )
                    {
                        ops_info += Operation_types.op_grayscale + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.negative ) )
                    {
                        ops_info += Operation_types.op_negative + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.dilate ) )
                    {
                        ops_info += Operation_types.op_dilate + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.erode ) )
                    {
                        ops_info += Operation_types.op_erode + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.set_RGB ) )
                    {
                        ops_info += Operation_types.op_set_RGB + ";" + some_operation.get_RGB_values()[0] + "," + some_operation.get_RGB_values()[1] + "," + some_operation.get_RGB_values()[2] + "\n";
                    }
                    else if ( some_operation.get_op_name().equals( Operation_types.change_RGB ) )
                    {
                        ops_info += Operation_types.op_change_RGB + ";" + some_operation.get_change_RGB_values()[0] + "," + some_operation.get_change_RGB_values()[1] + "," + some_operation.get_change_RGB_values()[2] + "\n";
                    }
                }
                LocalDateTime now = java.time.LocalDateTime.now();
                try ( FileWriter file_writer = new FileWriter( desktop_path + "operations" + now.getDayOfMonth() + "-" + now.getMonthValue() + "-" + now.getYear() + "-" + now.getHour() + "-" + now.getMinute() + "-" + now.getSecond() + ".cfg" ) )
                {
                    file_writer.write( ops_info );
                    Message_box.show( Constants.props.getProperty( "config.saved" ), "Info", Message_box.info_message );
                }
                catch ( IOException ex )
                {
                    Message_box.show( Constants.props.getProperty( "config.notsaved" ), "Error", Message_box.warning_message );
                }
            }
        };
    }

    private EventHandler<ActionEvent> btn_load_ops_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                FileChooser file_chooser = new FileChooser();
                file_chooser.setTitle( Constants.props.getProperty( "file_chooser.operations.title" ) );
                FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter( Constants.props.getProperty( "file_chooser.operations.ext" ), "*.cfg" );
                file_chooser.getExtensionFilters().add( extension );
                file_chooser.setSelectedExtensionFilter( extension );
                file_chooser.setInitialDirectory( new File( desktop_path ) );
                file_path = "";
                File selected_file = file_chooser.showOpenDialog( main_window );
                if ( selected_file != null )
                {
                    file_path = selected_file.getAbsolutePath();
                }
                if ( !file_path.equals( "" ) )
                {
                    ArrayList<String> op_infos = new ArrayList<>();
                    try ( BufferedReader buffered_reader = new BufferedReader( new FileReader( file_path ) ) )
                    {
                        while ( buffered_reader.ready() )
                        {
                            op_infos.add( buffered_reader.readLine() );
                        }
                    }
                    catch ( IOException ex )
                    {
                        Message_box.show( Constants.props.getProperty( "config.notloaded" ), "Error", Message_box.warning_message );
                    }
                    int loaded_ops = op_infos.size();
                    if ( loaded_ops <= number_of_operations )
                    {
                        for ( int i = 0; i < number_of_operations; i++ )
                        {
                            Operation operation_to_set = new Operation( i );
                            if ( i < loaded_ops )
                            {
                                operation_to_set = find_operation( i );
                                if ( op_infos.get( i ).startsWith( Operation_types.op_binary ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.binary );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_blurred ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.blurred );
                                    operation_to_set.set_blur_size( Integer.parseInt( op_infos.get( i ).split( ";" )[1] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_ghost ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.ghost );
                                    operation_to_set.set_ghost_size( Integer.parseInt( op_infos.get( i ).split( ";" )[1] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_deterioration ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.deterioration );
                                    operation_to_set.set_deterioration( Integer.parseInt( op_infos.get( i ).split( ";" )[1] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_brightnesss ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.brightnesss );
                                    operation_to_set.set_brightnesss( Integer.parseInt( op_infos.get( i ).split( ";" )[1] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_contrast ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.contrast );
                                    operation_to_set.set_contrast( Double.parseDouble( op_infos.get( i ).split( ";" )[1] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_convolution ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.convolution );
                                    double[][] convolution_matrix = new double[ 3 ][];
                                    convolution_matrix[0] = new double[]
                                    {
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[0] ),
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[1] ),
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[2] )
                                    };
                                    convolution_matrix[1] = new double[]
                                    {
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[3] ),
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[4] ),
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[5] )
                                    };
                                    convolution_matrix[2] = new double[]
                                    {
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[6] ),
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[7] ),
                                        Double.parseDouble( op_infos.get( i ).split( ";" )[1].split( "," )[8] )
                                    };
                                    operation_to_set.set_convolution( convolution_matrix );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_crop_RGB ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.crop_RGB );
                                    operation_to_set.set_crop_values( Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[0] ), Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[1] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_clahe ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.clahe );
                                    operation_to_set.set_clahe_values( Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[0] ), Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[1] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_edge_detect ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.edge_detect );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_grayscale ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.grayscale );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_negative ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.negative );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_dilate ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.dilate );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_erode ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.erode );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_set_RGB ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.set_RGB );
                                    operation_to_set.set_RGB_values( Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[0] ), Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[1] ), Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[2] ) );
                                }
                                else if ( op_infos.get( i ).startsWith( Operation_types.op_change_RGB ) )
                                {
                                    operation_to_set.set_op_name( Operation_types.change_RGB );
                                    operation_to_set.change_RGB_values( Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[0] ), Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[1] ), Integer.parseInt( op_infos.get( i ).split( ";" )[1].split( "," )[2] ) );
                                }
                            }
                            else
                            {
                                operations.set( i, operation_to_set );
                            }
                            load_from_file = true;
                            comboboxes.get( i ).getSelectionModel().select( operation_to_set.get_op_name() );
                            load_from_file = false;
                        }
                        Message_box.show( Constants.props.getProperty( "config.loaded" ), "Info", Message_box.info_message );
                    }
                    else
                    {
                        Message_box.show( Constants.props.getProperty( "config.notloaded.too_much" ) + loaded_ops, "Error", Message_box.warning_message );
                    }
                }
            }
        };
    }

    private EventHandler<ActionEvent> btn_reset_ops_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent t )
            {
                load_from_file = true;
                for ( ComboBox combobox_op : comboboxes )
                {
                    combobox_op.getSelectionModel().selectFirst();
                }
                for ( int k = 0; k < operations.size(); k++ )
                {
                    operations.set( k, new Operation( k ) );
                }
                load_from_file = false;
            }
        };
    }

    private EventHandler<ActionEvent> btn_how_to_action()
    {
        return new EventHandler<ActionEvent>()
        {
            @Override
            public void handle( ActionEvent event )
            {
                How_to.show();
            }
        };
    }

    private EventHandler<WindowEvent> cam_capture_closing()
    {
        return new EventHandler<WindowEvent>()
        {
            @Override
            public void handle( WindowEvent event )
            {
                capture.release();
                timer.cancel();
                btn_capture_image.setDisable( true );
            }
        };
    }
}
